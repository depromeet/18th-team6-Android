import Foundation
import SwiftUI

struct ReceiptAnalyzeDetailInputView: View {
    let result: ReceiptAnalyzeResult
    let isSubmitting: Bool
    let errorMessage: String?
    let onRegister: (ReceiptAnalyzeDetailDraft) -> Void

    @State private var currentItemID: Int?
    @State private var expandedDateItemID: Int?
    @State private var names: [Int: String] = [:]
    @State private var quantities: [Int: Int] = [:]
    @State private var replacementDateOptions: [Int: ItemReplacementDateOption] = [:]
    @State private var snackbar: ReceiptAnalyzeDetailSnackbarPresentation?
    @State private var snackbarDismissTask: Task<Void, Never>?

    var body: some View {
        GeometryReader { geometry in
            ZStack(alignment: .bottom) {
                ScrollView(showsIndicators: false) {
                    VStack(alignment: .leading, spacing: OBRitSpacing.s10) {
                        ReceiptAnalyzeDetailHeader()

                        VStack(spacing: OBRitSpacing.s0) {
                            ReceiptAnalyzeDetailCardPager(
                                items: result.items,
                                currentItemID: currentItemIDBinding,
                                expandedDateItemID: $expandedDateItemID,
                                names: $names,
                                quantities: $quantities,
                                replacementDateOptions: $replacementDateOptions
                            )

                            OBRitPageIndicator(
                                count: result.items.count,
                                selectedIndex: selectedIndex
                            )
                            .opacity(result.items.count > 1 ? 1 : 0)
                        }
                        .frame(maxWidth: .infinity)
                    }
                    .padding(.bottom, ReceiptAnalyzeDetailMetrics.bottomContentPadding)
                }

                ReceiptAnalyzeDetailBottomBar(
                    bottomInset: geometry.safeAreaInsets.bottom,
                    isEnabled: !draft.items.isEmpty && !isSubmitting,
                    isSubmitting: isSubmitting,
                    onRegister: handleRegister
                )

                if let snackbar {
                    VStack {
                        Spacer(minLength: 0)
                        OBRitSnackbar(message: snackbar.message, icon: snackbar.icon)
                            .padding(.horizontal, OBRitSpacing.s5)
                            .padding(
                                .bottom,
                                max(geometry.safeAreaInsets.bottom, OBRitSpacing.s10) +
                                    ReceiptAnalyzeDetailMetrics.snackbarBottomOffset
                            )
                            .transition(.move(edge: .bottom).combined(with: .opacity))
                    }
                    .allowsHitTesting(false)
                    .animation(.easeOut(duration: 0.18), value: snackbar.id)
                }
            }
            .frame(width: geometry.size.width, height: geometry.size.height)
            .background(OBRitColors.backgroundDefaultDefault)
            .ignoresSafeArea(edges: .bottom)
            .onAppear(perform: syncInitialData)
            .onChange(of: result) { _, _ in
                syncInitialData()
            }
            .onChange(of: errorMessage) { _, message in
                if let message {
                    showSnackbar(message, icon: .error)
                }
            }
            .onDisappear {
                snackbarDismissTask?.cancel()
            }
        }
        .ignoresSafeArea(.keyboard, edges: .bottom)
    }

    private var currentItemIDBinding: Binding<Int?> {
        Binding(
            get: {
                currentItemID ?? result.items.first?.id
            },
            set: { newValue in
                currentItemID = newValue
                expandedDateItemID = nil
            }
        )
    }

    private var selectedIndex: Int {
        guard let currentItemID = currentItemIDBinding.wrappedValue,
              let index = result.items.firstIndex(where: { $0.id == currentItemID }) else {
            return 0
        }
        return index
    }

