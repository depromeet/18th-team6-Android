import SwiftUI

struct ItemKindSelectionBottomSheet: View {
    private let kinds: [ItemKind]
    private let selectedKind: ItemKind?
    private let selectedKindCandidate: ItemKind?
    private let query: String
    let contentHeight: CGFloat
    let bottomPadding: CGFloat
    private let onDismiss: () -> Void
    private let onUpdateQuery: (String) -> Void
    private let onSelectCandidate: (ItemKind) -> Void
    private let onConfirmSelection: () -> Void
    private let onDirectRegister: (() -> Void)?

    init(
        data: ItemRegistrationViewData,
        action: ItemRegistrationAction,
        contentHeight: CGFloat,
        bottomPadding: CGFloat
    ) {
        self.init(
            kinds: data.itemKinds,
            selectedKind: data.draft.selectedKind,
            selectedKindCandidate: data.selectedKindCandidate,
            query: data.kindSearchQuery,
            contentHeight: contentHeight,
            bottomPadding: bottomPadding,
            onDismiss: action.onDismissBottomSheet,
            onUpdateQuery: action.onUpdateKindSearchQuery,
            onSelectCandidate: action.onSelectKindCandidate,
            onConfirmSelection: action.onConfirmKindSelection,
            onDirectRegister: action.onShowDirectKindRegistration
        )
    }

    init(
        kinds: [ItemKind],
        selectedKind: ItemKind?,
        selectedKindCandidate: ItemKind?,
        query: String,
        contentHeight: CGFloat,
        bottomPadding: CGFloat,
        onDismiss: @escaping () -> Void,
        onUpdateQuery: @escaping (String) -> Void,
        onSelectCandidate: @escaping (ItemKind) -> Void,
        onConfirmSelection: @escaping () -> Void,
        onDirectRegister: (() -> Void)? = nil
    ) {
        self.kinds = kinds
        self.selectedKind = selectedKind
        self.selectedKindCandidate = selectedKindCandidate
        self.query = query
        self.contentHeight = contentHeight
        self.bottomPadding = bottomPadding
        self.onDismiss = onDismiss
        self.onUpdateQuery = onUpdateQuery
        self.onSelectCandidate = onSelectCandidate
        self.onConfirmSelection = onConfirmSelection
        self.onDirectRegister = onDirectRegister
    }

    var body: some View {
        let displayKinds = filteredKinds

        OBRitBottomSheet(
            contentHeight: contentHeight,
            bottomPadding: bottomPadding,
            onDismiss: onDismiss
        ) {
            VStack(alignment: .leading, spacing: OBRitSpacing.s8) {
                ItemSearchInputField(
                    text: query,
                    placeholder: "원하시는 소모품을 검색해보세요",
                    onTextChange: onUpdateQuery
                )

                VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
                    ItemSheetCountText(
                        prefix: query.isEmpty ? "전체 소모품" : "검색 결과",
                        count: query.isEmpty ? kinds.count : displayKinds.count
                    )

                    if displayKinds.isEmpty {
                        ItemKindNoResultView(onDirectRegister: onDirectRegister)
                    } else {
                        ScrollView(showsIndicators: false) {
                            LazyVStack(spacing: OBRitSpacing.s2) {
                                ForEach(displayKinds) { kind in
                                    ItemKindSelectionRow(
                                        kind: kind,
                                        selected: kind == kindCandidateForDisplay,
                                        onSelect: { onSelectCandidate(kind) }
                                    )
                                }
                            }
                            .padding(.bottom, ItemRegistrationLayout.sheetBottomButtonReservedHeight + OBRitSpacing.s6)
                        }
                        .overlay(alignment: .bottom) {
                            ItemSheetGradientButton(
                                text: "소모품 종류 선택하기",
                                enabled: selectedKindCandidate != nil,
                                action: onConfirmSelection
                            )
                        }
                    }
                }
            }
        }
    }

    private var filteredKinds: [ItemKind] {
        let normalizedQuery = query.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !normalizedQuery.isEmpty else { return kinds }
        return kinds.filter {
            $0.title.localizedCaseInsensitiveContains(normalizedQuery)
        }
    }

    private var kindCandidateForDisplay: ItemKind? {
        selectedKindCandidate ?? selectedKind
    }
}

