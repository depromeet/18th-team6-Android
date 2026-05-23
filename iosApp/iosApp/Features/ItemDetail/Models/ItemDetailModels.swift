import Foundation

struct ItemDetailConsumable: Identifiable, Equatable {
    let id: Int
    var name: String
    var kindName: String
    var imageAssetName: String
    var spareQuantity: Int
    var replacementCycle: ItemDetailReplacementCycle
    var currentCycleStartedAt: Date
    var notification: ItemDetailNotificationSetting
    var replacementRecords: [ItemDetailReplacementRecord]
    var createdAt: Date
    var updatedAt: Date

    func daysInUse(on referenceDate: Date, calendar: Calendar = .current) -> Int {
        calendar.nonNegativeDayDistance(from: currentCycleStartedAt, to: referenceDate)
    }

    func replacementDday(on referenceDate: Date, calendar: Calendar = .current) -> Int {
        replacementCycle.intervalDays - daysInUse(on: referenceDate, calendar: calendar)
    }

    func detailStatus(on referenceDate: Date, calendar: Calendar = .current) -> ItemDetailStatusLevel {
        let dday = replacementDday(on: referenceDate, calendar: calendar)

        if dday <= 0 || spareQuantity == ItemDetailConfig.minimumSpareQuantity {
            return .danger
        }

        if dday <= ItemDetailConfig.replacementWarningRemainingDays ||
            spareQuantity <= ItemDetailConfig.lowSpareQuantityThreshold {
            return .warning
        }

        return .normal
    }

    func completingReplacement(at completedAt: Date, calendar: Calendar = .current) -> ItemDetailConsumable {
        let nextRecordID = (replacementRecords.map(\.id).max() ?? 0) + 1
        let record = ItemDetailReplacementRecord(
            id: nextRecordID,
            replacedAt: completedAt,
            previousStartedAt: currentCycleStartedAt,
            memo: nil
        )

        var updated = self
        updated.spareQuantity = max(ItemDetailConfig.minimumSpareQuantity, spareQuantity - 1)
        updated.currentCycleStartedAt = calendar.startOfDay(for: completedAt)
        updated.replacementRecords = ([record] + replacementRecords)
            .sorted { $0.replacedAt > $1.replacedAt }
        updated.updatedAt = completedAt
        return updated
    }

    func updatingSpareQuantity(_ quantity: Int, at updatedAt: Date) -> ItemDetailConsumable {
        var updated = self
        updated.spareQuantity = min(
            max(quantity, ItemDetailConfig.minimumSpareQuantity),
            ItemDetailConfig.maximumSpareQuantity
        )
        updated.updatedAt = updatedAt
        return updated
    }
}

struct ItemDetailReplacementCycle: Equatable {
    var intervalDays: Int

    init(intervalDays: Int) {
        self.intervalDays = min(
            max(intervalDays, ItemDetailConfig.minimumReplacementCycleDays),
            ItemDetailConfig.maximumReplacementCycleDays
        )
    }

    var title: String {
        "\(intervalDays)일"
    }
}

struct ItemDetailReplacementRecord: Identifiable, Equatable {
    let id: Int
    let replacedAt: Date
    let previousStartedAt: Date
    let memo: String?

    func usedDays(calendar: Calendar = .current) -> Int {
        calendar.nonNegativeDayDistance(from: previousStartedAt, to: replacedAt)
    }
}

struct ItemDetailNotificationSetting: Equatable {
    var isEnabled: Bool
    var leadDays: Int
    var time: DateComponents
    var permissionStatus: ItemDetailNotificationPermissionStatus

    init(
        isEnabled: Bool,
        leadDays: Int = ItemDetailConfig.defaultReminderLeadDays,
        time: DateComponents = DateComponents(
            hour: ItemDetailConfig.defaultReminderHour,
            minute: ItemDetailConfig.defaultReminderMinute
        ),
        permissionStatus: ItemDetailNotificationPermissionStatus = .notDetermined
    ) {
        self.isEnabled = isEnabled
        self.leadDays = max(0, leadDays)
        self.time = time
        self.permissionStatus = permissionStatus
    }
}

enum ItemDetailNotificationPermissionStatus: Equatable {
    case notDetermined
    case authorized
    case denied
}

enum ItemDetailStatusLevel: Equatable {
    case normal
    case warning
    case danger

    var title: String {
        switch self {
        case .normal:
            return "양호"
        case .warning:
            return "경고"
        case .danger:
            return "위험"
        }
    }

    var badgeTitle: String {
        switch self {
        case .normal:
            return "상태 양호"
        case .warning:
            return "교체 경고"
        case .danger:
            return "교체 위험"
        }
    }

