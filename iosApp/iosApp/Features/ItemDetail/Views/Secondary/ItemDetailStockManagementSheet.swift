import SwiftUI

struct ItemDetailStockManagementSheet: View {
    let itemName: String
    let initialQuantity: Int
    @Binding var quantity: Int
    @State private var quantityInput = ""
    @State private var isQuantityEditing = false
    @FocusState private var isQuantityFocused: Bool

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
            bottomPadding: OBRitSpacing.s5,
            onDismiss: onDismiss
        ) {
            VStack(spacing: ItemDetailStockSheetMetrics.contentSpacing) {
                VStack(spacing: ItemDetailStockSheetMetrics.controlSpacing) {
                    title

                    quantityStepper
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
                    finishQuantityEditing()
                    onCommit(quantity)
                }
            }
        }
        .onAppear {
            beginInitialQuantityEditing()
        }
        .onChange(of: quantity) { _, newValue in
            guard !isQuantityEditing else { return }
            quantityInput = "\(newValue)"
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

    private var quantityStepper: some View {
        HStack(spacing: OBRitSpacing.s8) {
            stepperButton(symbol: .minus, disabled: quantity <= ItemDetailConfig.minimumSpareQuantity, action: decrement)
            quantityValue
            stepperButton(symbol: .plus, disabled: quantity >= ItemDetailConfig.maximumSpareQuantity, action: increment)
        }
        .frame(width: ItemDetailStockSheetMetrics.stepperWidth)
    }

    @ViewBuilder
    private var quantityValue: some View {
        if isQuantityEditing {
            TextField("", text: quantityText)
                .keyboardType(.numberPad)
                .multilineTextAlignment(.center)
                .focused($isQuantityFocused)
                .lineLimit(1)
                .minimumScaleFactor(0.7)
                .obritTextStyle(OBRitTypography.s7xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                .frame(width: ItemDetailStockSheetMetrics.valueWidth, height: OBRitSpacing.s11)
                .onAppear {
                    focusQuantityField()
                }
                .onChange(of: isQuantityFocused) { _, isFocused in
                    if !isFocused {
                        finishQuantityEditing()
                    }
                }
        } else {
            Button {
                beginQuantityEditing()
            } label: {
                Text("\(quantity)")
                    .lineLimit(1)
                    .minimumScaleFactor(0.7)
                    .obritTextStyle(OBRitTypography.s7xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                    .frame(minWidth: ItemDetailStockSheetMetrics.valueWidth)
                    .contentShape(Rectangle())
            }
            .buttonStyle(.plain)
            .accessibilityLabel("여분 수량 직접 입력")
        }
    }

    private var quantityText: Binding<String> {
        Binding {
            quantityInput
        } set: { newValue in
            updateQuantityInput(newValue)
        }
    }

    private var canCommit: Bool {
        quantity != initialQuantity &&
            quantity >= ItemDetailConfig.minimumSpareQuantity &&
            quantity <= ItemDetailConfig.maximumSpareQuantity &&
            !isProcessing
    }

    private func decrement() {
        finishQuantityEditing()
        quantity = max(ItemDetailConfig.minimumSpareQuantity, quantity - 1)
    }

    private func increment() {
        finishQuantityEditing()
        quantity = min(ItemDetailConfig.maximumSpareQuantity, quantity + 1)
    }

    private func beginQuantityEditing() {
        quantityInput = "\(quantity)"
        isQuantityEditing = true
    }

    private func beginInitialQuantityEditing() {
        quantity = initialQuantity
        quantityInput = "\(initialQuantity)"
        isQuantityEditing = true
    }

    private func focusQuantityField() {
        Task { @MainActor in
            await Task.yield()
            isQuantityFocused = true
        }
    }

    private func finishQuantityEditing() {
        guard isQuantityEditing else { return }
        if quantityInput.isEmpty {
            quantityInput = "\(quantity)"
        }
        isQuantityEditing = false
        isQuantityFocused = false
    }

    private func updateQuantityInput(_ newValue: String) {
        let digits = newValue.filter(\.isNumber)
        let maximumDigitCount = "\(ItemDetailConfig.maximumSpareQuantity)".count
        let clippedDigits = String(digits.prefix(maximumDigitCount + 1))

        guard let value = Int(clippedDigits) else {
            quantityInput = ""
            return
        }

        let clampedValue = min(
            max(value, ItemDetailConfig.minimumSpareQuantity),
            ItemDetailConfig.maximumSpareQuantity
        )
        quantityInput = "\(clampedValue)"
        quantity = clampedValue
    }

    private func stepperButton(
        symbol: ItemDetailStockStepperSymbol,
        disabled: Bool,
        action: @escaping () -> Void
    ) -> some View {
        Button {
            guard !disabled else { return }
            action()
        } label: {
            symbol
                .stroke(disabled ? OBRitColors.gray600 : OBRitColors.common00, style: StrokeStyle(lineWidth: 2, lineCap: .round))
                .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
                .frame(
                    width: ItemDetailStockSheetMetrics.stepperButtonSize,
                    height: ItemDetailStockSheetMetrics.stepperButtonSize
                )
                .background(OBRitColors.gray800)
                .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.large))
                .contentShape(RoundedRectangle(cornerRadius: OBRitRadius.large))
        }
        .buttonStyle(.plain)
        .disabled(disabled)
        .accessibilityLabel(symbol.accessibilityLabel)
    }
}

private enum ItemDetailStockSheetMetrics {
    static let contentHeight: CGFloat = 178
    static let contentSpacing: CGFloat = 20
    static let controlSpacing: CGFloat = 20
    static let stepperWidth: CGFloat = 197
    static let stepperButtonSize: CGFloat = 56
    static let valueWidth: CGFloat = 44
}

private enum ItemDetailStockStepperSymbol: Shape, Equatable {
    case minus
    case plus

    var accessibilityLabel: Text {
        switch self {
        case .minus:
            return Text("decrease")
        case .plus:
            return Text("increase")
        }
    }

    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.move(to: CGPoint(x: rect.minX + rect.width * 0.2, y: rect.midY))
        path.addLine(to: CGPoint(x: rect.maxX - rect.width * 0.2, y: rect.midY))
        if self == .plus {
            path.move(to: CGPoint(x: rect.midX, y: rect.minY + rect.height * 0.2))
            path.addLine(to: CGPoint(x: rect.midX, y: rect.maxY - rect.height * 0.2))
        }
        return path
    }
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
