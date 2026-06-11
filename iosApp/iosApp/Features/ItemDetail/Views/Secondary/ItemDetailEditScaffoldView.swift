import SwiftUI

struct ItemDetailEditScaffoldView: View {
    @Binding var draft: ItemDetailEditDraft
    @State private var replacementCycleInput: String
    @State private var hasAttemptedNameOverflow = false

    private let originalName: String
    let validation: ItemDetailEditValidation
    let recommendedCycleDays: Int?
    let averageCycleDays: Int?
    let existingItemNames: [String]
    let isProcessing: Bool
    let canSubmitOverride: Bool?
    let onClose: () -> Void
    let onSubmit: () -> Void

    init(
        draft: Binding<ItemDetailEditDraft>,
        validation: ItemDetailEditValidation = .valid,
        recommendedCycleDays: Int? = nil,
        averageCycleDays: Int? = nil,
        existingItemNames: [String] = [],
        isProcessing: Bool = false,
        canSubmit: Bool? = nil,
        onClose: @escaping () -> Void,
        onSubmit: @escaping () -> Void
    ) {
        self._draft = draft
        self._replacementCycleInput = State(initialValue: "\(draft.wrappedValue.replacementCycleDays)")
        self.originalName = draft.wrappedValue.name
        self.validation = validation
        self.recommendedCycleDays = recommendedCycleDays
        self.averageCycleDays = averageCycleDays
        self.existingItemNames = existingItemNames
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

            VStack(alignment: .leading, spacing: OBRitSpacing.s2_5) {
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
            let input = ItemDetailEditInputPolicy.replacementCycleInput(from: newValue)
            replacementCycleInput = input

            guard let value = ItemDetailEditInputPolicy.replacementCycleDays(from: input) else { return }
            draft.replacementCycleDays = value
        }
    }

    private var clippedNameText: Binding<String> {
        Binding {
            draft.name
        } set: { newValue in
            let input = ItemDetailEditInputPolicy.clippedName(newValue)
            hasAttemptedNameOverflow = input.didOverflow
            draft.name = input.text
        }
    }

    private var nameHelperMessage: String? {
        validationResult.nameHelperMessage
    }

    private var canSubmit: Bool {
        canSubmitOverride ?? validationResult.canSubmit
    }

    private var validationResult: ItemDetailEditValidationResult {
        ItemDetailEditInputPolicy.validate(
            draft: draft,
            originalName: originalName,
            existingItemNames: existingItemNames,
            replacementCycleInput: replacementCycleInput,
            hasAttemptedNameOverflow: hasAttemptedNameOverflow,
            externalValidation: validation,
            isProcessing: isProcessing
        )
    }

    private func normalizeReplacementCycleInput() {
        guard let value = ItemDetailEditInputPolicy.replacementCycleDays(from: replacementCycleInput) else {
            return
        }

        replacementCycleInput = "\(value)"
        draft.replacementCycleDays = value
    }

    private func submitEdit() {
        normalizeReplacementCycleInput()
        onSubmit()
    }

    private func sectionTitle(_ title: String) -> some View {
        Text(title)
            .lineLimit(1)
            .obritTextStyle(OBRitTypography.s2xl, weight: OBRitFontWeight.bold, color: OBRitColors.textDefaultDefault)
            .frame(maxWidth: .infinity, alignment: .leading)
    }

    private var nameHelperText: some View {
        HStack(alignment: .center, spacing: OBRitSpacing.s1) {
            OBRitIcon(
                kind: nameHelperMessage == nil ? .success : .exclamation,
                color: nameHelperMessage == nil ? OBRitColors.gray300 : OBRitColors.red300
            )
            .frame(width: OBRitSpacing.s4, height: OBRitSpacing.s4)

            Text(nameHelperMessage ?? "다른 이름과 중복되지 않게 입력해주세요")
                .fixedSize(horizontal: false, vertical: true)
                .obritTextStyle(
                    OBRitTypography.s,
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

private enum ItemDetailEditMetrics {
    static let sectionSpacing: CGFloat = 36
    static let bottomButtonHeight: CGFloat = 60
}
