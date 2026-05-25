import SwiftUI

struct ItemRegistrationFormView: View {
    @State private var isReplacementDateDropdownExpanded = false

    let data: ItemRegistrationViewData
    let action: ItemRegistrationAction

    var body: some View {
        GeometryReader { geometry in
            let horizontalPadding = ItemRegistrationLayout.horizontalPadding(for: geometry.size.width)

            VStack(spacing: ItemRegistrationLayout.zeroSpacing) {
                OBRitDepthTopBar(
                    title: "소모품 등록",
                    backgroundColor: false,
                    onBackClick: action.onBack,
                    onMoreClick: action.onClose
                )

                ScrollView(showsIndicators: false) {
                    VStack(alignment: .leading, spacing: OBRitSpacing.s7) {
                        ItemRegistrationTitle(
                            title: "소모품의 상세 정보를\n입력해주세요",
                            subtitle: "원활한 관리를 위해 구체적인 정보를 입력해주세요"
                        )

                        VStack(alignment: .leading, spacing: OBRitSpacing.s10) {
                            ItemRequiredField(title: "소모품 종류") {
                                ItemKindPickerField(
                                    value: data.draft.selectedKind?.title ?? "",
                                    placeholder: "소모품 종류를 선택해주세요",
                                    onClick: action.onOpenKindSheet
                                )
                            }

                            ItemRequiredField(title: "소모품명") {
                                ItemTextInputField(
                                    text: data.draft.itemName,
                                    placeholder: "소모품명을 입력해주세요",
                                    maxLength: ItemRegistrationConfig.itemNameMaxLength,
                                    helperText: ItemRegistrationText.maxLengthHelper(ItemRegistrationConfig.itemNameMaxLength),
                                    singleLine: true,
                                    onTextChange: action.onUpdateItemName
                                )
                            }

                            ItemRequiredField(title: "마지막 교체 일자") {
                                ItemReplacementDateDropdown(
                                    selectedOption: data.draft.lastReplacementDateOption,
                                    isExpanded: $isReplacementDateDropdownExpanded,
                                    onSelect: { option in
                                        action.onSelectReplacementDate(option)
                                        isReplacementDateDropdownExpanded = false
                                    }
                                )
                            }
                            .zIndex(isReplacementDateDropdownExpanded ? ItemRegistrationLayout.expandedControlZIndex : ItemRegistrationLayout.defaultZIndex)

                            ItemRequiredField(title: "등록할 수량") {
                                ItemQuantityCard(
                                    kind: data.draft.selectedKind,
                                    quantity: data.draft.quantity,
                                    quantityLabelPrefix: "전체",
                                    helperText: "소모품의 전체 수량은 추후 수정할 수 있어요.",
                                    action: action
                                )
                            }
                        }

                        ItemScrollButton(
                            text: "소모품 등록하기",
                            enabled: data.canSubmitForm,
                            action: action.onSubmitForm
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
        }
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
                    onBackClick: action.onBack,
                    onMoreClick: action.onClose
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
        }
    }
}

struct ItemRegistrationCompleteView: View {
    let action: ItemRegistrationAction

    var body: some View {
        GeometryReader { geometry in
            let horizontalPadding = ItemRegistrationLayout.horizontalPadding(for: geometry.size.width)

            VStack(spacing: ItemRegistrationLayout.zeroSpacing) {
                Spacer(minLength: ItemRegistrationLayout.spacerMinimumLength)

                VStack(spacing: OBRitSpacing.s9) {
                    Image(ItemRegistrationAsset.completeBadge)
                        .resizable()
                        .scaledToFit()
                        .frame(
                            width: ItemRegistrationLayout.completeBadgeWidth,
                            height: ItemRegistrationLayout.completeBadgeHeight
                        )

                    VStack(spacing: OBRitSpacing.s4) {
                        Text("소모품이 등록되었어요!")
                            .lineLimit(ItemRegistrationLayout.singleLineLimit)
                            .obritTextStyle(
                                OBRitTypography.s6xl,
                                weight: OBRitFontWeight.bold,
                                color: OBRitColors.common00
                            )

                        Text("이제부터 OBRIT이 간편하게\n소모품을 관리해드릴게요")
                            .multilineTextAlignment(.center)
                            .obritTextStyle(
                                OBRitTypography.xl,
                                weight: OBRitFontWeight.medium,
                                color: OBRitColors.textDefaultSecondary
                            )
                    }
                }
                .frame(maxWidth: .infinity)
                .padding(.bottom, geometry.size.height * ItemRegistrationLayout.completeContentBottomPaddingRatio)

                Spacer(minLength: ItemRegistrationLayout.spacerMinimumLength)

                ItemBottomButton(
                    text: "홈 화면으로 돌아가기",
                    enabled: true,
                    horizontalPadding: horizontalPadding,
                    action: action.onComplete
                )
            }
        }
    }
}
