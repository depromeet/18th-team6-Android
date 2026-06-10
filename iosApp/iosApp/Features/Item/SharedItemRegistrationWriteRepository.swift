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
                imageURL: category.iconUrl
            )
            AppLog.success(AppLog.swiftRepository, event, "kindId=\(kind.id)")
            return kind
        } catch {
            AppLog.failure(AppLog.swiftRepository, event, error, details)
            throw error
        }
    }

    func createItem(request: ItemRegistrationCreateItemRequest) async throws {
        guard request.quantity >= ItemRegistrationConfig.quantityMinimum else {
            throw SharedItemRegistrationWriteRepositoryError.invalidQuantity
        }

        let event = "SharedItemRegistrationWriteRepository.createItem"
        let details = "categoryId=\(request.categoryId) quantity=\(request.quantity) lastReplacementPeriod=\(request.lastReplacementPeriod?.rawValue ?? "nil")"
        AppLog.enter(AppLog.swiftRepository, event, details)
        do {
            _ = try await writeService.createItem(
                categoryId: Int64(request.categoryId),
                name: request.name,
                count: KotlinInt(int: Int32(request.quantity)),
                lastReplacementPeriod: request.lastReplacementPeriod?.rawValue,
                replacementIntervalDays: nil
            )
            AppLog.success(AppLog.swiftRepository, event, details)
        } catch {
            AppLog.failure(AppLog.swiftRepository, event, error, details)
            throw error
        }
    }
}

private enum SharedItemRegistrationWriteRepositoryError: LocalizedError {
    case invalidQuantity

    var errorDescription: String? {
        "등록할 수량은 \(ItemRegistrationConfig.quantityMinimum)개 이상이어야 해요."
    }
}
