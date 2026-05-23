import SwiftUI
import UIKit

struct ItemDetailEditValidation: Equatable {
    var nameErrorMessage: String?

    static let valid = ItemDetailEditValidation(nameErrorMessage: nil)
    static let emptyName = ItemDetailEditValidation(nameErrorMessage: "소모품명을 입력해주세요")
    static let duplicateName = ItemDetailEditValidation(nameErrorMessage: "다른 이름과 중복되지 않게 입력해주세요")
    static let invalidNameCharacters = ItemDetailEditValidation(nameErrorMessage: "한글, 영문, 숫자, 공백, -, _, /, (, )만 사용할 수 있어요")
}

struct ItemDetailEditScaffoldView: View {
    @Binding var draft: ItemDetailEditDraft
    @State private var replacementCycleInput: String
    @State private var originalName: String
    @State private var selectedImageOptionID: Int?
    @State private var imageGridWidth = ItemDetailEditMetrics.referenceContentWidth
    @State private var hasAttemptedNameOverflow = false

    let validation: ItemDetailEditValidation
    let recommendedCycleDays: Int?
    let averageCycleDays: Int?
    let existingConsumableNames: [String]
    let imageAssetNames: [String]
    let isProcessing: Bool
    let canSubmitOverride: Bool?
    let onClose: () -> Void
    let onSubmit: () -> Void

    init(
        draft: Binding<ItemDetailEditDraft>,
        validation: ItemDetailEditValidation = .valid,
        recommendedCycleDays: Int? = nil,
        averageCycleDays: Int? = nil,
        existingConsumableNames: [String] = [],
        imageAssetNames: [String] = ItemDetailEditAssetCatalog.defaultAssetNames,
        isProcessing: Bool = false,
        canSubmit: Bool? = nil,
        onClose: @escaping () -> Void,
        onSubmit: @escaping () -> Void
    ) {
        self._draft = draft
        self._replacementCycleInput = State(initialValue: "\(draft.wrappedValue.replacementCycleDays)")
        self._originalName = State(initialValue: draft.wrappedValue.name)
        self.validation = validation
        self.recommendedCycleDays = recommendedCycleDays
        self.averageCycleDays = averageCycleDays
        self.existingConsumableNames = existingConsumableNames
        self.imageAssetNames = imageAssetNames
        self.isProcessing = isProcessing
        self.canSubmitOverride = canSubmit
        self.onClose = onClose
        self.onSubmit = onSubmit
    }

    var body: some View {
        VStack(spacing: OBRitSpacing.s0) {
            OBRitCloseTopBar(
                title: "편집하기",
                showRightButton: false,
                onCloseClick: onClose
            )

            ScrollView(showsIndicators: false) {
                VStack(alignment: .leading, spacing: ItemDetailEditMetrics.sectionSpacing) {
                    nameSection
                    replacementCycleSection
                    imageSection
                }
                .padding(.horizontal, OBRitSpacing.s5)
                .padding(.vertical, OBRitSpacing.s4)
                .frame(maxWidth: .infinity, alignment: .topLeading)
                .background {
                    OBRitColors.backgroundDefaultDefault
                        .onTapGesture(perform: dismissKeyboard)
                }
            }
            .scrollDismissesKeyboard(.interactively)
        }
        .background(OBRitColors.backgroundDefaultDefault.ignoresSafeArea())
        .safeAreaInset(edge: .bottom, spacing: OBRitSpacing.s0) {
            bottomBar
        }
    }

