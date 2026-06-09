import SwiftUI

struct HomeTodayStatusSection: View {
    static let height: CGFloat = 139

    let summary: HomeSummary

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s2) {
            VStack(alignment: .leading, spacing: 0) {
                Text("오늘의 소모품 관리")
                    .obritTextStyle(OBRitTypography.s5xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                    .lineLimit(1)
                    .minimumScaleFactor(0.88)
                    .frame(height: OBRitTypography.s5xl.lineHeight, alignment: .center)
                HStack(spacing: 0) {
                    Text("상태는 ")
                        .obritTextStyle(OBRitTypography.s5xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                    Text(summary.status)
                        .obritTextStyle(OBRitTypography.s5xl, weight: OBRitFontWeight.bold, color: statusColor)
                    Text(statusSuffix)
                        .obritTextStyle(OBRitTypography.s5xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                }
                .lineLimit(1)
                .minimumScaleFactor(0.88)
                .frame(height: OBRitTypography.s5xl.lineHeight, alignment: .center)
            }

            HStack(spacing: OBRitSpacing.s3) {
                HomeStatusPair(label: "교체 관리", value: summary.replacementStatus)
                HomeStatusPair(label: "여분 관리", value: summary.stockStatus)
            }
            .frame(height: OBRitTypography.base.lineHeight, alignment: .center)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(OBRitSpacing.s5)
        .frame(height: Self.height, alignment: .topLeading)
    }

    private var statusColor: Color {
        summary.status == "완벽" || summary.status == "양호" ? OBRitColors.textPositiveDefault : OBRitColors.textWarningDefault
    }

    private var statusSuffix: String {
        summary.status == "완벽" || summary.status == "양호" ? "해요" : "예요"
    }
}

private struct HomeStatusPair: View {
    let label: String
    let value: String

    var body: some View {
        HStack(spacing: OBRitSpacing.s1) {
            Text(label)
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultSecondary)
                .lineLimit(1)
            Text(value)
                .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                .lineLimit(1)
        }
    }
}