    var cardLevel: OBRitCardLevel {
        switch self {
        case .normal:
            return .l6
        case .warning:
            return .l3
        case .danger:
            return .l1
        }
    }
}

struct ItemDetailViewData: Equatable {
    let consumable: ItemDetailConsumable
    let statusSummary: ItemDetailStatusSummary
    let spareSummary: ItemDetailSpareSummary
    let replacementCycleSummary: ItemDetailReplacementCycleSummary
    let replacementRecords: [ItemDetailReplacementRecordViewData]
    let notificationSummary: ItemDetailNotificationSummary
    let editDraft: ItemDetailEditDraft
    let spareDraft: ItemDetailSpareDraft
    let moreMenuItems: [ItemDetailMoreMenuItem]
    let confirmationDialog: ItemDetailConfirmationDialog?
    let isProcessing: Bool
}

struct ItemDetailStatusSummary: Equatable {
    let level: ItemDetailStatusLevel
    let title: String
    let message: String
    let daysInUseLabel: String
    let replacementDday: Int
    let replacementDdayLabel: String
    let cardLevel: OBRitCardLevel
}

struct ItemDetailSpareSummary: Equatable {
    let quantity: Int
    let quantityLabel: String
    let statusTitle: String
    let isLowStock: Bool
}

struct ItemDetailReplacementCycleSummary: Equatable {
    let intervalDays: Int
    let intervalLabel: String
    let nextReplacementLabel: String
}

struct ItemDetailReplacementRecordViewData: Identifiable, Equatable {
    let id: Int
    let replacedAt: Date
    let dateLabel: String
    let usedDaysLabel: String
    let memo: String?
}

struct ItemDetailNotificationSummary: Equatable {
    let isEnabled: Bool
    let title: String
    let message: String
    let permissionStatus: ItemDetailNotificationPermissionStatus
}

struct ItemDetailEditDraft: Equatable {
    var name: String
    var replacementCycleDays: Int
    var imageAssetName: String
    var spareQuantity: Int

    init(consumable: ItemDetailConsumable) {
        self.name = consumable.name
        self.replacementCycleDays = consumable.replacementCycle.intervalDays
        self.imageAssetName = consumable.imageAssetName
        self.spareQuantity = consumable.spareQuantity
    }

    var isValid: Bool {
        let trimmedName = name.trimmingCharacters(in: .whitespacesAndNewlines)
        return !trimmedName.isEmpty &&
            trimmedName.count <= ItemDetailConfig.maximumNameLength &&
            replacementCycleDays >= ItemDetailConfig.minimumReplacementCycleDays &&
            replacementCycleDays <= ItemDetailConfig.maximumReplacementCycleDays &&
            spareQuantity >= ItemDetailConfig.minimumSpareQuantity &&
            spareQuantity <= ItemDetailConfig.maximumSpareQuantity
    }
}

struct ItemDetailSpareDraft: Equatable {
    var quantity: Int

    init(quantity: Int) {
        self.quantity = quantity
    }

    var isValid: Bool {
        quantity >= ItemDetailConfig.minimumSpareQuantity &&
            quantity <= ItemDetailConfig.maximumSpareQuantity
    }
}

enum ItemDetailMoreMenuItem: CaseIterable, Identifiable, Equatable {
    case edit
    case spareEdit
    case notification
    case delete

    var id: String {
        title
    }

    var title: String {
        switch self {
        case .edit:
            return "편집"
        case .spareEdit:
            return "여분 수정"
        case .notification:
            return "알림 설정"
        case .delete:
            return "삭제"
        }
    }
}

enum ItemDetailConfirmationDialog: Equatable {
    case delete

    var title: String {
        switch self {
        case .delete:
            return "소모품을 삭제할까요?"
        }
    }

    var message: String {
        switch self {
        case .delete:
            return "삭제하면 등록된 교체 기록과 알림 설정을 되돌릴 수 없어요."
        }
    }
}

enum ItemDetailViewState: Equatable {
    case loading
    case loadFailed(message: String)
    case success(ItemDetailViewData)
}

enum ItemDetailViewEffect: Equatable {
    case navigate(ItemDetailDestination)
    case itemDeleted(consumableId: Int)
    case replacementCompleted(consumableId: Int)
    case showMessage(String)
}

enum ItemDetailDestination: Equatable {
    case statusInfo(consumableId: Int)
    case edit(consumableId: Int)
    case spareEdit(consumableId: Int)
    case notification(consumableId: Int)
}