    private var nameSection: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s4) {
            sectionTitle("소모품명")

            OBRitOutlinedTextField(
                text: clippedNameText,
                inputResultState: nameHelperMessage == nil ? .default : .error,
                maxLength: ItemDetailConfig.maximumNameLength,
                supportingText: "",
                singleLine: true
            )

            nameHelperText
        }
    }

    private var replacementCycleSection: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s4) {
            sectionTitle("교체 주기")

            VStack(alignment: .leading, spacing: OBRitSpacing.s2_5) {
                OBRitOutlinedTextField(
                    text: replacementCycleText,
                    singleLine: true,
                    inputType: .number,
                    submitLabel: .done,
                    onSubmit: normalizeReplacementCycleInput,
                    trailingIcon: {
                        Text("일")
                            .lineLimit(1)
                            .obritTextStyle(
                                OBRitTypography.s,
                                weight: OBRitFontWeight.medium,
                                color: OBRitColors.common00
                            )
                    }
                )

                VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
                    if let recommendedCycleDays {
                        helperText(
                            prefix: "\(draft.name)의 ",
                            highlighted: "권장 교체 주기는 \(recommendedCycleDays)일",
                            suffix: "이에요"
                        )
                    }

                    if let averageCycleDays {
                        helperText(
                            prefix: "\(draft.name)의 ",
                            highlighted: "나의 평균 교체 주기는 \(averageCycleDays)일",
                            suffix: "이에요"
                        )
                    }
                }
            }
        }
    }

    private var imageSection: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
            sectionTitle("대표 이미지")

            GeometryReader { geometry in
                let imageSize = imageSize(in: geometry.size.width)

                LazyVGrid(columns: imageGridColumns(in: geometry.size.width), spacing: ItemDetailEditMetrics.imageRowSpacing) {
                    ForEach(imageOptions) { option in
                        ItemDetailEditImageButton(
                            assetName: option.assetName,
                            size: imageSize,
                            selected: isSelectedImageOption(option),
                            accessibilityLabel: option.accessibilityLabel,
                            onSelect: {
                                selectedImageOptionID = option.id
                                draft.imageAssetName = option.assetName
                                dismissKeyboard()
                            }
                        )
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .onAppear {
                    imageGridWidth = geometry.size.width
                }
                .onChange(of: geometry.size.width) { _, newWidth in
                    imageGridWidth = newWidth
                }
            }
            .frame(height: imageGridHeight(for: imageGridWidth))
        }
    }

    private var bottomBar: some View {
        OBRitFilledTextButton(
            text: isProcessing ? "편집 중" : "편집 완료",
            size: .large,
            color: canSubmit ? .green : .gray,
            enabled: canSubmit,
            fillsWidth: true,
            action: submitEdit
        )
        .frame(height: ItemDetailEditMetrics.bottomButtonHeight)
        .padding(.horizontal, OBRitSpacing.s5)
        .padding(.vertical, OBRitSpacing.s4)
        .background(OBRitColors.backgroundDefaultDefault)
    }

    private var replacementCycleText: Binding<String> {
        Binding {
            replacementCycleInput
        } set: { newValue in
            let digits = newValue.filter(\.isNumber)
            let maximumDigitCount = "\(ItemDetailConfig.maximumReplacementCycleDays)".count
            let clippedDigits = String(digits.prefix(maximumDigitCount))

            replacementCycleInput = clippedDigits

            guard let value = Int(clippedDigits) else { return }
            draft.replacementCycleDays = clampedReplacementCycleDays(value)
        }
    }

    private var clippedNameText: Binding<String> {
        Binding {
            draft.name
        } set: { newValue in
            let clippedName = String(newValue.prefix(ItemDetailConfig.maximumNameLength))
            if newValue.count > ItemDetailConfig.maximumNameLength {
                hasAttemptedNameOverflow = true
            } else if newValue.count < ItemDetailConfig.maximumNameLength {
                hasAttemptedNameOverflow = false
            }
            draft.name = clippedName
        }
    }

    private var nameHelperMessage: String? {
        if hasAttemptedNameOverflow {
            return "\(ItemDetailConfig.maximumNameLength)자 이내로 입력해주세요"
        }

        return blockingNameValidationMessage
    }

    private var blockingNameValidationMessage: String? {
        if normalizedName(draft.name).isEmpty {
            return ItemDetailEditValidation.emptyName.nameErrorMessage
        }

        if hasInvalidNameCharacters {
            return ItemDetailEditValidation.invalidNameCharacters.nameErrorMessage
        }

        if hasDuplicateName {
            return ItemDetailEditValidation.duplicateName.nameErrorMessage
        }

        return validation.nameErrorMessage
    }

    private var hasDuplicateName: Bool {
        let currentName = normalizedName(draft.name)
        guard !currentName.isEmpty && !namesMatch(currentName, originalName) else {
            return false
        }

        return existingConsumableNames.contains { namesMatch($0, currentName) }
    }

    private var hasInvalidNameCharacters: Bool {
        let currentName = normalizedName(draft.name)
        guard !currentName.isEmpty else { return false }

        return currentName.range(
            of: ItemDetailEditMetrics.allowedNamePattern,
            options: .regularExpression
        ) == nil
    }

    private var imageOptions: [ItemDetailEditImageOption] {
        imageAssetNames.enumerated().map { index, assetName in
            ItemDetailEditImageOption(
                id: index,
                assetName: assetName,
                accessibilityLabel: "\(index + 1)번째 \(imageAccessibilityName(for: assetName)) 대표 이미지"
            )
        }
    }

    private func imageGridHeight(for availableWidth: CGFloat) -> CGFloat {
        guard !imageOptions.isEmpty else { return 0 }

        let imageSize = imageSize(in: availableWidth)
        let rowCount = ceil(CGFloat(imageOptions.count) / CGFloat(ItemDetailEditMetrics.imageColumnCount))
        return rowCount * imageSize +
            max(0, rowCount - 1) * ItemDetailEditMetrics.imageRowSpacing
    }

    private func imageGridColumns(in availableWidth: CGFloat) -> [GridItem] {
        let columnCount = ItemDetailEditMetrics.imageColumnCount
        let imageSize = imageSize(in: availableWidth)
        let totalImageWidth = imageSize * CGFloat(columnCount)
        let spacing = max(
            OBRitSpacing.s2,
            (availableWidth - totalImageWidth) / CGFloat(columnCount - 1)
        )

        return Array(
            repeating: GridItem(.fixed(imageSize), spacing: spacing, alignment: .leading),
            count: columnCount
        )
    }

    private func imageSize(in availableWidth: CGFloat) -> CGFloat {
        min(
            ItemDetailEditMetrics.maximumImageSize,
            max(
                ItemDetailEditMetrics.minimumImageSize,
                availableWidth * ItemDetailEditMetrics.imageWidthRatio
            )
        )
    }

    private var canSubmit: Bool {
        canSubmitOverride ?? (
            draft.isValid &&
                isReplacementCycleInputValid &&
                blockingNameValidationMessage == nil &&
                !isProcessing
        )
    }

    private var isReplacementCycleInputValid: Bool {
        guard let value = Int(replacementCycleInput) else { return false }

        return value >= ItemDetailConfig.minimumReplacementCycleDays &&
            value <= ItemDetailConfig.maximumReplacementCycleDays
    }

    private func isSelectedImageOption(_ option: ItemDetailEditImageOption) -> Bool {
        let selectedID = selectedImageOptionID ?? imageOptions.first { $0.assetName == draft.imageAssetName }?.id

        return option.id == selectedID && option.assetName == draft.imageAssetName
    }

    private func normalizeReplacementCycleInput() {
        guard let value = Int(replacementCycleInput) else {
            return
        }

        let clampedValue = clampedReplacementCycleDays(value)
        replacementCycleInput = "\(clampedValue)"
        draft.replacementCycleDays = clampedValue
    }

    private func clampedReplacementCycleDays(_ value: Int) -> Int {
        min(
            max(value, ItemDetailConfig.minimumReplacementCycleDays),
            ItemDetailConfig.maximumReplacementCycleDays
        )
    }

    private func normalizedName(_ name: String) -> String {
        name.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    private func namesMatch(_ lhs: String, _ rhs: String) -> Bool {
        normalizedName(lhs).localizedCaseInsensitiveCompare(normalizedName(rhs)) == .orderedSame
    }

    private func submitEdit() {
        normalizeReplacementCycleInput()
        onSubmit()
    }

    private func imageAccessibilityName(for assetName: String) -> String {
        ItemDetailEditAssetCatalog.accessibilityNames[assetName] ?? "소모품"
    }

    private func sectionTitle(_ title: String) -> some View {
        Text(title)
            .lineLimit(1)
            .obritTextStyle(OBRitTypography.s2xl, weight: OBRitFontWeight.bold, color: OBRitColors.textDefaultDefault)
            .frame(maxWidth: .infinity, alignment: .leading)
    }

    private var nameHelperText: some View {
        HStack(alignment: .top, spacing: OBRitSpacing.s1_5) {
            if nameHelperMessage != nil {
                OBRitIcon(kind: .exclamation, color: OBRitColors.red300)
                    .frame(width: OBRitSpacing.s4, height: OBRitSpacing.s4)
            }

            Text(nameHelperMessage ?? "소모품을 구분하기 쉬운 이름으로 입력해주세요")
                .fixedSize(horizontal: false, vertical: true)
                .obritTextStyle(
                    OBRitTypography.base,
                    weight: nameHelperMessage == nil ? OBRitFontWeight.medium : OBRitFontWeight.semiBold,
                    color: nameHelperMessage == nil ? OBRitColors.gray300 : OBRitColors.red300
                )
        }
    }

    private func helperText(
        prefix: String,
        highlighted: String,
        suffix: String
    ) -> some View {
        HStack(spacing: OBRitSpacing.s1) {
            OBRitIcon(kind: .success, color: OBRitColors.gray300)
                .frame(width: OBRitSpacing.s4, height: OBRitSpacing.s4)

            Text(prefix) +
                Text(highlighted).foregroundColor(OBRitColors.green500) +
                Text(suffix)
        }
        .lineLimit(1)
        .minimumScaleFactor(0.75)
        .obritTextStyle(OBRitTypography.s, weight: OBRitFontWeight.medium, color: OBRitColors.gray300)
    }
}

