import SwiftUI
import Shared
import UIKit

public enum OBRitInputResultState {
    case `default`
    case error
    case success
}

public enum OBRitInputFieldStyle {
    case `default`
    case lined
}

public enum OBRitTextInputType {
    case text
    case name
    case email
    case password
    case number
    case decimal
    case phone
    case url
    case oneTimeCode
    case custom(
        keyboardType: UIKeyboardType,
        textContentType: UITextContentType? = nil,
        isSecure: Bool = false,
        autocapitalization: TextInputAutocapitalization? = nil,
        autocorrectionDisabled: Bool = false
    )

    var keyboardType: UIKeyboardType {
        switch self {
        case .text, .name, .password, .oneTimeCode:
            return .default
        case .email:
            return .emailAddress
        case .number:
            return .numberPad
        case .decimal:
            return .decimalPad
        case .phone:
            return .phonePad
        case .url:
            return .URL
        case let .custom(keyboardType, _, _, _, _):
            return keyboardType
        }
    }

    var textContentType: UITextContentType? {
        switch self {
        case .text, .number, .decimal:
            return nil
        case .name:
            return .name
        case .email:
            return .emailAddress
        case .password:
            return .password
        case .phone:
            return .telephoneNumber
        case .url:
            return .URL
        case .oneTimeCode:
            return .oneTimeCode
        case let .custom(_, textContentType, _, _, _):
            return textContentType
        }
    }

    var isSecure: Bool {
        switch self {
        case .password:
            return true
        case let .custom(_, _, isSecure, _, _):
            return isSecure
        default:
            return false
        }
    }

    var autocapitalization: TextInputAutocapitalization? {
        switch self {
        case .name:
            return .words
        case .email, .password, .url, .oneTimeCode:
            return .never
        case let .custom(_, _, _, autocapitalization, _):
            return autocapitalization
        default:
            return nil
        }
    }

    var autocorrectionDisabled: Bool {
        switch self {
        case .email, .password, .number, .decimal, .phone, .url, .oneTimeCode:
            return true
        case let .custom(_, _, _, _, autocorrectionDisabled):
            return autocorrectionDisabled
        default:
            return false
        }
    }
}

public struct OBRitOutlinedTextField<LeadingIcon: View, TrailingIcon: View>: View {
    @Binding private var text: String
    @FocusState private var isFocused: Bool

    private let placeholder: String
    private let inputResultState: OBRitInputResultState
    private let style: OBRitInputFieldStyle
    private let containerColor: Color
    private let maxLength: Int?
    private let supportingText: String
    private let enabled: Bool
    private let readOnly: Bool
    private let singleLine: Bool
    private let forceFocused: Bool
    private let inputType: OBRitTextInputType
    private let submitLabel: SubmitLabel
    private let onSubmit: () -> Void
    private let leadingIcon: () -> LeadingIcon
    private let trailingIcon: () -> TrailingIcon

