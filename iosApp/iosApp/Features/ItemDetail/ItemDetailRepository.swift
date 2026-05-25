import Foundation

protocol ItemDetailRepository {
    func detail(itemId: Int) async throws -> ItemDetailItem
    func updateSpareQuantity(itemId: Int, quantity: Int, updatedAt: Date) async throws -> ItemDetailItem
    func completeReplacement(itemId: Int, completedAt: Date) async throws -> ItemDetailItem
    func delete(itemId: Int) async throws
}

enum ItemDetailRepositoryError: LocalizedError, Equatable {
    case notFound(itemId: Int)
    case unsupportedMutation

    var errorDescription: String? {
        switch self {
        case let .notFound(itemId):
            return "소모품 ID \(itemId)를 찾을 수 없어요."
        case .unsupportedMutation:
            return "이 기능은 아직 준비 중이에요."
        }
    }
}

actor ItemDetailSampleRepository: ItemDetailRepository {
    private var itemsByID: [Int: ItemDetailItem]

    init(items: [ItemDetailItem] = ItemDetailDomainSampleData.items) {
        self.itemsByID = Dictionary(uniqueKeysWithValues: items.map { ($0.id, $0) })
    }

    func detail(itemId: Int) async throws -> ItemDetailItem {
        guard let item = itemsByID[itemId] else {
            throw ItemDetailRepositoryError.notFound(itemId: itemId)
        }

        return item
    }

    func updateSpareQuantity(
        itemId: Int,
        quantity: Int,
        updatedAt: Date
    ) async throws -> ItemDetailItem {
        let item = try await detail(itemId: itemId)
        let updated = item.updatingSpareQuantity(quantity, at: updatedAt)
        itemsByID[itemId] = updated
        return updated
    }

    func completeReplacement(
        itemId: Int,
        completedAt: Date
    ) async throws -> ItemDetailItem {
        let item = try await detail(itemId: itemId)
        let updated = item.completingReplacement(at: completedAt)
        itemsByID[itemId] = updated
        return updated
    }

    func delete(itemId: Int) async throws {
        guard itemsByID.removeValue(forKey: itemId) != nil else {
            throw ItemDetailRepositoryError.notFound(itemId: itemId)
        }
    }
}
