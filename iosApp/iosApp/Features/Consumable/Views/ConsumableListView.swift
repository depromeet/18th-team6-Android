import SwiftUI

struct ConsumableListTabView: View {
    let onNavigate: (ConsumableRoute) -> Void
    let onNavigateMyPage: (MyPageRoute) -> Void

    init(
        onNavigate: @escaping (ConsumableRoute) -> Void,
        onNavigateMyPage: @escaping (MyPageRoute) -> Void = { _ in }
    ) {
        self.onNavigate = onNavigate
        self.onNavigateMyPage = onNavigateMyPage
    }

    var body: some View {
        ConsumableListContentView(
            chrome: .mainTab,
            action: ConsumableListViewAction(
                onSearch: { onNavigate(.search) },
                onFilter: { onNavigate(.filter) },
                onSort: { onNavigate(.sort) },
                onRegister: { onNavigate(.registrationMethod) },
                onShowList: { onNavigate(.list) },
                onSelectConsumable: { onNavigate(.detail(consumableId: $0)) },
                onProfile: { onNavigateMyPage(.profile) },
                onBack: {}
            )
        )
    }
}

struct ConsumableListView: View {
    @Environment(\.dismiss) private var dismiss

    let onNavigate: (ConsumableRoute) -> Void

    var body: some View {
        ConsumableListContentView(
            chrome: .pushedList,
            action: ConsumableListViewAction(
                onSearch: { onNavigate(.search) },
                onFilter: { onNavigate(.filter) },
                onSort: { onNavigate(.sort) },
                onRegister: { onNavigate(.registrationMethod) },
                onShowList: {},
                onSelectConsumable: { onNavigate(.detail(consumableId: $0)) },
                onProfile: {},
                onBack: { dismiss() }
            )
        )
    }
}

private struct ConsumableListContentView: View {
    let chrome: ConsumableListChrome
    let action: ConsumableListViewAction

    private let summary = ConsumableListSampleData.summary
    private let quickItems = ConsumableListSampleData.quickItems
    private let usageItems = ConsumableListSampleData.usageItems

    var body: some View {
        ZStack {
            OBRitColors.backgroundDefaultDefault
                .ignoresSafeArea()

            ScrollView(showsIndicators: false) {
                VStack(spacing: 0) {
                    ConsumableListStatusSection(summary: summary)
                    ConsumableListOrbSection(summary: summary, items: usageItems)
                    ConsumableListSummarySection(summary: summary)
                    ConsumableListQuickSection(
                        filters: ConsumableListSampleData.filters,
                        selectedFilter: .replacementRequired,
                        items: quickItems,
                        action: action
                    )
                    ConsumableListPreviewSection(
                        items: quickItems,
                        selectedSort: "교체 임박 순",
                        action: action
                    )
                    ConsumableListUsageSection(items: usageItems, onSelect: action.onSelectConsumable)
                }
                .padding(.bottom, chrome.scrollBottomPadding)
            }
            .padding(.top, ConsumableListChromeMetrics.topContentInset)
            .ignoresSafeArea(edges: .top)

            VStack(spacing: 0) {
                Color.clear.frame(height: ConsumableListChromeMetrics.statusBarHeight)
                ConsumableListTopBar(chrome: chrome, action: action)
                Spacer(minLength: 0)
            }
            .ignoresSafeArea(edges: .top)

            VStack {
                Spacer(minLength: 0)
                HStack {
                    Spacer(minLength: 0)
                    ConsumableListRegisterButton(action: action.onRegister)
                        .padding(.trailing, OBRitSpacing.s5)
                        .padding(.bottom, chrome.floatingButtonBottomPadding)
                }
            }
        }
        .toolbar(.hidden, for: .navigationBar)
        .navigationBarBackButtonHidden(true)
        .background(OBRitColors.backgroundDefaultDefault)
    }
}

private struct ConsumableListViewAction {
    let onSearch: () -> Void
    let onFilter: () -> Void
    let onSort: () -> Void
    let onRegister: () -> Void
    let onShowList: () -> Void
    let onSelectConsumable: (Int) -> Void
    let onProfile: () -> Void
    let onBack: () -> Void
}

private enum ConsumableListChrome {
    case mainTab
    case pushedList

    var scrollBottomPadding: CGFloat {
        switch self {
        case .mainTab:
            return OBRitSpacing.s36
        case .pushedList:
            return OBRitSpacing.s24
        }
    }

