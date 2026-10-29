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

    var body: some View {
        GeometryReader { geometry in
            let horizontalPadding = ItemRegistrationLayout.horizontalPadding(for: geometry.size.width)

            VStack(spacing: ItemRegistrationLayout.zeroSpacing) {
                OBRitDepthTopBar(
                    title: "소모품 직접 등록",
                    backgroundColor: false,
                    onBackClick: action.onBack
                )

                ScrollView(showsIndicators: false) {
                    VStack(alignment: .leading, spacing: OBRitSpacing.s7) {
                        ItemRegistrationTitle(
                            title: "소모품 종류 직접 등록하기",
                            subtitle: "원하는 소모품 종류가 없다면 직접 추가할 수 있어요."
                        )

                        VStack(alignment: .leading, spacing: OBRitSpacing.s10) {
                            ItemRequiredField(title: "소모품 종류명") {
                                ItemTextInputField(
                                    text: data.draft.directKindName,
                                    placeholder: "소모품 종류명을 입력해주세요",
                                    maxLength: ItemRegistrationConfig.kindNameMaxLength,
                                    helperText: ItemRegistrationText.maxLengthHelper(ItemRegistrationConfig.kindNameMaxLength),
                                    singleLine: true,
                                    onTextChange: action.onUpdateDirectKindName
                                )
                            }

                            ItemRequiredField(title: "대표 이미지") {
                                LazyVGrid(
                                    columns: ItemRegistrationLayout.imageGridColumns,
                                    alignment: .leading,
                                    spacing: OBRitSpacing.s3
                                ) {
                                    ForEach(data.imageOptions) { option in
                                        ItemImageOptionButton(
                                            option: option,
                                            selected: option == data.draft.selectedImageOption,
                                            onSelect: { action.onSelectImageOption(option) }
                                        )
                                    }
                                }
                            }
                        }

                        ItemScrollButton(
                            text: "소모품 종류 등록하기",
                            enabled: data.canSubmitDirectKind,
                            action: action.onSubmitDirectKind
                        )
                    }
                    .padding(.horizontal, horizontalPadding)
                    .padding(.top, OBRitSpacing.s5)
                    .padding(.bottom, OBRitSpacing.s10)
                }
                .scrollDismissesKeyboard(.interactively)
                .background(
                    OBRitColors.backgroundDefaultDefault
                        .contentShape(Rectangle())
                        .onTapGesture(perform: dismissKeyboard)
                )
            }
            .frame(width: geometry.size.width, height: geometry.size.height)
            .background(OBRitColors.backgroundDefaultDefault)
            .dismissKeyboardOnBackgroundTap()
        }
    }
}

struct ItemRegistrationCompleteView: View {
    let action: ItemRegistrationAction

    var body: some View {
        RegistrationCompleteScreen(action: action.onComplete)
    }
}
