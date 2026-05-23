import SwiftUI

@MainActor
final class ItemDetailViewModel: ObservableObject {
    @Published private(set) var state: ItemDetailViewState = .loading
    @Published private(set) var effect: ItemDetailViewEffect?

    private let consumableId: Int
    private let repository: ItemDetailRepository
    private let dateProvider: () -> Date
    private let calendar: Calendar

    private var consumable: ItemDetailConsumable?
    private var isMoreMenuPresented = false
    private var confirmationDialog: ItemDetailConfirmationDialog?
    private var isProcessing = false

    init(
        consumableId: Int,
        repository: ItemDetailRepository = ItemDetailSampleRepository(),
        dateProvider: @escaping () -> Date = Date.init,
        calendar: Calendar = .current,
        automaticallyLoads: Bool = true
    ) {
        self.consumableId = consumableId
        self.repository = repository
        self.dateProvider = dateProvider
        self.calendar = calendar

        if automaticallyLoads {
            load()
        }
    }

    func load() {
        Task {
            await loadDetail(showLoading: true)
        }
    }

    func refresh() {
        Task {
            await loadDetail(showLoading: false)
        }
    }

    func retry() {
        load()
    }

    func clearEffect() {
        effect = nil
    }

    func openStatusInfo() {
        effect = .navigate(.statusInfo(consumableId: consumableId))
    }

    func openMoreMenu() {
        isMoreMenuPresented = true
        publishSuccess()
    }

    func dismissMoreMenu() {
        isMoreMenuPresented = false
        publishSuccess()
    }

    func selectMoreMenuItem(_ item: ItemDetailMoreMenuItem) {
        isMoreMenuPresented = false

        switch item {
        case .edit:
            publishSuccess()
            effect = .navigate(.edit(consumableId: consumableId))
        case .spareEdit:
            publishSuccess()
            effect = .navigate(.spareEdit(consumableId: consumableId))
        case .notification:
            publishSuccess()
            effect = .navigate(.notification(consumableId: consumableId))
        case .delete:
            confirmationDialog = .delete
            publishSuccess()
        }
    }

    func requestReplacementCompletion() {
        confirmationDialog = .replacementComplete
        publishSuccess()
    }

    func dismissConfirmationDialog() {
        guard !isProcessing else { return }
        confirmationDialog = nil
        publishSuccess()
    }

    func updateSpareQuantity(_ quantity: Int) {
        guard quantity >= ItemDetailConfig.minimumSpareQuantity,
              quantity <= ItemDetailConfig.maximumSpareQuantity else {
            effect = .showMessage("여분 수량은 \(ItemDetailConfig.minimumSpareQuantity)개부터 \(ItemDetailConfig.maximumSpareQuantity)개까지 입력할 수 있어요.")
            return
        }

        guard !isProcessing else { return }
        isProcessing = true
        publishSuccess()

        Task {
            await updateSpareQuantityTask(quantity)
        }
    }

    func confirmCurrentDialog() {
        guard let confirmationDialog, !isProcessing else { return }
        isProcessing = true
        publishSuccess()

        Task {
            switch confirmationDialog {
            case .delete:
                await deleteTask()
            case .replacementComplete:
                await completeReplacementTask()
            }
        }
    }

    private func loadDetail(showLoading: Bool) async {
        if showLoading {
            state = .loading
        }

        do {
            consumable = try await repository.detail(consumableId: consumableId)
            isProcessing = false
            confirmationDialog = nil
            publishSuccess()
        } catch {
            isProcessing = false
            state = .loadFailed(message: error.itemDetailMessage)
        }
    }

    private func updateSpareQuantityTask(_ quantity: Int) async {
        do {
            consumable = try await repository.updateSpareQuantity(
                consumableId: consumableId,
                quantity: quantity,
                updatedAt: dateProvider()
            )
            isProcessing = false
            publishSuccess()
            effect = .showMessage("여분 수량을 수정했어요.")
        } catch {
            handleMutationFailure(error)
        }
    }

    private func completeReplacementTask() async {
        do {
            consumable = try await repository.completeReplacement(
                consumableId: consumableId,
                completedAt: dateProvider()
            )
            confirmationDialog = nil
            isProcessing = false
            publishSuccess()
            effect = .replacementCompleted(consumableId: consumableId)
        } catch {
            handleMutationFailure(error)
        }
    }

    private func deleteTask() async {
        do {
            try await repository.delete(consumableId: consumableId)
            confirmationDialog = nil
            isProcessing = false
            consumable = nil
            state = .loadFailed(message: "삭제된 소모품이에요.")
            effect = .itemDeleted(consumableId: consumableId)
        } catch {
            handleMutationFailure(error)
        }
    }

    private func handleMutationFailure(_ error: Error) {
        isProcessing = false
        publishSuccess()
        effect = .showMessage(error.itemDetailMessage)
    }

    private func publishSuccess() {
        guard let consumable else {
            state = .loadFailed(message: ItemDetailRepositoryError.notFound(consumableId: consumableId).itemDetailMessage)
            return
        }

        state = .success(
            ItemDetailViewData(
                consumable: consumable,
                referenceDate: dateProvider(),
                calendar: calendar,
                isMoreMenuPresented: isMoreMenuPresented,
                confirmationDialog: confirmationDialog,
                isProcessing: isProcessing
            )
        )
    }
}

private extension Error {
    var itemDetailMessage: String {
        if let localizedError = self as? LocalizedError,
           let description = localizedError.errorDescription {
            return description
        }

        return "소모품 상세 정보를 불러오지 못했어요."
    }
}