    var floatingButtonBottomPadding: CGFloat {
        switch self {
        case .mainTab:
            return OBRitSpacing.s2_5
        case .pushedList:
            return OBRitSpacing.s5
        }
    }
}

private enum ConsumableListChromeMetrics {
    static let statusBarHeight: CGFloat = 52
    static let topContentInset: CGFloat = 105
}

private struct ConsumableListTopBar: View {
    let chrome: ConsumableListChrome
    let action: ConsumableListViewAction

    var body: some View {
        ZStack {
            switch chrome {
            case .mainTab:
                HStack {
                    ConsumableListLogo()
                    Spacer()
                    HStack(spacing: 0) {
                        ConsumableListTopBarIcon(
                            symbolName: "magnifyingglass",
                            accessibilityLabel: "검색",
                            action: action.onSearch
                        )
                        ConsumableListTopBarIcon(
                            symbolName: "person",
                            accessibilityLabel: "프로필",
                            action: action.onProfile
                        )
                    }
                }
                .padding(.leading, OBRitSpacing.s5)
                .padding(.trailing, OBRitSpacing.s3)
            case .pushedList:
                ZStack {
                    HStack {
                        ConsumableListTopBarIcon(
                            symbolName: "chevron.left",
                            accessibilityLabel: "뒤로",
                            action: action.onBack
                        )
                        Spacer()
                        ConsumableListTopBarIcon(
                            symbolName: "magnifyingglass",
                            accessibilityLabel: "검색",
                            action: action.onSearch
                        )
                    }
                    .padding(.horizontal, OBRitSpacing.s3)

                    Text("전체 소모품 목록")
                        .lineLimit(1)
                        .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                }
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: OBRitSpacing.s14)
    }
}

private struct ConsumableListLogo: View {
    var body: some View {
        Text("OBRit")
            .font(.custom("Pretendard-Black", size: 24))
            .tracking(-0.9)
            .foregroundStyle(Color(red: 240 / 255, green: 253 / 255, blue: 251 / 255))
            .fixedSize()
            .scaleEffect(x: 1.26, y: 1, anchor: .leading)
            .frame(width: 80, height: 25, alignment: .leading)
            .lineLimit(1)
            .accessibilityLabel("OBRit")
    }
}

private struct ConsumableListTopBarIcon: View {
    let symbolName: String
    let accessibilityLabel: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: symbolName)
                .font(.system(size: OBRitSpacing.s5, weight: .regular))
                .foregroundStyle(OBRitColors.common00)
                .frame(width: OBRitSpacing.s10, height: OBRitSpacing.s10)
                .contentShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
        }
        .buttonStyle(.plain)
        .accessibilityLabel(accessibilityLabel)
    }
}

