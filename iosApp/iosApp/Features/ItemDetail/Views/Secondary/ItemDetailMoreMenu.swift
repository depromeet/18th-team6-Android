import SwiftUI

struct ItemDetailMoreMenu: View {
    let items: [ItemDetailMoreMenuItem]
    let onSelect: (ItemDetailMoreMenuItem) -> Void

    init(
        items: [ItemDetailMoreMenuItem] = [.edit, .delete],
        onSelect: @escaping (ItemDetailMoreMenuItem) -> Void
    ) {
        self.items = items
        self.onSelect = onSelect
    }

    var body: some View {
        VStack(alignment: .leading, spacing: ItemDetailMoreMenuMetrics.rowSpacing) {
            ForEach(Array(items.enumerated()), id: \.element.id) { index, item in
                Button {
                    onSelect(item)
                } label: {
                    HStack(spacing: ItemDetailMoreMenuMetrics.iconTextSpacing) {
                        Image(systemName: item.symbolName)
                            .font(.system(size: ItemDetailMoreMenuMetrics.iconSize, weight: .regular))
                            .foregroundStyle(item.tintColor)
                            .frame(
                                width: ItemDetailMoreMenuMetrics.iconFrameSize,
                                height: ItemDetailMoreMenuMetrics.iconFrameSize
                            )

                        Text(item.title)
                            .lineLimit(1)
                            .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.medium, color: item.tintColor)
                    }
                    .contentShape(Rectangle())
                }
                .buttonStyle(.plain)

                if index < items.count - 1 {
                    Rectangle()
                        .fill(OBRitColors.commonWhite00_40)
                        .frame(height: OBRitSpacing.px)
                }
            }
        }
        .padding(.horizontal, ItemDetailMoreMenuMetrics.horizontalPadding)
        .padding(.vertical, ItemDetailMoreMenuMetrics.verticalPadding)
        .frame(width: ItemDetailMoreMenuMetrics.width, alignment: .leading)
        .background(OBRitColors.commonWhite00_20)
        .clipShape(RoundedRectangle(cornerRadius: ItemDetailMoreMenuMetrics.cornerRadius))
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

    var tintColor: Color {
        self == .delete ? OBRitColors.textWarningDefault : OBRitColors.textDefaultDefault
    }
}

private enum ItemDetailMoreMenuMetrics {
    static let width: CGFloat = 120
    static let horizontalPadding: CGFloat = 14
    static let verticalPadding: CGFloat = 18
    static let rowSpacing: CGFloat = 10
    static let iconTextSpacing: CGFloat = 10
    static let iconFrameSize: CGFloat = 24
    static let iconSize: CGFloat = 22
    static let cornerRadius: CGFloat = 16
}

#Preview {
    ZStack(alignment: .topTrailing) {
        OBRitColors.gray900
        ItemDetailMoreMenu { _ in }
            .padding(OBRitSpacing.s5)
    }
}
