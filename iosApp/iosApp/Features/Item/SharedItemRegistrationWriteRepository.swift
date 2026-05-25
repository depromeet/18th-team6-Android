import Foundation
import Shared

actor SharedItemRegistrationWriteRepository: ItemRegistrationWriteRepository {
    private let writeService: SharedWriteService

    init(writeService: SharedWriteService) {
        self.writeService = writeService
    }

    func createKind(name: String, imageOption: ItemImageOption) async throws -> ItemKind {
        let event = "SharedItemRegistrationWriteRepository.createKind"
        let details = "iconId=\(imageOption.id) nameLength=\(name.count)"
        AppLog.enter(AppLog.swiftRepository, event, details)
        do {
            let category = try await writeService.createCategory(
                name: name,
                iconId: Int64(imageOption.id)
            )

            let kind = ItemKind(
                id: Int(clamping: category.id),
                title: category.name,
                addedCount: ItemRegistrationConfig.newKindInitialAddedCount,
                imageAssetName: imageOption.assetName
            )
            AppLog.success(AppLog.swiftRepository, event, "kindId=\(kind.id)")
            return kind
        } catch {
            AppLog.failure(AppLog.swiftRepository, event, error, details)
            throw error
        }
    }

    func createItem(request: ItemRegistrationCreateItemRequest) async throws {
        let event = "SharedItemRegistrationWriteRepository.createItem"
        let details = "categoryId=\(request.categoryId) quantity=\(request.quantity) hasLastReplacementDate=\(request.lastReplacementDate != nil)"
        AppLog.enter(AppLog.swiftRepository, event, details)
        do {
            _ = try await writeService.createItem(
                categoryId: Int64(request.categoryId),
                name: request.name,
                count: KotlinInt(int: Int32(request.quantity)),
                lastReplacedDate: request.lastReplacementDate,
                replacementIntervalDays: nil
            )
            AppLog.success(AppLog.swiftRepository, event, details)
        } catch {
            AppLog.failure(AppLog.swiftRepository, event, error, details)
            throw error
        }
    }
}
