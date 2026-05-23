import Foundation

protocol ItemDetailRepository {
    func detail(consumableId: Int) async throws -> ItemDetailConsumable
    func updateSpareQuantity(consumableId: Int, quantity: Int, updatedAt: Date) async throws -> ItemDetailConsumable
    func completeReplacement(consumableId: Int, completedAt: Date) async throws -> ItemDetailConsumable
    func delete(consumableId: Int) async throws
}

enum ItemDetailRepositoryError: LocalizedError, Equatable {
    case notFound(consumableId: Int)

    var errorDescription: String? {
        switch self {
        case let .notFound(consumableId):
            return "소모품 ID \(consumableId)를 찾을 수 없어요."
        }
    }
}

actor ItemDetailSampleRepository: ItemDetailRepository {
    private var consumablesByID: [Int: ItemDetailConsumable]

    init(consumables: [ItemDetailConsumable] = ItemDetailDomainSampleData.consumables) {
        self.consumablesByID = Dictionary(uniqueKeysWithValues: consumables.map { ($0.id, $0) })
    }

    func detail(consumableId: Int) async throws -> ItemDetailConsumable {
        guard let consumable = consumablesByID[consumableId] else {
            throw ItemDetailRepositoryError.notFound(consumableId: consumableId)
        }

        return consumable
    }

    func updateSpareQuantity(
        consumableId: Int,
        quantity: Int,
        updatedAt: Date
    ) async throws -> ItemDetailConsumable {
        let consumable = try await detail(consumableId: consumableId)
        let updated = consumable.updatingSpareQuantity(quantity, at: updatedAt)
        consumablesByID[consumableId] = updated
        return updated
    }

    func completeReplacement(
        consumableId: Int,
        completedAt: Date
    ) async throws -> ItemDetailConsumable {
        let consumable = try await detail(consumableId: consumableId)
        let updated = consumable.completingReplacement(at: completedAt)
        consumablesByID[consumableId] = updated
        return updated
    }

    func delete(consumableId: Int) async throws {
        guard consumablesByID.removeValue(forKey: consumableId) != nil else {
            throw ItemDetailRepositoryError.notFound(consumableId: consumableId)
        }
    }
}