    private var draft: ReceiptAnalyzeDetailDraft {
        ReceiptAnalyzeDetailDraft(
            receiptImageURL: result.receiptImageURL,
            items: result.items.map { item in
                ReceiptAnalyzeDetailDraftItem(
                    id: item.id,
                    name: names[item.id] ?? clippedReceiptItemName(item.suggestedName),
                    quantity: quantities[item.id] ?? max(ItemRegistrationConfig.quantityMinimum, item.quantity),
                    replacementDateOption: replacementDateOptions[item.id],
                    categoryId: item.categoryId,
                    newCategoryName: item.categoryId == nil ? item.suggestedCategoryName : nil,
                    newCategoryDefaultReplacementIntervalDays: item.categoryId == nil ? item.suggestedReplacementIntervalDays : nil
                )
            }
        )
    }

    private func syncInitialData() {
        let itemIDs = Set(result.items.map(\.id))
        names = names.filter { itemIDs.contains($0.key) }
        quantities = quantities.filter { itemIDs.contains($0.key) }
        replacementDateOptions = replacementDateOptions.filter { itemIDs.contains($0.key) }

        for item in result.items {
            if let name = names[item.id] {
                names[item.id] = clippedReceiptItemName(name)
            } else {
                names[item.id] = clippedReceiptItemName(item.suggestedName)
            }
            if quantities[item.id] == nil {
                quantities[item.id] = max(ItemRegistrationConfig.quantityMinimum, item.quantity)
            }
        }

        guard let firstID = result.items.first?.id else {
            currentItemID = nil
            expandedDateItemID = nil
            return
        }

        if let currentItemID,
           itemIDs.contains(currentItemID) {
            return
        }

        currentItemID = firstID
        expandedDateItemID = nil
    }

    private func handleRegister() {
        guard let rejectionMessage = draft.rejectionMessage else {
            onRegister(draft)
            return
        }

        showSnackbar(rejectionMessage, icon: .error)
    }

    private func showSnackbar(
        _ message: String,
        icon: OBRitSnackbarIcon
    ) {
        snackbarDismissTask?.cancel()
        snackbar = ReceiptAnalyzeDetailSnackbarPresentation(
            message: message,
            icon: icon
        )
        snackbarDismissTask = Task { @MainActor in
            try? await Task.sleep(nanoseconds: ReceiptAnalyzeDetailMetrics.snackbarDisplayDurationNanoseconds)
            guard !Task.isCancelled else { return }
            snackbar = nil
        }
    }
}

private struct ReceiptAnalyzeDetailHeader: View {
    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s0) {
            ReceiptAnalyzeStepProgressView(activeStep: 2)
                .padding(.horizontal, OBRitSpacing.s5)
                .padding(.vertical, OBRitSpacing.s4)

            VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
                Text("소모품의 상세 정보를\n입력해주세요")
                    .fixedSize(horizontal: false, vertical: true)
                    .obritTextStyle(OBRitTypography.s6xl, weight: OBRitFontWeight.bold, color: OBRitColors.textDefaultDefault)

                Text("원활한 관리를 위해 구체적인 정보를 입력해주세요")
                    .fixedSize(horizontal: false, vertical: true)
                    .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultSecondary)
            }
            .padding(.horizontal, OBRitSpacing.s5)
            .padding(.vertical, OBRitSpacing.s4)
        }
    }
}

private struct ReceiptAnalyzeStepProgressView: View {
    let activeStep: Int

    var body: some View {
        HStack(spacing: OBRitSpacing.s1) {
            ReceiptAnalyzeStepNumber(number: 1, isActive: activeStep == 1)

            Rectangle()
                .fill(OBRitColors.gray750)
                .frame(width: 28, height: 1)

            ReceiptAnalyzeStepNumber(number: 2, isActive: activeStep == 2)
        }
        .frame(height: 28)
    }
}

private struct ReceiptAnalyzeStepNumber: View {
    let number: Int
    let isActive: Bool

