import SwiftUI

struct ItemRegistrationFormView: View {
    let data: ItemRegistrationViewData
    let action: ItemRegistrationAction

    var body: some View {
        VStack(spacing: 0) {
            OBRitDepthTopBar(
                title: "소모품 추가",
                backgroundColor: false,
                showRightButton: false,
                onBackClick: action.onBack
            )
            ScrollView(showsIndicators: false) {
                VStack(alignment: .leading, spacing: OBRitSpacing.s7) {
                    ItemRegistrationTitle(
                        title: "소모품의 상세 정보를\n입력해주세요",
                        subtitle: "원활한 관리를 위해 구체적인 정보를 입력해주세요"
                    )
                    registrationFields
                }
                .padding(.horizontal, OBRitSpacing.s5)
                .padding(.top, OBRitSpacing.s5)
                .padding(.bottom, OBRitSpacing.s6)
            }
            .scrollDismissesKeyboard(.interactively)
        }
        .background(OBRitColors.backgroundDefaultDefault.ignoresSafeArea())
        .dismissKeyboardOnBackgroundTap()
        .safeAreaInset(edge: .bottom, spacing: 0) {
            registrationButton
        }
    }

    private var registrationFields: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s10) {
            ItemRequiredField(title: "소모품") {
                ItemKindPickerField(
                    value: data.draft.selectedKind?.title ?? "",
                    placeholder: "소모품을 선택해주세요",
                    onClick: action.onOpenKindSheet
                )
            }
            ItemRequiredField(title: "이름") {
                ItemTextInputField(
                    text: data.draft.itemName,
                    placeholder: "구분을 위한 이름을 입력해주세요",
                    maxLength: ItemRegistrationConfig.itemNameMaxLength,
                    errorMessage: data.itemNameError,
                    limitsInputLength: false,
                    singleLine: true,
                    onTextChange: action.onUpdateItemName
                )
            }
            ItemRequiredField(title: "등록할 수량") {
                quantityInput
            }
        }
    }

    private var quantityInput: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
            OBRitNumberSelector(
                value: data.draft.quantity,
                range: ItemRegistrationConfig.manualQuantityRange,
                isEnabled: !data.isProcessing,
                onValueChange: action.onUpdateQuantity
            )
            HStack(spacing: OBRitSpacing.s1_5) {
                OBRitIcon(kind: .info, color: OBRitColors.textDefaultTertiary)
                    .frame(width: OBRitSpacing.s4, height: OBRitSpacing.s4)
                Text("소모품의 전체 수량은 추후 수정할 수 있어요.")
                    .obritTextStyle(
                        OBRitTypography.base,
                        weight: OBRitFontWeight.semiBold,
                        color: OBRitColors.textDefaultTertiary
                    )
                    .fixedSize(horizontal: false, vertical: true)
            }
        }
    }

    private var registrationButton: some View {
        OBRitFilledTextButton(
            text: data.isProcessing ? "등록 중..." : "소모품 등록하기",
            enabled: data.canSubmitForm,
            fillsWidth: true,
            action: action.onSubmitForm
        )
        .padding(OBRitSpacing.s5)
        .background(OBRitColors.backgroundDefaultDefault.ignoresSafeArea(edges: .bottom))
    }
}

struct ItemDirectKindRegistrationView: View {
    let data: ItemRegistrationViewData
    let action: ItemRegistrationAction
    let contentHeight: CGFloat
    let bottomPadding: CGFloat

    var body: some View {
        OBRitBottomSheet(
            contentHeight: contentHeight,
            bottomPadding: bottomPadding,
            onDismiss: action.onDismissBottomSheet
        ) {
            VStack(alignment: .leading, spacing: OBRitSpacing.s6) {
                HStack(spacing: 0) {
                    Button(action: action.onDismissBottomSheet) {
                        Image("registration_sheet_back")
                            .frame(width: 28, height: 28)
                            .frame(height: 44)
                            .contentShape(Rectangle())
                    }
                    .buttonStyle(.plain)
                    .disabled(data.isProcessing)
                    .accessibilityLabel("소모품 선택으로 돌아가기")
                    Text("직접 등록하기")
                        .obritTextStyle(OBRitTypography.s5xl, weight: OBRitFontWeight.semiBold, color: OBRitColors.common00)
                }

                ScrollView(showsIndicators: false) {
                    VStack(alignment: .leading, spacing: OBRitSpacing.s7) {
                        ItemRequiredField(title: "소모품 종류 이름") {
                            ItemTextInputField(
                                text: data.draft.directKindName,
                                placeholder: "소모품의 종류 이름을 입력해주세요",
                                maxLength: ItemRegistrationConfig.kindNameMaxLength,
                                errorMessage: data.directKindNameError,
                                limitsInputLength: false,
                                singleLine: true,
                                onTextChange: action.onUpdateDirectKindName
                            )
                        }
                        ItemRequiredField(title: "대표 이미지") {
                            LazyVGrid(
                                columns: [GridItem(.adaptive(minimum: 60), spacing: OBRitSpacing.s2)],
                                spacing: OBRitSpacing.s3
                            ) {
                                ForEach(data.imageOptions) { option in
                                    ItemImageOptionButton(
                                        option: option,
                                        selected: option == data.draft.selectedImageOption,
                                        onSelect: { action.onSelectImageOption(option) }
                                    )
                                    .background(OBRitColors.gray750, in: Circle())
                                }
                            }
                        }
                    }
                    .disabled(data.isProcessing)
                    .padding(.bottom, OBRitSpacing.s5)
                }
                .scrollDismissesKeyboard(.interactively)
                .frame(maxHeight: .infinity)

                OBRitFilledTextButton(
                    text: data.isProcessing ? "등록 중..." : "등록하기",
                    enabled: data.canSubmitDirectKind,
                    fillsWidth: true,
                    action: action.onSubmitDirectKind
                )
            }
        }
    }
}

struct ItemRegistrationCompleteView: View {
    let action: ItemRegistrationAction

    var body: some View {
        RegistrationCompleteScreen(action: action.onComplete)
    }
}
