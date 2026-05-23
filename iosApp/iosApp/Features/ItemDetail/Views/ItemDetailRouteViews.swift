import SwiftUI

struct ItemDetailEditRouteView: View {
    @State private var draft: ItemDetailEditDraft
    @State private var isExitConfirmationPresented = false

    private let consumable: ItemDetailConsumable
    private let onBack: () -> Void

    init(
        consumableId: Int,
        onBack: @escaping () -> Void
    ) {
        let consumable = ItemDetailDomainSampleData.consumable(id: consumableId)
        self.consumable = consumable
        self.onBack = onBack
        _draft = State(initialValue: ItemDetailEditDraft(consumable: consumable))
    }

    var body: some View {
        ZStack {
            ItemDetailEditScaffoldView(
                draft: $draft,
                recommendedCycleDays: consumable.replacementCycle.intervalDays,
                averageCycleDays: averageCycleDays,
                existingConsumableNames: ItemDetailDomainSampleData.consumables.map(\.name),
                onClose: {
                    isExitConfirmationPresented = true
                },
                onSubmit: onBack
            )

            if isExitConfirmationPresented {
                OBRitColors.backgroundDefaultDimDefault
                    .ignoresSafeArea()
                    .onTapGesture {
                        isExitConfirmationPresented = false
                    }

                ItemDetailConfirmationModal(
                    kind: .editExit,
                    onCancel: {
                        isExitConfirmationPresented = false
                    },
                    onConfirm: onBack
                )
                .transition(.scale.combined(with: .opacity))
            }
        }
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
    @State private var quantity: Int

    private let consumable: ItemDetailConsumable
    private let onBack: () -> Void

    init(
        consumableId: Int,
        onBack: @escaping () -> Void
    ) {
        let consumable = ItemDetailDomainSampleData.consumable(id: consumableId)
        self.consumable = consumable
        self.onBack = onBack
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
                onCommit: { _ in onBack() },
                onDismiss: onBack
            )
        }
        .navigationBarBackButtonHidden(true)
        .toolbar(.hidden, for: .navigationBar)
    }
}

struct ItemDetailReplacementCompleteRouteView: View {
    private let consumable: ItemDetailConsumable
    private let onBack: () -> Void

    init(
        consumableId: Int,
        onBack: @escaping () -> Void
    ) {
        consumable = ItemDetailDomainSampleData.consumable(id: consumableId)
        self.onBack = onBack
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
                onConfirm: onBack,
                onCancel: onBack
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
