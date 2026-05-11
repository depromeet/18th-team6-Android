import SwiftUI
import Shared

// 배지 타입이 Figma 레이어 이름을 그대로 가져와서 직관적이지 못함. 보다 의미론적으로 추가될 필요 있을듯함
public enum OBRitBadgeType {
    case warningFilled
    case gray750Filled
    case warningWhiteBackgroundFilled
    case red250Filled
    case red800Filled
}

public struct OBRitBadge: View {
    private let text: String
    private let type: OBRitBadgeType

    public init(
        text: String,
        type: OBRitBadgeType = .warningFilled
    ) {
        self.text = text
        self.type = type
    }

    public var body: some View {
        Text(text)
            .lineLimit(1)
            .obritTextStyle(OBRitTypography.xs, weight: AtomFontWeight.shared.Bold, color: contentColor)
            .padding(.horizontal, OBRitSpacing.s2)
            .padding(.vertical, OBRitSpacing.s1)
            .background(containerColor)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.small))
    }

    private var containerColor: Color {
        switch type {
        case .warningFilled:
            return OBRitColors.red300
        case .gray750Filled:
            return OBRitColors.gray750
        case .warningWhiteBackgroundFilled:
            return OBRitColors.common00
        case .red250Filled:
            return OBRitColors.red250
        case .red800Filled:
            return OBRitColors.red800
        }
    }

    // Semantic 색상과 Atom 색상의 혼용은 의도된 부분?
    private var contentColor: Color {
        switch type {
        case .warningFilled, .gray750Filled, .red250Filled:
            return OBRitColors.common00
        case .warningWhiteBackgroundFilled, .red800Filled:
            return OBRitColors.red300
        }
    }
}