    var body: some View {
        Text("\(number)")
            .frame(width: 28, height: 28)
            .background(isActive ? OBRitColors.common00 : OBRitColors.gray750)
            .clipShape(Circle())
            .obritTextStyle(
                OBRitTypography.base,
                weight: OBRitFontWeight.semiBold,
                color: isActive ? OBRitColors.common1000 : OBRitColors.common00
            )
    }
}

private struct ReceiptAnalyzeDetailCardPager: View {
    let items: [ReceiptAnalyzeResultItem]
    @Binding var currentItemID: Int?
    @Binding var expandedDateItemID: Int?
    @Binding var names: [Int: String]
    @Binding var quantities: [Int: Int]
    @Binding var replacementDateOptions: [Int: ItemReplacementDateOption]

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            LazyHStack(alignment: .top, spacing: OBRitSpacing.s3) {
                ForEach(items) { item in
                    ReceiptAnalyzeDetailCard(
                        item: item,
                        name: binding(
                            dictionary: $names,
                            id: item.id,
                            fallback: clippedReceiptItemName(item.suggestedName)
                        ),
                        quantity: binding(
                            dictionary: $quantities,
                            id: item.id,
                            fallback: max(1, item.quantity)
                        ),
                        replacementDateOption: binding(
                            dictionary: $replacementDateOptions,
                            id: item.id
                        ),
                        isDatePickerExpanded: expandedDateItemID == item.id,
                        onToggleDatePicker: {
                            expandedDateItemID = expandedDateItemID == item.id ? nil : item.id
                        },
                        onSelectDateOption: {
                            replacementDateOptions[item.id] = $0
                            expandedDateItemID = nil
                        }
                    )
                    .id(item.id)
                    .scrollTransition(.interactive, axis: .horizontal) { content, phase in
                        content
                            .opacity(phase.isIdentity ? 1 : 0.3)
                            .scaleEffect(phase.isIdentity ? 1 : 0.9)
                    }
                }
            }
            .scrollTargetLayout()
        }
        .contentMargins(.horizontal, ReceiptAnalyzeDetailMetrics.cardSideMargin, for: .scrollContent)
        .scrollTargetBehavior(.viewAligned)
        .scrollPosition(id: $currentItemID)
        .frame(height: ReceiptAnalyzeDetailMetrics.cardPagerHeight(expanded: expandedDateItemID != nil))
        .animation(.easeOut(duration: 0.18), value: expandedDateItemID)
    }

    private func binding<Value>(
        dictionary: Binding<[Int: Value]>,
        id: Int,
        fallback: Value
    ) -> Binding<Value> {
        Binding {
            dictionary.wrappedValue[id] ?? fallback
        } set: { newValue in
            dictionary.wrappedValue[id] = newValue
        }
    }

    private func binding<Value>(
        dictionary: Binding<[Int: Value]>,
        id: Int
    ) -> Binding<Value?> {
        Binding {
            dictionary.wrappedValue[id]
        } set: { newValue in
            dictionary.wrappedValue[id] = newValue
        }
    }
}

