import SwiftUI
import Shared

public enum OBRitSnackbarIcon {
    case none
    case question
    case warning
    case check

    public static let `default`: OBRitSnackbarIcon = .question
    public static let error: OBRitSnackbarIcon = .warning
    public static let success: OBRitSnackbarIcon = .check
}

public struct OBRitSnackbar: View {
    private let message: String
    private let icon: OBRitSnackbarIcon

    public init(
        message: String,
        icon: OBRitSnackbarIcon = .none
    ) {
        self.message = message
        self.icon = icon
    }

    public var body: some View {
        HStack(alignment: .top, spacing: contentSpacing) {
            if hasIcon {
                OBRitIcon(kind: iconKind, color: iconColor)
                    .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
            }

            Text(message)
                .fixedSize(horizontal: false, vertical: true)
                .multilineTextAlignment(hasIcon ? .leading : .center)
                .frame(width: hasIcon ? nil : OBRitSnackbarMetrics.normalTextWidth)
                .obritTextStyle(OBRitTypography.base, weight: AtomFontWeight.shared.Medium, color: OBRitColors.common00)
        }
        .padding(.leading, hasIcon ? OBRitSpacing.s3 : OBRitSpacing.s4)
        .padding(.trailing, hasIcon ? OBRitSpacing.s5 : OBRitSpacing.s4)
        .padding(.vertical, hasIcon ? OBRitSpacing.s3 : OBRitSpacing.s2_5)
        .background(OBRitColors.gray800)
        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.large))
        .overlay(
            RoundedRectangle(cornerRadius: OBRitRadius.large)
                .stroke(OBRitColors.gray750, lineWidth: 1.4)
        )
    }

    private var hasIcon: Bool {
        icon != .none
    }

    private var contentSpacing: CGFloat {
        hasIcon ? OBRitSpacing.s3 : 0
    }

    private var iconKind: OBRitIconKind {
        switch icon {
        case .none:
            return .question
        case .question:
            return .question
        case .warning:
            return .exclamation
        case .check:
            return .success
        }
    }

    private var iconColor: Color {
        switch icon {
        case .none:
            return OBRitColors.gray300
        case .question:
            return OBRitColors.gray300
        case .warning:
            return OBRitColors.red300
        case .check:
            return OBRitColors.green300
        }
    }
}

private enum OBRitSnackbarMetrics {
    static let normalTextWidth: CGFloat = 173
}
