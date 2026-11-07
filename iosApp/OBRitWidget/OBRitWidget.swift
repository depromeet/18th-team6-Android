import AppIntents
import SwiftUI
import WidgetKit

@main
struct OBRitWidgetBundle: WidgetBundle {
    var body: some Widget {
        OBRitStatusWidget()
    }
}

struct OBRitStatusWidget: Widget {
    let kind = "OBRitStatusWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: StatusWidgetProvider()) { entry in
            Group {
                if let state = entry.state {
                    if state.isEmpty {
                        StatusWidgetNoticeView(isEmpty: true)
                            .widgetURL(URL(string: "obrit://home"))
                    } else {
                        StatusWidgetView(state: state)
                            .widgetURL(URL(string: "obrit://home"))
                    }
                } else {
                    StatusWidgetNoticeView(isEmpty: false)
                }
            }
            .containerBackground(StatusWidgetStyle.background, for: .widget)
        }
        .configurationDisplayName("소모품 상태")
        .description("위험·경고 소모품 현황을 확인해요.")
        .supportedFamilies([.systemSmall])
        .contentMarginsDisabled()
    }
}

private struct StatusWidgetEntry: TimelineEntry {
    let date: Date
    let state: StatusWidgetState?

    static var sample: StatusWidgetEntry {
        sample(goodRatio: 0.7)
    }

    static func sample(goodRatio: Double) -> StatusWidgetEntry {
        StatusWidgetEntry(date: Date(), state: StatusWidgetState(dangerCount: 2, warningCount: 0, goodRatio: goodRatio))
    }
}

private struct StatusWidgetProvider: TimelineProvider {
    func placeholder(in context: Context) -> StatusWidgetEntry { .sample }

    func getSnapshot(in context: Context, completion: @escaping (StatusWidgetEntry) -> Void) {
        if context.isPreview { completion(.sample) }
        else {
            Task {
                let state = await StatusWidgetDataSource.shared.refresh()
                completion(StatusWidgetEntry(date: Date(), state: state))
            }
        }
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<StatusWidgetEntry>) -> Void) {
        Task {
            let state = await StatusWidgetDataSource.shared.refresh()
            let entry = StatusWidgetEntry(date: Date(), state: state)
            completion(Timeline(entries: [entry], policy: .after(Date().addingTimeInterval(86_400))))
        }
    }
}

struct RetryStatusWidgetIntent: AppIntent {
    static var title: LocalizedStringResource = "위젯 다시 시도"
    static var isDiscoverable = false

    func perform() async throws -> some IntentResult {
        _ = await StatusWidgetDataSource.shared.refresh(force: true)
        WidgetCenter.shared.reloadTimelines(ofKind: "OBRitStatusWidget")
        return .result()
    }
}

private struct StatusWidgetNoticeView: View {
    let isEmpty: Bool

