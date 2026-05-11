import SwiftUI

struct DesignSystemPreviewGallery: View {
    @State private var defaultText = ""
    @State private var filledText = "TEXT"
    @State private var disabledText = ""
    @State private var errorText = "TEXT"
    @State private var successText = "TEXT"
    @State private var trailingText = "TEXT"

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: OBRitSpacing.s6) {
                previewSection("Buttons") {
                    VStack(spacing: OBRitSpacing.s3) {
                        buttonRow(size: .large)
                        buttonRow(size: .middle)
                        buttonRow(size: .small)
                        buttonRow(size: .large, enabled: false)
                    }
                }

                previewSection("Selection") {
                    HStack(spacing: OBRitSpacing.s5) {
                        OBRitCheckBox(checked: true)
                        OBRitCheckBox(checked: false)
                        OBRitCheckBox(checked: true, enabled: false)
                        OBRitCheckBox(checked: false, enabled: false)
                        OBRitRadioButton(selected: true)
                        OBRitRadioButton(selected: false)
                        OBRitRadioButton(selected: true, enabled: false)
                        OBRitRadioButton(selected: false, enabled: false)
                    }
                }

                previewSection("Dropdown") {
                    VStack(spacing: OBRitSpacing.s3) {
                        OBRitDropdown(value: "", placeholder: "TEXT", onClick: {})
                        OBRitDropdown(value: "TEXT", placeholder: "TEXT", onClick: {})
                        OBRitDropdown(value: "", placeholder: "TEXT", expanded: true, onClick: {})
                        OBRitDropdown(value: "TEXT", placeholder: "TEXT", inputState: .error, supportingText: "에러 텍스트를 입력해주세요", onClick: {})
                        OBRitDropdown(value: "", placeholder: "TEXT", enabled: false, onClick: {})
                        HStack(spacing: OBRitSpacing.s6) {
                            VStack(spacing: OBRitSpacing.s3) {
                                OBRitDropdownMenuItem(text: "TEXT", size: .small)
                                OBRitDropdownMenuItem(text: "TEXT", size: .small, selected: true)
                            }
                            VStack(spacing: OBRitSpacing.s3) {
                                OBRitDropdownMenuItem(text: "TEXT", size: .large)
                                OBRitDropdownMenuItem(text: "TEXT", size: .large, selected: true)
                            }
                        }
                        OBRitDropdownMenu(items: Array(repeating: "TEXT", count: 6), selectedIndex: 1, onItemClick: { _ in })
                    }
                }

                previewSection("Snackbar") {
                    VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
                        OBRitSnackbar(message: "토스트 팝업 내용을 입력하세요.")
                        OBRitSnackbar(message: "토스트 팝업 내용을 입력하세요.\n토스트 팝업 내용을 입력하세요.", icon: .default)
                        OBRitSnackbar(message: "토스트 팝업 내용을 입력하세요.", icon: .error)
                        OBRitSnackbar(message: "토스트 팝업 내용을 입력하세요.", icon: .success)
                    }
                }

                previewSection("TextField") {
                    VStack(spacing: OBRitSpacing.s3) {
                        OBRitOutlinedTextField(text: $defaultText, placeholder: "TEXT", maxLength: 30, singleLine: true)
                        OBRitOutlinedTextField(text: $filledText, placeholder: "TEXT", maxLength: 30, singleLine: true)
                        OBRitOutlinedTextField(text: $filledText, placeholder: "TEXT", maxLength: 30, singleLine: true, forceFocused: true)
                        OBRitOutlinedTextField(text: $defaultText, placeholder: "TEXT", style: .lined, maxLength: 30, singleLine: true)
                        OBRitOutlinedTextField(text: $filledText, placeholder: "TEXT", style: .lined, maxLength: 30, singleLine: true)
                        OBRitOutlinedTextField(text: $disabledText, placeholder: "TEXT", maxLength: 30, enabled: false, singleLine: true)
                        OBRitOutlinedTextField(text: $errorText, placeholder: "TEXT", inputResultState: .error, maxLength: 30, supportingText: "에러 텍스트를 입력해주세요", singleLine: true)
                        OBRitOutlinedTextField(text: $successText, placeholder: "TEXT", inputResultState: .success, maxLength: 30, supportingText: "완료 텍스트를 입력해주세요", singleLine: true)
                        OBRitOutlinedTextField(text: $trailingText, placeholder: "TEXT", maxLength: 30, singleLine: true) {
                            OBRitIcon(kind: .question, color: OBRitColors.common00)
                        } trailingIcon: {
                            OBRitIcon(kind: .success, color: OBRitColors.green300)
                        }
                    }
                }
            }
            .padding(OBRitSpacing.s5)
        }
        .background(OBRitColors.gray900)
    }

    private func buttonRow(size: OBRitFilledButtonSize, enabled: Bool = true) -> some View {
        HStack(spacing: OBRitSpacing.s3) {
            OBRitFilledTextButton(text: "Button", size: size, color: .green, enabled: enabled, action: {})
            OBRitFilledTextButton(text: "Button", size: size, color: .gray, enabled: enabled, action: {})
            OBRitFilledTextButton(text: "Button", size: size, color: .white, enabled: enabled, action: {})
        }
    }

    private func previewSection<Content: View>(
        _ title: String,
        @ViewBuilder content: () -> Content
    ) -> some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
            Text(title)
                .obritTextStyle(OBRitTypography.base, color: OBRitColors.gray300)
            content()
        }
    }
}

struct DesignSystemPreviewGallery_Previews: PreviewProvider {
    static var previews: some View {
        DesignSystemPreviewGallery()
            .previewLayout(.sizeThatFits)
    }
}
