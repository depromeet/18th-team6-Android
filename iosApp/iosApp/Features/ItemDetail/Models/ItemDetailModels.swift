import Foundation

struct ItemDetailItem: Identifiable, Equatable {
    let id: Int
    var name: String
    var kindId: Int
    var kindName: String
    var imageURL: String
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

        if dday <= ItemDetailConfig.replacementWarningRemainingDays ||
            spareQuantity <= ItemDetailConfig.lowSpareQuantityThreshold {
            return .warning
        }

        return .normal
    }

    func completingReplacement(at completedAt: Date, calendar: Calendar = .current) -> ItemDetailItem {
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

    func updatingSpareQuantity(_ quantity: Int, at updatedAt: Date) -> ItemDetailItem {
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

    var title: String {
        switch self {
        case .normal:
            return "양호"
        case .warning:
            return "경고"
        }
    }

    var badgeTitle: String {
        switch self {
        case .normal:
            return "상태 양호"
        case .warning:
            return "교체 경고"
        }
    }

    var cardLevel: OBRitCardLevel {
        switch self {
        case .normal:
            return .l6
        case .warning:
            return .l3
        }
    }
}

struct ItemDetailViewData: Equatable {
    let item: ItemDetailItem
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
    var selectedKind: ItemKind?
    var replacementCycleDays: Int
    var imageURL: String
    var spareQuantity: Int

    init(item: ItemDetailItem) {
        self.name = item.name
        self.selectedKind = ItemKind(
            id: item.kindId,
            title: item.kindName,
            addedCount: 0,
            imageURL: item.imageURL
        )
        self.replacementCycleDays = item.replacementCycle.intervalDays
        self.imageURL = item.imageURL
        self.spareQuantity = item.spareQuantity
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

struct ItemDetailEditValidation: Equatable {
    var nameErrorMessage: String?

    static let valid = ItemDetailEditValidation(nameErrorMessage: nil)
    static let emptyName = ItemDetailEditValidation(nameErrorMessage: "소모품명을 입력해주세요")
    static let duplicateName = ItemDetailEditValidation(nameErrorMessage: "다른 이름과 중복되지 않게 입력해주세요")
    static let invalidNameCharacters = ItemDetailEditValidation(nameErrorMessage: "한글, 영문, 숫자, 공백, -, _, /, (, )만 사용할 수 있어요")
}

struct ItemDetailEditValidationResult: Equatable {
    let nameHelperMessage: String?
    let blockingNameMessage: String?
    let canSubmit: Bool
}

enum ItemDetailEditInputPolicy {
    static func clippedName(_ value: String) -> (text: String, didOverflow: Bool) {
        (
            text: String(value.prefix(ItemDetailConfig.maximumNameLength)),
            didOverflow: value.count > ItemDetailConfig.maximumNameLength
        )
    }

    static func replacementCycleInput(from value: String) -> String {
        let maximumDigitCount = "\(ItemDetailConfig.maximumReplacementCycleDays)".count
        return String(value.filter(\.isNumber).prefix(maximumDigitCount))
    }

    static func replacementCycleDays(from input: String) -> Int? {
        Int(input).map(clampedReplacementCycleDays)
    }

    static func clampedReplacementCycleDays(_ value: Int) -> Int {
        min(
            max(value, ItemDetailConfig.minimumReplacementCycleDays),
            ItemDetailConfig.maximumReplacementCycleDays
        )
    }

    static func validate(
        draft: ItemDetailEditDraft,
        originalName: String,
        existingItemNames: [String],
        replacementCycleInput: String,
        hasAttemptedNameOverflow: Bool,
        externalValidation: ItemDetailEditValidation,
        isProcessing: Bool
    ) -> ItemDetailEditValidationResult {
        let blockingNameMessage = Self.blockingNameMessage(
            for: draft.name,
            originalName: originalName,
            existingItemNames: existingItemNames,
            externalValidation: externalValidation
        )
        let nameHelperMessage = hasAttemptedNameOverflow
            ? "\(ItemDetailConfig.maximumNameLength)자 이내로 입력해주세요"
            : blockingNameMessage
        let canSubmit = draft.isValid &&
            isReplacementCycleInputValid(replacementCycleInput) &&
            blockingNameMessage == nil &&
            !isProcessing

        return ItemDetailEditValidationResult(
            nameHelperMessage: nameHelperMessage,
            blockingNameMessage: blockingNameMessage,
            canSubmit: canSubmit
        )
    }

    private static func blockingNameMessage(
        for name: String,
        originalName: String,
        existingItemNames: [String],
        externalValidation: ItemDetailEditValidation
    ) -> String? {
        let currentName = normalizedName(name)
        if currentName.isEmpty {
            return ItemDetailEditValidation.emptyName.nameErrorMessage
        }

        if hasInvalidNameCharacters(currentName) {
            return ItemDetailEditValidation.invalidNameCharacters.nameErrorMessage
        }

        if hasDuplicateName(currentName, originalName: originalName, existingItemNames: existingItemNames) {
            return ItemDetailEditValidation.duplicateName.nameErrorMessage
        }

        return externalValidation.nameErrorMessage
    }

    private static func isReplacementCycleInputValid(_ input: String) -> Bool {
        guard let value = Int(input) else { return false }

        return value >= ItemDetailConfig.minimumReplacementCycleDays &&
            value <= ItemDetailConfig.maximumReplacementCycleDays
    }

    private static func hasInvalidNameCharacters(_ name: String) -> Bool {
        name.range(
            of: #"^[가-힣ㄱ-ㅎㅏ-ㅣA-Za-z0-9 _\-/()]+$"#,
            options: .regularExpression
        ) == nil
    }

    private static func hasDuplicateName(
        _ name: String,
        originalName: String,
        existingItemNames: [String]
    ) -> Bool {
        guard !namesMatch(name, originalName) else {
            return false
        }

        return existingItemNames.contains { namesMatch($0, name) }
    }

    private static func normalizedName(_ name: String) -> String {
        name.trimmingCharacters(in: .whitespacesAndNewlines)
    }

    private static func namesMatch(_ lhs: String, _ rhs: String) -> Bool {
        normalizedName(lhs).localizedCaseInsensitiveCompare(normalizedName(rhs)) == .orderedSame
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
    case itemDeleted(itemId: Int)
    case itemUpdated(message: String)
    case replacementCompleted(itemId: Int)
    case showMessage(String)
}

enum ItemDetailDestination: Equatable {
    case statusInfo(itemId: Int)
    case edit(itemId: Int)
    case spareEdit(itemId: Int)
    case notification(itemId: Int)
}

extension ItemDetailViewData {
    init(
        item: ItemDetailItem,
        referenceDate: Date,
        calendar: Calendar = .current,
        confirmationDialog: ItemDetailConfirmationDialog? = nil,
        isProcessing: Bool = false
    ) {
        let status = item.detailStatus(on: referenceDate, calendar: calendar)
        let daysInUse = item.daysInUse(on: referenceDate, calendar: calendar)
        let replacementDday = item.replacementDday(on: referenceDate, calendar: calendar)

        self.item = item
        self.statusSummary = ItemDetailStatusSummary(
            level: status,
            title: status.badgeTitle,
            message: Self.statusMessage(for: status, replacementDday: replacementDday, spareQuantity: item.spareQuantity),
            daysInUseLabel: "\(daysInUse)일째 사용중",
            replacementDday: replacementDday,
            replacementDdayLabel: replacementDday.itemDetailDdayText,
            cardLevel: status.cardLevel
        )
        self.spareSummary = ItemDetailSpareSummary(
            quantity: item.spareQuantity,
            quantityLabel: "여분 \(item.spareQuantity)개",
            statusTitle: item.spareQuantity <= ItemDetailConfig.lowSpareQuantityThreshold ? "여분 부족" : "여분 충분",
            isLowStock: item.spareQuantity <= ItemDetailConfig.lowSpareQuantityThreshold
        )
        self.replacementCycleSummary = ItemDetailReplacementCycleSummary(
            intervalDays: item.replacementCycle.intervalDays,
            intervalLabel: item.replacementCycle.title,
            nextReplacementLabel: "다음 교체 \(replacementDday.itemDetailDdayText)"
        )
        self.replacementRecords = item.replacementRecords
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
        self.notificationSummary = ItemDetailNotificationSummary(setting: item.notification)
        self.editDraft = ItemDetailEditDraft(item: item)
        self.spareDraft = ItemDetailSpareDraft(quantity: item.spareQuantity)
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
            if replacementDday <= 0 {
                return "교체 예정일이 지났어요."
            }
            if spareQuantity == ItemDetailConfig.minimumSpareQuantity {
                return "바로 사용할 여분이 없어요."
            }
            if replacementDday <= ItemDetailConfig.replacementWarningRemainingDays {
                return "교체일이 가까워지고 있어요."
            }
            return spareQuantity <= ItemDetailConfig.lowSpareQuantityThreshold ? "여분 수량을 확인해 주세요." : "상태를 확인해 주세요."
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
