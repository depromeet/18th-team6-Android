import SwiftUI

struct HomeQuickItemPreviewSection: View {
    @State private var sortMenuPresented = false

    let items: [HomeConsumableItem]
    let selectedSort: HomeWarningSort
    let onSelectSort: (HomeWarningSort) -> Void
    let onShowList: () -> Void
    let onSelect: (Int) -> Void

    var body: some View {
        VStack(spacing: OBRitSpacing.s4) {
            sortHeader

            VStack(spacing: OBRitSpacing.s2) {
                ForEach(items.prefix(3)) { item in
                    Button {
                        onSelect(item.id)
                    } label: {
                        OBRitCardList(
                            level: item.cardLevel,
                            title: item.title,
                            daysInUseLabel: "\(item.daysInUse)일",
                            replaceLabel: item.replaceLabel,
                            sparesLabel: item.sparesLabel
                        ) {
                            HomeConsumableImage(color: item.imageColor)
                                .padding(OBRitSpacing.s2)
                        }
                    }
                    .buttonStyle(.plain)
                }
            }

            OBRitFilledTextButton(
                text: "더보기",
                size: .middle,
                color: .gray,
                fillsWidth: true,
                action: onShowList
            )
        }
        .padding(.horizontal, OBRitSpacing.s5)
        .padding(.bottom, OBRitSpacing.s6)
    }

    private var sortHeader: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
            HStack {
                OBRitFilledTextButton(
                    text: selectedSort.title,
                    size: .small,
                    color: .gray,
                    action: {
                        sortMenuPresented.toggle()
                    },
                    trailingIcon: { contentColor in
                        OBRitIcon(kind: .chevronDown, color: contentColor)
                    }
                )

                Text("미리보기")
                    .obritTextStyle(OBRitTypography.xs, weight: OBRitFontWeight.bold, color: OBRitColors.common00)

                Spacer()
            }

            if sortMenuPresented {
                OBRitDropdownMenu(
                    items: HomeWarningSort.allCases.map(\.title),
                    selectedIndex: selectedSortIndex,
                    itemSize: .small
                ) { index in
                    onSelectSort(HomeWarningSort.allCases[index])
                    sortMenuPresented = false
                }
            }
        }
    }

    private var selectedSortIndex: Int? {
        HomeWarningSort.allCases.firstIndex(of: selectedSort)
    }
}