private struct ItemSheetCountText: View {
    let prefix: String
    let count: Int

    var body: some View {
        (
            Text(prefix + ItemRegistrationText.space)
                .foregroundColor(OBRitColors.common00) +
                Text(ItemRegistrationText.countText(count))
                .foregroundColor(OBRitColors.green300)
        )
        .font(OBRitTypography.font(OBRitTypography.s3xl, weight: OBRitFontWeight.bold))
        .tracking(OBRitTypography.letterSpacing(for: OBRitTypography.s3xl))
    }
}

private struct ItemKindSelectionRow: View {
    let kind: ItemKind
    let selected: Bool
    let onSelect: () -> Void

    var body: some View {
        Button(action: onSelect) {
            HStack(spacing: OBRitSpacing.s4) {
                ItemImage(
                    imageURL: kind.imageURL,
                    size: ItemRegistrationLayout.itemThumbnailSize
                )

                VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
                    Text(kind.title)
                        .lineLimit(ItemRegistrationLayout.singleLineLimit)
                        .obritTextStyle(
                            OBRitTypography.xl,
                            weight: OBRitFontWeight.bold,
                            color: OBRitColors.common00
                        )
                    Text(ItemRegistrationText.addedCountText(kind.addedCount))
                        .lineLimit(ItemRegistrationLayout.singleLineLimit)
                        .obritTextStyle(
                            OBRitTypography.small,
                            weight: OBRitFontWeight.medium,
                            color: OBRitColors.common00.opacity(ItemRegistrationLayout.secondaryTextOpacity)
                        )
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                ItemKindSelectionRadioIndicator(selected: selected)
                    .frame(width: OBRitSpacing.s10, height: OBRitSpacing.s10)
            }
            .padding(.horizontal, OBRitSpacing.s5)
            .padding(.vertical, OBRitSpacing.s4)
            .background(OBRitColors.backgroundDefaultSecondary)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
        }
        .buttonStyle(.plain)
    }
}

private struct ItemKindSelectionRadioIndicator: View {
    let selected: Bool

    var body: some View {
        ZStack {
            Circle()
                .stroke(ringColor, lineWidth: ItemRegistrationLayout.radioBorderWidth)
                .frame(width: ItemRegistrationLayout.radioOuterSize, height: ItemRegistrationLayout.radioOuterSize)
            if selected {
                Circle()
                    .fill(ringColor)
                    .frame(width: OBRitSpacing.s3, height: OBRitSpacing.s3)
            }
        }
        .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
        .accessibilityHidden(true)
    }

    private var ringColor: Color {
        selected ? OBRitColors.green300 : OBRitColors.gray400
    }
}

private struct ItemKindNoResultView: View {
    let onDirectRegister: (() -> Void)?

    var body: some View {
        VStack(spacing: OBRitSpacing.s5) {
            VStack(spacing: OBRitSpacing.s2) {
                Text("소모품 검색 결과가 없어요!")
                    .obritTextStyle(
                        OBRitTypography.s3xl,
                        weight: OBRitFontWeight.bold,
                        color: OBRitColors.common00
                    )
                Text("직접 소모품 종류를 등록할 수 있어요.")
                    .obritTextStyle(
                        OBRitTypography.base,
                        weight: OBRitFontWeight.medium,
                        color: OBRitColors.textDefaultSecondary
                    )
            }

            if let onDirectRegister {
                OBRitFilledTextButton(
                    text: "직접 등록",
                    size: .small,
                    color: .white,
                    action: onDirectRegister
                )
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: ItemRegistrationLayout.kindNoResultHeight)

        ItemSheetGradientButton(text: "소모품 종류 선택하기", enabled: false, action: {})
    }
}

private struct ItemSheetGradientButton: View {
    let text: String
    let enabled: Bool
    let action: () -> Void

    init(text: String, enabled: Bool = true, action: @escaping () -> Void) {
        self.text = text
        self.enabled = enabled
        self.action = action
    }

