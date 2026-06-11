import SwiftUI

private enum HomeQuickItemLayoutMetrics {
    static let horizontalContentMargin = OBRitSpacing.s5
    static let verticalPadding = OBRitSpacing.s5
    static let contentSpacing = OBRitSpacing.s4
    static let itemSpacing = OBRitSpacing.s2
    static let chipRowHeight = OBRitSpacing.s9 + OBRitSpacing.s0_5
    static let carouselHeight = OBRitSpacing.s40
}

struct HomeQuickItemSection: View {
    let items: [HomeItemItem]
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
            .contentMargins(.horizontal, HomeQuickItemLayoutMetrics.horizontalContentMargin, for: .scrollContent)
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
                                OBRitRemoteImage(urlString: item.imageURL)
                            }
                        }
                        .buttonStyle(.plain)
                    }
                }
            }
            .contentMargins(.horizontal, HomeQuickItemLayoutMetrics.horizontalContentMargin, for: .scrollContent)
            .frame(height: HomeQuickItemLayoutMetrics.carouselHeight)
        }
        .padding(.vertical, HomeQuickItemLayoutMetrics.verticalPadding)
    }

    private var visibleFilters: [HomeStatusFilter] {
        HomeStatusFilter.allCases.filter { filterCounts[$0, default: 0] > 0 }
    }
}
