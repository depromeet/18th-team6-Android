import SwiftUI

struct ReceiptAnalyzeResultView: View {
    let result: ReceiptAnalyzeResult
    let removedItemIDs: Set<Int>
    let onRemoveItem: (ReceiptAnalyzeResultItem) -> Void
    let onNextStep: (ReceiptAnalyzeResult) -> Void
    let onDirectRegistration: () -> Void

    private var visibleItems: [ReceiptAnalyzeResultItem] {
        result.items.filter { !removedItemIDs.contains($0.id) }
    }

    private var visibleResult: ReceiptAnalyzeResult {
        ReceiptAnalyzeResult(
            receiptImageURL: result.receiptImageURL,
            purchasedDate: result.purchasedDate,
            items: visibleItems
        )
    }

    var body: some View {
        GeometryReader { geometry in
            ZStack(alignment: .bottom) {
                ScrollView(showsIndicators: false) {
                    VStack(alignment: .leading, spacing: OBRitSpacing.s5) {
                        ReceiptAnalyzeResultHeader(purchasedDate: result.purchasedDate)
                        ReceiptAnalyzeResultItemSection(
                            items: visibleItems,
                            onRemove: removeItem
                        )
                    }
                    .padding(.bottom, ReceiptAnalyzeResultMetrics.bottomContentPadding)
                }

                ReceiptAnalyzeResultBottomBar(
                    bottomInset: geometry.safeAreaInsets.bottom,
                    isNextStepEnabled: !visibleItems.isEmpty,
                    onNextStep: {
                        onNextStep(visibleResult)
                    },
                    onDirectRegistration: onDirectRegistration
                )
            }
            .frame(width: geometry.size.width, height: geometry.size.height)
            .background(OBRitColors.backgroundDefaultDefault)
            .ignoresSafeArea(edges: .bottom)
        }
    }

    private func removeItem(_ item: ReceiptAnalyzeResultItem) {
        onRemoveItem(item)
    }
}

private struct ReceiptAnalyzeResultHeader: View {
    let purchasedDate: String?

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s0) {
            ReceiptAnalyzeProgressView()
                .padding(.horizontal, OBRitSpacing.s5)
                .padding(.vertical, OBRitSpacing.s4)

            VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
                Text("영수증 분석이 완료되었어요")
                    .fixedSize(horizontal: false, vertical: true)
                    .obritTextStyle(OBRitTypography.s6xl, weight: OBRitFontWeight.bold, color: OBRitColors.textDefaultDefault)

                HStack(spacing: OBRitSpacing.s2) {
                    Text("영수증")
                        .padding(.horizontal, OBRitSpacing.s2)
                        .padding(.vertical, OBRitSpacing.s1)
                        .background(OBRitColors.backgroundDefaultLightGrayDefault)
                        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.extraSmall))
                        .obritTextStyle(OBRitTypography.xs, weight: OBRitFontWeight.semiBold, color: OBRitColors.textDefaultDarkSecondary)

                    Text(formattedPurchasedDate)
                        .lineLimit(1)
                        .truncationMode(.tail)
                        .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultSecondary)
                }
            }
            .padding(.horizontal, OBRitSpacing.s5)
            .padding(.vertical, OBRitSpacing.s4)
        }
    }

    private var formattedPurchasedDate: String {
        guard let purchasedDate = purchasedDate?.trimmingCharacters(in: .whitespacesAndNewlines),
              !purchasedDate.isEmpty else {
            return "구매일 확인 불가"
        }

        let datePart = String(purchasedDate.prefix(10))
        let components = datePart.split(separator: "-").compactMap { Int($0) }
        if components.count == 3 {
            return String(format: "%04d. %02d. %02d 구매", components[0], components[1], components[2])
        }

        if purchasedDate.contains("구매") {
            return purchasedDate
        }
        return "\(purchasedDate) 구매"
    }
}

private struct ReceiptAnalyzeProgressView: View {
    var body: some View {
        HStack(spacing: OBRitSpacing.s1) {
            ReceiptAnalyzeProgressNumber(number: 1, isActive: true)

            Rectangle()
                .fill(OBRitColors.gray750)
                .frame(width: 28, height: 1)

            ReceiptAnalyzeProgressNumber(number: 2, isActive: false)
        }
        .frame(height: 28)
    }
}

private struct ReceiptAnalyzeProgressNumber: View {
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

private struct ReceiptAnalyzeResultItemSection: View {
    let items: [ReceiptAnalyzeResultItem]
    let onRemove: (ReceiptAnalyzeResultItem) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
            HStack(spacing: OBRitSpacing.s0) {
                Text("인식한 소모품 ")
                    .obritTextStyle(OBRitTypography.s3xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                Text("\(items.count)개")
                    .obritTextStyle(OBRitTypography.s3xl, weight: OBRitFontWeight.bold, color: OBRitColors.green300)
            }

            LazyVStack(spacing: OBRitSpacing.s2) {
                ForEach(items) { item in
                    ReceiptAnalyzeResultItemRow(
                        item: item,
                        onRemove: {
                            onRemove(item)
                        }
                    )
                }
            }
        }
        .padding(.horizontal, OBRitSpacing.s5)
    }
}

private struct ReceiptAnalyzeResultItemRow: View {
    let item: ReceiptAnalyzeResultItem
    let onRemove: () -> Void