extension ItemDetailViewData {
    init(
        consumable: ItemDetailConsumable,
        referenceDate: Date,
        calendar: Calendar = .current,
        confirmationDialog: ItemDetailConfirmationDialog? = nil,
        isProcessing: Bool = false
    ) {
        let status = consumable.detailStatus(on: referenceDate, calendar: calendar)
        let daysInUse = consumable.daysInUse(on: referenceDate, calendar: calendar)
        let replacementDday = consumable.replacementDday(on: referenceDate, calendar: calendar)

        self.consumable = consumable
        self.statusSummary = ItemDetailStatusSummary(
            level: status,
            title: status.badgeTitle,
            message: Self.statusMessage(for: status, replacementDday: replacementDday, spareQuantity: consumable.spareQuantity),
            daysInUseLabel: "\(daysInUse)일째 사용중",
            replacementDday: replacementDday,
            replacementDdayLabel: replacementDday.itemDetailDdayText,
            cardLevel: status.cardLevel
        )
        self.spareSummary = ItemDetailSpareSummary(
            quantity: consumable.spareQuantity,
            quantityLabel: "여분 \(consumable.spareQuantity)개",
            statusTitle: consumable.spareQuantity <= ItemDetailConfig.lowSpareQuantityThreshold ? "여분 부족" : "여분 충분",
            isLowStock: consumable.spareQuantity <= ItemDetailConfig.lowSpareQuantityThreshold
        )
        self.replacementCycleSummary = ItemDetailReplacementCycleSummary(
            intervalDays: consumable.replacementCycle.intervalDays,
            intervalLabel: consumable.replacementCycle.title,
            nextReplacementLabel: "다음 교체 \(replacementDday.itemDetailDdayText)"
        )
        self.replacementRecords = consumable.replacementRecords
            .sorted { $0.replacedAt > $1.replacedAt }
            .prefix(ItemDetailConfig.visibleReplacementRecordLimit)
            .map {
                ItemDetailReplacementRecordViewData(
                    id: $0.id,
                    replacedAt: $0.replacedAt,
                    dateLabel: $0.replacedAt.itemDetailDateLabel,
                    usedDaysLabel: "\($0.usedDays(calendar: calendar))일 사용",
                    memo: $0.memo
                )
            }
        self.notificationSummary = ItemDetailNotificationSummary(setting: consumable.notification)
        self.editDraft = ItemDetailEditDraft(consumable: consumable)
        self.spareDraft = ItemDetailSpareDraft(quantity: consumable.spareQuantity)
        self.moreMenuItems = [.edit, .delete]
        self.confirmationDialog = confirmationDialog
        self.isProcessing = isProcessing
    }

    private static func statusMessage(
        for status: ItemDetailStatusLevel,
        replacementDday: Int,
        spareQuantity: Int
    ) -> String {
        switch status {
        case .normal:
            return "교체 주기와 여분 수량이 안정적이에요."
        case .warning:
            if replacementDday <= ItemDetailConfig.replacementWarningRemainingDays {
                return "교체일이 가까워지고 있어요."
            }
            return spareQuantity <= ItemDetailConfig.lowSpareQuantityThreshold ? "여분 수량을 확인해 주세요." : "상태를 확인해 주세요."
        case .danger:
            if replacementDday <= 0 {
                return "교체 예정일이 지났어요."
            }
            return "바로 사용할 여분이 없어요."
        }
    }
}

private extension ItemDetailNotificationSummary {
    init(setting: ItemDetailNotificationSetting) {
        self.isEnabled = setting.isEnabled
        self.permissionStatus = setting.permissionStatus

        guard setting.isEnabled else {
            self.title = "알림 꺼짐"
            self.message = "교체 알림을 받지 않아요."
            return
        }

        self.title = "알림 켜짐"
        self.message = "교체 \(setting.leadDays)일 전 \(setting.time.itemDetailTimeLabel)에 알려드려요."
    }
}

private extension Calendar {
    func nonNegativeDayDistance(from startDate: Date, to endDate: Date) -> Int {
        let start = startOfDay(for: startDate)
        let end = startOfDay(for: endDate)
        return max(dateComponents([.day], from: start, to: end).day ?? 0, 0)
    }
}

private extension Date {
    var itemDetailDateLabel: String {
        formatted(.dateTime.year().month(.twoDigits).day(.twoDigits))
    }
}

private extension Int {
    var itemDetailDdayText: String {
        if self < 0 {
            return "D+\(abs(self))"
        }

        if self == 0 {
            return "D-day"
        }

        return "D-\(self)"
    }
}

private extension DateComponents {
    var itemDetailTimeLabel: String {
        let hour = hour ?? ItemDetailConfig.defaultReminderHour
        let minute = minute ?? ItemDetailConfig.defaultReminderMinute
        return String(format: "%02d:%02d", hour, minute)
    }
}
