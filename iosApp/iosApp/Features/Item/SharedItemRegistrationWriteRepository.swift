import Foundation
import Shared

actor SharedItemRegistrationWriteRepository: ItemRegistrationWriteRepository {
    private let writeService: SharedWriteService

    init(writeService: SharedWriteService) {
        self.writeService = writeService
    }

    func createKind(name: String, imageOption: ItemImageOption) async throws -> ItemKind {
        let category = try await writeService.createCategory(
            name: name,
            iconId: Int64(imageOption.id)
        )

        return ItemKind(
            id: Int(clamping: category.id),
            title: category.name,
            addedCount: ItemRegistrationConfig.newKindInitialAddedCount,
            imageAssetName: imageOption.assetName
        )
    }

    func createItem(request: ItemRegistrationCreateItemRequest) async throws {
        _ = try await writeService.createItem(
            categoryId: Int64(request.categoryId),
            name: request.name,
            count: KotlinInt(int: Int32(request.quantity)),
            lastReplacedDate: request.lastReplacementDate,
            replacementIntervalDays: nil
        )
    }
}
