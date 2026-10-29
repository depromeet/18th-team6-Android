import Foundation
import Shared

@MainActor
final class OnboardingViewModel: ObservableObject {
    @Published private(set) var state: OnboardingViewState
    @Published private(set) var effect: OnboardingViewEffect?

    private let repository: OnboardingRepository?
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
        selectedOptionIds: Set<Int> = []
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
        self.data = initialData
        self.state = .success(initialData)
    }

    func selectGroup(_ group: OnboardingCategoryGroup) {
        guard data.groups.contains(where: { $0.id == group.id }) else { return }
        update { $0.selectedGroupId = group.id }
    }

    func toggleOption(_ option: OnboardingItemOption) {
        guard !data.isProcessing else { return }
        update { data in
            if data.selectedOptionIds.contains(option.id) {
                data.selectedOptionIds.remove(option.id)
                data.itemNames[option.id] = nil
                data.replacementPeriods[option.id] = nil
                data.quantities[option.id] = nil
            } else {
                data.selectedOptionIds.insert(option.id)
                data.itemNames[option.id] = option.title
                data.quantities[option.id] = OnboardingRegistrationConfig.defaultQuantity
            }
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
        let requests = data.selectedOptions.compactMap { option -> OnboardingRegistrationRequest? in
            let name = option.title.trimmingCharacters(in: .whitespacesAndNewlines)
            guard !name.isEmpty else { return nil }
            return OnboardingRegistrationRequest(categoryId: option.id, name: name)
        }
        guard requests.count == data.selectedCount else { return }

        guard let repository else {
            update { $0.step = .complete }
            return
        }

        update { $0.isProcessing = true }
        Task {
            await submitTask(requests: requests, repository: repository)
        }
    }

    private func loadOptionsAndMoveToCategorySelection(using repository: OnboardingRepository) async {
        do {
            async let loadedOptions = repository.options()
            async let loadedGroups = repository.groups()
            let (options, fetchedGroups) = try await (loadedOptions, loadedGroups)
            let groups = Self.sortedGroups(fetchedGroups)
            update { data in
                data.options = options
                data.groups = groups
                if !groups.contains(where: { $0.id == data.selectedGroupId }) {
                    data.selectedGroupId = Self.defaultGroupId(in: groups)
                }
                data.selectedOptionIds.formIntersection(Set(options.map(\.id)))
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

    private func submitTask(
        requests: [OnboardingRegistrationRequest],
        repository: OnboardingRepository
    ) async {
        do {
            try await repository.registerItems(requests: requests)
            update { data in
                data.step = .complete
                data.isProcessing = false
            }
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
}

private extension Error {
    var onboardingMessage: String {
        if self is CreateItemError.DuplicatedName {
            return "이미 사용중인 이름이에요."
        }

        if let localizedError = self as? LocalizedError,
           let description = localizedError.errorDescription {
            return description
        } else {
            return "온보딩 정보를 저장하지 못했어요."
        }
    }
}
