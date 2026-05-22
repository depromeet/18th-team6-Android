import SwiftUI

public struct OBRitBottomSheet<Content: View>: View {
    private let contentHeight: CGFloat
    private let bottomPadding: CGFloat
    private let content: Content

    public init(
        contentHeight: CGFloat = 414,
        bottomPadding: CGFloat = OBRitSpacing.s5,
        @ViewBuilder content: () -> Content
    ) {
        self.contentHeight = contentHeight
        self.bottomPadding = bottomPadding
        self.content = content()
    }

    public var body: some View {
        VStack(spacing: OBRitSpacing.s2_5) {
            OBRitBottomSheetHeader()

            content
                .padding(.horizontal, OBRitBottomSheetMetrics.contentHorizontalPadding)
                .frame(maxWidth: .infinity, alignment: .top)
                .frame(height: contentHeight, alignment: .top)
        }
        .frame(maxWidth: .infinity)
        .padding(.bottom, bottomPadding)
        .background(OBRitColors.gray900)
        .clipShape(OBRitTopRoundedRectangle(radius: OBRitRadius.bottomSheet))
    }
}

private struct OBRitBottomSheetHeader: View {
    var body: some View {
        HStack {
            Capsule()
                .fill(OBRitColors.gray250.opacity(0.4))
                .frame(
                    width: OBRitBottomSheetMetrics.handleWidth,
                    height: OBRitBottomSheetMetrics.handleHeight
                )
        }
        .frame(maxWidth: .infinity)
        .padding(OBRitSpacing.s4)
    }
}

private struct OBRitTopRoundedRectangle: Shape {
    let radius: CGFloat

    func path(in rect: CGRect) -> Path {
        let cornerRadius = min(radius, rect.width / 2, rect.height / 2)

        var path = Path()
        path.move(to: CGPoint(x: rect.minX, y: rect.maxY))
        path.addLine(to: CGPoint(x: rect.minX, y: rect.minY + cornerRadius))
        path.addQuadCurve(
            to: CGPoint(x: rect.minX + cornerRadius, y: rect.minY),
            control: CGPoint(x: rect.minX, y: rect.minY)
        )
        path.addLine(to: CGPoint(x: rect.maxX - cornerRadius, y: rect.minY))
        path.addQuadCurve(
            to: CGPoint(x: rect.maxX, y: rect.minY + cornerRadius),
            control: CGPoint(x: rect.maxX, y: rect.minY)
        )
        path.addLine(to: CGPoint(x: rect.maxX, y: rect.maxY))
        path.closeSubpath()
        return path
    }
}

private enum OBRitBottomSheetMetrics {
    static let contentHorizontalPadding = OBRitSpacing.s5
    static let handleWidth: CGFloat = 32
    static let handleHeight: CGFloat = 4
}
