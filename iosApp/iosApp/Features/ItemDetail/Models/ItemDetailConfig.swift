import Foundation

enum ItemDetailConfig {
    static let minimumSpareQuantity = 0
    static let maximumSpareQuantity = 99
    static let minimumReplacementCycleDays = 1
    static let maximumReplacementCycleDays = 365
    static let replacementWarningRemainingDays = 3
    static let lowSpareQuantityThreshold = 1
    static let visibleReplacementRecordLimit = 5
    static let maximumNameLength = 30
    static let defaultReminderLeadDays = 1
    static let defaultReminderHour = 9
    static let defaultReminderMinute = 0
}
