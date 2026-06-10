import Foundation
import Shared

@MainActor
final class ItemRegistrationViewModel: ObservableObject {
    @Published private(set) var state: ItemRegistrationViewState
    @Published private(set) var effect: ItemRegistrationViewEffect?

    private let catalogRepository: ItemRegistrationCatalogRepository?
    private let writeRepository: ItemRegistrationWriteRepository?

    private var data: ItemRegistrationViewData {
        didSet {
            guard data != oldValue else { return }
            state = .success(data)
        }
    }

    init(
        itemKinds: [ItemKind] = [],
        imageOptions: [ItemImageOption] = []
    ) {
        let initialData = ItemRegistrationViewData(
            mode: .form,
            draft: Self.initialDraft(imageOptions: imageOptions),
            kindSearchQuery: "",
            itemKinds: itemKinds,
            imageOptions: imageOptions,
            bottomSheet: nil,
            selectedKindCandidate: nil,
            isProcessing: false
        )
        self.catalogRepository = nil
        self.writeRepository = nil
        self.data = initialData
        self.state = .success(initialData)

        #if DEBUG
            applyDebugInitialState()
        #endif
    }

    init(
        catalogRepository: ItemRegistrationCatalogRepository,
        writeRepository: ItemRegistrationWriteRepository,
        fallbackItemKinds: [ItemKind] = [],
        fallbackImageOptions: [ItemImageOption] = [],
        automaticallyLoads: Bool = true
    ) {
        let initialData = ItemRegistrationViewData(
            mode: .form,
            draft: Self.initialDraft(imageOptions: fallbackImageOptions),
            kindSearchQuery: "",
            itemKinds: fallbackItemKinds,
            imageOptions: fallbackImageOptions,
            bottomSheet: nil,
            selectedKindCandidate: nil,
            isProcessing: false
        )
        self.catalogRepository = catalogRepository
        self.writeRepository = writeRepository
        self.data = initialData
        self.state = automaticallyLoads ? .loading : .success(initialData)

        if automaticallyLoads {
            load()
        }

        #if DEBUG
            applyDebugInitialState()
        #endif
    }

    private static func initialDraft(
        imageOptions: [ItemImageOption]
    ) -> ItemRegistrationDraft {
        ItemRegistrationDraft(
            selectedKind: nil,
            itemName: "",
            lastReplacementDateOption: nil,
            quantity: ItemRegistrationConfig.defaultQuantity,
            directKindName: "",
            selectedImageOption: imageOptions.first
        )
    }

    #if DEBUG
        private func applyDebugInitialState() {
            switch ProcessInfo.processInfo.environment["OBRIT_MANUAL_REGISTRATION_STATE"] {
            case "filled":
                update { data in
                    data.draft.selectedKind = data.itemKinds.first
                    data.draft.itemName = data.itemKinds.first?.title ?? "면도기"
                    data.draft.lastReplacementDateOption = .withinOneWeek
                    data.draft.quantity = ItemRegistrationConfig.defaultQuantity
                }
            case "kindSheet":
                update { data in
                    data.bottomSheet = .kind
                    data.selectedKindCandidate = data.itemKinds.first
                }
            case "dateSheet":
                update { data in
                    data.draft.selectedKind = data.itemKinds.first
                    data.draft.itemName = data.itemKinds.first?.title ?? "면도기"
                    data.draft.lastReplacementDateOption = .withinOneWeek
                    data.draft.quantity = ItemRegistrationConfig.defaultQuantity
                }
            case "directKind":
                update { $0.mode = .directKind }
            case "complete":
                update { $0.mode = .complete }
            default:
                break
            }
        }
    #endif

    func load() {
        guard let catalogRepository else {
            state = .success(data)
            return
        }

        Task {
            await loadCatalog(using: catalogRepository, showLoading: true)
        }
    }

    func retry() {
        load()
    }

    func clearEffect() {
        effect = nil
    }

    func updateItemName(_ itemName: String) {
        let clippedName = String(itemName.prefix(ItemRegistrationConfig.itemNameMaxLength))
        guard data.draft.itemName != clippedName else { return }
        update { $0.draft.itemName = clippedName }
    }

