import SwiftUI

@MainActor
final class ItemDetailViewModel: ObservableObject {
    @Published private(set) var state: ItemDetailViewState = .loading
    @Published private(set) var effect: ItemDetailViewEffect?

    private let itemId: Int
    private let repository: ItemDetailRepository
    private let dateProvider: () -> Date
    private let calendar: Calendar

    private var item: ItemDetailItem?
    private var confirmationDialog: ItemDetailConfirmationDialog?
    private var isProcessing = false

    init(
        itemId: Int,
        repository: ItemDetailRepository = ItemDetailSampleRepository(),
        dateProvider: @escaping () -> Date = Date.init,
        calendar: Calendar = .current,
        automaticallyLoads: Bool = true
    ) {
        self.itemId = itemId
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
        effect = .navigate(.statusInfo(itemId: itemId))
    }

    func selectMoreMenuItem(_ item: ItemDetailMoreMenuItem) {
        switch item {
        case .edit:
            effect = .navigate(.edit(itemId: itemId))
        case .spareEdit:
            effect = .navigate(.spareEdit(itemId: itemId))
        case .notification:
            effect = .navigate(.notification(itemId: itemId))
        case .delete:
            confirmationDialog = .delete
            publishSuccess()
        }
    }

    func requestReplacementCompletion() {
        guard !isProcessing else { return }
        confirmationDialog = nil
        isProcessing = true
        publishSuccess()

        Task {
            await completeReplacementTask()
        }
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
        guard confirmationDialog == .delete, !isProcessing else { return }
        isProcessing = true
        publishSuccess()

        Task {
            await deleteTask()
        }
    }

    private func loadDetail(showLoading: Bool) async {
        if showLoading {
            state = .loading
        }

        do {
            item = try await repository.detail(itemId: itemId)
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
            item = try await repository.updateSpareQuantity(
                itemId: itemId,
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
            item = try await repository.completeReplacement(
                itemId: itemId,
                completedAt: dateProvider()
            )
            confirmationDialog = nil
            isProcessing = false
            publishSuccess()
            effect = .replacementCompleted(itemId: itemId)
        } catch {
            handleMutationFailure(error)
        }
    }

    private func deleteTask() async {
        do {
            try await repository.delete(itemId: itemId)
            confirmationDialog = nil
            isProcessing = false
            item = nil
            state = .loadFailed(message: "삭제된 소모품이에요.")
            effect = .itemDeleted(itemId: itemId)
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
        guard let item else {
            state = .loadFailed(message: ItemDetailRepositoryError.notFound(itemId: itemId).itemDetailMessage)
            return
        }

        state = .success(
            ItemDetailViewData(
                item: item,
                referenceDate: dateProvider(),
                calendar: calendar,
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