    public init(
        text: Binding<String>,
        placeholder: String = "",
        inputResultState: OBRitInputResultState = .default,
        style: OBRitInputFieldStyle = .default,
        containerColor: Color = OBRitColors.gray800,
        maxLength: Int? = nil,
        supportingText: String = "",
        enabled: Bool = true,
        readOnly: Bool = false,
        singleLine: Bool = false,
        forceFocused: Bool = false,
        inputType: OBRitTextInputType = .text,
        submitLabel: SubmitLabel = .done,
        onSubmit: @escaping () -> Void = {},
        @ViewBuilder leadingIcon: @escaping () -> LeadingIcon,
        @ViewBuilder trailingIcon: @escaping () -> TrailingIcon
    ) {
        self._text = text
        self.placeholder = placeholder
        self.inputResultState = inputResultState
        self.style = style
        self.containerColor = containerColor
        self.maxLength = maxLength
        self.supportingText = supportingText
        self.enabled = enabled
        self.readOnly = readOnly
        self.singleLine = singleLine
        self.forceFocused = forceFocused
        self.inputType = inputType
        self.submitLabel = submitLabel
        self.onSubmit = onSubmit
        self.leadingIcon = leadingIcon
        self.trailingIcon = trailingIcon
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s2) {
            HStack(spacing: OBRitSpacing.s2) {
                leadingIcon()
                    .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)

                ZStack(alignment: .leading) {
                    if text.isEmpty && !placeholder.isEmpty {
                        Text(placeholder)
                            .lineLimit(singleLine ? 1 : nil)
                            .obritTextStyle(OBRitTypography.xl, weight: AtomFontWeight.shared.Medium, color: OBRitColors.gray700)
                    }

                    inputField
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                if let maxLength {
                    Text("\(min(text.count, maxLength))/\(maxLength)")
                        .lineLimit(1)
                        .obritTextStyle(OBRitTypography.small, weight: AtomFontWeight.shared.Medium, color: counterColor)
                }

                trailingIcon()
                    .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
            }
            .frame(minHeight: OBRitSpacing.s14)
            .padding(.horizontal, OBRitSpacing.s5)
            .padding(.vertical, OBRitSpacing.s4)
            .background(containerColor)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
            .overlay { borderOverlay }

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

    @ViewBuilder
    private var inputField: some View {
        if inputType.isSecure {
            configuredInputField(SecureField("", text: $text))
                .lineLimit(1)
        } else {
            configuredInputField(TextField("", text: $text, axis: singleLine ? .horizontal : .vertical))
                .lineLimit(singleLine ? 1 : nil)
        }
    }

    private func configuredInputField<Field: View>(_ field: Field) -> some View {
        field
            .focused($isFocused)
            .disabled(!enabled)
            .allowsHitTesting(enabled && !readOnly)
            .keyboardType(inputType.keyboardType)
            .textContentType(inputType.textContentType)
            .textInputAutocapitalization(inputType.autocapitalization)
            .autocorrectionDisabled(inputType.autocorrectionDisabled)
            .submitLabel(submitLabel)
            .onSubmit(onSubmit)
            .tint(OBRitColors.common00)
            .obritTextStyle(OBRitTypography.xl, weight: AtomFontWeight.shared.Medium, color: contentColor)
    }

    @ViewBuilder
    private var borderOverlay: some View {
        if let borderStroke {
            RoundedRectangle(cornerRadius: OBRitRadius.middle)
                .stroke(borderStroke.color, lineWidth: borderStroke.lineWidth)
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

    private var borderStroke: (color: Color, lineWidth: CGFloat)? {
        if !enabled {
            return (OBRitColors.gray700, OBRitSpacing.px)
        }
        switch inputResultState {
        case .error:
            return (OBRitColors.red300, OBRitSpacing.px)
        case .success:
            return (OBRitColors.green300, OBRitSpacing.px)
        case .default:
            if isFocused || forceFocused {
                return (OBRitColors.common00, OBRitSpacing.px)
            }
            if style == .lined {
                return (OBRitColors.gray300, OBRitInputFieldLinedBorderWidth)
            }
            return nil
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

public extension OBRitOutlinedTextField where LeadingIcon == EmptyView, TrailingIcon == EmptyView {
    init(
        text: Binding<String>,
        placeholder: String = "",
        inputResultState: OBRitInputResultState = .default,
        style: OBRitInputFieldStyle = .default,
        containerColor: Color = OBRitColors.gray800,
        maxLength: Int? = nil,
        supportingText: String = "",
        enabled: Bool = true,
        readOnly: Bool = false,
        singleLine: Bool = false,
        forceFocused: Bool = false,
        inputType: OBRitTextInputType = .text,
        submitLabel: SubmitLabel = .done,
        onSubmit: @escaping () -> Void = {}
    ) {
        self.init(
            text: text,
            placeholder: placeholder,
            inputResultState: inputResultState,
            style: style,
            containerColor: containerColor,
            maxLength: maxLength,
            supportingText: supportingText,
            enabled: enabled,
            readOnly: readOnly,
            singleLine: singleLine,
            forceFocused: forceFocused,
            inputType: inputType,
            submitLabel: submitLabel,
            onSubmit: onSubmit,
            leadingIcon: { EmptyView() },
            trailingIcon: { EmptyView() }
        )
    }
}

public extension OBRitOutlinedTextField where LeadingIcon == EmptyView {
    init(
        text: Binding<String>,
        placeholder: String = "",
        inputResultState: OBRitInputResultState = .default,
        style: OBRitInputFieldStyle = .default,
        containerColor: Color = OBRitColors.gray800,
        maxLength: Int? = nil,
        supportingText: String = "",
        enabled: Bool = true,
        readOnly: Bool = false,
        singleLine: Bool = false,
        forceFocused: Bool = false,
        inputType: OBRitTextInputType = .text,
        submitLabel: SubmitLabel = .done,
        onSubmit: @escaping () -> Void = {},
        @ViewBuilder trailingIcon: @escaping () -> TrailingIcon
    ) {
        self.init(
            text: text,
            placeholder: placeholder,
            inputResultState: inputResultState,
            style: style,
            containerColor: containerColor,
            maxLength: maxLength,
            supportingText: supportingText,
            enabled: enabled,
            readOnly: readOnly,
            singleLine: singleLine,
            forceFocused: forceFocused,
            inputType: inputType,
            submitLabel: submitLabel,
            onSubmit: onSubmit
        ) {
            EmptyView()
        } trailingIcon: {
            trailingIcon()
        }
    }
}

public extension OBRitOutlinedTextField where TrailingIcon == EmptyView {
    init(
        text: Binding<String>,
        placeholder: String = "",
        inputResultState: OBRitInputResultState = .default,
        style: OBRitInputFieldStyle = .default,
        containerColor: Color = OBRitColors.gray800,
        maxLength: Int? = nil,
        supportingText: String = "",
        enabled: Bool = true,
        readOnly: Bool = false,
        singleLine: Bool = false,
        forceFocused: Bool = false,
        inputType: OBRitTextInputType = .text,
        submitLabel: SubmitLabel = .done,
        onSubmit: @escaping () -> Void = {},
        @ViewBuilder leadingIcon: @escaping () -> LeadingIcon,
        trailingIcon: EmptyView = EmptyView()
    ) {
        self.init(
            text: text,
            placeholder: placeholder,
            inputResultState: inputResultState,
            style: style,
            containerColor: containerColor,
            maxLength: maxLength,
            supportingText: supportingText,
            enabled: enabled,
            readOnly: readOnly,
            singleLine: singleLine,
            forceFocused: forceFocused,
            inputType: inputType,
            submitLabel: submitLabel,
            onSubmit: onSubmit,
            leadingIcon: {
                leadingIcon()
            },
            trailingIcon: {
                EmptyView()
            }
        )
    }
}

private let OBRitInputFieldLinedBorderWidth: CGFloat = 1.4
