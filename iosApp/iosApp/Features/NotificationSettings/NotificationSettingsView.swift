import SwiftUI
import UIKit

struct NotificationSettingsView: View {
    @StateObject private var viewModel: NotificationSettingsViewModel
    @State private var isTimePickerPresented = false
    @State private var isOpeningNotificationSettings = false
    @State private var draftTime = Date()

    let onBack: () -> Void

    @MainActor
    init(
        viewModelFactory: @MainActor @escaping () -> NotificationSettingsViewModel,
        onBack: @escaping () -> Void
    ) {
        _viewModel = StateObject(wrappedValue: viewModelFactory())
        self.onBack = onBack
    }

    var body: some View {
        Group {
            switch viewModel.state {
            case .loading:
                unavailableContent { ProgressView().tint(OBRitColors.green300) }
            case .loadFailed:
                unavailableContent {
                    VStack(spacing: OBRitSpacing.s4) {
                        Text("알림 설정을 불러오지 못했어요.")
                            .foregroundStyle(OBRitColors.textDefaultDefault)
                        OBRitFilledTextButton(text: "다시 시도", size: .large, color: .green) {
                            viewModel.retry()
                        }
                    }
                }
            case let .success(settings):
                NotificationSettingsContentView(
                    settings: settings,
                    action: NotificationSettingsViewAction(
                        onBack: onBack,
                        onPermission: openNotificationSettings,
                        onToggle: viewModel.setToggle,
                        onSelectTime: {
                            draftTime = settings.date
                            isTimePickerPresented = true
                        }
                    )
                )
            }
        }
        .task {
            await viewModel.loadIfNeeded()
        }
        .preferredColorScheme(.dark)
        .sheet(isPresented: $isTimePickerPresented) { timePicker }
        .alert("알림 권한", isPresented: permissionErrorPresented) {
            Button("확인", role: .cancel) { viewModel.permissionError = nil }
        } message: {
            Text(viewModel.permissionError ?? "")
        }
        .alert("알림 설정 저장 실패", isPresented: saveErrorPresented) {
            Button("확인", role: .cancel) { viewModel.saveError = nil }
        } message: {
            Text(viewModel.saveError ?? "")
        }
    }

    @ViewBuilder
    private func unavailableContent<Content: View>(@ViewBuilder content: () -> Content) -> some View {
        VStack(spacing: 0) {
            OBRitDepthTopBar(title: "알림 설정", showRightButton: false, onBackClick: onBack)
            content().frame(maxWidth: .infinity, maxHeight: .infinity)
        }
        .background(OBRitColors.backgroundDefaultDefault.ignoresSafeArea())
        .toolbar(.hidden, for: .navigationBar)
    }

    private var permissionErrorPresented: Binding<Bool> {
        Binding(
            get: { viewModel.permissionError != nil },
            set: { if !$0 { viewModel.permissionError = nil } }
        )
    }

    private var saveErrorPresented: Binding<Bool> {
        Binding(
            get: { viewModel.saveError != nil },
            set: { if !$0 { viewModel.saveError = nil } }
        )
    }

    private func openNotificationSettings() {
        guard !isOpeningNotificationSettings else { return }
        isOpeningNotificationSettings = true
        Task { @MainActor in
            defer { isOpeningNotificationSettings = false }
            guard let url = URL(string: UIApplication.openNotificationSettingsURLString) else { return }
            let didOpen = await UIApplication.shared.open(url, options: [:])
            if !didOpen {
                viewModel.permissionError = "시스템 알림 설정을 열지 못했어요. 잠시 후 다시 시도해 주세요."
            }
        }
    }

    private var timePicker: some View {
        NavigationStack {
            VStack(spacing: OBRitSpacing.s4) {
                NotificationTimePicker(selection: $draftTime)
                    .frame(maxWidth: .infinity)
                OBRitFilledTextButton(text: "확인", size: .large, color: .green, fillsWidth: true) {
                    viewModel.setTime(draftTime)
                    isTimePickerPresented = false
                }
                .padding(.horizontal, OBRitSpacing.s5)
            }
            .padding(.bottom, OBRitSpacing.s5)
            .background(OBRitColors.backgroundDefaultDefault)
            .navigationTitle("알림 시간 설정")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("취소") { isTimePickerPresented = false }
                }
            }
        }
        .presentationDetents([.height(380)])
        .presentationDragIndicator(.visible)
        .preferredColorScheme(.dark)
    }
}

private struct NotificationSettingsViewAction {
    let onBack: () -> Void
    let onPermission: () -> Void
    let onToggle: (WritableKeyPath<NotificationSettings, Bool>, Bool) -> Void
    let onSelectTime: () -> Void
}

private struct NotificationSettingsContentView: View {
    let settings: NotificationSettings
    let action: NotificationSettingsViewAction

    var body: some View {
        VStack(spacing: 0) {
            OBRitDepthTopBar(title: "알림 설정", showRightButton: false, onBackClick: action.onBack)
            ScrollView {
                VStack(alignment: .leading, spacing: OBRitSpacing.s6) {
                    permissionBanner
                    VStack(alignment: .leading, spacing: OBRitSpacing.s7) {
                        replacementSection
                        stockSection
                        timeSection
                    }
                }
                .padding(.horizontal, OBRitSpacing.s5)
                .padding(.top, OBRitSpacing.s2_5)
                .padding(.bottom, OBRitSpacing.s6)
            }
        }
        .background(OBRitColors.backgroundDefaultDefault.ignoresSafeArea())
        .toolbar(.hidden, for: .navigationBar)
    }