    var body: some View {
        VStack(spacing: ItemRegistrationLayout.zeroSpacing) {
            LinearGradient(
                colors: [
                    OBRitColors.backgroundDefaultDefault.opacity(ItemRegistrationLayout.transparentOpacity),
                    OBRitColors.backgroundDefaultDefault
                ],
                startPoint: .top,
                endPoint: .bottom
            )
            .frame(height: OBRitSpacing.s5)

            OBRitFilledTextButton(
                text: text,
                color: .green,
                enabled: enabled,
                fillsWidth: true,
                action: action
            )
        }
        .background(OBRitColors.backgroundDefaultDefault)
        .disabled(!enabled)
    }
}

struct ManualKindSelectionBottomSheet: View {
    let data: ItemRegistrationViewData
    let action: ItemRegistrationAction
    let contentHeight: CGFloat
    let bottomPadding: CGFloat

    @State private var activeGroupId: Int?
    @State private var pinnedGroupId: Int?

    private var isSearching: Bool { data.submittedKindSearchQuery != nil }

    var body: some View {
        OBRitBottomSheet(contentHeight: contentHeight, bottomPadding: bottomPadding, onDismiss: action.onDismissBottomSheet) {
            VStack(spacing: OBRitSpacing.s4) {
                VStack(spacing: OBRitSpacing.s2) {
                    ItemSearchInputField(text: data.kindSearchQuery, placeholder: "원하시는 소모품을 검색해보세요", onTextChange: action.onUpdateKindSearchQuery, onSearch: { action.onSubmitKindSearch(nil) })
                    if !data.kindSearchSuggestions.isEmpty {
                        RegistrationSearchSuggestions(query: data.kindSearchQuery, suggestions: data.kindSearchSuggestions) { action.onSubmitKindSearch($0) }
                    }
                }
                HStack(alignment: .top, spacing: OBRitSpacing.s4) {
                    sidebar
                    ScrollViewReader { proxy in
                        ScrollView(showsIndicators: false) {
                            LazyVStack(alignment: .leading, spacing: OBRitSpacing.s6, pinnedViews: [.sectionHeaders]) {
                                if isSearching {
                                    Section {
                                        if data.hasNoSearchResults {
                                            RegistrationNoSearchResults(enabled: !data.isProcessing, onDirectRegister: action.onCreateSearchItem)
                                        } else { kindGrid(data.filteredKinds) }
                                    } header: { sectionTitle("검색 결과", count: data.filteredKinds.count) }
                                } else {
                                    ForEach(data.visibleGroups) { group in
                                        Section {
                                            kindGrid(kinds(in: group))
                                        } header: {
                                            sectionHeader(group)
                                                .id(group.id)
                                                .background {
                                                    GeometryReader { geometry in
                                                        Color.clear.preference(key: RegistrationGroupOffsets.self, value: [group.id: geometry.frame(in: .named("kindList")).minY])
                                                    }
                                                }
                                        }
                                    }
                                }
                            }
                            .padding(.bottom, OBRitSpacing.s6)
                        }
                        .coordinateSpace(name: "kindList")
                        .simultaneousGesture(DragGesture().onChanged { _ in pinnedGroupId = nil })
                        .onPreferenceChange(RegistrationGroupOffsets.self) { offsets in
                            guard !isSearching, pinnedGroupId == nil else { return }
                            let visible = data.visibleGroups.last { (offsets[$0.id] ?? .infinity) <= 1 }
                                ?? data.visibleGroups.first { offsets[$0.id] != nil }
                            if let visible { activeGroupId = visible.id }
                        }
                        .onChange(of: pinnedGroupId) { _, groupId in
                            guard let groupId else { return }
                            withAnimation(.easeInOut(duration: 0.25)) { proxy.scrollTo(groupId, anchor: .top) }
                        }
                        .onChange(of: isSearching) { _, searching in
                            if !searching, let id = activeGroupId { proxy.scrollTo(id, anchor: .top) }
                        }
                        .onAppear {
                            let id = data.draft.selectedKind?.categoryGroupId
                                ?? data.visibleGroups.first { $0.name == "욕실" }?.id
                                ?? data.visibleGroups.first?.id
                            activeGroupId = id
                            pinnedGroupId = id
                            if let id { proxy.scrollTo(id, anchor: .top) }
                        }
                    }
                }
                .frame(maxHeight: .infinity)
                OBRitFilledTextButton(text: "선택하기", enabled: data.selectedKindCandidate != nil && !data.isProcessing, fillsWidth: true, action: action.onConfirmKindSelection)
            }
            .disabled(data.isProcessing)
        }
    }

