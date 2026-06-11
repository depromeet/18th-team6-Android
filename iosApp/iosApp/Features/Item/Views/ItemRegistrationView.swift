import SwiftUI

struct ItemRegistrationView: View {
    @StateObject private var viewModel: ItemRegistrationViewModel
    @State private var snackbar: ItemRegistrationSnackbarPresentation?
    @State private var snackbarDismissTask: Task<Void, Never>?

    let onBack: () -> Void
    let onClose: () -> Void
    let onComplete: () -> Void

    @MainActor
    init(
        viewModelFactory: @MainActor @escaping () -> ItemRegistrationViewModel,
        onBack: @escaping () -> Void,
        onClose: @escaping () -> Void,
        onComplete: @escaping () -> Void
    ) {
        _viewModel = StateObject(wrappedValue: viewModelFactory())
        self.onBack = onBack
        self.onClose = onClose
        self.onComplete = onComplete
    }

    @MainActor
    init(
        onBack: @escaping () -> Void,
        onClose: @escaping () -> Void,
        onComplete: @escaping () -> Void
    ) {
        self.init(
            viewModelFactory: AppDependencies.preview.makeItemRegistrationViewModel,
            onBack: onBack,
            onClose: onClose,
            onComplete: onComplete
        )
    }

    @MainActor
    init(
        viewModel: ItemRegistrationViewModel,
        onBack: @escaping () -> Void,
        onClose: @escaping () -> Void,
        onComplete: @escaping () -> Void
    ) {
        _viewModel = StateObject(wrappedValue: viewModel)
        self.onBack = onBack
        self.onClose = onClose
        self.onComplete = onComplete
    }

