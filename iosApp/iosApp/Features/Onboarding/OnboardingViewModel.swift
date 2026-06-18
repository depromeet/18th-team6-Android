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
        selectedOptionIds: Set<Int> = []
    ) {
        let initialData = OnboardingViewData(
            step: .start,
            options: initialOptions,
            selectedOptionIds: selectedOptionIds,
            itemNames: [:],
            replacementPeriods: [:],
            quantities: [:],
            isProcessing: false
        )
        self.repository = repository
        self.data = initialData
        self.state = .success(initialData)
    }

    func toggleOption(_ option: OnboardingItemOption) {
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

        if data.options.isEmpty, let repository {
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
            guard data.canContinue else { return }
            update { data in
                data.selectedOptions.forEach { option in
                    data.itemNames[option.id] = data.itemName(for: option)
                    data.quantities[option.id] = data.quantity(for: option)
                }
                data.step = .replacementPeriod
            }
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
        guard data.canContinue else { return }
        let requests = data.selectedOptions.compactMap { option -> OnboardingRegistrationRequest? in
            guard let period = data.replacementPeriods[option.id] else { return nil }
            let name = data.itemName(for: option).trimmingCharacters(in: .whitespacesAndNewlines)
            guard !name.isEmpty else { return nil }
            return OnboardingRegistrationRequest(
                categoryId: option.id,
                name: name,
                quantity: data.quantity(for: option),
                replacementPeriod: period
            )
        }

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
            let options = try await repository.options()
            update { data in
                data.options = options
                data.step = .categorySelection
                data.isProcessing = false
            }
        } catch {
            state = .loadFailed(message: error.onboardingMessage)
        }
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
