import SwiftUI

enum ItemDetailConfirmationModalKind: Equatable {
    case delete
    case editExit

    var title: String {
        switch self {
        case .delete:
            return "해당 소모품을 삭제하시겠습니까?"
        case .editExit:
            return "편집하기를 종료하시겠습니까?"
        }
    }

    var message: String? {
        switch self {
        case .delete:
            return nil
        case .editExit:
            return "지금까지의 내용은 저장되지 않습니다."
        }
    }

    var primaryTitle: String {
        switch self {
        case .delete:
            return "삭제"
        case .editExit:
            return "종료하기"
        }
    }
}

struct ItemDetailConfirmationModal: View {
    let kind: ItemDetailConfirmationModalKind
    let isProcessing: Bool
    let onCancel: () -> Void
    let onConfirm: () -> Void

    init(
        kind: ItemDetailConfirmationModalKind,
        isProcessing: Bool = false,
        onCancel: @escaping () -> Void,
        onConfirm: @escaping () -> Void
    ) {
        self.kind = kind
        self.isProcessing = isProcessing
        self.onCancel = onCancel
        self.onConfirm = onConfirm
    }

    var body: some View {
        VStack(spacing: ItemDetailConfirmationModalMetrics.contentSpacing) {
            VStack(spacing: OBRitSpacing.s2) {
                Text(kind.title)
                    .lineLimit(2)
                    .multilineTextAlignment(.center)
                    .frame(maxWidth: .infinity)
                    .obritTextStyle(
                        OBRitTypography.s3xl,
                        weight: OBRitFontWeight.bold,
                        color: OBRitColors.common00
                    )

                if let message = kind.message {
                    Text(message)
                        .lineLimit(2)
                        .multilineTextAlignment(.center)
                        .frame(maxWidth: .infinity)
                        .obritTextStyle(
                            OBRitTypography.base,
                            weight: OBRitFontWeight.medium,
                            color: OBRitColors.textDefaultSecondary
                        )
                }
            }

            HStack(spacing: OBRitSpacing.s3) {
                confirmationButton(
                    title: "아니오",
                    backgroundColor: OBRitColors.gray750,
                    foregroundColor: OBRitColors.textDefaultSecondary,
                    fillsWidth: kind == .delete,
                    width: kind == .editExit ? ItemDetailConfirmationModalMetrics.secondaryButtonWidth : nil,
                    action: onCancel
                )

                confirmationButton(
                    title: isProcessing ? "처리 중" : kind.primaryTitle,
                    backgroundColor: OBRitColors.backgroundPositiveDefault,
                    foregroundColor: OBRitColors.common100,
                    fillsWidth: true,
                    action: onConfirm
                )
                .disabled(isProcessing)
            }
        }
        .padding(.horizontal, OBRitSpacing.s7)
        .padding(.vertical, OBRitSpacing.s6)
        .frame(width: ItemDetailConfirmationModalMetrics.width)
        .background(OBRitColors.surfaceDefaultDefaultDark)
        .clipShape(RoundedRectangle(cornerRadius: ItemDetailConfirmationModalMetrics.cornerRadius))
    }

    private func confirmationButton(
        title: String,
        backgroundColor: Color,
        foregroundColor: Color,
        fillsWidth: Bool,
        width: CGFloat? = nil,
        action: @escaping () -> Void
    ) -> some View {
        Button(action: action) {
            Text(title)
                .lineLimit(1)
                .minimumScaleFactor(0.85)
                .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.semiBold, color: foregroundColor)
                .frame(maxWidth: fillsWidth ? .infinity : nil)
                .frame(width: width)
                .frame(height: ItemDetailConfirmationModalMetrics.buttonHeight)
                .padding(.horizontal, OBRitSpacing.s5)
                .background(backgroundColor)
                .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
                .contentShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
        }
        .buttonStyle(.plain)
    }
}

private enum ItemDetailConfirmationModalMetrics {
    static let width: CGFloat = 370
    static let contentSpacing: CGFloat = 16
    static let buttonHeight: CGFloat = 46
    static let secondaryButtonWidth: CGFloat = 82
    static let cornerRadius: CGFloat = 16
}

#Preview("Delete") {
    ZStack {
        OBRitColors.backgroundDefaultDimDefault
        ItemDetailConfirmationModal(kind: .delete, onCancel: {}, onConfirm: {})
    }
}

#Preview("Exit Edit") {
    ZStack {
        OBRitColors.backgroundDefaultDimDefault
        ItemDetailConfirmationModal(kind: .editExit, onCancel: {}, onConfirm: {})
    }
}