    var body: some View {
        ZStack {
            switch viewModel.state {
            case .loading:
                ItemRegistrationMessageView(title: "소모품 등록 정보를 불러오는 중이에요")
            case let .loadFailed(message):
                ItemRegistrationMessageView(title: message, buttonTitle: "다시 시도", action: viewModel.retry)
            case let .success(data):
                ItemRegistrationContentView(
                    data: data,
                    action: ItemRegistrationAction(
                        onBack: {
                            if data.mode == .directKind {
                                viewModel.resetToForm()
                            } else {
                                onBack()
                            }
                        },
                        onClose: onClose,
                        onUpdateItemName: viewModel.updateItemName,
                        onOpenKindSheet: viewModel.openKindSheet,
                        onDismissBottomSheet: viewModel.dismissBottomSheet,
                        onUpdateKindSearchQuery: viewModel.updateKindSearchQuery,
                        onSelectKind: viewModel.selectKind,
                        onSelectKindCandidate: viewModel.selectKindCandidate,
                        onConfirmKindSelection: viewModel.confirmKindSelection,
                        onSelectReplacementDate: viewModel.selectReplacementDate,
                        onIncrementQuantity: viewModel.incrementQuantity,
                        onDecrementQuantity: viewModel.decrementQuantity,
                        onUpdateQuantity: viewModel.updateQuantity,
                        onShowDirectKindRegistration: viewModel.showDirectKindRegistration,
                        onUpdateDirectKindName: viewModel.updateDirectKindName,
                        onSelectImageOption: viewModel.selectImageOption,
                        onSubmitDirectKind: viewModel.submitDirectKind,
                        onSubmitForm: viewModel.submitForm,
                        onComplete: onComplete
                    )
                )
            }

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

    private func handleEffect(_ effect: ItemRegistrationViewEffect?) {
        guard let effect else { return }
        defer { viewModel.clearEffect() }

        switch effect {
        case let .showMessage(message):
            showSnackbar(message)
        }
    }

    private func showSnackbar(_ message: String) {
        snackbarDismissTask?.cancel()
        snackbar = ItemRegistrationSnackbarPresentation(
            message: message,
            icon: message.hasSuffix("했어요.") ? .success : .error
        )
        snackbarDismissTask = Task { @MainActor in
            try? await Task.sleep(nanoseconds: ItemRegistrationSnackbarMetrics.displayDurationNanoseconds)
            guard !Task.isCancelled else { return }
            snackbar = nil
        }
    }
}

private struct ItemRegistrationSnackbarPresentation: Identifiable {
    let id = UUID()
    let message: String
    let icon: OBRitSnackbarIcon
}

private enum ItemRegistrationSnackbarMetrics {
    static let displayDurationNanoseconds: UInt64 = 2_400_000_000
}

private struct ItemRegistrationMessageView: View {
    let title: String
    var buttonTitle: String?
    var action: (() -> Void)?

    var body: some View {
        VStack(spacing: OBRitSpacing.s5) {
            Text(title)
                .multilineTextAlignment(.center)
                .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.semiBold, color: OBRitColors.textDefaultDefault)

            if let buttonTitle, let action {
                OBRitFilledTextButton(text: buttonTitle, size: .middle, action: action)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding(OBRitSpacing.s5)
        .background(OBRitColors.backgroundDefaultDefault.ignoresSafeArea())
    }
}

struct ItemRegistrationAction {
    let onBack: () -> Void
    let onClose: () -> Void
    let onUpdateItemName: (String) -> Void
    let onOpenKindSheet: () -> Void
    let onDismissBottomSheet: () -> Void
    let onUpdateKindSearchQuery: (String) -> Void
    let onSelectKind: (ItemKind) -> Void
    let onSelectKindCandidate: (ItemKind) -> Void
    let onConfirmKindSelection: () -> Void
    let onSelectReplacementDate: (ItemReplacementDateOption) -> Void
    let onIncrementQuantity: () -> Void
    let onDecrementQuantity: () -> Void
    let onUpdateQuantity: (Int) -> Void
    let onShowDirectKindRegistration: () -> Void
    let onUpdateDirectKindName: (String) -> Void
    let onSelectImageOption: (ItemImageOption) -> Void
    let onSubmitDirectKind: () -> Void
    let onSubmitForm: () -> Void
    let onComplete: () -> Void
}

private struct ItemRegistrationContentView: View {
    let data: ItemRegistrationViewData
    let action: ItemRegistrationAction

    var body: some View {
        GeometryReader { geometry in
            ZStack {
                OBRitColors.backgroundDefaultDefault
                    .ignoresSafeArea()

                Group {
                    switch data.mode {
                    case .form:
                        ItemRegistrationFormView(data: data, action: action)
                    case .directKind:
                        ItemDirectKindRegistrationView(data: data, action: action)
                    case .complete:
                        ItemRegistrationCompleteView(action: action)
                    }
                }
                .ignoresSafeArea(.keyboard)

                if let bottomSheet = data.bottomSheet {
                    let bottomPadding = bottomSheetBottomPadding(in: geometry)

                    Color.black.opacity(ItemRegistrationLayout.bottomSheetDimOpacity)
                        .ignoresSafeArea()
                        .onTapGesture(perform: action.onDismissBottomSheet)
                        .transition(.opacity)

                    VStack {
                        Spacer(minLength: ItemRegistrationLayout.spacerMinimumLength)
                        bottomSheetView(
                            bottomSheet,
                            contentHeight: bottomSheetContentHeight(
                                in: geometry,
                                bottomPadding: bottomPadding
                            ),
                            bottomPadding: bottomPadding
                        )
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                    }
                    .ignoresSafeArea(.container, edges: .bottom)
                }
            }
            .frame(width: geometry.size.width, height: geometry.size.height)
            .animation(.easeOut(duration: ItemRegistrationLayout.bottomSheetAnimationDuration), value: data.bottomSheet)
        }
    }

    @ViewBuilder
    private func bottomSheetView(
        _ bottomSheet: ItemRegistrationBottomSheet,
        contentHeight: CGFloat,
        bottomPadding: CGFloat
    ) -> some View {
        switch bottomSheet {
        case .kind:
            ItemKindSelectionBottomSheet(
                data: data,
                action: action,
                contentHeight: contentHeight,
                bottomPadding: bottomPadding
            )
        }
    }

    private func bottomSheetContentHeight(
        in geometry: GeometryProxy,
        bottomPadding: CGFloat
    ) -> CGFloat {
        let availableHeight = geometry.size.height
            - geometry.safeAreaInsets.top
            - ItemRegistrationLayout.bottomSheetTopPadding
            - ItemRegistrationLayout.bottomSheetHeaderHeight
            - bottomPadding
        return min(ItemRegistrationLayout.kindSheetMaxContentHeight, max(0, availableHeight))
    }

    private func bottomSheetBottomPadding(in geometry: GeometryProxy) -> CGFloat {
        max(
            OBRitSpacing.s5,
            min(geometry.safeAreaInsets.bottom, ItemRegistrationLayout.bottomSheetMaximumSafeAreaPadding)
        )
    }
}

#Preview("Item Registration") {
    ItemRegistrationView(
        onBack: {},
        onClose: {},
        onComplete: {}
    )
}
