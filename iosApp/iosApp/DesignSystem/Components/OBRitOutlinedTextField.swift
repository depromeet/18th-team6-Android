import SwiftUI
import Shared

public enum OBRitInputResultState {
    case `default`
    case error
    case success
}

public struct OBRitOutlinedTextField<TrailingContent: View>: View {
    @Binding private var text: String
    @FocusState private var isFocused: Bool

    private let placeholder: String
    private let inputResultState: OBRitInputResultState
    private let containerColor: Color
    private let maxLength: Int?
    private let supportingText: String
    private let enabled: Bool
    private let readOnly: Bool
    private let singleLine: Bool
    private let forceFocused: Bool
    private let trailingContent: () -> TrailingContent

    public init(
        text: Binding<String>,
        placeholder: String = "",
        inputResultState: OBRitInputResultState = .default,
        containerColor: Color = OBRitColors.gray800,
        maxLength: Int? = nil,
        supportingText: String = "",
        enabled: Bool = true,
        readOnly: Bool = false,
        singleLine: Bool = false,
        forceFocused: Bool = false,
        @ViewBuilder trailingContent: @escaping () -> TrailingContent
    ) {
        self._text = text
        self.placeholder = placeholder
        self.inputResultState = inputResultState
        self.containerColor = containerColor
        self.maxLength = maxLength
        self.supportingText = supportingText
        self.enabled = enabled
        self.readOnly = readOnly
        self.singleLine = singleLine
        self.forceFocused = forceFocused
        self.trailingContent = trailingContent
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s2) {
            HStack(spacing: OBRitSpacing.s2) {
                ZStack(alignment: .leading) {
                    if text.isEmpty && !placeholder.isEmpty {
                        Text(placeholder)
                            .lineLimit(singleLine ? 1 : nil)
                            .obritTextStyle(OBRitTypography.xl, weight: AtomFontWeight.shared.Medium, color: OBRitColors.gray700)
                    }

                    TextField("", text: $text, axis: singleLine ? .horizontal : .vertical)
                        .lineLimit(singleLine ? 1 : nil)
                        .focused($isFocused)
                        .disabled(!enabled)
                        .allowsHitTesting(enabled && !readOnly)
                        .tint(OBRitColors.common00)
                        .obritTextStyle(OBRitTypography.xl, weight: AtomFontWeight.shared.Medium, color: contentColor)
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                if let maxLength {
                    Text("\(min(text.count, maxLength))/\(maxLength)")
                        .lineLimit(1)
                        .obritTextStyle(OBRitTypography.small, weight: AtomFontWeight.shared.Medium, color: counterColor)
                }

                trailingContent()
            }
            .frame(minHeight: OBRitSpacing.s14)
            .padding(.horizontal, OBRitSpacing.s5)
            .padding(.vertical, OBRitSpacing.s4)
            .background(containerColor)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
            .overlay(
                RoundedRectangle(cornerRadius: OBRitRadius.middle)
                    .stroke(borderColor, lineWidth: 1.4)
            )

            if inputResultState != .default && !supportingText.isEmpty {
                HStack(spacing: OBRitSpacing.s1_5) {
                    OBRitIcon(kind: statusIconKind, color: statusColor)
                        .frame(width: OBRitSpacing.s4, height: OBRitSpacing.s4)
                    Text(supportingText)
                        .obritTextStyle(OBRitTypography.base, weight: AtomFontWeight.shared.SemiBold, color: statusColor)
                }
            }
        }
    }

    private var contentColor: Color {
        enabled ? OBRitColors.common00 : OBRitColors.gray700
    }

    private var counterColor: Color {
        if !enabled {
            return OBRitColors.gray600
        }
        switch inputResultState {
        case .default:
            return OBRitColors.common00
        case .error:
            return OBRitColors.red300
        case .success:
            return OBRitColors.green300
        }
    }

    private var borderColor: Color {
        if !enabled {
            return OBRitColors.gray600
        }
        switch inputResultState {
        case .error:
            return OBRitColors.red300
        case .success:
            return OBRitColors.green300
        case .default:
            return isFocused || forceFocused ? OBRitColors.common00 : OBRitColors.gray300
        }
    }

    private var statusColor: Color {
        switch inputResultState {
        case .default:
            return contentColor
        case .error:
            return OBRitColors.red300
        case .success:
            return OBRitColors.green300
        }
    }

    private var statusIconKind: OBRitIconKind {
        inputResultState == .success ? .success : .exclamation
    }
}

public extension OBRitOutlinedTextField where TrailingContent == EmptyView {
    init(
        text: Binding<String>,
        placeholder: String = "",
        inputResultState: OBRitInputResultState = .default,
        containerColor: Color = OBRitColors.gray800,
        maxLength: Int? = nil,
        supportingText: String = "",
        enabled: Bool = true,
        readOnly: Bool = false,
        singleLine: Bool = false,
        forceFocused: Bool = false
    ) {
        self.init(
            text: text,
            placeholder: placeholder,
            inputResultState: inputResultState,
            containerColor: containerColor,
            maxLength: maxLength,
            supportingText: supportingText,
            enabled: enabled,
            readOnly: readOnly,
            singleLine: singleLine,
            forceFocused: forceFocused
        ) {
            EmptyView()
        }
    }
}
