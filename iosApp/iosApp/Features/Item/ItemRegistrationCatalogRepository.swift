import Foundation

struct ItemRegistrationCatalog: Equatable {
    let itemKinds: [ItemKind]
    let imageOptions: [ItemImageOption]
}

protocol ItemRegistrationCatalogRepository {
    func catalog() async throws -> ItemRegistrationCatalog
}

protocol ItemRegistrationWriteRepository {
    func createKind(name: String, imageOption: ItemImageOption) async throws -> ItemKind
    func createItem(request: ItemRegistrationCreateItemRequest) async throws
}

struct ItemRegistrationSampleCatalogRepository: ItemRegistrationCatalogRepository {
    func catalog() async throws -> ItemRegistrationCatalog {
        ItemRegistrationCatalog(
            itemKinds: [],
            imageOptions: []
        )
    }
}

actor ItemRegistrationSampleWriteRepository: ItemRegistrationWriteRepository {
    func createKind(name: String, imageOption: ItemImageOption) async throws -> ItemKind {
        ItemKind(
            id: ItemRegistrationConfig.emptyIDBase,
            title: name,
            addedCount: ItemRegistrationConfig.newKindInitialAddedCount,
            imageURL: imageOption.imageURL
        )
    }

    func createItem(request: ItemRegistrationCreateItemRequest) async throws {
    }
}
