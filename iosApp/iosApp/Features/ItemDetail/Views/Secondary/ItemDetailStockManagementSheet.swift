import SwiftUI

struct ItemDetailStockManagementSheet: View {
    let itemName: String
    let initialQuantity: Int
    @Binding var quantity: Int
    let isProcessing: Bool
    let onCommit: (Int) -> Void
    let onDismiss: () -> Void

    init(
        itemName: String,
        initialQuantity: Int,
        quantity: Binding<Int>,
        isProcessing: Bool = false,
        onCommit: @escaping (Int) -> Void,
        onDismiss: @escaping () -> Void
    ) {
        self.itemName = itemName
        self.initialQuantity = initialQuantity
        self._quantity = quantity
        self.isProcessing = isProcessing
        self.onCommit = onCommit
        self.onDismiss = onDismiss
    }

    var body: some View {
        OBRitBottomSheet(
            contentHeight: ItemDetailStockSheetMetrics.contentHeight,
            bottomPadding: OBRitSpacing.s5
        ) {
            VStack(spacing: ItemDetailStockSheetMetrics.contentSpacing) {
                VStack(spacing: ItemDetailStockSheetMetrics.controlSpacing) {
                    title

                    OBRitStepper(
                        value: quantity,
                        size: .large,
                        minimumValue: ItemDetailConfig.minimumSpareQuantity,
                        onDecrement: decrement,
                        onIncrement: increment
                    )
                    .accessibilityValue("\(quantity)개")
                    .accessibilityAdjustableAction { direction in
                        switch direction {
                        case .increment:
                            increment()
                        case .decrement:
                            decrement()
                        @unknown default:
                            break
                        }
                    }
                }

                OBRitFilledTextButton(
                    text: isProcessing ? "수정 중" : "수정 완료",
                    size: .large,
                    color: canCommit ? .green : .gray,
                    enabled: canCommit,
                    fillsWidth: true
                ) {
                    onCommit(quantity)
                }
            }
        }
    }

    private var title: some View {
        HStack(spacing: OBRitSpacing.s0) {
            Text(itemName)
                .foregroundStyle(OBRitColors.textPositiveDefault)
            Text(" 여분 갯수를 조정해주세요")
                .foregroundStyle(OBRitColors.textDefaultDefault)
        }
        .lineLimit(1)
        .minimumScaleFactor(0.8)
        .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.semiBold, color: OBRitColors.textDefaultDefault)
        .frame(maxWidth: .infinity)
    }

    private var canCommit: Bool {
        quantity != initialQuantity &&
            quantity >= ItemDetailConfig.minimumSpareQuantity &&
            quantity <= ItemDetailConfig.maximumSpareQuantity &&
            !isProcessing
    }

    private func decrement() {
        quantity = max(ItemDetailConfig.minimumSpareQuantity, quantity - 1)
    }

    private func increment() {
        quantity = min(ItemDetailConfig.maximumSpareQuantity, quantity + 1)
    }
}

private enum ItemDetailStockSheetMetrics {
    static let contentHeight: CGFloat = 178
    static let contentSpacing: CGFloat = 20
    static let controlSpacing: CGFloat = 20
}

#Preview {
    @Previewable @State var quantity = 0

    VStack {
        Spacer()
        ItemDetailStockManagementSheet(
            itemName: "칫솔",
            initialQuantity: 0,
            quantity: $quantity,
            onCommit: { _ in },
            onDismiss: {}
        )
    }
    .background(OBRitColors.gray900)
}
