import Foundation
import Shared

@MainActor
final class OnboardingViewModel: ObservableObject {
    @Published private(set) var state: OnboardingViewState
    @Published private(set) var effect: OnboardingViewEffect?

    private let repository: OnboardingRepository?
    private let completionStore: OnboardingCompletionStore?
    private let notificationSetup: (any OnboardingNotificationSetup)?
    private var registeredOptionIds: Set<Int> = []
    private var data: OnboardingViewData {
        didSet {
            guard data != oldValue else { return }
            state = .success(data)
        }
    }

    init(
        repository: OnboardingRepository? = nil,
        initialOptions: [OnboardingItemOption] = OnboardingDefaults.options,
        initialGroups: [OnboardingCategoryGroup] = OnboardingDefaults.groups,
        selectedOptionIds: Set<Int> = [],
        completionStore: OnboardingCompletionStore? = nil,
        notificationSetup: (any OnboardingNotificationSetup)? = nil
    ) {
        let groups = Self.sortedGroups(initialGroups)
        let initialData = OnboardingViewData(
            step: .start,
            options: initialOptions,
            selectedOptionIds: selectedOptionIds,
            itemNames: [:],
            replacementPeriods: [:],
            quantities: [:],
            isProcessing: false,
            groups: groups,
            selectedGroupId: Self.defaultGroupId(in: groups)
        )
        self.repository = repository
        self.completionStore = completionStore
        self.notificationSetup = notificationSetup
        self.data = initialData
        self.state = .success(initialData)
    }

    func selectGroup(_ group: OnboardingCategoryGroup) {
        guard data.visibleGroups.contains(where: { $0.id == group.id }) else { return }
        update {
            $0.selectedGroupId = group.id
            $0.searchQuery = ""
            $0.submittedSearchQuery = nil
        }
    }

    func toggleOption(_ option: OnboardingItemOption) {
        guard !data.isProcessing, !registeredOptionIds.contains(option.id) else { return }
        update { data in
            if data.selectedOptionIds.contains(option.id) {
                data.selectedOptionIds.remove(option.id)
                data.itemNames[option.id] = nil
                data.replacementPeriods[option.id] = nil
                data.quantities[option.id] = nil
                if option.isNew {
                    data.options.removeAll { $0.id == option.id }
                    if !data.options.contains(where: \.isNew) {
                        data.selectedGroupId = data.visibleGroups.first?.id
                    }
                }
            } else {
                data.selectedOptionIds.insert(option.id)
                data.itemNames[option.id] = option.title
                data.quantities[option.id] = OnboardingRegistrationConfig.defaultQuantity
            }
        }
    }

    func updateSearchQuery(_ query: String) {
        guard !data.isProcessing else { return }
        update {
            $0.searchQuery = query
            if query.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty { $0.submittedSearchQuery = nil }
        }
    }

    func submitSearch(_ suggestion: String? = nil) {
        guard !data.isProcessing else { return }
        let query = (suggestion ?? data.searchQuery).trimmingCharacters(in: .whitespacesAndNewlines)
        update {
            $0.searchQuery = query
            $0.submittedSearchQuery = query.isEmpty ? nil : query
        }
    }

    func addSearchItem() {
        guard !data.isProcessing, data.searchResults.isEmpty,
              data.newItemNameError == nil, let name = data.submittedSearchQuery else { return }
        update { data in
            let id = min(data.options.map(\.id).min() ?? 0, 0) - 1
            data.options.append(OnboardingItemOption(id: id, title: name, addedCount: 0, imageURL: data.commonIconURL, categoryGroupId: OnboardingViewData.userGroupId, isNew: true))
            data.selectedOptionIds.insert(id)
            data.selectedGroupId = OnboardingViewData.userGroupId
            data.searchQuery = ""
            data.submittedSearchQuery = nil
        }
    }

    func startOnboarding() {
        guard !data.isProcessing else { return }

        if (data.options.isEmpty || data.groups.isEmpty), let repository {
            state = .loading
            Task {
                await loadOptionsAndMoveToCategorySelection(using: repository)
            }
            return
        }

        update { $0.step = .categorySelection }
    }

    func moveBack() -> Bool {
        switch data.step {
        case .start:
            return false
        case .categorySelection:
            update { $0.step = .start }
            return true
        case .replacementPeriod:
            update { $0.step = .categorySelection }
            return true
        case .complete:
            return false
        }
    }

    func retry() {
        guard let repository else {
            state = .success(data)
            return
        }
        state = .loading
        Task {
            await loadOptionsAndMoveToCategorySelection(using: repository)
        }
    }

    func selectReplacementPeriod(
        _ period: OnboardingReplacementPeriod,
        for option: OnboardingItemOption
    ) {
        guard data.selectedOptionIds.contains(option.id) else { return }
        update { $0.replacementPeriods[option.id] = period }
    }

