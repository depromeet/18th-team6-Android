import Foundation

struct ItemDetailCompletionModalData: Equatable {
    let itemName: String
    let imageURL: String
    let remainingSpareQuantity: Int
    let isLowStock: Bool
    let daysComparedToPrevious: Int
    let nextReplacementLabel: String
    let recordedAtText: String

    init(data: ItemDetailViewData) {
        let intervalDays = data.item.replacementCycle.intervalDays
        let records = data.item.replacementRecords.sorted { $0.replacedAt > $1.replacedAt }
        let latestUsedDays = records.first?.usedDays() ?? intervalDays
        let previousUsedDays = records.dropFirst().first?.usedDays() ?? latestUsedDays
        let nextReplacementDate = Calendar.current.date(
            byAdding: .day,
            value: intervalDays,
            to: data.item.currentCycleStartedAt
        ) ?? data.item.currentCycleStartedAt

        self.itemName = data.item.name
        self.imageURL = data.item.imageURL
        self.remainingSpareQuantity = data.item.spareQuantity
        self.isLowStock = data.spareSummary.isLowStock
        self.daysComparedToPrevious = latestUsedDays - previousUsedDays
        self.nextReplacementLabel = "\(nextReplacementDate.itemDetailMonthDayText)(\(intervalDays)일 후)"
        self.recordedAtText = Date().itemDetailRecordedAtText
    }
}
