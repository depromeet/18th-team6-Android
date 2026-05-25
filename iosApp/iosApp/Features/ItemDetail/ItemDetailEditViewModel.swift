import Foundation

@MainActor
final class ItemDetailEditViewModel: ObservableObject {
    @Published private(set) var state: ItemDetailEditViewState = .loading
    @Published private(set) var effect: ItemDetailEditViewEffect?

    private let itemId: Int
    private let repository: ItemDetailEditRepository

    init(
        itemId: Int,
        repository: ItemDetailEditRepository = ItemDetailSampleEditRepository(),
        automaticallyLoads: Bool = true
    ) {
        self.itemId = itemId
        self.repository = repository

        if automaticallyLoads {
            load()
        }
    }

    func load() {
        Task {
            await loadEditSource(showLoading: true)
        }
    }

    func retry() {
        load()
    }

    func clearEffect() {
        effect = nil
    }

    func submit(draft: ItemDetailEditDraft) {
        guard case let .success(data) = state,
              !data.isProcessing else { return }

        state = .success(data.withProcessing(true))
        Task {
            await submitTask(draft: draft, original: data.item)
        }
    }

    private func loadEditSource(showLoading: Bool) async {
        if showLoading {
            state = .loading
        }

        do {
            let source = try await repository.editSource(itemId: itemId)
            state = .success(ItemDetailEditViewData(source: source, isProcessing: false))
        } catch {
            state = .loadFailed(message: error.itemDetailEditMessage)
        }
    }

    private func submitTask(draft: ItemDetailEditDraft, original: ItemDetailItem) async {
        do {
            let item = try await repository.updateItem(itemId: itemId, draft: draft, original: original)
            state = .success(
                ItemDetailEditViewData(
                    item: item,
                    existingItemNames: currentExistingNames,
                    isProcessing: false
                )
            )
            effect = .completed
        } catch {
            if case let .success(data) = state {
                state = .success(data.withProcessing(false))
            }
            effect = .showMessage(error.itemDetailEditMessage)
        }
    }

    private var currentExistingNames: [String] {
        guard case let .success(data) = state else { return [] }
        return data.existingItemNames
    }
}

enum ItemDetailEditViewState: Equatable {
    case loading
    case loadFailed(message: String)
    case success(ItemDetailEditViewData)
}

struct ItemDetailEditViewData: Equatable {
    let item: ItemDetailItem
    let existingItemNames: [String]
    let averageCycleDays: Int?
    let isProcessing: Bool

    init(source: ItemDetailEditSource, isProcessing: Bool) {
        self.init(
            item: source.item,
            existingItemNames: source.existingItemNames,
            isProcessing: isProcessing
        )
    }

    init(
        item: ItemDetailItem,
        existingItemNames: [String],
        isProcessing: Bool
    ) {
        self.item = item
        self.existingItemNames = existingItemNames
        self.averageCycleDays = Self.averageCycleDays(for: item)
        self.isProcessing = isProcessing
    }

    func withProcessing(_ isProcessing: Bool) -> ItemDetailEditViewData {
        ItemDetailEditViewData(
            item: item,
            existingItemNames: existingItemNames,
            isProcessing: isProcessing
        )
    }

    private static func averageCycleDays(for item: ItemDetailItem) -> Int? {
        let usedDays = item.replacementRecords.map { $0.usedDays() }
        guard !usedDays.isEmpty else { return nil }
        return Int((Double(usedDays.reduce(0, +)) / Double(usedDays.count)).rounded())
    }
}

enum ItemDetailEditViewEffect: Equatable {
    case completed
    case showMessage(String)
}

private extension Error {
    var itemDetailEditMessage: String {
        if let localizedError = self as? LocalizedError,
           let description = localizedError.errorDescription {
            return description
        }

        return "소모품 정보를 수정하지 못했어요."
    }
}
