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
        imageOptions: [ItemImageOption] = [],
        existingItemNames: [String] = []
    ) {
        let initialData = ItemRegistrationViewData(
            mode: .form,
            draft: Self.initialDraft(imageOptions: imageOptions),
            kindSearchQuery: "",
            itemKinds: itemKinds,
            imageOptions: imageOptions,
            bottomSheet: nil,
            selectedKindCandidate: nil,
            isProcessing: false,
            existingItemNames: existingItemNames
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
                showDirectKindRegistration()
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
        guard !data.isProcessing else { return }
        update {
            $0.draft.itemName = itemName
            $0.hasEditedItemName = true
            $0.hasCustomItemName = !itemName.isEmpty && itemName != $0.automaticItemName
        }
    }

    func updateKindSearchQuery(_ query: String) {
        guard !data.isProcessing else { return }
        guard data.kindSearchQuery != query else { return }
        update { data in
            data.kindSearchQuery = query
            if query.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty { data.submittedKindSearchQuery = nil }
        }
    }

    func submitKindSearch(_ suggestion: String? = nil) {
        guard !data.isProcessing else { return }
        let query = (suggestion ?? data.kindSearchQuery).trimmingCharacters(in: .whitespacesAndNewlines)
        update {
            $0.kindSearchQuery = query
            $0.submittedKindSearchQuery = query.isEmpty ? nil : query
        }
    }

    func clearKindSearch() {
        update {
            $0.kindSearchQuery = ""
            $0.submittedKindSearchQuery = nil
        }
    }

    func updateDirectKindName(_ name: String) {
        guard !data.isProcessing else { return }
        update {
            $0.draft.directKindName = name
            $0.hasEditedDirectKindName = true
        }
    }

    func openKindSheet() {
        update { data in
            data.selectedKindCandidate = data.draft.selectedKind
            data.prefersUserKinds = data.categoryGroups.contains {
                $0.isUserDefined && $0.id == data.draft.selectedKind?.categoryGroupId
            }
            data.kindSearchQuery = ""
            data.submittedKindSearchQuery = nil
            data.bottomSheet = .kind
        }
    }

    func dismissBottomSheet() {
        guard !data.isProcessing else { return }
        update {
            $0.bottomSheet = nil
            $0.selectedKindCandidate = nil
        }
    }

    func returnToKindList() {
        guard !data.isProcessing else { return }
        update { $0.bottomSheet = .kind }
    }

    func selectKindCandidate(_ kind: ItemKind) {
        guard !data.isProcessing else { return }
        update { $0.selectedKindCandidate = kind }
    }

    func confirmKindSelection() {
        guard let kind = data.selectedKindCandidate else { return }
        selectKind(kind)
    }

    func selectKind(_ kind: ItemKind) {
        update { data in
            data.draft.selectedKind = kind
            let autoName = ItemRegistrationNameRule.availableName(for: kind.title, existingNames: data.existingItemNames)
            if !data.hasCustomItemName { data.draft.itemName = autoName }
            data.automaticItemName = autoName
            data.hasCustomItemName = !data.draft.itemName.isEmpty && data.draft.itemName != autoName
            data.hasEditedItemName = true
            data.kindSearchQuery = ""
            data.submittedKindSearchQuery = nil
            data.bottomSheet = nil
            data.selectedKindCandidate = nil
        }
    }

    func selectReplacementDate(_ option: ItemReplacementDateOption) {
        update { $0.draft.lastReplacementDateOption = option }
    }

    func incrementQuantity() {
        guard !data.isProcessing else { return }
        update {
            $0.draft.quantity = min($0.draft.quantity + 1, ItemRegistrationConfig.manualQuantityRange.upperBound)
        }
    }

    func decrementQuantity() {
        guard !data.isProcessing else { return }
        update {
            $0.draft.quantity = max($0.draft.quantity - 1, ItemRegistrationConfig.manualQuantityRange.lowerBound)
        }
    }

    func updateQuantity(_ quantity: Int) {
        guard !data.isProcessing else { return }
        update {
            $0.draft.quantity = min(
                max(quantity, ItemRegistrationConfig.manualQuantityRange.lowerBound),
                ItemRegistrationConfig.manualQuantityRange.upperBound
            )
        }
    }

    func showDirectKindRegistration() {
        guard !data.isProcessing else { return }
        update { data in
            data.prefersUserKinds = true
            data.draft.directKindName = ""
            data.hasEditedDirectKindName = false
            data.bottomSheet = .directKind
        }
    }

    func selectImageOption(_ option: ItemImageOption) {
        update { $0.draft.selectedImageOption = option }
    }

    func submitDirectKind() {
        guard data.canSubmitDirectKind else { return }
        let name = data.draft.directKindName
        guard let writeRepository else {
            insertDirectKind(name: name)
            return
        }
        update { $0.isProcessing = true }
        Task { await submitDirectKindTask(name: name, repository: writeRepository) }
    }

    func createSearchItem() {
        guard data.hasNoSearchResults else { return }
        let query = data.submittedKindSearchQuery ?? ""
        showDirectKindRegistration()
        update {
            $0.draft.directKindName = query
            $0.hasEditedDirectKindName = !query.isEmpty
        }
    }

    func submitForm() {
        guard data.canSubmitForm,
              let request = makeCreateItemRequest() else { return }
        guard let writeRepository else {
            finishRegistration(name: request.name)
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
            data.draft = Self.initialDraft(imageOptions: data.imageOptions)
            data.hasEditedItemName = false
            data.hasCustomItemName = false
            data.automaticItemName = ""
            data.hasEditedDirectKindName = false
            data.kindSearchQuery = ""
            data.submittedKindSearchQuery = nil
            data.bottomSheet = nil
            data.selectedKindCandidate = nil
        }
    }

    private func insertDirectKind(name: String) {
        update { data in
            insert(kind: ItemKind(
                id: (data.itemKinds.map(\.id).max() ?? 0) + ItemRegistrationConfig.nextIDIncrement,
                title: name,
                addedCount: ItemRegistrationConfig.newKindInitialAddedCount,
                imageURL: data.imageOptions.first { $0.id == 1 }?.imageURL ?? ""
            ), into: &data)
        }
    }

    private func insert(kind: ItemKind, into data: inout ItemRegistrationViewData) {
        var userKind = kind
        if !data.categoryGroups.contains(where: \.isUserDefined) {
            data.categoryGroups.append(ItemKindGroup(
                id: kind.categoryGroupId ?? -1,
                name: "직접 등록",
                displayOrder: Int.max,
                isUserDefined: true
            ))
        }
        userKind.categoryGroupId = data.categoryGroups.first(where: \.isUserDefined)?.id
        data.itemKinds.insert(userKind, at: ItemRegistrationConfig.newKindInsertionIndex)
        data.draft.directKindName = ""
        data.hasEditedDirectKindName = false
        data.kindSearchQuery = ""
        data.selectedKindCandidate = userKind
        data.prefersUserKinds = true
        data.draft.selectedKind = userKind
        let autoName = ItemRegistrationNameRule.availableName(for: userKind.title, existingNames: data.existingItemNames)
        if !data.hasCustomItemName { data.draft.itemName = autoName }
        data.automaticItemName = autoName
        data.hasCustomItemName = !data.draft.itemName.isEmpty && data.draft.itemName != autoName
        data.hasEditedItemName = true
        data.submittedKindSearchQuery = nil
        data.bottomSheet = nil
        data.isProcessing = false
    }

    private func makeCreateItemRequest() -> ItemRegistrationCreateItemRequest? {
        guard let selectedKind = data.draft.selectedKind else { return nil }

        return ItemRegistrationCreateItemRequest(
            categoryId: selectedKind.id,
            name: data.draft.itemName,
            quantity: data.draft.quantity,
            lastReplacementPeriod: nil
        )
    }

    private func submitDirectKindTask(
        name: String,
        repository: ItemRegistrationWriteRepository
    ) async {
        AppLog.enter(
            AppLog.itemRegistrationViewModel,
            "ItemRegistrationViewModel.submitDirectKindTask",
            "nameLength=\(name.count)"
        )
        do {
            let kind = try await repository.createKind(name: name)
            update { data in
                insert(kind: kind, into: &data)
            }
            AppLog.success(
                AppLog.itemRegistrationViewModel,
                "ItemRegistrationViewModel.submitDirectKindTask",
                "kindId=\(kind.id)"
            )
        } catch {
            update {
                $0.isProcessing = false
                if error is CreateCategoryError.DuplicatedName || error.itemRegistrationMessage == ItemRegistrationNameRule.duplicateMessage {
                    $0.existingItemNames.append(name)
                }
            }
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
            "categoryId=\(request.categoryId) quantity=\(request.quantity) lastReplacementPeriod=\(request.lastReplacementPeriod?.rawValue ?? "nil")"
        )
        do {
            try await repository.createItem(request: request)
            finishRegistration(name: request.name)
            AppLog.success(
                AppLog.itemRegistrationViewModel,
                "ItemRegistrationViewModel.submitFormTask",
                "categoryId=\(request.categoryId)"
            )
        } catch {
            update {
                $0.isProcessing = false
                if error is CreateItemError.DuplicatedName || error.itemRegistrationMessage == ItemRegistrationNameRule.duplicateMessage {
                    $0.existingItemNames.append(request.name)
                }
            }
            effect = .showMessage(error.itemRegistrationMessage)
            AppLog.failure(
                AppLog.itemRegistrationViewModel,
                "ItemRegistrationViewModel.submitFormTask",
                error,
                "categoryId=\(request.categoryId)"
            )
        }
    }

    private func finishRegistration(name: String) {
        resetToForm()
        update {
            $0.isProcessing = false
            $0.existingItemNames.append(name)
        }
        effect = .registered(name)
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
                data.existingItemNames = catalog.existingItemNames
                data.hasEditedItemName = false
                data.hasCustomItemName = false
                data.automaticItemName = ""
                data.hasEditedDirectKindName = false
                data.categoryGroups = catalog.categoryGroups
                data.itemKinds = catalog.itemKinds
                data.draft.selectedKind = nil
                data.draft.quantity = ItemRegistrationConfig.defaultQuantity
                data.draft.selectedImageOption = catalog.imageOptions.first
                data.kindSearchQuery = ""
                data.submittedKindSearchQuery = nil
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
    case registered(String)
    case showMessage(String)
}

private extension Error {
    var itemRegistrationMessage: String {
        if self is CreateItemError.DuplicatedName {
            return ItemRegistrationNameRule.duplicateMessage
        }

        if self is CreateCategoryError.DuplicatedName {
            return ItemRegistrationNameRule.duplicateMessage
        }

        if let localizedError = self as? LocalizedError,
           let description = localizedError.errorDescription {
            return description
        }

        return "소모품 등록 정보를 저장하지 못했어요."
    }
}