private struct ConsumableListStatusSection: View {
    let summary: ConsumableListSummary

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
            VStack(alignment: .leading, spacing: 0) {
                Text("오늘의 소모품 관리")
                    .obritTextStyle(OBRitTypography.s5xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                HStack(spacing: 0) {
                    Text("상태는 ")
                        .obritTextStyle(OBRitTypography.s5xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                    Text(summary.status)
                        .obritTextStyle(OBRitTypography.s5xl, weight: OBRitFontWeight.bold, color: OBRitColors.textPositiveDefault)
                    Text("해요")
                        .obritTextStyle(OBRitTypography.s5xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                }
            }

            HStack(spacing: OBRitSpacing.s3) {
                ConsumableListStatusPair(label: "교체 관리", value: summary.replacementStatus)
                ConsumableListStatusPair(label: "여분 관리", value: summary.spareStatus)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(OBRitSpacing.s5)
        .frame(height: 139, alignment: .topLeading)
    }
}

private struct ConsumableListStatusPair: View {
    let label: String
    let value: String

    var body: some View {
        HStack(spacing: OBRitSpacing.s1) {
            Text(label)
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultSecondary)
            Text(value)
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
        }
    }
}

private struct ConsumableListOrbSection: View {
    let summary: ConsumableListSummary
    let items: [ConsumableListItem]

    var body: some View {
        HStack(alignment: .top, spacing: OBRitSpacing.s2) {
            ConsumableListRatioLabel(
                value: summary.positiveRatio,
                label: "양호",
                color: OBRitColors.textPositiveDefault,
                alignment: .trailing
            )
            .padding(.top, OBRitSpacing.s8)
            .offset(x: OBRitSpacing.s2)

            HomeGlassBall(
                normalRatio: Double(summary.positiveRatio) / 100,
                warningRatio: Double(summary.warningRatio) / 100,
                interiorItems: items.map(\.orbInteriorItem)
            )
            .frame(width: HomeOrbMetrics.outerDiameter, height: HomeOrbMetrics.outerDiameter)

            ConsumableListRatioLabel(
                value: summary.warningRatio,
                label: "경고",
                color: OBRitColors.textWarningDefault,
                alignment: .leading
            )
            .padding(.top, OBRitSpacing.s8)
            .offset(x: -OBRitSpacing.s2)
        }
        .frame(maxWidth: .infinity)
        .frame(height: 256, alignment: .top)
    }
}

private struct ConsumableListRatioLabel: View {
    let value: Int
    let label: String
    let color: Color
    let alignment: HorizontalAlignment

    var body: some View {
        VStack(alignment: alignment, spacing: OBRitSpacing.s1) {
            Text("\(value)%")
                .obritTextStyle(OBRitTypography.s3xl, weight: OBRitFontWeight.bold, color: color)
            Text(label)
                .obritTextStyle(OBRitTypography.xl, color: color)
        }
        .frame(width: OBRitSpacing.s14, alignment: alignment == .leading ? .leading : .trailing)
    }
}

private struct ConsumableListSummarySection: View {
    let summary: ConsumableListSummary

    var body: some View {
        HStack(spacing: OBRitSpacing.s6) {
            VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
                ConsumableListMetricRow(title: "내 소모품", value: "\(summary.totalCount)", color: OBRitColors.common00)
                ConsumableListMetricRow(title: "교체 필요", value: "\(summary.replacementRequiredCount)", color: OBRitColors.textWarningDefault)
            }
            .frame(width: OBRitSpacing.s20, alignment: .leading)

            VStack(spacing: OBRitSpacing.s2) {
                Text("내 상태")
                    .obritTextStyle(OBRitTypography.small, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                ZStack(alignment: .leading) {
                    ConsumableListStatusMeter()
                    Capsule()
                        .fill(OBRitColors.common00)
                        .frame(width: OBRitSpacing.s1, height: OBRitSpacing.s7)
                        .offset(x: OBRitSpacing.s24 + OBRitSpacing.s4)
                }
                Text("평균")
                    .obritTextStyle(OBRitTypography.small, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                    .frame(maxWidth: .infinity, alignment: .center)
            }
            .frame(maxWidth: .infinity)
        }
        .padding(.horizontal, OBRitSpacing.s6)
        .padding(.vertical, OBRitSpacing.s6)
        .frame(maxWidth: .infinity)
        .frame(minHeight: OBRitSpacing.s24)
        .background(Color.black)
        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
        .padding(.horizontal, OBRitSpacing.s5)
        .padding(.bottom, OBRitSpacing.s6)
    }
}

private struct ConsumableListMetricRow: View {
    let title: String
    let value: String
    let color: Color

    var body: some View {
        HStack(spacing: OBRitSpacing.s2) {
            Text(title)
                .lineLimit(1)
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: color)
            Text(value)
                .lineLimit(1)
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.bold, color: color)
        }
    }
}

private struct ConsumableListStatusMeter: View {
    private let tickCount = 44

    var body: some View {
        HStack(spacing: OBRitSpacing.px) {
            ForEach(0..<tickCount, id: \.self) { index in
                Capsule()
                    .fill(color(at: index))
                    .frame(width: OBRitSpacing.s1, height: OBRitSpacing.s4 + OBRitSpacing.s0_5)
            }
        }
        .frame(maxWidth: .infinity, alignment: .center)
    }