    func updateKindSearchQuery(_ query: String) {
        guard data.kindSearchQuery != query else { return }
        update { data in
            data.kindSearchQuery = query
            data.selectedKindCandidate = data.filteredKinds.first
        }
    }

    func updateDirectKindName(_ name: String) {
        let clippedName = String(name.prefix(ItemRegistrationConfig.kindNameMaxLength))
        guard data.draft.directKindName != clippedName else { return }
        update { $0.draft.directKindName = clippedName }
    }

    func openKindSheet() {
        update { data in
            data.selectedKindCandidate = data.draft.selectedKind ?? data.filteredKinds.first
            data.bottomSheet = .kind
        }
    }

    func dismissBottomSheet() {
        update { data in
            data.bottomSheet = nil
            data.selectedKindCandidate = nil
        }
    }

    func selectKindCandidate(_ kind: ItemKind) {
        update { $0.selectedKindCandidate = kind }
    }

    func confirmKindSelection() {
        guard let kind = data.selectedKindCandidate else { return }
        selectKind(kind)
    }

    func selectKind(_ kind: ItemKind) {
        update { data in
            data.draft.selectedKind = kind
            data.draft.itemName = kind.title
            data.kindSearchQuery = ""
            data.bottomSheet = nil
            data.selectedKindCandidate = nil
        }
    }

    func selectReplacementDate(_ option: ItemReplacementDateOption) {
        update { $0.draft.lastReplacementDateOption = option }
    }

    func incrementQuantity() {
        update {
            $0.draft.quantity = min($0.draft.quantity + 1, ItemRegistrationConfig.quantityMaximum)
        }
    }

    func decrementQuantity() {
        update {
            $0.draft.quantity = max($0.draft.quantity - 1, ItemRegistrationConfig.quantityMinimum)
        }
    }

    func showDirectKindRegistration() {
        update { data in
            data.bottomSheet = nil
            data.selectedKindCandidate = nil
            data.mode = .directKind
        }
    }

    func selectImageOption(_ option: ItemImageOption) {
        update { $0.draft.selectedImageOption = option }
    }

    func submitDirectKind() {
        let trimmedName = data.draft.directKindName.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmedName.isEmpty, let selectedImageOption = data.draft.selectedImageOption else { return }
        guard let writeRepository else {
            insertDirectKind(name: trimmedName, imageOption: selectedImageOption)
            return
        }

        update { $0.isProcessing = true }
        Task {
            await submitDirectKindTask(
                name: trimmedName,
                imageOption: selectedImageOption,
                repository: writeRepository
            )
        }
    }

    func submitForm() {
        guard data.canSubmitForm,
              let request = makeCreateItemRequest() else { return }
        guard let writeRepository else {
            update { data in
                data.mode = .complete
                data.bottomSheet = nil
                data.selectedKindCandidate = nil
            }
            return
        }

        update { $0.isProcessing = true }
        Task {
            await submitFormTask(request: request, repository: writeRepository)
        }
    }

    func resetToForm() {
        update { data in
            data.mode = .form
            data.bottomSheet = nil
            data.selectedKindCandidate = nil
        }
    }

    private func insertDirectKind(name: String, imageOption: ItemImageOption) {
        update { data in
            insert(kind: ItemKind(
                id: (data.itemKinds.map(\.id).max() ?? 0) + ItemRegistrationConfig.nextIDIncrement,
                title: name,
                addedCount: ItemRegistrationConfig.newKindInitialAddedCount,
                imageURL: imageOption.imageURL
            ), into: &data)
        }
    }

    private func insert(kind: ItemKind, into data: inout ItemRegistrationViewData) {
        data.itemKinds.insert(kind, at: ItemRegistrationConfig.newKindInsertionIndex)
        data.draft.selectedKind = kind
        data.draft.itemName = kind.title
        data.draft.directKindName = ""
        data.kindSearchQuery = ""
        data.selectedKindCandidate = nil
        data.mode = .form
        data.isProcessing = false
    }

    private func makeCreateItemRequest() -> ItemRegistrationCreateItemRequest? {
        guard let selectedKind = data.draft.selectedKind,
              let lastReplacementPeriod = data.draft.lastReplacementDateOption?.apiPeriod else { return nil }

        return ItemRegistrationCreateItemRequest(
            categoryId: selectedKind.id,
            name: data.draft.itemName.trimmingCharacters(in: .whitespacesAndNewlines),
            quantity: data.draft.quantity,
            lastReplacementPeriod: lastReplacementPeriod
        )
    }