    var body: some View {
        GeometryReader { geometry in
            let scale = min(geometry.size.width / StatusWidgetStyle.width, geometry.size.height / StatusWidgetStyle.height)
            VStack(spacing: 12 * scale) {
                VStack(spacing: 4 * scale) {
                    Image(isEmpty ? "ic_widget_add_lined" : "ic_widget_warning_filled")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 32 * scale, height: 32 * scale)
                        .accessibilityHidden(true)
                    Text(isEmpty ? "등록된 소모품이 없어요" : "정보를 불러오지 못했어요")
                        .font(.custom("Pretendard-SemiBold", fixedSize: 10 * scale))
                        .tracking(-0.3 * scale)
                        .foregroundStyle(.white)
                        .lineLimit(1)
                        .minimumScaleFactor(0.7)
                        .frame(maxWidth: .infinity, minHeight: 16 * scale)
                }
                if !isEmpty {
                    Button(intent: RetryStatusWidgetIntent()) {
                        Text("다시 시도")
                            .font(.custom("Pretendard-SemiBold", fixedSize: 10 * scale))
                            .foregroundStyle(.white)
                            .padding(.horizontal, 8 * scale)
                            .frame(height: 24 * scale)
                            .background(StatusWidgetStyle.buttonBackground, in: Capsule())
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, 6 * scale)
            .frame(width: StatusWidgetStyle.width * scale, height: StatusWidgetStyle.height * scale)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }
}

private struct StatusWidgetView: View {
    let state: StatusWidgetState

    private var accentColor: Color {
        state.isGood ? StatusWidgetStyle.positive : StatusWidgetStyle.warning
    }

    var body: some View {
        GeometryReader { geometry in
            let scale = min(geometry.size.width / StatusWidgetStyle.width, geometry.size.height / StatusWidgetStyle.height)
            VStack(alignment: .leading, spacing: 0) {
                countRow(state.dangerCount, label: "위험", scale: scale)
                countRow(state.warningCount, label: "경고", scale: scale)
                Spacer().frame(height: 3 * scale)
                gauge(scale: scale)
            }
            .padding(.horizontal, 8 * scale)
            .padding(.top, 2 * scale)
            .padding(.bottom, 7 * scale)
            .frame(width: StatusWidgetStyle.width * scale, height: StatusWidgetStyle.height * scale, alignment: .topLeading)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }

    private func countRow(_ count: Int, label: String, scale: CGFloat) -> some View {
        HStack(alignment: .top, spacing: 0) {
            Text(count, format: .number)
                .font(.custom("Pretendard-SemiBold", fixedSize: 32 * scale))
                .tracking(-0.96 * scale)
                .foregroundStyle(accentColor)
                .minimumScaleFactor(0.4)
                .lineLimit(1)
                .frame(width: 41 * scale, height: 44 * scale, alignment: .leading)
                .frame(height: 38 * scale, alignment: .top)
            Text(label)
                .font(.custom("Pretendard-SemiBold", fixedSize: 10 * scale))
                .tracking(-0.3 * scale)
                .foregroundStyle(.white.opacity(0.3))
                .frame(height: 16 * scale)
                .padding(.top, 18 * scale)
        }
        .frame(height: 38 * scale, alignment: .top)
        .accessibilityElement(children: .ignore)
        .accessibilityLabel("\(label) \(count)개")
    }

    private func gauge(scale: CGFloat) -> some View {
        VStack(spacing: StatusWidgetStyle.dotSpacing * scale) {
            ForEach(0..<StatusWidgetState.rows, id: \.self) { row in
                HStack(spacing: StatusWidgetStyle.dotSpacing * scale) {
                    ForEach(0..<StatusWidgetState.columns, id: \.self) { column in
                        Circle()
                            .fill(accentColor.opacity(state.isFilled(rowFromTop: row, column: column) ? 1 : 0.2))
                            .frame(width: StatusWidgetStyle.dotDiameter * scale, height: StatusWidgetStyle.dotDiameter * scale)
                    }
                }
            }
        }
        .frame(maxWidth: .infinity)
        .accessibilityElement(children: .ignore)
        .accessibilityLabel(state.isGood ? "양호 구간" : "경고·위험 구간")
        .accessibilityValue("30개 중 \(state.filledDotCount)개")
    }
}

private enum StatusWidgetStyle {
    static let background = Color("WidgetBackgroundDefault")
    static let buttonBackground = Color("WidgetBackgroundHover")
    static let positive = Color("WidgetTextPositiveDefault")
    static let warning = Color("WidgetTextWarningDefault")
    static let width: CGFloat = 109
    static let height: CGFloat = 115
    static let dotDiameter: CGFloat = 8.31
    static let dotSpacing: CGFloat = 1.035
}

#Preview("양호 70%", as: .systemSmall) {
    OBRitStatusWidget()
} timeline: {
    StatusWidgetEntry.sample(goodRatio: 0.7)
    StatusWidgetEntry.sample(goodRatio: 0.6)
    StatusWidgetEntry.sample(goodRatio: 0.5)
}

#Preview("양호 40%", as: .systemSmall) {
    OBRitStatusWidget()
} timeline: {
    StatusWidgetEntry.sample(goodRatio: 0.4)
    StatusWidgetEntry.sample(goodRatio: 0.2)
    StatusWidgetEntry.sample(goodRatio: 0.499)
}

#Preview("조회 실패", as: .systemSmall) {
    OBRitStatusWidget()
} timeline: {
    StatusWidgetEntry(date: Date(), state: nil)
}

#Preview("소모품 없음", as: .systemSmall) {
    OBRitStatusWidget()
} timeline: {
    StatusWidgetEntry(date: Date(), state: StatusWidgetState(dangerCount: 0, warningCount: 0, goodRatio: 1, isEmpty: true))
}