private struct ReceiptAnalyzeDetailCard: View {
    let item: ReceiptAnalyzeResultItem
    @Binding var name: String
    @Binding var quantity: Int
    @Binding var replacementDateOption: ItemReplacementDateOption?
    let isDatePickerExpanded: Bool
    let onToggleDatePicker: () -> Void
    let onSelectDateOption: (ItemReplacementDateOption) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s5) {
            ReceiptAnalyzeRequiredField(title: "소모품 명") {
                ReceiptAnalyzeNameInputField(name: $name)
            }

            ReceiptAnalyzeRequiredField(title: "마지막 교체 일자") {
                ReceiptAnalyzeReplacementDateDropdown(
                    selectedOption: replacementDateOption,
                    isExpanded: isDatePickerExpanded,
                    onToggle: onToggleDatePicker,
                    onSelect: onSelectDateOption
                )
            }
            .zIndex(isDatePickerExpanded ? 1 : 0)

            VStack(alignment: .leading, spacing: OBRitSpacing.s2) {
                Text("등록할 수량")
                    .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.semiBold, color: OBRitColors.common00)

                    OBRitStepper(
                        value: quantity,
                        size: .small,
                        minimumValue: ItemRegistrationConfig.quantityMinimum,
                        maximumValue: ItemRegistrationConfig.quantityMaximum,
                        onDecrement: {
                        quantity = max(ItemRegistrationConfig.quantityMinimum, quantity - 1)
                        },
                        onIncrement: {
                        quantity = min(ItemRegistrationConfig.quantityMaximum, quantity + 1)
                        },
                        onValueChange: { value in
                        quantity = value
                    }
                )

                HStack(alignment: .center, spacing: OBRitSpacing.s1) {
                    OBRitIcon(kind: .info, color: OBRitColors.textDefaultTertiary)
                        .frame(width: OBRitSpacing.s4, height: OBRitSpacing.s4)
                    Text("소모품의 전체 수량은 추후에도 등록할 수 있어요.")
                        .fixedSize(horizontal: false, vertical: true)
                        .obritTextStyle(OBRitTypography.s, weight: OBRitFontWeight.semiBold, color: OBRitColors.textDefaultTertiary)
                }
            }
        }
        .padding(OBRitSpacing.s5)
        .frame(width: ReceiptAnalyzeDetailMetrics.cardWidth, alignment: .leading)
        .background {
            RoundedRectangle(cornerRadius: OBRitRadius.extraLarge)
                .fill(OBRitColors.backgroundDefaultSecondary)
        }
        .accessibilityElement(children: .contain)
        .accessibilityLabel("\(item.suggestedName) 상세 정보 입력")
    }
}

private struct ReceiptAnalyzeRequiredField<Content: View>: View {
    let title: String
    let content: Content

    init(
        title: String,
        @ViewBuilder content: () -> Content
    ) {
        self.title = title
        self.content = content()
    }

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s2) {
            HStack(alignment: .top, spacing: OBRitSpacing.s0_5) {
                Text(title)
                    .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.semiBold, color: OBRitColors.common00)
                Text("*")
                    .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.bold, color: OBRitColors.textWarningDefault)
                    .padding(.top, 2)
            }

            content
        }
    }
}

private struct ReceiptAnalyzeNameInputField: View {
    @Binding var name: String

    var body: some View {
        HStack(spacing: OBRitSpacing.s2) {
            TextField("", text: limitedName)
                .lineLimit(1)
                .textInputAutocapitalization(.never)
                .autocorrectionDisabled()
                .tint(OBRitColors.common00)
                .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.medium, color: OBRitColors.common00)

            Text("\(name.count)/\(ItemRegistrationConfig.itemNameMaxLength)")
                .obritTextStyle(OBRitTypography.s, weight: OBRitFontWeight.medium, color: OBRitColors.common00)
        }
        .frame(height: OBRitSpacing.s14)
        .padding(.horizontal, OBRitSpacing.s5)
        .background(OBRitColors.gray800)
        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
    }

    private var limitedName: Binding<String> {
        Binding {
            name
        } set: { newValue in
            name = clippedReceiptItemName(newValue)
        }
    }
}

private struct ReceiptAnalyzeReplacementDateDropdown: View {
    let selectedOption: ItemReplacementDateOption?
    let isExpanded: Bool
    let onToggle: () -> Void
    let onSelect: (ItemReplacementDateOption) -> Void

    var body: some View {
        ZStack(alignment: .topLeading) {
            OBRitDropdown(
                value: selectedOption?.title ?? "",
                placeholder: "마지막 교체 일자를 등록해주세요",
                expanded: isExpanded,
                onClick: onToggle
            )

            if isExpanded {
                OBRitDropdownMenu(
                    items: ItemReplacementDateOption.allCases.map(\.title),
                    selectedIndex: selectedIndex,
                    fillsWidth: true,
                    onItemClick: select
                )
                .offset(y: OBRitSpacing.s14 + OBRitSpacing.s2)
                .zIndex(1)
            }
        }
        .frame(height: OBRitSpacing.s14, alignment: .top)
    }