    private func submitDirectKindTask(
        name: String,
        imageOption: ItemImageOption,
        repository: ItemRegistrationWriteRepository
    ) async {
        AppLog.enter(
            AppLog.itemRegistrationViewModel,
            "ItemRegistrationViewModel.submitDirectKindTask",
            "iconId=\(imageOption.id) nameLength=\(name.count)"
        )
        do {
            let kind = try await repository.createKind(name: name, imageOption: imageOption)
            update { data in
                insert(kind: kind, into: &data)
            }
            effect = .showMessage("소모품 종류를 등록했어요.")
            AppLog.success(
                AppLog.itemRegistrationViewModel,
                "ItemRegistrationViewModel.submitDirectKindTask",
                "kindId=\(kind.id)"
            )
        } catch {
            update { $0.isProcessing = false }
            effect = .showMessage(error.itemRegistrationMessage)
            AppLog.failure(AppLog.itemRegistrationViewModel, "ItemRegistrationViewModel.submitDirectKindTask", error)
        }
    }

    private func submitFormTask(
        request: ItemRegistrationCreateItemRequest,
        repository: ItemRegistrationWriteRepository
    ) async {
        AppLog.enter(
            AppLog.itemRegistrationViewModel,
            "ItemRegistrationViewModel.submitFormTask",
            "categoryId=\(request.categoryId) quantity=\(request.quantity) lastReplacementPeriod=\(request.lastReplacementPeriod.rawValue)"
        )
        do {
            try await repository.createItem(request: request)
            update { data in
                data.mode = .complete
                data.bottomSheet = nil
                data.selectedKindCandidate = nil
                data.isProcessing = false
            }
            AppLog.success(
                AppLog.itemRegistrationViewModel,
                "ItemRegistrationViewModel.submitFormTask",
                "categoryId=\(request.categoryId)"
            )
        } catch {
            update { $0.isProcessing = false }
            effect = .showMessage(error.itemRegistrationMessage)
            AppLog.failure(
                AppLog.itemRegistrationViewModel,
                "ItemRegistrationViewModel.submitFormTask",
                error,
                "categoryId=\(request.categoryId)"
            )
        }
    }

    private func update(_ transform: (inout ItemRegistrationViewData) -> Void) {
        var nextData = data
        transform(&nextData)
        data = nextData
    }

    private func loadCatalog(
        using repository: ItemRegistrationCatalogRepository,
        showLoading: Bool
    ) async {
        AppLog.enter(
            AppLog.itemRegistrationViewModel,
            "ItemRegistrationViewModel.loadCatalog",
            "showLoading=\(showLoading)"
        )
        if showLoading {
            state = .loading
        }

        do {
            let catalog = try await repository.catalog()
            update { data in
                data.itemKinds = catalog.itemKinds
                data.draft.selectedKind = nil
                data.draft.selectedImageOption = catalog.imageOptions.first
                data.kindSearchQuery = ""
                data.imageOptions = catalog.imageOptions
                data.bottomSheet = nil
                data.selectedKindCandidate = nil
                data.isProcessing = false
            }
            AppLog.success(
                AppLog.itemRegistrationViewModel,
                "ItemRegistrationViewModel.loadCatalog",
                "itemKinds=\(catalog.itemKinds.count) imageOptions=\(catalog.imageOptions.count)"
            )
        } catch {
            state = .loadFailed(message: error.itemRegistrationMessage)
            AppLog.failure(AppLog.itemRegistrationViewModel, "ItemRegistrationViewModel.loadCatalog", error)
        }
    }
}

enum ItemRegistrationViewEffect: Equatable {
    case showMessage(String)
}

private extension Error {
    var itemRegistrationMessage: String {
        if self is CreateItemError.DuplicatedName {
            return "이미 사용중인 이름이에요."
        }

        if self is CreateCategoryError.DuplicatedName {
            return "이미 등록된 소모품이에요."
        }

        if let localizedError = self as? LocalizedError,
           let description = localizedError.errorDescription {
            return description
        }

        return "소모품 등록 정보를 저장하지 못했어요."
    }
}