    private func color(at index: Int) -> Color {
        let progress = Double(index) / Double(tickCount - 1)
        return Color(
            red: 1.0 * (1 - progress) + (38 / 255) * progress,
            green: (89 / 255) * (1 - progress) + (239 / 255) * progress,
            blue: (34 / 255) * (1 - progress) + (205 / 255) * progress
        )
    }
}

private struct ConsumableListQuickSection: View {
    let filters: [ConsumableListFilter]
    let selectedFilter: ConsumableListFilter.ID
    let items: [ConsumableListItem]
    let action: ConsumableListViewAction

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s4) {
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: OBRitSpacing.s2) {
                    ForEach(filters) { filter in
                        OBRitChip(
                            text: filter.title,
                            selected: filter.id == selectedFilter,
                            number: filter.count,
                            onClick: action.onFilter
                        )
                    }
                }
                .padding(.horizontal, OBRitSpacing.s5)
            }

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: OBRitSpacing.s3) {
                    ForEach(items) { item in
                        Button {
                            action.onSelectConsumable(item.id)
                        } label: {
                            OBRitCardGrid(
                                level: item.cardLevel,
                                title: item.title,
                                stockCount: item.stockCount,
                                daysLabel: item.daysLabel
                            ) {
                                ConsumableListAssetImage(assetName: item.assetName)
                                    .padding(OBRitSpacing.s3)
                            }
                        }
                        .buttonStyle(.plain)
                    }
                }
                .padding(.horizontal, OBRitSpacing.s5)
            }
        }
        .padding(.top, OBRitSpacing.s1)
        .padding(.bottom, OBRitSpacing.s6)
    }
}

private struct ConsumableListPreviewSection: View {
    let items: [ConsumableListItem]
    let selectedSort: String
    let action: ConsumableListViewAction

    var body: some View {
        VStack(spacing: OBRitSpacing.s3) {
            HStack(spacing: OBRitSpacing.s3) {
                Button(action: action.onSort) {
                    HStack(spacing: OBRitSpacing.s1) {
                        Text(selectedSort)
                        Image(systemName: "chevron.down")
                            .font(.system(size: OBRitSpacing.s2 + OBRitSpacing.s0_5, weight: .bold))
                    }
                    .obritTextStyle(OBRitTypography.xs, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                    .padding(.horizontal, OBRitSpacing.s3)
                    .padding(.vertical, OBRitSpacing.s2)
                    .background(OBRitColors.backgroundDefaultSecondary)
                    .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.small))
                }
                .buttonStyle(.plain)

                Text("미리보기")
                    .obritTextStyle(OBRitTypography.xs, weight: OBRitFontWeight.bold, color: OBRitColors.common00)

                Spacer(minLength: 0)
            }

            ForEach(items.prefix(3)) { item in
                Button {
                    action.onSelectConsumable(item.id)
                } label: {
                    OBRitCardList(
                        level: item.cardLevel,
                        title: item.title,
                        daysInUseLabel: "\(item.daysInUse)일",
                        replaceLabel: item.replaceLabel,
                        sparesLabel: item.sparesLabel
                    ) {
                        ConsumableListAssetImage(assetName: item.assetName)
                            .padding(OBRitSpacing.s2)
                    }
                }
                .buttonStyle(.plain)
            }

            OBRitFilledTextButton(
                text: "더 보기",
                size: .middle,
                color: .gray,
                fillsWidth: true,
                action: action.onShowList
            )
        }
        .padding(.horizontal, OBRitSpacing.s5)
        .padding(.bottom, OBRitSpacing.s7)
    }
}

private struct ConsumableListUsageSection: View {
    let items: [ConsumableListItem]
    let onSelect: (Int) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
            Text("사용 현황")
                .obritTextStyle(OBRitTypography.small, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                .padding(.horizontal, OBRitSpacing.s5)

            LazyVStack(spacing: 0) {
                ForEach(items) { item in
                    Button {
                        onSelect(item.id)
                    } label: {
                        ConsumableListUsageRow(item: item)
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, OBRitSpacing.s5)
        }
    }
}

private struct ConsumableListUsageRow: View {
    let item: ConsumableListItem

    var body: some View {
        HStack(spacing: OBRitSpacing.s4) {
            ConsumableListAvatar(assetName: item.assetName)
                .frame(width: OBRitSpacing.s8, height: OBRitSpacing.s8)

            Text(item.title)
                .lineLimit(1)
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                .frame(maxWidth: .infinity, alignment: .leading)

            HStack(spacing: OBRitSpacing.s0_5) {
                Text("\(item.daysInUse)일")
                    .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                Text("째 사용중")
                    .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultSecondary)
            }

            Image(systemName: "chevron.right")
                .font(.system(size: OBRitSpacing.s3, weight: .bold))
                .foregroundStyle(OBRitColors.iconDefaultSecondary)
        }
        .padding(.vertical, OBRitSpacing.s4)
        .overlay(alignment: .bottom) {
            Rectangle()
                .fill(OBRitColors.common00.opacity(0.04))
                .frame(height: OBRitSpacing.px)
        }
        .contentShape(Rectangle())
    }
}