private struct ItemDetailEditImageOption: Identifiable, Equatable {
    let id: Int
    let assetName: String
    let accessibilityLabel: String
}

private struct ItemDetailEditImageButton: View {
    let assetName: String
    let size: CGFloat
    let selected: Bool
    let accessibilityLabel: String
    let onSelect: () -> Void

    var body: some View {
        Button(action: onSelect) {
            Image(assetName)
                .resizable()
                .scaledToFill()
                .frame(
                    width: size,
                    height: size
                )
                .background(OBRitColors.gray750)
                .clipShape(Circle())
                .overlay {
                    if selected {
                        Circle()
                            .stroke(OBRitColors.backgroundPositiveDefault, lineWidth: ItemDetailEditMetrics.selectedImageBorderWidth)
                    }
                }
                .contentShape(Circle())
        }
        .buttonStyle(.plain)
        .accessibilityLabel(accessibilityLabel)
        .accessibilityAddTraits(selected ? .isSelected : [])
        .accessibilityValue(selected ? "선택됨" : "선택 안 됨")
    }
}

private enum ItemDetailEditAssetCatalog {
    static let defaultAssetNames = [
        "item_razor",
        "item_water_filter",
        "item_toothbrush",
        "item_detergent",
        "item_towel",
        "item_shower_filter",
        "item_scrub_sponge",
        "item_diffuser",
        "item_kitchen_towel",
        "item_body_wash",
        "item_shampoo",
        "item_treatment",
        "item_hand_sanitizer",
        "item_toothpaste",
        "item_wet_wipes",
        "item_toilet_paper",
        "item_bandage",
        "item_misc",
        "item_air_purifier_filter",
        "item_trash_bag",
        "item_zip_bag",
        "item_toothbrush_sanitizer_filter",
        "item_clothespin",
        "item_rubber_gloves",
        "item_dish_soap",
        "item_fabric_softener",
        "item_cleaning_wipe",
        "item_sponge",
        "item_laundry_net",
        "item_cotton_swab",
        "item_cotton_pad",
        "item_foam_cleanser",
        "item_mask",
        "item_diffuser_refill",
        "item_drain_filter",
        "item_bathroom_cleaner",
        "item_dishcloth",
        "item_wrap_foil",
        "item_shower_ball",
        "item_light_bulb",
        "item_toilet_cleaner"
    ]

