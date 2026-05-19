import SwiftUI

public enum OBRitFloatingActionButtonIcon {
    case plus

    var symbolName: String {
        switch self {
        case .plus:
            return "plus"
        }
    }
}

public struct OBRitFloatingActionButton: View {
    private let icon: OBRitFloatingActionButtonIcon
    private let accessibilityLabel: String
    private let action: () -> Void

    public init(
        icon: OBRitFloatingActionButtonIcon = .plus,
        accessibilityLabel: String,
        action: @escaping () -> Void
    ) {
        self.icon = icon
        self.accessibilityLabel = accessibilityLabel
        self.action = action
    }

    public var body: some View {
        Button(action: action) {
            Image(systemName: icon.symbolName)
                .font(.system(size: OBRitSpacing.s5, weight: .bold))
                .foregroundStyle(OBRitColors.gray900)
                .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
                .padding(OBRitSpacing.s4)
                .background(OBRitColors.common00)
                .clipShape(Circle())
                .shadow(color: Color.black.opacity(0.24), radius: OBRitSpacing.s6, x: 0, y: OBRitSpacing.s4)
        }
        .buttonStyle(.plain)
        .accessibilityLabel(accessibilityLabel)
    }
}
