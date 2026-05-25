import Foundation

struct ItemRegistrationCatalog: Equatable {
    let itemKinds: [ItemKind]
    let imageOptions: [ItemImageOption]
}

protocol ItemRegistrationCatalogRepository {
    func catalog() async throws -> ItemRegistrationCatalog
}

struct ItemRegistrationSampleCatalogRepository: ItemRegistrationCatalogRepository {
    func catalog() async throws -> ItemRegistrationCatalog {
        ItemRegistrationCatalog(
            itemKinds: ItemRegistrationSampleData.itemKinds,
            imageOptions: ItemRegistrationSampleData.imageOptions
        )
    }
}
