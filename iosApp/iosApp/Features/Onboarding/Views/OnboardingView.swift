import SwiftUI

struct OnboardingView: View {
    @StateObject private var viewModel: OnboardingViewModel
    @State private var snackbar: OnboardingSnackbarPresentation?
    @State private var snackbarDismissTask: Task<Void, Never>?

    let onBack: () -> Void
    let onComplete: () -> Void

    init(
        viewModelFactory: @MainActor @escaping () -> OnboardingViewModel = { OnboardingViewModel() },
        onBack: @escaping () -> Void,
        onComplete: @escaping () -> Void
    ) {
        _viewModel = StateObject(wrappedValue: viewModelFactory())
        self.onBack = onBack
        self.onComplete = onComplete
    }

    init(
        viewModel: OnboardingViewModel,
        onBack: @escaping () -> Void,
        onComplete: @escaping () -> Void
    ) {
        _viewModel = StateObject(wrappedValue: viewModel)
        self.onBack = onBack
        self.onComplete = onComplete
    }

    var body: some View {
        ZStack {
            content

            if let snackbar {
                VStack {
                    Spacer(minLength: 0)
                    OBRitSnackbar(message: snackbar.message, icon: snackbar.icon)
                        .padding(.horizontal, OBRitSpacing.s5)
                        .padding(.bottom, OBRitSpacing.s6)
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                }
                .allowsHitTesting(false)
                .animation(.easeOut(duration: 0.18), value: snackbar.id)
            }
        }
        .onChange(of: viewModel.effect) { _, effect in
            handleEffect(effect)
        }
        .onDisappear {
            snackbarDismissTask?.cancel()
        }
    }

    @ViewBuilder
    private var content: some View {
        switch viewModel.state {
        case .loading:
            OnboardingMessageView(title: "온보딩 정보를 불러오는 중이에요")
        case let .loadFailed(message):
            OnboardingFailureView(
                message: message,
                action: action
            )
        case let .success(data):
            switch data.step {
            case .start:
                OnboardingStartView(action: action)
            case .categorySelection:
                OnboardingCategorySelectionView(
                    data: data,
                    action: action
                )
            case .replacementPeriod:
                OnboardingReplacementPeriodView(
                    data: data,
                    action: action
                )
            case .complete:
                OnboardingCompleteView(action: action)
            }
        }
    }

    private var action: OnboardingViewAction {
        OnboardingViewAction(
            onStart: viewModel.startOnboarding,
            onBack: handleBack,
            onRetry: viewModel.retry,
            onToggleOption: viewModel.toggleOption,
            onUpdateItemName: { option, name in
                viewModel.updateItemName(name, for: option)
            },
            onSelectReplacementPeriod: { option, period in
                viewModel.selectReplacementPeriod(period, for: option)
            },
            onDecrementQuantity: { option in
                viewModel.decrementQuantity(for: option)
            },
            onIncrementQuantity: { option in
                viewModel.incrementQuantity(for: option)
            },
            onUpdateQuantity: { option, quantity in
                viewModel.updateQuantity(quantity, for: option)
            },
            onNext: viewModel.moveNext,
            onComplete: onComplete
        )
    }

    private func handleBack() {
        if !viewModel.moveBack() {
            onBack()
        }
    }

    private func handleEffect(_ effect: OnboardingViewEffect?) {
        guard let effect else { return }
        defer { viewModel.clearEffect() }

        switch effect {
        case let .showMessage(message):
            showSnackbar(message)
        }
    }

    private func showSnackbar(_ message: String) {
        snackbarDismissTask?.cancel()
        snackbar = OnboardingSnackbarPresentation(
            message: message,
            icon: .error
        )
        snackbarDismissTask = Task { @MainActor in
            try? await Task.sleep(nanoseconds: OnboardingSnackbarMetrics.displayDurationNanoseconds)
            guard !Task.isCancelled else { return }
            withAnimation(.easeOut(duration: 0.18)) {
                snackbar = nil
            }
        }
    }
}

private struct OnboardingSnackbarPresentation: Equatable, Identifiable {
    let id = UUID()
    let message: String
    let icon: OBRitSnackbarIcon
}

private enum OnboardingSnackbarMetrics {
    static let displayDurationNanoseconds: UInt64 = 2_000_000_000
}