    var body: some View {
        HStack(spacing: OBRitSpacing.s4) {
            ReceiptAnalyzeResultItemImage(urlString: item.iconURL)

            VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
                Text(item.suggestedName)
                    .lineLimit(1)
                    .truncationMode(.tail)
                    .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)

                HStack(spacing: OBRitSpacing.s0_5) {
                    Text("인식한 소모품")
                        .foregroundStyle(OBRitColors.common00.opacity(0.64))
                    Text("\(item.quantity)개")
                        .foregroundStyle(OBRitColors.common00)
                }
                .font(OBRitTypography.font(OBRitTypography.small, weight: OBRitFontWeight.medium))
                .tracking(OBRitTypography.letterSpacing(for: OBRitTypography.small))
                .lineSpacing(max(0, OBRitTypography.small.lineHeight - OBRitTypography.small.size))
            }
            .frame(maxWidth: .infinity, alignment: .leading)

            Button("삭제", systemImage: "trash", action: onRemove)
                .labelStyle(.iconOnly)
                .font(.system(size: 18, weight: .semibold))
                .foregroundStyle(OBRitColors.common00)
                .frame(width: 48, height: 48)
                .background(OBRitColors.gray750)
                .overlay {
                    RoundedRectangle(cornerRadius: OBRitRadius.small)
                        .stroke(OBRitColors.gray700, lineWidth: 1)
                }
                .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.small))
                .buttonStyle(.plain)
                .accessibilityLabel("\(item.suggestedName) 삭제")
        }
        .padding(.horizontal, OBRitSpacing.s5)
        .padding(.vertical, OBRitSpacing.s4)
        .frame(minHeight: 84)
        .background(OBRitColors.backgroundDefaultSecondary)
        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
    }
}

private struct ReceiptAnalyzeResultItemImage: View {
    let urlString: String

    var body: some View {
        OBRitRemoteImage(urlString: urlString, contentMode: .fill) {
            Image(systemName: "shippingbox")
                .font(.system(size: 20, weight: .semibold))
                .foregroundStyle(OBRitColors.textDefaultSecondary)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(OBRitColors.gray750)
        }
        .frame(width: 52, height: 52)
        .background(OBRitColors.gray750)
        .clipShape(Circle())
    }
}

private struct ReceiptAnalyzeResultBottomBar: View {
    let bottomInset: CGFloat
    let isNextStepEnabled: Bool
    let onNextStep: () -> Void
    let onDirectRegistration: () -> Void

    var body: some View {
        VStack(spacing: OBRitSpacing.s4) {
            Button(action: onNextStep) {
                Text("다음 단계로")
                    .frame(maxWidth: .infinity)
                    .frame(height: 56)
                    .background(nextButtonBackground)
                    .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.large))
                    .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.semiBold, color: nextButtonTextColor)
            }
            .buttonStyle(.plain)
            .disabled(!isNextStepEnabled)

            HStack(spacing: OBRitSpacing.s2) {
                Text("인식되지 않은 소모품이 있나요?")
                    .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.gray200)

                Button("직접 등록하기", action: onDirectRegistration)
                    .buttonStyle(.plain)
                    .underline()
                    .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.semiBold, color: OBRitColors.common00)
            }
            .lineLimit(1)
            .minimumScaleFactor(0.85)
        }
        .padding(OBRitSpacing.s5)
        .padding(.bottom, max(bottomInset, OBRitSpacing.s5))
        .background {
            LinearGradient(
                stops: [
                    .init(color: OBRitColors.backgroundDefaultDefault.opacity(0), location: 0),
                    .init(color: OBRitColors.backgroundDefaultDefault, location: 0.2),
                    .init(color: OBRitColors.backgroundDefaultDefault, location: 1)
                ],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea(edges: .bottom)
        }
    }

    private var nextButtonBackground: Color {
        isNextStepEnabled ? OBRitColors.backgroundPositiveDefault : OBRitColors.backgroundDisabledDefault
    }

    private var nextButtonTextColor: Color {
        isNextStepEnabled ? OBRitColors.common1000 : OBRitColors.textDisabledOnDisabled
    }
}

private enum ReceiptAnalyzeResultMetrics {
    static let bottomContentPadding: CGFloat = 190
}

#Preview("Receipt Analyze Result") {
    ReceiptAnalyzeResultView(
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
                    quantity: 0,
                    suggestedReplacementIntervalDays: 30
                ),
                ReceiptAnalyzeResultItem(
                    id: 1,
                    suggestedName: "정수기 필터",
                    suggestedCategoryName: "정수기 필터",
                    categoryId: 2,
                    iconURL: "",
                    quantity: 0,
                    suggestedReplacementIntervalDays: 90
                ),
                ReceiptAnalyzeResultItem(
                    id: 2,
                    suggestedName: "칫솔",
                    suggestedCategoryName: "칫솔",
                    categoryId: 3,
                    iconURL: "",
                    quantity: 0,
                    suggestedReplacementIntervalDays: 30
                ),
                ReceiptAnalyzeResultItem(
                    id: 3,
                    suggestedName: "세탁 세제",
                    suggestedCategoryName: "세탁 세제",
                    categoryId: 4,
                    iconURL: "",
                    quantity: 0,
                    suggestedReplacementIntervalDays: 60
                )
            ]
        ),
        removedItemIDs: [],
        onRemoveItem: { _ in },
        onNextStep: { _ in },
        onDirectRegistration: {}
    )
}