    private var sidebar: some View {
        ScrollViewReader { proxy in
            ScrollView(showsIndicators: false) {
                VStack(spacing: OBRitSpacing.s1) {
                    ForEach(data.visibleGroups) { group in
                        let selected = !isSearching && (pinnedGroupId ?? activeGroupId) == group.id
                        Button {
                            action.onClearKindSearch()
                            activeGroupId = group.id
                            // Retain the explicitly selected chip when the last section cannot reach the top.
                            if pinnedGroupId == group.id { pinnedGroupId = nil }
                            pinnedGroupId = group.id
                        } label: {
                            Text(group.name)
                                .obritTextStyle(OBRitTypography.xl, weight: selected ? OBRitFontWeight.semiBold : OBRitFontWeight.medium, color: selected ? OBRitColors.common00 : OBRitColors.gray500)
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .padding(.horizontal, OBRitSpacing.s3)
                                .padding(.vertical, OBRitSpacing.s2_5)
                                .background(selected ? OBRitColors.gray800 : .clear, in: RoundedRectangle(cornerRadius: OBRitRadius.small))
                        }
                        .buttonStyle(.plain)
                        .id(group.id)
                        .accessibilityAddTraits(selected ? .isSelected : [])
                    }
                }
                .padding(.trailing, OBRitSpacing.s3)
            }
            .onChange(of: activeGroupId) { _, id in
                if let id { withAnimation { proxy.scrollTo(id) } }
            }
        }
        .frame(width: 104)
        .overlay(alignment: .trailing) { Rectangle().fill(OBRitColors.gray850).frame(width: 1) }
    }

    private func kinds(in group: ItemKindGroup) -> [ItemKind] {
        data.itemKinds.filter { $0.categoryGroupId == group.id }
    }

    private func sectionTitle(_ title: String, count: Int) -> some View {
        HStack(spacing: OBRitSpacing.s1) {
            Text(title).foregroundStyle(OBRitColors.common00)
            Text("\(count)").foregroundStyle(OBRitColors.green300)
        }
        .font(OBRitTypography.font(OBRitTypography.s3xl, weight: OBRitFontWeight.semiBold))
    }

    private func sectionHeader(_ group: ItemKindGroup) -> some View {
        HStack {
            sectionTitle(group.name, count: kinds(in: group).count)
            if group.isUserDefined {
                Spacer(minLength: 0)
                Button("추가하기 +", action: action.onShowDirectKindRegistration)
                    .buttonStyle(.plain)
                    .obritTextStyle(OBRitTypography.small, color: OBRitColors.gray300)
            }
        }
        .padding(.vertical, OBRitSpacing.s2)
        .background(OBRitColors.backgroundDefaultDefault)
    }

    private func kindGrid(_ kinds: [ItemKind]) -> some View {
        LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: OBRitSpacing.s2), count: 3), spacing: OBRitSpacing.s6) {
            ForEach(kinds) { kind in
                let selected = data.selectedKindCandidate?.id == kind.id
                Button { action.onSelectKindCandidate(kind) } label: {
                    VStack(spacing: OBRitSpacing.s2) {
                        ItemImage(imageURL: kind.imageURL, size: 64)
                            .background(OBRitColors.gray750, in: Circle())
                            .overlay { Circle().stroke(selected ? OBRitColors.green300 : .clear, lineWidth: 2) }
                        Text(kind.title)
                            .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.common00)
                            .multilineTextAlignment(.center)
                    }
                    .frame(maxWidth: .infinity)
                }
                .buttonStyle(.plain)
                .accessibilityAddTraits(selected ? .isSelected : [])
            }
        }
    }
}

private struct RegistrationGroupOffsets: PreferenceKey {
    static let defaultValue: [Int: CGFloat] = [:]
    static func reduce(value: inout [Int: CGFloat], nextValue: () -> [Int: CGFloat]) {
        value.merge(nextValue(), uniquingKeysWith: { _, new in new })
    }
}
