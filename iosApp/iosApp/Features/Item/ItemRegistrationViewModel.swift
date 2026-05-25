import Foundation

@MainActor
final class ItemRegistrationViewModel: ObservableObject {
    @Published private(set) var state: ItemRegistrationViewState

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
            selectedKindCandidate: nil
        )
        self.data = initialData
        self.state = .success(initialData)

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
                    data.draft.lastReplacementDateOption = .today
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
                    data.draft.lastReplacementDateOption = .today
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

        update { data in
            let kind = ItemKind(
                id: (data.itemKinds.map(\.id).max() ?? 0) + 1,
                title: trimmedName,
                addedCount: 0,
                imageAssetName: selectedImageOption.assetName
            )
            data.itemKinds.insert(kind, at: 0)
            data.draft.selectedKind = kind
            data.draft.itemName = trimmedName
            data.draft.directKindName = ""
            data.kindSearchQuery = ""
            data.selectedKindCandidate = nil
            data.mode = .form
        }
    }

    func submitForm() {
        guard data.canSubmitForm else { return }
        update { data in
            data.mode = .complete
            data.bottomSheet = nil
            data.selectedKindCandidate = nil
        }
    }

    func resetToForm() {
        update { data in
            data.mode = .form
            data.bottomSheet = nil
            data.selectedKindCandidate = nil
        }
    }

    private func update(_ transform: (inout ItemRegistrationViewData) -> Void) {
        var nextData = data
        transform(&nextData)
        data = nextData
    }
}

enum ItemRegistrationConfig {
    static let itemNameMaxLength = 15
    static let kindNameMaxLength = 15
    static let quantityMinimum = 0
    static let quantityMaximum = 99
}