    static let accessibilityNames = [
        "item_razor": "면도기",
        "item_water_filter": "정수기 필터",
        "item_toothbrush": "칫솔",
        "item_detergent": "세제",
        "item_towel": "수건",
        "item_shower_filter": "샤워기 필터",
        "item_scrub_sponge": "수세미",
        "item_diffuser": "디퓨저",
        "item_kitchen_towel": "키친타월",
        "item_body_wash": "바디워시",
        "item_shampoo": "샴푸",
        "item_treatment": "트리트먼트",
        "item_hand_sanitizer": "손소독제",
        "item_toothpaste": "치약",
        "item_wet_wipes": "물티슈",
        "item_toilet_paper": "휴지",
        "item_bandage": "밴드",
        "item_misc": "생활용품",
        "item_air_purifier_filter": "공기청정기 필터",
        "item_trash_bag": "쓰레기 봉투",
        "item_zip_bag": "지퍼백",
        "item_toothbrush_sanitizer_filter": "칫솔 살균기 필터",
        "item_clothespin": "빨래집게",
        "item_rubber_gloves": "고무장갑",
        "item_dish_soap": "주방 세제",
        "item_fabric_softener": "섬유유연제",
        "item_cleaning_wipe": "청소포",
        "item_sponge": "스펀지",
        "item_laundry_net": "세탁망",
        "item_cotton_swab": "면봉",
        "item_cotton_pad": "화장솜",
        "item_foam_cleanser": "폼클렌저",
        "item_mask": "마스크",
        "item_diffuser_refill": "디퓨저 리필",
        "item_drain_filter": "배수구 거름망",
        "item_bathroom_cleaner": "욕실 세정제",
        "item_dishcloth": "행주",
        "item_wrap_foil": "랩/호일",
        "item_shower_ball": "샤워볼",
        "item_light_bulb": "전구",
        "item_toilet_cleaner": "변기 세정제"
    ]
}

private enum ItemDetailEditMetrics {
    static let allowedNamePattern = #"^[가-힣ㄱ-ㅎㅏ-ㅣA-Za-z0-9 _\-/()]+$"#
    static let sectionSpacing: CGFloat = 36
    static let imageColumnCount = 5
    static let referenceContentWidth: CGFloat = 372
    static let imageWidthRatio: CGFloat = 60 / 372
    static let minimumImageSize: CGFloat = 48
    static let maximumImageSize: CGFloat = 60
    static let imageRowSpacing: CGFloat = 12
    static let selectedImageBorderWidth: CGFloat = 2
    static let bottomButtonHeight: CGFloat = 60
}

private func dismissKeyboard() {
    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
}

#Preview {
    @Previewable @State var draft = ItemDetailEditDraft(consumable: ItemDetailDomainSampleData.consumable(id: 1))

    ItemDetailEditScaffoldView(
        draft: $draft,
        validation: .duplicateName,
        recommendedCycleDays: 30,
        averageCycleDays: 31,
        existingConsumableNames: ItemDetailDomainSampleData.consumables.map(\.name),
        onClose: {},
        onSubmit: {}
    )
}
