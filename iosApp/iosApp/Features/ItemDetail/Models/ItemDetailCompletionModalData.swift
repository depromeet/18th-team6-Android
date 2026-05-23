import Foundation

struct ItemDetailCompletionModalData: Equatable {
    let itemName: String
    let imageAssetName: String
    let remainingSpareQuantity: Int
    let isLowStock: Bool
    let daysComparedToPrevious: Int
    let nextReplacementLabel: String
    let recordedAtText: String

    init(data: ItemDetailViewData) {
        let intervalDays = data.consumable.replacementCycle.intervalDays
        let records = data.consumable.replacementRecords.sorted { $0.replacedAt > $1.replacedAt }
        let latestUsedDays = records.first?.usedDays() ?? intervalDays
        let previousUsedDays = records.dropFirst().first?.usedDays() ?? latestUsedDays
        let nextReplacementDate = Calendar.current.date(
            byAdding: .day,
            value: intervalDays,
            to: data.consumable.currentCycleStartedAt
        ) ?? data.consumable.currentCycleStartedAt

        self.itemName = data.consumable.name
        self.imageAssetName = data.consumable.imageAssetName
        self.remainingSpareQuantity = data.consumable.spareQuantity
        self.isLowStock = data.spareSummary.isLowStock
        self.daysComparedToPrevious = latestUsedDays - previousUsedDays
        self.nextReplacementLabel = "\(nextReplacementDate.itemDetailMonthDayText)(\(intervalDays)일 후)"
        self.recordedAtText = Date().itemDetailRecordedAtText
    }
}
