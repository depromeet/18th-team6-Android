import SwiftUI

private enum HomeQuickItemLayoutMetrics {
    static let sectionPadding = OBRitSpacing.s5
    static let contentSpacing = OBRitSpacing.s4
    static let itemSpacing = OBRitSpacing.s2
    static let chipRowHeight = OBRitSpacing.s9 + OBRitSpacing.s0_5
    static let carouselHeight = OBRitSpacing.s40
}

struct HomeQuickItemSection: View {
    let items: [HomeConsumableItem]
    let selectedFilter: HomeStatusFilter
    let filterCounts: [HomeStatusFilter: Int]
    let onSelectFilter: (HomeStatusFilter) -> Void
    let onSelect: (Int) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: HomeQuickItemLayoutMetrics.contentSpacing) {
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: HomeQuickItemLayoutMetrics.itemSpacing) {
                    ForEach(visibleFilters, id: \.self) { filter in
                        OBRitChip(
                            text: filter.title,
                            selected: selectedFilter == filter,
                            number: filterCounts[filter, default: 0]
                        ) {
                            onSelectFilter(filter)
                        }
                    }
                }
            }
            .frame(height: HomeQuickItemLayoutMetrics.chipRowHeight)

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: HomeQuickItemLayoutMetrics.itemSpacing) {
                    ForEach(items) { item in
                        Button {
                            onSelect(item.id)
                        } label: {
                            OBRitCardGrid(
                                level: item.cardLevel,
                                title: item.title,
                                stockCount: item.stockCount,
                                daysLabel: item.dDayLabel
                            ) {
                                Image(item.imageAssetName)
                                    .resizable()
                                    .scaledToFit()
                            }
                        }
                        .buttonStyle(.plain)
                    }
                }
            }
            .frame(height: HomeQuickItemLayoutMetrics.carouselHeight)
        }
        .padding(HomeQuickItemLayoutMetrics.sectionPadding)
    }

    private var visibleFilters: [HomeStatusFilter] {
        HomeStatusFilter.allCases.filter { filterCounts[$0, default: 0] > 0 }
    }
}
