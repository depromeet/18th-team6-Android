import SwiftUI
import UserNotifications

struct NotificationInboxView: View {
    @ObservedObject var viewModel: NotificationInboxViewModel
    @Environment(\.scenePhase) private var scenePhase
    @State private var showsPermissionBanner = false

    let onBack: () -> Void
    let onSettings: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            header
            NotificationInboxContentView(
                state: viewModel.state,
                unreadCount: viewModel.unreadCount,
                isProcessing: viewModel.isProcessing,
                showsPermissionBanner: showsPermissionBanner,
                action: NotificationInboxViewAction(
                    onSelect: viewModel.select,
                    onReadAll: viewModel.markAllRead,
                    onRefresh: { await viewModel.refresh() }
                )
            )
        }
        .background(OBRitColors.backgroundDefaultDefault.ignoresSafeArea())
        .task {
            await updatePermissionBanner()
            await viewModel.refresh()
        }
        .onChange(of: scenePhase) { _, phase in
            if phase == .active { Task { await updatePermissionBanner() } }
        }
    }

    private var header: some View {
        HStack {
            Button(action: onBack) {
                Image("notification_inbox_back").frame(width: 24, height: 24)
                    .frame(width: 40, height: 40)
            }
            .accessibilityLabel("뒤로가기")
            Spacer()
            Text("알림")
                .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
            Spacer()
            Button(action: onSettings) {
                Image("notification_inbox_settings").frame(width: 24, height: 24)
                    .frame(width: 40, height: 40)
            }
            .accessibilityLabel("알림 설정")
        }
        .buttonStyle(.plain)
        .padding(.horizontal, OBRitSpacing.s3)
        .frame(height: 56)
    }

    private func updatePermissionBanner() async {
        let settings = await UNUserNotificationCenter.current().notificationSettings()
        showsPermissionBanner = settings.authorizationStatus == .denied || settings.authorizationStatus == .notDetermined
    }
}

private struct NotificationInboxViewAction {
    let onSelect: (NotificationInboxCard) -> Void
    let onReadAll: () -> Void
    let onRefresh: () async -> Void
}