    func updateItemName(
        _ name: String,
        for option: OnboardingItemOption
    ) {
        guard data.selectedOptionIds.contains(option.id) else { return }
        let clippedName = String(name.prefix(OnboardingRegistrationConfig.itemNameMaxLength))
        update { $0.itemNames[option.id] = clippedName }
    }

    func incrementQuantity(for option: OnboardingItemOption) {
        guard data.selectedOptionIds.contains(option.id) else { return }
        update { data in
            data.quantities[option.id] = min(
                data.quantity(for: option) + 1,
                OnboardingRegistrationConfig.quantityMaximum
            )
        }
    }

    func decrementQuantity(for option: OnboardingItemOption) {
        guard data.selectedOptionIds.contains(option.id) else { return }
        update { data in
            data.quantities[option.id] = max(
                data.quantity(for: option) - 1,
                OnboardingRegistrationConfig.quantityMinimum
            )
        }
    }

    func updateQuantity(
        _ quantity: Int,
        for option: OnboardingItemOption
    ) {
        guard data.selectedOptionIds.contains(option.id) else { return }
        update { data in
            data.quantities[option.id] = min(
                max(quantity, OnboardingRegistrationConfig.quantityMinimum),
                OnboardingRegistrationConfig.quantityMaximum
            )
        }
    }

    func moveNext() {
        switch data.step {
        case .start:
            startOnboarding()
        case .categorySelection:
            submit()
        case .replacementPeriod:
            submit()
        case .complete:
            break
        }
    }

    func clearEffect() {
        effect = nil
    }

    private func submit() {
        guard data.canContinue, !data.selectedOptions.isEmpty else { return }
        let options = data.selectedOptions
        update { $0.isProcessing = true }
        Task { await submitTask(options: options) }
    }

    private func loadOptionsAndMoveToCategorySelection(using repository: OnboardingRepository) async {
        do {
            async let loadedOptions = repository.options()
            async let loadedGroups = repository.groups()
            async let commonIconURL = repository.commonIconURL()
            let (options, fetchedGroups) = try await (loadedOptions, loadedGroups)
            let groups = Self.sortedGroups(fetchedGroups.filter { !$0.isUserDefined })
            let presetIds = Set(groups.map(\.id))
            let commonIcon = (try? await commonIconURL) ?? ""
            update { data in
                data.options = options.filter { $0.categoryGroupId.map(presetIds.contains) ?? false }
                data.commonIconURL = commonIcon
                data.groups = groups
                if !groups.contains(where: { $0.id == data.selectedGroupId }) {
                    data.selectedGroupId = Self.defaultGroupId(in: groups)
                }
                data.selectedOptionIds.formIntersection(Set(data.options.map(\.id)))
                if !data.visibleGroups.contains(where: { $0.id == data.selectedGroupId }) {
                    data.selectedGroupId = data.visibleGroups.first { $0.name == "욕실" }?.id ?? data.visibleGroups.first?.id
                }
                data.step = .categorySelection
                data.isProcessing = false
            }
        } catch {
            state = .loadFailed(message: error.onboardingMessage)
        }
    }

    private static func sortedGroups(_ groups: [OnboardingCategoryGroup]) -> [OnboardingCategoryGroup] {
        groups.sorted {
            if $0.displayOrder == $1.displayOrder { return $0.id < $1.id }
            return $0.displayOrder < $1.displayOrder
        }
    }

    private static func defaultGroupId(in groups: [OnboardingCategoryGroup]) -> Int? {
        groups.first { $0.name == "욕실" }?.id ?? groups.first?.id
    }

    private func submitTask(options: [OnboardingItemOption]) async {
        do {
            let pending = options.filter { !registeredOptionIds.contains($0.id) }
            for start in stride(from: 0, to: pending.count, by: 20) {
                let batch = Array(pending[start..<min(start + 20, pending.count)])
                let requests = batch.map { option in
                    OnboardingRegistrationRequest(categoryId: option.isNew ? nil : option.id, name: option.title, newCategoryName: option.isNew ? option.title : nil)
                }
                try await repository?.registerItems(requests: requests)
                registeredOptionIds.formUnion(batch.map(\.id))
            }
            // Persist before presenting OS permission so an interrupted prompt never re-registers items.
            completionStore?.markCompleted()
            await notificationSetup?.enableAndRequestPermission()
            update { $0.isProcessing = false }
            effect = .registered(options.count)
        } catch {
            update { $0.isProcessing = false }
            effect = .showMessage(error.onboardingMessage)
        }
    }

    private func update(_ transform: (inout OnboardingViewData) -> Void) {
        var nextData = data
        transform(&nextData)
        data = nextData
    }
}

enum OnboardingViewEffect: Equatable {
    case showMessage(String)
    case registered(Int)
}

private extension Error {
    var onboardingMessage: String {
        if self is CreateItemError.DuplicatedName {
            return ItemRegistrationNameRule.duplicateMessage
        }

        if let localizedError = self as? LocalizedError,
           let description = localizedError.errorDescription {
            return description
        } else {
            return "온보딩 정보를 저장하지 못했어요."
        }
    }
}
