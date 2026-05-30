import SwiftUI

struct ItemDetailBottomActionBar: View {
    let onManageStock: () -> Void
    let onCompleteReplacement: () -> Void

    var body: some View {
        HStack(spacing: OBRitSpacing.s3) {
            actionButton(
                title: "여분 관리",
                backgroundColor: OBRitColors.backgroundDefaultDefaultHover,
                foregroundColor: OBRitColors.gray150,
                action: onManageStock
            )

            actionButton(
                title: "교체 완료",
                backgroundColor: OBRitColors.backgroundPositiveDefault,
                foregroundColor: OBRitColors.common1000,
                action: onCompleteReplacement
            )
        }
        .padding(.horizontal, ItemDetailLayout.horizontalPadding)
        .padding(.vertical, ItemDetailLayout.actionBarVerticalPadding)
        .background(OBRitColors.backgroundDefaultDefault)
    }

    private func actionButton(
        title: String,
        backgroundColor: Color,
        foregroundColor: Color,
        action: @escaping () -> Void
    ) -> some View {
        Button(action: action) {
            Text(title)
                .lineLimit(1)
                .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.semiBold, color: foregroundColor)
                .frame(maxWidth: .infinity)
                .frame(height: ItemDetailLayout.actionBarButtonSize.height)
                .background(backgroundColor)
                .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.large))
        }
        .buttonStyle(.plain)
    }
}