private struct NotificationInboxContentView: View {
    let state: NotificationInboxViewState
    let unreadCount: Int
    let isProcessing: Bool
    let showsPermissionBanner: Bool
    let action: NotificationInboxViewAction

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: OBRitSpacing.s5) {
                if showsPermissionBanner { permissionBanner }
                switch state {
                case .loading:
                    ProgressView().tint(OBRitColors.green300)
                        .frame(maxWidth: .infinity, minHeight: 240)
                case .loadFailed:
                    VStack(spacing: OBRitSpacing.s4) {
                        Text("알림을 불러오지 못했어요.")
                            .foregroundStyle(OBRitColors.textDefaultSecondary)
                        OBRitFilledTextButton(text: "다시 시도", size: .small) {
                            Task { await action.onRefresh() }
                        }
                    }
                    .frame(maxWidth: .infinity, minHeight: 240)
                case let .success(cards):
                    if cards.isEmpty {
                        Text("아직 도착한 알림이 없어요")
                            .obritTextStyle(OBRitTypography.xl, color: OBRitColors.textDefaultSecondary)
                            .frame(maxWidth: .infinity, minHeight: 240)
                    } else {
                        HStack {
                            Spacer()
                            Button("전체 읽음", action: action.onReadAll)
                                .obritTextStyle(OBRitTypography.small, color: OBRitColors.textDefaultSecondary)
                                .disabled(unreadCount == 0 || isProcessing)
                        }
                        LazyVStack(alignment: .leading, spacing: OBRitSpacing.s6) {
                            ForEach(sections(cards)) { section in
                                VStack(alignment: .leading, spacing: OBRitSpacing.s4) {
                                    Text(section.title)
                                        .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.bold, color: section.title == "오늘" ? OBRitColors.green300 : OBRitColors.gray450)
                                    ForEach(section.cards) { card in
                                        NotificationInboxCardView(card: card) { action.onSelect(card) }
                                            .disabled(isProcessing)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            .padding(.horizontal, OBRitSpacing.s5)
            .padding(.top, OBRitSpacing.s2_5)
            .padding(.bottom, OBRitSpacing.s6)
        }
        .refreshable { await action.onRefresh() }
    }

    private var permissionBanner: some View {
        HStack(spacing: OBRitSpacing.s1_5) {
            Image("notification_settings_bell").frame(width: 20, height: 20)
            Text("알림받기를 설정하고 유용한 알림들을 받아보세요!")
                .obritTextStyle(OBRitTypography.small, weight: OBRitFontWeight.semiBold, color: OBRitColors.green300)
                .fixedSize(horizontal: false, vertical: true)
            Spacer(minLength: 0)
        }
        .padding(.horizontal, OBRitSpacing.s3)
        .padding(.vertical, OBRitSpacing.s2)
        .background(OBRitColors.green850, in: RoundedRectangle(cornerRadius: OBRitRadius.small))
    }

    private func sections(_ cards: [NotificationInboxCard]) -> [NotificationInboxSection] {
        var result: [NotificationInboxSection] = []
        for card in cards {
            let title = NotificationInboxDate.groupLabel(for: card.date)
            if let index = result.firstIndex(where: { $0.title == title }) {
                result[index].cards.append(card)
            } else {
                result.append(NotificationInboxSection(title: title, cards: [card]))
            }
        }
        return result
    }
}

private struct NotificationInboxSection: Identifiable {
    let title: String
    var cards: [NotificationInboxCard]
    var id: String { title }
}

private struct NotificationInboxCardView: View {
    let card: NotificationInboxCard
    let onSelect: () -> Void

    var body: some View {
        Button(action: onSelect) {
            VStack(alignment: .leading, spacing: OBRitSpacing.s4) {
                HStack {
                    if let label = card.label {
                        Text(label)
                            .obritTextStyle(OBRitTypography.xs, weight: OBRitFontWeight.bold, color: OBRitColors.textWarningDefault)
                            .padding(.horizontal, OBRitSpacing.s2)
                            .padding(.vertical, OBRitSpacing.s1)
                            .background(OBRitColors.red800, in: RoundedRectangle(cornerRadius: OBRitRadius.small))
                    }
                    Spacer(minLength: 0)
                    HStack(alignment: .top, spacing: OBRitSpacing.s1) {
                        Text(NotificationInboxDate.relativeTime(for: card.date))
                            .obritTextStyle(OBRitTypography.xs, color: OBRitColors.common00.opacity(0.3))
                        if !card.isRead {
                            Image("notification_inbox_unread").frame(width: 4, height: 4)
                        }
                    }
                }
                VStack(alignment: .leading, spacing: OBRitSpacing.s2) {
                    HStack(spacing: OBRitSpacing.s2_5) {
                        Circle().fill(OBRitColors.gray750).frame(width: 32, height: 32)
                        Text(card.title)
                            .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.semiBold, color: OBRitColors.common00)
                    }
                    if let name = card.itemName, !name.isEmpty {
                        Text(name)
                            .obritTextStyle(OBRitTypography.base, color: OBRitColors.textDefaultSecondary)
                    }
                    if let content = card.content, content != card.title {
                        Text(content)
                            .obritTextStyle(OBRitTypography.base, color: OBRitColors.textDefaultSecondary)
                    }
                    if let date = card.nextReplacementDate {
                        Text("다음 예상 교체일 : \(date)")
                            .obritTextStyle(OBRitTypography.base, color: OBRitColors.textDefaultTertiary)
                    }
                    if card.hasLowStock {
                        Text("남은 여분 : 0개")
                            .obritTextStyle(OBRitTypography.base, color: OBRitColors.textDefaultTertiary)
                    }
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.horizontal, OBRitSpacing.s4)
            .padding(.vertical, 14)
            .background(OBRitColors.backgroundDefaultSecondary, in: RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
            .overlay {
                RoundedRectangle(cornerRadius: OBRitRadius.extraLarge)
                    .stroke(OBRitColors.gray700.opacity(0.5), lineWidth: 1)
            }
            .multilineTextAlignment(.leading)
        }
        .buttonStyle(.plain)
        .accessibilityValue(card.isRead ? "읽은 알림" : "읽지 않은 알림")
    }
}

#Preview {
    NotificationInboxContentView(
        state: .success([]), unreadCount: 0, isProcessing: false, showsPermissionBanner: true,
        action: NotificationInboxViewAction(onSelect: { _ in }, onReadAll: {}, onRefresh: {})
    )
    .background(OBRitColors.backgroundDefaultDefault)
}
