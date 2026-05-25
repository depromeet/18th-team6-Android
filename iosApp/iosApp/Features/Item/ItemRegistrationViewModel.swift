import Foundation

@MainActor
final class ItemRegistrationViewModel: ObservableObject {
    @Published private(set) var state: ItemRegistrationViewState
    @Published private(set) var effect: ItemRegistrationViewEffect?

    private let catalogRepository: ItemRegistrationCatalogRepository?
    private let writeRepository: ItemRegistrationWriteRepository?
    private let dateProvider: () -> Date
    private let calendar: Calendar

    private var data: ItemRegistrationViewData {
        didSet {
            guard data != oldValue else { return }
            state = .success(data)
        }
    }

    init(
        itemKinds: [ItemKind] = ItemRegistrationSampleData.itemKinds,
        imageOptions: [ItemImageOption] = ItemRegistrationSampleData.imageOptions
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
        self.dateProvider = Date.init
        self.calendar = .current
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
        dateProvider: @escaping () -> Date = Date.init,
        calendar: Calendar = .current,
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
        self.dateProvider = dateProvider
        self.calendar = calendar
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
            quantity: 1,
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
                    data.draft.quantity = 1
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
                    data.draft.quantity = 1
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
                imageAssetName: imageOption.assetName
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
        guard let selectedKind = data.draft.selectedKind else { return nil }

        return ItemRegistrationCreateItemRequest(
            categoryId: selectedKind.id,
            name: data.draft.itemName.trimmingCharacters(in: .whitespacesAndNewlines),
            quantity: data.draft.quantity,
            lastReplacementDate: data.draft.lastReplacementDateOption?.replacementDateString(
                referenceDate: dateProvider(),
                calendar: calendar
            )
        )
    }

    private func submitDirectKindTask(
        name: String,
        imageOption: ItemImageOption,
        repository: ItemRegistrationWriteRepository
    ) async {
        do {
            let kind = try await repository.createKind(name: name, imageOption: imageOption)
            update { data in
                insert(kind: kind, into: &data)
            }
            effect = .showMessage("소모품 종류를 등록했어요.")
        } catch {
            update { $0.isProcessing = false }
            effect = .showMessage(error.itemRegistrationMessage)
        }
    }

    private func submitFormTask(
        request: ItemRegistrationCreateItemRequest,
        repository: ItemRegistrationWriteRepository
    ) async {
        do {
            try await repository.createItem(request: request)
            update { data in
                data.mode = .complete
                data.bottomSheet = nil
                data.selectedKindCandidate = nil
                data.isProcessing = false
            }
        } catch {
            update { $0.isProcessing = false }
            effect = .showMessage(error.itemRegistrationMessage)
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
        } catch {
            state = .loadFailed(message: error.itemRegistrationMessage)
        }
    }
}

enum ItemRegistrationViewEffect: Equatable {
    case showMessage(String)
}

private extension ItemReplacementDateOption {
    func replacementDateString(referenceDate: Date, calendar: Calendar) -> String? {
        let dayOffset: Int
        switch self {
        case .withinOneWeek:
            dayOffset = -3
        case .twoToFourWeeksAgo:
            dayOffset = -21
        case .oneToThreeMonthsAgo:
            dayOffset = -60
        case .unknown:
            return nil
        }

        let date = calendar.date(byAdding: .day, value: dayOffset, to: referenceDate) ?? referenceDate
        return Self.dateFormatter.string(from: date)
    }

    private static let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.calendar = Calendar(identifier: .gregorian)
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter
    }()
}

private extension Error {
    var itemRegistrationMessage: String {
        if let localizedError = self as? LocalizedError,
           let description = localizedError.errorDescription {
            return description
        }

        return "소모품 등록 정보를 저장하지 못했어요."
    }
}