    private var selectedIndex: Int? {
        guard let selectedOption else { return nil }
        return ItemReplacementDateOption.allCases.firstIndex(of: selectedOption)
    }

    private func select(_ index: Int) {
        guard ItemReplacementDateOption.allCases.indices.contains(index) else { return }
        onSelect(ItemReplacementDateOption.allCases[index])
    }
}

private struct ReceiptAnalyzeDetailBottomBar: View {
    let bottomInset: CGFloat
    let isEnabled: Bool
    let isSubmitting: Bool
    let onRegister: () -> Void

    var body: some View {
        VStack(spacing: OBRitSpacing.s2) {
            Button(action: onRegister) {
                Text(isSubmitting ? "등록 중..." : "소모품 등록하기")
                    .frame(maxWidth: .infinity)
                    .frame(height: OBRitSpacing.s14)
                    .background(buttonBackground)
                    .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.large))
                    .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.semiBold, color: buttonTextColor)
            }
            .buttonStyle(.plain)
            .disabled(!isEnabled)
        }
        .padding(.horizontal, OBRitSpacing.s5)
        .padding(.bottom, max(bottomInset, OBRitSpacing.s10))
        .background(OBRitColors.backgroundDefaultDefault)
    }

    private var buttonBackground: Color {
        isEnabled ? OBRitColors.backgroundPositiveDefault : OBRitColors.backgroundDisabledDefault
    }

    private var buttonTextColor: Color {
        isEnabled ? OBRitColors.common1000 : OBRitColors.textDisabledOnDisabled
    }
}

private enum ReceiptAnalyzeDetailMetrics {
    static let cardWidth: CGFloat = 340
    static let cardSideMargin: CGFloat = 36
    static let bottomContentPadding: CGFloat = 160
    static let snackbarBottomOffset: CGFloat = OBRitSpacing.s20 + OBRitSpacing.s5
    static let snackbarDisplayDurationNanoseconds: UInt64 = 2_400_000_000

    static func cardPagerHeight(expanded: Bool) -> CGFloat {
        expanded ? 560 : 400
    }
}

private struct ReceiptAnalyzeDetailSnackbarPresentation: Identifiable {
    let id = UUID()
    let message: String
    let icon: OBRitSnackbarIcon
}

private func clippedReceiptItemName(_ name: String) -> String {
    String(name.prefix(ItemRegistrationConfig.itemNameMaxLength))
}

#Preview("Receipt Analyze Detail Input") {
    ReceiptAnalyzeDetailInputView(
        result: ReceiptAnalyzeResult(
            receiptImageURL: "",
            purchasedDate: "2026-01-01",
            items: [
                ReceiptAnalyzeResultItem(
                    id: 0,
                    suggestedName: "면도기",
                    suggestedCategoryName: "면도기",
                    categoryId: 1,
                    iconURL: "",
                    quantity: 1,
                    suggestedReplacementIntervalDays: 30
                ),
                ReceiptAnalyzeResultItem(
                    id: 1,
                    suggestedName: "정수기 필터",
                    suggestedCategoryName: "정수기 필터",
                    categoryId: 2,
                    iconURL: "",
                    quantity: 1,
                    suggestedReplacementIntervalDays: 90
                ),
                ReceiptAnalyzeResultItem(
                    id: 2,
                    suggestedName: "칫솔",
                    suggestedCategoryName: "칫솔",
                    categoryId: 3,
                    iconURL: "",
                    quantity: 1,
                    suggestedReplacementIntervalDays: 30
                )
            ]
        ),
        isSubmitting: false,
        errorMessage: nil,
        onRegister: { _ in }
    )
}
