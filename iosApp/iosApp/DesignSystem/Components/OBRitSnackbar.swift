import SwiftUI
import Shared

public enum OBRitSnackbarIcon {
    case none
    case `default`
    case error
    case success
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
        HStack(alignment: .top, spacing: 14) {
            if icon != .none {
                OBRitIcon(kind: iconKind, color: iconColor)
                    .frame(width: OBRitSpacing.s5, height: OBRitSpacing.s5)
            }

            Text(message)
                .fixedSize(horizontal: false, vertical: true)
                .obritTextStyle(OBRitTypography.xl, weight: AtomFontWeight.shared.Medium, color: OBRitColors.common00)
        }
        .padding(.leading, icon == .none ? OBRitSpacing.s5 : 14)
        .padding(.trailing, OBRitSpacing.s5)
        .padding(.vertical, icon == .none ? OBRitSpacing.s2 : OBRitSpacing.s2_5)
        .background(OBRitColors.gray800)
        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.large))
        .overlay(
            RoundedRectangle(cornerRadius: OBRitRadius.large)
                .stroke(OBRitColors.gray750, lineWidth: 1.4)
        )
    }

    private var iconKind: OBRitIconKind {
        switch icon {
        case .none:
            return .question
        case .default:
            return .question
        case .error:
            return .exclamation
        case .success:
            return .success
        }
    }

    private var iconColor: Color {
        switch icon {
        case .none:
            return OBRitColors.gray300
        case .default:
            return OBRitColors.gray300
        case .error:
            return OBRitColors.red300
        case .success:
            return OBRitColors.green300
        }
    }
}