private struct ConsumableListAvatar: View {
    let assetName: String

    var body: some View {
        ZStack {
            Circle()
                .fill(Color(red: 48 / 255, green: 51 / 255, blue: 62 / 255))
            ConsumableListAssetImage(assetName: assetName)
                .padding(OBRitSpacing.s0_5)
        }
    }
}

private struct ConsumableListAssetImage: View {
    let assetName: String

    var body: some View {
        Image(assetName)
            .resizable()
            .scaledToFit()
            .accessibilityHidden(true)
    }
}

private struct ConsumableListRegisterButton: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: "plus")
                .font(.system(size: OBRitSpacing.s5, weight: .bold))
                .foregroundStyle(OBRitColors.gray900)
                .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
                .padding(OBRitSpacing.s4)
                .background(OBRitColors.common00)
                .clipShape(Circle())
                .shadow(color: Color.black.opacity(0.24), radius: OBRitSpacing.s6, x: 0, y: OBRitSpacing.s4)
        }
        .buttonStyle(.plain)
        .accessibilityLabel("소모품 등록")
    }
}

private struct ConsumableListSummary {
    let status: String
    let replacementStatus: String
    let spareStatus: String
    let positiveRatio: Int
    let warningRatio: Int
    let totalCount: Int
    let replacementRequiredCount: Int
}

private struct ConsumableListFilter: Identifiable {
    enum ID: Hashable {
        case replacementRequired
        case spareShortage
        case replacementSoon
    }

    let id: ID
    let title: String
    let count: Int
}

private struct ConsumableListItem: Identifiable {
    let id: Int
    let title: String
    let daysInUse: Int
    let stockCount: Int
    let daysLabel: String
    let replaceLabel: String
    let sparesLabel: String
    let cardLevel: OBRitCardLevel
    let assetName: String
}

private extension ConsumableListItem {
    var orbInteriorItem: HomeOrbInteriorItem {
        HomeOrbInteriorItem(
            id: id,
            assetName: assetName,
            weight: 0.92 + CGFloat(max(1, 7 - cardLevel.consumableListRiskRank)) * 0.055
        )
    }
}

private extension OBRitCardLevel {
    var consumableListRiskRank: Int {
        switch self {
        case .l1:
            return 1
        case .l2:
            return 2
        case .l3:
            return 3
        case .l4:
            return 4
        case .l5:
            return 5
        case .l6:
            return 6
        }
    }
}

private enum ConsumableListSampleData {
    static let summary = ConsumableListSummary(
        status: "양호",
        replacementStatus: "보통",
        spareStatus: "보통",
        positiveRatio: 62,
        warningRatio: 38,
        totalCount: 16,
        replacementRequiredCount: 4
    )

    static let filters = [
        ConsumableListFilter(id: .replacementRequired, title: "교체 필요", count: 4),
        ConsumableListFilter(id: .spareShortage, title: "여분 부족", count: 3),
        ConsumableListFilter(id: .replacementSoon, title: "교체 임박", count: 4)
    ]

    static let quickItems = [
        ConsumableListItem(
            id: 101,
            title: "샴푸",
            daysInUse: 58,
            stockCount: 3,
            daysLabel: "D+3",
            replaceLabel: "교체 D+3",
            sparesLabel: "여분 1개",
            cardLevel: .l2,
            assetName: "home_orb_diffuser"
        ),
        ConsumableListItem(
            id: 102,
            title: "칫솔",
            daysInUse: 30,
            stockCount: 0,
            daysLabel: "D-day",
            replaceLabel: "교체 D-day",
            sparesLabel: "여분 0개",
            cardLevel: .l1,
            assetName: "home_orb_toothbrush"
        ),
        ConsumableListItem(
            id: 103,
            title: "수세미",
            daysInUse: 30,
            stockCount: 0,
            daysLabel: "D-day",
            replaceLabel: "교체 D-day",
            sparesLabel: "여분 0개",
            cardLevel: .l3,
            assetName: "home_orb_sponge"
        ),
        ConsumableListItem(
            id: 104,
            title: "고무 장갑",
            daysInUse: 44,
            stockCount: 0,
            daysLabel: "D-3",
            replaceLabel: "교체 D-3",
            sparesLabel: "여분 0개",
            cardLevel: .l2,
            assetName: "home_orb_towel"
        )
    ]

