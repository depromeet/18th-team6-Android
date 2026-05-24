import SwiftUI

struct ItemDetailEditRouteView: View {
    @State private var draft: ItemDetailEditDraft
    @State private var isExitConfirmationPresented = false

    private let item: ItemDetailItem
    private let onBack: () -> Void

    init(
        itemId: Int,
        onBack: @escaping () -> Void
    ) {
        let item = ItemDetailDomainSampleData.item(id: itemId)
        self.item = item
        self.onBack = onBack
        _draft = State(initialValue: ItemDetailEditDraft(item: item))
    }

    var body: some View {
        ZStack {
            ItemDetailEditScaffoldView(
                draft: $draft,
                recommendedCycleDays: item.replacementCycle.intervalDays,
                averageCycleDays: averageCycleDays,
                existingItemNames: ItemDetailDomainSampleData.items.map(\.name),
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
        let usedDays = item.replacementRecords.map { $0.usedDays() }
        guard !usedDays.isEmpty else { return nil }
        return Int((Double(usedDays.reduce(0, +)) / Double(usedDays.count)).rounded())
    }
}

struct ItemDetailSpareRouteView: View {
    @State private var quantity: Int

    private let item: ItemDetailItem
    private let onBack: () -> Void

    init(
        itemId: Int,
        onBack: @escaping () -> Void
    ) {
        let item = ItemDetailDomainSampleData.item(id: itemId)
        self.item = item
        self.onBack = onBack
        _quantity = State(initialValue: item.spareQuantity)
    }

    var body: some View {
        ZStack(alignment: .bottom) {
            OBRitColors.backgroundDefaultDefault
                .ignoresSafeArea()

            ItemDetailStockManagementSheet(
                itemName: item.name,
                initialQuantity: item.spareQuantity,
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
    private let item: ItemDetailItem
    private let onBack: () -> Void

    init(
        itemId: Int,
        onBack: @escaping () -> Void
    ) {
        item = ItemDetailDomainSampleData.item(id: itemId)
        self.onBack = onBack
    }

    var body: some View {
        ZStack {
            OBRitColors.backgroundDefaultDefault
                .ignoresSafeArea()

            ItemDetailReplacementCompletionModal(
                itemName: item.name,
                itemImageAssetName: item.imageAssetName,
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
            value: item.replacementCycle.intervalDays,
            to: Date()
        ) ?? Date()
        return "\(nextDate.itemDetailMonthDayText)(\(item.replacementCycle.intervalDays)일 후)"
    }
}
