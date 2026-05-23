import SwiftUI

struct ItemDetailMoreMenu: View {
    let items: [ItemDetailMoreMenuItem]
    let onSelect: (ItemDetailMoreMenuItem) -> Void

    var body: some View {
        VStack(spacing: OBRitSpacing.s0) {
            ForEach(Array(items.enumerated()), id: \.element.id) { index, item in
                Button {
                    onSelect(item)
                } label: {
                    HStack(spacing: OBRitSpacing.s2_5) {
                        Image(systemName: item.symbolName)
                            .font(.system(size: ItemDetailMoreMenuMetrics.iconSize, weight: .regular))
                            .frame(
                                width: ItemDetailMoreMenuMetrics.iconFrame,
                                height: ItemDetailMoreMenuMetrics.iconFrame
                            )

                        Text(item.title)
                            .lineLimit(1)
                            .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.medium, color: item.foregroundColor)
                    }
                    .foregroundStyle(item.foregroundColor)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .frame(height: ItemDetailMoreMenuMetrics.rowHeight)
                    .contentShape(Rectangle())
                }
                .buttonStyle(.plain)

                if index < items.count - 1 {
                    Rectangle()
                        .fill(OBRitColors.gray700)
                        .frame(height: 1)
                        .padding(.vertical, OBRitSpacing.s2)
                }
            }
        }
        .padding(.horizontal, 14)
        .padding(.vertical, OBRitSpacing.s4)
        .frame(width: ItemDetailMoreMenuMetrics.width)
        .background(OBRitColors.surfaceDefaultDefaultDark)
        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
        .shadow(color: OBRitColors.commonBlack00_40, radius: 16, x: 0, y: 8)
    }
}

private extension ItemDetailMoreMenuItem {
    var symbolName: String {
        switch self {
        case .edit:
            return "pencil"
        case .spareEdit:
            return "shippingbox"
        case .notification:
            return "bell"
        case .delete:
            return "trash"
        }
    }

    var foregroundColor: Color {
        self == .delete ? OBRitColors.textWarningDefault : OBRitColors.textDefaultDefault
    }
}

private enum ItemDetailMoreMenuMetrics {
    static let width: CGFloat = 120
    static let rowHeight: CGFloat = 24
    static let iconFrame: CGFloat = 24
    static let iconSize: CGFloat = 18
}
