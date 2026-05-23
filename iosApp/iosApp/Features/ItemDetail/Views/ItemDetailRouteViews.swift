import SwiftUI

struct ItemDetailEditRouteView: View {
    @Environment(\.dismiss) private var dismiss
    @State private var draft: ItemDetailEditDraft

    private let consumable: ItemDetailConsumable

    init(consumableId: Int) {
        let consumable = ItemDetailDomainSampleData.consumable(id: consumableId)
        self.consumable = consumable
        _draft = State(initialValue: ItemDetailEditDraft(consumable: consumable))
    }

    var body: some View {
        ItemDetailEditScaffoldView(
            draft: $draft,
            recommendedCycleDays: consumable.replacementCycle.intervalDays,
            averageCycleDays: averageCycleDays,
            onClose: { dismiss() },
            onSubmit: { dismiss() }
        )
        .navigationBarBackButtonHidden(true)
        .toolbar(.hidden, for: .navigationBar)
    }

    private var averageCycleDays: Int? {
        let usedDays = consumable.replacementRecords.map { $0.usedDays() }
        guard !usedDays.isEmpty else { return nil }
        return Int((Double(usedDays.reduce(0, +)) / Double(usedDays.count)).rounded())
    }
}

struct ItemDetailSpareRouteView: View {
    @Environment(\.dismiss) private var dismiss
    @State private var quantity: Int

    private let consumable: ItemDetailConsumable

    init(consumableId: Int) {
        let consumable = ItemDetailDomainSampleData.consumable(id: consumableId)
        self.consumable = consumable
        _quantity = State(initialValue: consumable.spareQuantity)
    }

    var body: some View {
        ZStack(alignment: .bottom) {
            OBRitColors.backgroundDefaultDefault
                .ignoresSafeArea()

            ItemDetailStockManagementSheet(
                itemName: consumable.name,
                initialQuantity: consumable.spareQuantity,
                quantity: $quantity,
                onCommit: { _ in dismiss() },
                onDismiss: { dismiss() }
            )
        }
        .navigationBarBackButtonHidden(true)
        .toolbar(.hidden, for: .navigationBar)
    }
}

struct ItemDetailReplacementCompleteRouteView: View {
    @Environment(\.dismiss) private var dismiss

    private let consumable: ItemDetailConsumable

    init(consumableId: Int) {
        consumable = ItemDetailDomainSampleData.consumable(id: consumableId)
    }

    var body: some View {
        ZStack {
            OBRitColors.backgroundDefaultDefault
                .ignoresSafeArea()

            ItemDetailReplacementCompletionModal(
                itemName: consumable.name,
                itemImageAssetName: consumable.imageAssetName,
                daysComparedToPrevious: -2,
                nextReplacementLabel: nextReplacementLabel,
                recordedAtText: Date().itemDetailRecordedAtText,
                onConfirm: { dismiss() },
                onCancel: { dismiss() }
            )
        }
        .navigationBarBackButtonHidden(true)
        .toolbar(.hidden, for: .navigationBar)
    }

    private var nextReplacementLabel: String {
        let nextDate = Calendar.current.date(
            byAdding: .day,
            value: consumable.replacementCycle.intervalDays,
            to: Date()
        ) ?? Date()
        return "\(nextDate.itemDetailMonthDayText)(\(consumable.replacementCycle.intervalDays)일 후)"
    }
}