    static let usageItems = [
        ConsumableListItem(id: 1, title: "샤워기 필터", daysInUse: 82, stockCount: 1, daysLabel: "D-8", replaceLabel: "교체 D-8", sparesLabel: "여분 1개", cardLevel: .l4, assetName: "home_orb_shower_filter"),
        ConsumableListItem(id: 2, title: "헤어브러쉬", daysInUse: 74, stockCount: 2, daysLabel: "D-12", replaceLabel: "교체 D-12", sparesLabel: "여분 2개", cardLevel: .l4, assetName: "home_orb_razor"),
        ConsumableListItem(id: 3, title: "정수기 필터", daysInUse: 66, stockCount: 1, daysLabel: "D-6", replaceLabel: "교체 D-6", sparesLabel: "여분 1개", cardLevel: .l4, assetName: "home_orb_shower_filter"),
        ConsumableListItem(id: 4, title: "샴푸", daysInUse: 58, stockCount: 1, daysLabel: "D+3", replaceLabel: "교체 D+3", sparesLabel: "여분 1개", cardLevel: .l2, assetName: "home_orb_diffuser"),
        ConsumableListItem(id: 5, title: "고무 장갑", daysInUse: 44, stockCount: 0, daysLabel: "D-3", replaceLabel: "교체 D-3", sparesLabel: "여분 0개", cardLevel: .l2, assetName: "home_orb_towel"),
        ConsumableListItem(id: 6, title: "유리 세정제", daysInUse: 35, stockCount: 2, daysLabel: "D-7", replaceLabel: "교체 D-7", sparesLabel: "여분 2개", cardLevel: .l5, assetName: "home_orb_detergent"),
        ConsumableListItem(id: 7, title: "칫솔", daysInUse: 30, stockCount: 0, daysLabel: "D-day", replaceLabel: "교체 D-day", sparesLabel: "여분 0개", cardLevel: .l1, assetName: "home_orb_toothbrush"),
        ConsumableListItem(id: 8, title: "수세미", daysInUse: 30, stockCount: 0, daysLabel: "D-day", replaceLabel: "교체 D-day", sparesLabel: "여분 0개", cardLevel: .l3, assetName: "home_orb_sponge"),
        ConsumableListItem(id: 9, title: "수건", daysInUse: 26, stockCount: 4, daysLabel: "D-4", replaceLabel: "교체 D-4", sparesLabel: "여분 4개", cardLevel: .l5, assetName: "home_orb_towel"),
        ConsumableListItem(id: 10, title: "주방 세제", daysInUse: 22, stockCount: 0, daysLabel: "D+2", replaceLabel: "교체 D+2", sparesLabel: "여분 0개", cardLevel: .l1, assetName: "home_orb_detergent"),
        ConsumableListItem(id: 11, title: "세탁 세제", daysInUse: 20, stockCount: 1, daysLabel: "D-10", replaceLabel: "교체 D-10", sparesLabel: "여분 1개", cardLevel: .l4, assetName: "home_orb_detergent"),
        ConsumableListItem(id: 12, title: "면도기 날", daysInUse: 18, stockCount: 0, daysLabel: "D+14", replaceLabel: "교체 D+14", sparesLabel: "여분 0개", cardLevel: .l2, assetName: "home_orb_razor"),
        ConsumableListItem(id: 13, title: "지퍼백", daysInUse: 15, stockCount: 5, daysLabel: "D-20", replaceLabel: "교체 D-20", sparesLabel: "여분 5개", cardLevel: .l6, assetName: "home_orb_sponge"),
        ConsumableListItem(id: 14, title: "바디워시", daysInUse: 12, stockCount: 1, daysLabel: "D-18", replaceLabel: "교체 D-18", sparesLabel: "여분 1개", cardLevel: .l4, assetName: "home_orb_diffuser"),
        ConsumableListItem(id: 15, title: "키친타월", daysInUse: 8, stockCount: 2, daysLabel: "D-22", replaceLabel: "교체 D-22", sparesLabel: "여분 2개", cardLevel: .l5, assetName: "home_orb_towel"),
        ConsumableListItem(id: 16, title: "쓰레기 봉투", daysInUse: 3, stockCount: 6, daysLabel: "D-27", replaceLabel: "교체 D-27", sparesLabel: "여분 6개", cardLevel: .l6, assetName: "home_orb_sponge")
    ]
}

#Preview {
    ConsumableListTabView(onNavigate: { _ in })
}