    private var permissionBanner: some View {
        Button(action: action.onPermission) {
            HStack(spacing: OBRitSpacing.s2) {
                Image("notification_settings_bell")
                    .frame(width: 20, height: 20)
                Text("알림받기를 설정하고 유용한 알림들을 받아보세요!")
                    .obritTextStyle(OBRitTypography.s, weight: OBRitFontWeight.semiBold, color: OBRitColors.green300)
                    .fixedSize(horizontal: false, vertical: true)
                Spacer(minLength: 0)
                Image("notification_settings_chevron")
                    .scaleEffect(x: -1, y: 1)
                    .frame(width: 20, height: 20)
            }
            .padding(.horizontal, OBRitSpacing.s3)
            .padding(.vertical, OBRitSpacing.s2)
            .background(OBRitColors.green850, in: RoundedRectangle(cornerRadius: OBRitRadius.small))
        }
        .buttonStyle(.plain)
        .accessibilityHint("iOS 시스템의 앱 알림 설정 화면으로 이동합니다")
    }

    private var replacementSection: some View {
        section("교체 알림") {
            VStack(spacing: OBRitSpacing.s0_5) {
                toggleRow("교체 예정 알림", subtitle: "교체 예정일이 다가오면 미리 알려드려요", keyPath: \.replacementUpcoming)
                    .overlay(alignment: .bottom) {
                        Rectangle().fill(OBRitColors.borderDefaultDefault).frame(height: 1)
                    }
                toggleRow("교체 지연 알림", subtitle: "교체 예정일이 지나면 다시 알려드려요", keyPath: \.replacementOverdue)
            }
        }
    }

    private var stockSection: some View {
        section("여분 알림") {
            toggleRow("여분 부족 알림", subtitle: "여분이 없고 교체 예정일이 가까운 항목을 알려드려요", keyPath: \.lowStock)
        }
    }

    private var timeSection: some View {
        section("알림 시간") {
            Button(action: action.onSelectTime) {
                HStack(spacing: OBRitSpacing.s2) {
                    rowLabel("알림 시간 설정", subtitle: "알림을 받을 시간을 설정해 주세요")
                    Spacer(minLength: 0)
                    Text(settings.timeLabel)
                        .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultDefault)
                        .fixedSize()
                    Image("notification_settings_time_chevron")
                        .scaleEffect(x: -1, y: 1)
                        .frame(width: 20, height: 20)
                }
                .padding(.vertical, OBRitSpacing.s3)
                .contentShape(Rectangle())
            }
            .buttonStyle(.plain)
            .accessibilityElement(children: .combine)
        }
    }

    private func section<Content: View>(_ title: String, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s4) {
            Text(title)
                .obritTextStyle(OBRitTypography.lg, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultSecondary)
                .accessibilityAddTraits(.isHeader)
            content()
                .padding(.horizontal, OBRitSpacing.s4)
                .padding(.vertical, OBRitSpacing.s2)
                .background(OBRitColors.backgroundDefaultSecondary, in: RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
        }
    }

    private func toggleRow(_ title: String, subtitle: String, keyPath: WritableKeyPath<NotificationSettings, Bool>) -> some View {
        Toggle(isOn: Binding(
            get: { settings[keyPath: keyPath] },
            set: { action.onToggle(keyPath, $0) }
        )) {
            rowLabel(title, subtitle: subtitle)
        }
        .toggleStyle(NotificationSettingsToggleStyle())
        .padding(.vertical, OBRitSpacing.s3)
    }

    private func rowLabel(_ title: String, subtitle: String) -> some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s0_5) {
            Text(title)
                .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.semiBold, color: OBRitColors.textDefaultDefault)
            Text(subtitle)
                .obritTextStyle(OBRitTypography.xs, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultTertiary)
                .fixedSize(horizontal: false, vertical: true)
        }
    }
}

private struct NotificationSettingsToggleStyle: ToggleStyle {
    func makeBody(configuration: Configuration) -> some View {
        Button {
            configuration.isOn.toggle()
        } label: {
            HStack(spacing: OBRitSpacing.s4) {
                configuration.label
                    .frame(maxWidth: .infinity, alignment: .leading)
                Image(configuration.isOn ? "notification_settings_toggle_on" : "notification_settings_toggle_off")
                    .frame(width: 44, height: 24)
            }
            .frame(minHeight: 44)
            .contentShape(Rectangle())
        }
        .buttonStyle(NotificationSettingsToggleButtonStyle())
        .accessibilityElement(children: .combine)
        .accessibilityValue(configuration.isOn ? "켜짐" : "꺼짐")
        .accessibilityHint("이중 탭하여 변경")
    }
}

private struct NotificationSettingsToggleButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
    }
}

#Preview {
    NotificationSettingsContentView(
        settings: NotificationSettings(),
        action: NotificationSettingsViewAction(onBack: {}, onPermission: {}, onToggle: { _, _ in }, onSelectTime: {})
    )
    .preferredColorScheme(.dark)
}

private struct NotificationTimePicker: UIViewRepresentable {
    @Binding var selection: Date

    func makeUIView(context: Context) -> UIDatePicker {
        let picker = UIDatePicker()
        picker.datePickerMode = .time
        picker.preferredDatePickerStyle = .wheels
        picker.minuteInterval = 30
        picker.locale = Locale(identifier: "ko_KR")
        picker.addTarget(context.coordinator, action: #selector(Coordinator.changed(_:)), for: .valueChanged)
        return picker
    }

    func updateUIView(_ picker: UIDatePicker, context: Context) {
        context.coordinator.selection = $selection
        if picker.date != selection { picker.setDate(selection, animated: false) }
    }

    func makeCoordinator() -> Coordinator { Coordinator(selection: $selection) }

    final class Coordinator: NSObject {
        var selection: Binding<Date>

        init(selection: Binding<Date>) { self.selection = selection }

        @objc func changed(_ picker: UIDatePicker) { selection.wrappedValue = picker.date }
    }
}
