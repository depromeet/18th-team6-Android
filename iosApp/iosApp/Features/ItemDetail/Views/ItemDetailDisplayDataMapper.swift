import Foundation

extension ItemDetailScreenView {
    init(
        data: ItemDetailViewData,
        action: ItemDetailViewAction = .noop
    ) {
        self.init(
            item: ItemDetailDisplayData(viewData: data),
            action: action
        )
    }
}

extension ItemDetailDisplayData {
    init(
        viewData: ItemDetailViewData,
        calendar: Calendar = .current
    ) {
        let consumable = viewData.consumable
        let intervalDays = max(consumable.replacementCycle.intervalDays, 1)
        let daysInUse = max(intervalDays - viewData.statusSummary.replacementDday, 0)
        let nextReplacementDate = calendar.date(
            byAdding: .day,
            value: intervalDays,
            to: consumable.currentCycleStartedAt
        ) ?? consumable.currentCycleStartedAt
        let visualStatus = ItemDetailStatus(level: viewData.statusSummary.level)

        self.init(
            id: consumable.id,
            title: consumable.name,
            imageAssetName: consumable.imageAssetName,
            status: visualStatus,
            heroProgress: min(max(Double(daysInUse) / Double(intervalDays), 0), 1),
            lastReplacementDateText: Self.koreanMonthDay(consumable.currentCycleStartedAt, calendar: calendar),
            nextReplacementDateText: Self.koreanMonthDay(nextReplacementDate, calendar: calendar),
            replacementDayBadgeText: viewData.statusSummary.replacementDdayLabel,
            stockCount: viewData.spareSummary.quantity,
            averageReplacementDaysText: Self.averageReplacementDaysText(
                records: consumable.replacementRecords,
                fallbackDays: intervalDays,
                calendar: calendar
            ),
            recommendedReplacementDaysText: viewData.replacementCycleSummary.intervalLabel,
            currentUsageDaysText: "\(daysInUse)일째",
            currentStatusBadgeText: viewData.statusSummary.level == .normal ? nil : viewData.statusSummary.replacementDdayLabel,
            replacementHistory: Self.replacementHistoryEntries(
                records: consumable.replacementRecords,
                currentDaysInUse: daysInUse,
                intervalDays: intervalDays,
                calendar: calendar
            ),
            replacementHistoryAverageText: Self.averageReplacementDaysText(
                records: consumable.replacementRecords,
                fallbackDays: intervalDays,
                calendar: calendar
            )
        )
    }

    private static func averageReplacementDaysText(
        records: [ItemDetailReplacementRecord],
        fallbackDays: Int,
        calendar: Calendar
    ) -> String {
        let days = records.map { $0.usedDays(calendar: calendar) }

        guard !days.isEmpty else {
            return "\(fallbackDays)일"
        }

        let average = Double(days.reduce(0, +)) / Double(days.count)
        return String(format: "%.1f일", average)
    }

    private static func replacementHistoryEntries(
        records: [ItemDetailReplacementRecord],
        currentDaysInUse: Int,
        intervalDays: Int,
        calendar: Calendar
    ) -> [ItemDetailReplacementHistoryEntry] {
        let visiblePastRecords = records
            .sorted { $0.replacedAt < $1.replacedAt }
            .suffix(max(ItemDetailConfig.visibleReplacementRecordLimit - 1, 0))
        let usedDays = visiblePastRecords.map { $0.usedDays(calendar: calendar) }
        let maxDays = max((usedDays + [currentDaysInUse, intervalDays]).max() ?? intervalDays, 1)
        let pastEntries = visiblePastRecords.map { record in
            let days = record.usedDays(calendar: calendar)
            return ItemDetailReplacementHistoryEntry(
                id: "record-\(record.id)",
                daysText: "\(days)일",
                dateText: numericMonthDay(record.replacedAt, calendar: calendar),
                ratio: Double(days) / Double(maxDays)
            )
        }

        return pastEntries + [
            ItemDetailReplacementHistoryEntry(
                id: "current",
                daysText: "\(currentDaysInUse)일",
                dateText: "현재",
                ratio: Double(currentDaysInUse) / Double(maxDays),
                isCurrent: true
            )
        ]
    }

    private static func koreanMonthDay(_ date: Date, calendar: Calendar) -> String {
        let components = calendar.dateComponents([.month, .day], from: date)
        return "\(components.month ?? 1)월 \(components.day ?? 1)일"
    }

    private static func numericMonthDay(_ date: Date, calendar: Calendar) -> String {
        let components = calendar.dateComponents([.month, .day], from: date)
        return String(format: "%02d/%02d", components.month ?? 1, components.day ?? 1)
    }
}

private extension ItemDetailStatus {
    init(level: ItemDetailStatusLevel) {
        switch level {
        case .normal:
            self = .good
        case .warning, .danger:
            self = .warning
        }
    }
}
