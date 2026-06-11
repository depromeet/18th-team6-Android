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
        let item = viewData.item
        let intervalDays = max(item.replacementCycle.intervalDays, 1)
        let daysInUse = max(intervalDays - viewData.statusSummary.replacementDday, 0)
        let nextReplacementDate = calendar.date(
            byAdding: .day,
            value: intervalDays,
            to: item.currentCycleStartedAt
        ) ?? item.currentCycleStartedAt
        let visualStatus = ItemDetailStatus(level: viewData.statusSummary.level)

        self.init(
            id: item.id,
            title: item.name,
            imageURL: item.imageURL,
            status: visualStatus,
            heroProgress: min(max(Double(daysInUse) / Double(intervalDays), 0), 1),
            lastReplacementDateText: Self.koreanMonthDay(item.currentCycleStartedAt, calendar: calendar),
            nextReplacementDateText: Self.koreanMonthDay(nextReplacementDate, calendar: calendar),
            replacementDayBadgeText: viewData.statusSummary.replacementDdayLabel,
            stockCount: viewData.spareSummary.quantity,
            averageReplacementDaysText: Self.averageReplacementDaysText(
                records: item.replacementRecords,
                fallbackDays: intervalDays,
                calendar: calendar,
                allowsFraction: false
            ),
            recommendedReplacementDaysText: viewData.replacementCycleSummary.intervalLabel,
            currentUsageDaysText: "\(daysInUse)일째",
            currentStatusBadgeText: viewData.statusSummary.level == .normal ? nil : viewData.statusSummary.replacementDdayLabel,
            replacementHistory: Self.replacementHistoryEntries(
                records: item.replacementRecords,
                currentDaysInUse: daysInUse,
                intervalDays: intervalDays,
                calendar: calendar
            ),
            replacementHistoryAverageText: Self.averageReplacementDaysText(
                records: item.replacementRecords,
                fallbackDays: intervalDays,
                calendar: calendar,
                allowsFraction: true
            )
        )
    }

    private static func averageReplacementDaysText(
        records: [ItemDetailReplacementRecord],
        fallbackDays: Int,
        calendar: Calendar,
        allowsFraction: Bool
    ) -> String {
        let days = records.map { $0.usedDays(calendar: calendar) }

        guard !days.isEmpty else {
            return "\(fallbackDays)일"
        }

        let average = Double(days.reduce(0, +)) / Double(days.count)
        if allowsFraction {
            return String(format: "%.1f일", average)
        }

        return "\(Int(average.rounded()))일"
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
            self = .normal
        case .warning:
            self = .warning
        }
    }
}
