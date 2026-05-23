import SwiftUI

struct ItemDetailEditValidation: Equatable {
    var nameErrorMessage: String?

    static let valid = ItemDetailEditValidation(nameErrorMessage: nil)
    static let duplicateName = ItemDetailEditValidation(nameErrorMessage: "다른 이름과 중복되지 않게 입력해주세요")
}

struct ItemDetailEditScaffoldView: View {
    @Binding var draft: ItemDetailEditDraft

    let validation: ItemDetailEditValidation
    let recommendedCycleDays: Int?
    let averageCycleDays: Int?
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
        imageAssetNames: [String] = ItemDetailEditAssetCatalog.defaultAssetNames,
        isProcessing: Bool = false,
        canSubmit: Bool? = nil,
        onClose: @escaping () -> Void,
        onSubmit: @escaping () -> Void
    ) {
        self._draft = draft
        self.validation = validation
        self.recommendedCycleDays = recommendedCycleDays
        self.averageCycleDays = averageCycleDays
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
                .padding(.top, ItemDetailEditMetrics.topContentPadding)
                .padding(.bottom, ItemDetailEditMetrics.scrollBottomPadding)
            }
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
                text: $draft.name,
                inputResultState: validation.nameErrorMessage == nil ? .default : .error,
                maxLength: ItemDetailConfig.maximumNameLength,
                supportingText: validation.nameErrorMessage ?? "",
                singleLine: true
            )
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

            LazyVGrid(columns: imageGridColumns, spacing: ItemDetailEditMetrics.imageRowSpacing) {
                ForEach(imageAssetNames, id: \.self) { assetName in
                    Button {
                        draft.imageAssetName = assetName
                    } label: {
                        ZStack {
                            Circle()
                                .fill(OBRitColors.gray750)

                            Image(assetName)
                                .resizable()
                                .scaledToFit()
                                .padding(ItemDetailEditMetrics.imagePadding)
                        }
                        .frame(
                            width: ItemDetailEditMetrics.imageSize,
                            height: ItemDetailEditMetrics.imageSize
                        )
                        .overlay {
                            if draft.imageAssetName == assetName {
                                Circle()
                                    .stroke(OBRitColors.backgroundPositiveDefault, lineWidth: ItemDetailEditMetrics.selectedImageBorderWidth)
                            }
                        }
                    }
                    .buttonStyle(.plain)
                    .accessibilityLabel("대표 이미지 선택")
                }
            }
        }
    }

    private var bottomBar: some View {
        VStack(spacing: OBRitSpacing.s0) {
            OBRitFilledTextButton(
                text: isProcessing ? "편집 중" : "편집 완료",
                size: .large,
                color: canSubmit ? .green : .gray,
                enabled: canSubmit,
                fillsWidth: true,
                action: onSubmit
            )
            .padding(.horizontal, OBRitSpacing.s5)
            .padding(.vertical, OBRitSpacing.s4)

            Capsule()
                .fill(OBRitColors.common00)
                .frame(width: ItemDetailEditMetrics.homeIndicatorWidth, height: ItemDetailEditMetrics.homeIndicatorHeight)
                .padding(.bottom, OBRitSpacing.s2)
        }
        .background(OBRitColors.backgroundDefaultDefault)
    }

    private var replacementCycleText: Binding<String> {
        Binding {
            "\(draft.replacementCycleDays)"
        } set: { newValue in
            let digits = newValue.filter(\.isNumber)
            guard let value = Int(digits) else {
                draft.replacementCycleDays = ItemDetailConfig.minimumReplacementCycleDays
                return
            }
            draft.replacementCycleDays = min(
                max(value, ItemDetailConfig.minimumReplacementCycleDays),
                ItemDetailConfig.maximumReplacementCycleDays
            )
        }
    }

    private var imageGridColumns: [GridItem] {
        Array(
            repeating: GridItem(.fixed(ItemDetailEditMetrics.imageSize), spacing: ItemDetailEditMetrics.imageColumnSpacing),
            count: ItemDetailEditMetrics.imageColumnCount
        )
    }

    private var canSubmit: Bool {
        canSubmitOverride ?? (draft.isValid && validation.nameErrorMessage == nil && !isProcessing)
    }

    private func sectionTitle(_ title: String) -> some View {
        Text(title)
            .lineLimit(1)
            .obritTextStyle(OBRitTypography.s2xl, weight: OBRitFontWeight.bold, color: OBRitColors.textDefaultDefault)
            .frame(maxWidth: .infinity, alignment: .leading)
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
                Text(highlighted).foregroundColor(OBRitColors.green400) +
                Text(suffix)
        }
        .lineLimit(1)
        .minimumScaleFactor(0.75)
        .obritTextStyle(OBRitTypography.s, weight: OBRitFontWeight.medium, color: OBRitColors.gray300)
    }
}

private enum ItemDetailEditAssetCatalog {
    static let defaultAssetNames = [
        "home_orb_toothbrush",
        "home_orb_razor",
        "home_orb_shower_filter",
        "home_orb_detergent",
        "home_orb_diffuser",
        "home_orb_sponge",
        "home_orb_towel"
    ]
}

private enum ItemDetailEditMetrics {
    static let topContentPadding: CGFloat = 16
    static let sectionSpacing: CGFloat = 36
    static let scrollBottomPadding: CGFloat = 132
    static let imageColumnCount = 5
    static let imageSize: CGFloat = 60
    static let imagePadding: CGFloat = 8
    static let imageColumnSpacing: CGFloat = 18
    static let imageRowSpacing: CGFloat = 12
    static let selectedImageBorderWidth: CGFloat = 2.3
    static let homeIndicatorWidth: CGFloat = 108
    static let homeIndicatorHeight: CGFloat = 4
}

#Preview {
    @Previewable @State var draft = ItemDetailEditDraft(consumable: ItemDetailDomainSampleData.consumable(id: 1))

    ItemDetailEditScaffoldView(
        draft: $draft,
        validation: .duplicateName,
        recommendedCycleDays: 30,
        averageCycleDays: 31,
        onClose: {},
        onSubmit: {}
    )
}
