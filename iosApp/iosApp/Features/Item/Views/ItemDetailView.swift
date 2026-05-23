import SwiftUI

struct ItemDetailView: View {
    let itemId: Int
    let onNavigate: (ItemRoute) -> Void

    @Environment(\.dismiss) private var dismiss
    @StateObject private var viewModel: ItemDetailViewModel
    @State private var stockDraftQuantity = 0
    @State private var isStockSheetPresented = false
    @State private var completionModal: ItemDetailCompletionModalData?

    init(
        itemId: Int,
        onNavigate: @escaping (ItemRoute) -> Void
    ) {
        self.itemId = itemId
        self.onNavigate = onNavigate
        _viewModel = StateObject(wrappedValue: ItemDetailViewModel(consumableId: itemId))
    }

    var body: some View {
        ZStack {
            content
            overlay
        }
        .onChange(of: viewModel.effect) { _, effect in
            handleEffect(effect)
        }
    }

    @ViewBuilder
    private var content: some View {
        switch viewModel.state {
        case .loading:
            ItemDetailMessageView(title: "소모품 정보를 불러오는 중이에요")
        case let .loadFailed(message):
            ItemDetailMessageView(title: message, buttonTitle: "다시 시도", action: viewModel.retry)
        case let .success(data):
            ItemDetailScreenView(
                data: data,
                action: ItemDetailViewAction(
                    onBack: { dismiss() },
                    onMore: viewModel.openMoreMenu,
                    onManageStock: {
                        stockDraftQuantity = data.spareDraft.quantity
                        isStockSheetPresented = true
                    },
                    onCompleteReplacement: viewModel.requestReplacementCompletion
                )
            )
        }
    }

    @ViewBuilder
    private var overlay: some View {
        if case let .success(data) = viewModel.state {
            if data.isMoreMenuPresented {
                Color.clear
                    .contentShape(Rectangle())
                    .ignoresSafeArea()
                    .onTapGesture(perform: viewModel.dismissMoreMenu)

                VStack {
                    HStack {
                        Spacer()
                        ItemDetailMoreMenu(items: data.moreMenuItems, onSelect: viewModel.selectMoreMenuItem)
                            .padding(.top, OBRitSpacing.s14)
                            .padding(.trailing, OBRitSpacing.s3)
                    }
                    Spacer()
                }
            }

            if isStockSheetPresented {
                Color.black.opacity(ItemDetailOverlayMetrics.dimOpacity)
                    .ignoresSafeArea()
                    .onTapGesture {
                        isStockSheetPresented = false
                    }

                VStack {
                    Spacer(minLength: 0)
                    ItemDetailStockManagementSheet(
                        itemName: data.consumable.name,
                        initialQuantity: data.spareDraft.quantity,
                        quantity: $stockDraftQuantity,
                        isProcessing: data.isProcessing,
                        onCommit: { quantity in
                            isStockSheetPresented = false
                            viewModel.updateSpareQuantity(quantity)
                        },
                        onDismiss: {
                            isStockSheetPresented = false
                        }
                    )
                }
                .ignoresSafeArea(.container, edges: .bottom)
                .transition(.move(edge: .bottom).combined(with: .opacity))
            }

            if let confirmationDialog = data.confirmationDialog {
                modalDim

                confirmationModal(for: confirmationDialog, data: data)
                    .transition(.scale.combined(with: .opacity))
            }

            if let completionModal {
                modalDim

                completionModalView(completionModal)
                    .transition(.scale.combined(with: .opacity))
            }
        }
    }

    private var modalDim: some View {
        Color.black.opacity(ItemDetailOverlayMetrics.dimOpacity)
            .ignoresSafeArea()
            .onTapGesture {
                viewModel.dismissConfirmationDialog()
                completionModal = nil
            }
    }

    @ViewBuilder
    private func confirmationModal(
        for dialog: ItemDetailConfirmationDialog,
        data: ItemDetailViewData
    ) -> some View {
        switch dialog {
        case .delete:
            ItemDetailConfirmationModal(
                kind: .delete,
                isProcessing: data.isProcessing,
                onCancel: viewModel.dismissConfirmationDialog,
                onConfirm: viewModel.confirmCurrentDialog
            )
        case .replacementComplete:
            OBRitModal(
                title: "\(data.consumable.name) 교체를 완료할까요?",
                description: "오늘 날짜로 교체 기록이 추가되고 여분 수량이 1개 줄어들어요.",
                buttonCount: .two,
                showsImage: false,
                primaryTitle: data.isProcessing ? "처리 중" : "완료",
                secondaryTitle: "취소",
                onPrimaryClick: viewModel.confirmCurrentDialog,
                onSecondaryClick: viewModel.dismissConfirmationDialog
            )
        }
    }

    @ViewBuilder
    private func completionModalView(
        _ modal: ItemDetailCompletionModalData
    ) -> some View {
        if modal.isLowStock {
            ItemDetailReplacementCompletionModal(
                itemName: modal.itemName,
                itemImageAssetName: modal.imageAssetName,
                remainingSpareQuantity: modal.remainingSpareQuantity,
                recordedAtText: modal.recordedAtText,
                onConfirm: { completionModal = nil },
                onCancel: { completionModal = nil }
            )
        } else {
            ItemDetailReplacementCompletionModal(
                itemName: modal.itemName,
                itemImageAssetName: modal.imageAssetName,
                daysComparedToPrevious: modal.daysComparedToPrevious,
                nextReplacementLabel: modal.nextReplacementLabel,
                recordedAtText: modal.recordedAtText,
                onConfirm: { completionModal = nil },
                onCancel: { completionModal = nil }
            )
        }
    }

    private func handleEffect(_ effect: ItemDetailViewEffect?) {
        guard let effect else { return }
        defer { viewModel.clearEffect() }

        switch effect {
        case let .navigate(destination):
            handleNavigation(destination)
        case .itemDeleted:
            dismiss()
        case .replacementCompleted:
            if case let .success(data) = viewModel.state {
                completionModal = ItemDetailCompletionModalData(data: data)
            }
        case .showMessage:
            break
        }
    }

    private func handleNavigation(_ destination: ItemDetailDestination) {
        switch destination {
        case let .statusInfo(consumableId):
            onNavigate(.statusInfo(itemId: consumableId))
        case let .edit(consumableId):
            onNavigate(.edit(itemId: consumableId))
        case let .spareEdit(consumableId):
            onNavigate(.spareEdit(itemId: consumableId))
        case .notification:
            break
        }
    }
}

private struct ItemDetailMessageView: View {
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
        .navigationBarBackButtonHidden(true)
        .toolbar(.hidden, for: .navigationBar)
    }
}

private enum ItemDetailOverlayMetrics {
    static let dimOpacity: CGFloat = 0.62
}
