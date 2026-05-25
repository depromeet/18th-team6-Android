import SwiftUI

struct ItemRegistrationView: View {
    @StateObject private var viewModel: ItemRegistrationViewModel

    let onBack: () -> Void
    let onClose: () -> Void
    let onComplete: () -> Void

    init(
        onBack: @escaping () -> Void,
        onClose: @escaping () -> Void,
        onComplete: @escaping () -> Void
    ) {
        _viewModel = StateObject(wrappedValue: ItemRegistrationViewModel())
        self.onBack = onBack
        self.onClose = onClose
        self.onComplete = onComplete
    }

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
        switch viewModel.state {
        case let .success(data):
            contentView(
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
                    onShowDirectKindRegistration: viewModel.showDirectKindRegistration,
                    onUpdateDirectKindName: viewModel.updateDirectKindName,
                    onSelectImageOption: viewModel.selectImageOption,
                    onSubmitDirectKind: viewModel.submitDirectKind,
                    onSubmitForm: viewModel.submitForm,
                    onComplete: onComplete
                )
            )
        }
    }

    private func contentView(
        data: ItemRegistrationViewData,
        action: ItemRegistrationAction
    ) -> some View {
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
                            data: data,
                            action: action,
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
        data: ItemRegistrationViewData,
        action: ItemRegistrationAction,
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
            - ItemRegistrationLayout.topBarHeight
            - ItemRegistrationLayout.bottomSheetTopPadding
            - ItemRegistrationLayout.bottomSheetHeaderHeight
            - bottomPadding
        return min(
            ItemRegistrationLayout.kindSheetMaxContentHeight,
            max(ItemRegistrationLayout.minimumVisibleHeight, availableHeight)
        )
    }

    private func bottomSheetBottomPadding(in geometry: GeometryProxy) -> CGFloat {
        max(
            OBRitSpacing.s5,
            min(geometry.safeAreaInsets.bottom, ItemRegistrationLayout.bottomSheetMaximumSafeAreaPadding)
        )
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
    let onShowDirectKindRegistration: () -> Void
    let onUpdateDirectKindName: (String) -> Void
    let onSelectImageOption: (ItemImageOption) -> Void
    let onSubmitDirectKind: () -> Void
    let onSubmitForm: () -> Void
    let onComplete: () -> Void
}

struct ItemRegistrationView_Previews: PreviewProvider {
    static var previews: some View {
        ItemRegistrationView(
            onBack: {},
            onClose: {},
            onComplete: {}
        )
    }
}
