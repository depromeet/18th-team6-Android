import Foundation

@MainActor
final class ManualRegistrationViewModel: ObservableObject {
    @Published private(set) var state: ManualRegistrationViewState

    private var mode: ManualRegistrationMode = .form
    private var draft: ManualRegistrationDraft
    private var kindSearchQuery = ""
    private var consumableKinds: [ManualConsumableKind]
    private let imageOptions: [ManualConsumableImageOption]
    private var bottomSheet: ManualRegistrationBottomSheet?
    private var selectedKindCandidate: ManualConsumableKind?

    init(
        consumableKinds: [ManualConsumableKind] = ManualRegistrationSampleData.consumableKinds,
        imageOptions: [ManualConsumableImageOption] = ManualRegistrationSampleData.imageOptions
    ) {
        self.consumableKinds = consumableKinds
        self.imageOptions = imageOptions
        self.draft = Self.initialDraft(imageOptions: imageOptions, consumableKinds: consumableKinds)
        self.state = .success(
            ManualRegistrationViewData(
                mode: mode,
                draft: draft,
                kindSearchQuery: kindSearchQuery,
                consumableKinds: consumableKinds,
                filteredKinds: consumableKinds,
                imageOptions: imageOptions,
                bottomSheet: bottomSheet,
                selectedKindCandidate: selectedKindCandidate
            )
        )
        #if DEBUG
        applyDebugInitialState()
        #endif
        publish()
    }

    private static func initialDraft(
        imageOptions: [ManualConsumableImageOption],
        consumableKinds: [ManualConsumableKind]
    ) -> ManualRegistrationDraft {
        ManualRegistrationDraft(
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
            fillDraftForPreview()
        case "kindSheet":
            bottomSheet = .kind
            selectedKindCandidate = consumableKinds.first
        case "dateSheet":
            fillDraftForPreview()
        case "directKind":
            mode = .directKind
        case "complete":
            mode = .complete
        default:
            break
        }
    }
    #endif

    private func fillDraftForPreview() {
        draft.selectedKind = consumableKinds.first
        draft.itemName = consumableKinds.first?.title ?? "면도기"
        draft.lastReplacementDateOption = .today
        draft.quantity = 1
    }

    func updateItemName(_ itemName: String) {
        draft.itemName = String(itemName.prefix(ManualRegistrationConfig.itemNameMaxLength))
        publish()
    }

    func updateKindSearchQuery(_ query: String) {
        kindSearchQuery = query
        let filteredKinds = filteredConsumableKinds()
        selectedKindCandidate = filteredKinds.first
        publish()
    }

    func updateDirectKindName(_ name: String) {
        draft.directKindName = String(name.prefix(ManualRegistrationConfig.kindNameMaxLength))
        publish()
    }

    func openKindSheet() {
        selectedKindCandidate = draft.selectedKind ?? filteredConsumableKinds().first
        bottomSheet = .kind
        publish()
    }

    func dismissBottomSheet() {
        bottomSheet = nil
        selectedKindCandidate = nil
        publish()
    }

    func selectKind(_ kind: ManualConsumableKind) {
        draft.selectedKind = kind
        draft.itemName = kind.title
        kindSearchQuery = ""
        bottomSheet = nil
        selectedKindCandidate = nil
        publish()
    }

    func selectKindCandidate(_ kind: ManualConsumableKind) {
        selectedKindCandidate = kind
        publish()
    }

    func confirmKindSelection() {
        guard let kind = selectedKindCandidate else { return }
        selectKind(kind)
    }

    func selectReplacementDate(_ option: ManualReplacementDateOption) {
        draft.lastReplacementDateOption = option
        publish()
    }

    func incrementQuantity() {
        draft.quantity = min(draft.quantity + 1, ManualRegistrationConfig.quantityMaximum)
        publish()
    }

    func decrementQuantity() {
        draft.quantity = max(draft.quantity - 1, ManualRegistrationConfig.quantityMinimum)
        publish()
    }

    func showDirectKindRegistration() {
        bottomSheet = nil
        selectedKindCandidate = nil
        mode = .directKind
        publish()
    }

    func selectImageOption(_ option: ManualConsumableImageOption) {
        draft.selectedImageOption = option
        publish()
    }

    func submitDirectKind() {
        let trimmedName = draft.directKindName.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmedName.isEmpty, let selectedImageOption = draft.selectedImageOption else { return }

        let kind = ManualConsumableKind(
            id: (consumableKinds.map(\.id).max() ?? 0) + 1,
            title: trimmedName,
            addedCount: 0,
            imageAssetName: selectedImageOption.assetName
        )
        consumableKinds.insert(kind, at: 0)
        draft.selectedKind = kind
        draft.itemName = trimmedName
        draft.directKindName = ""
        kindSearchQuery = ""
        selectedKindCandidate = nil
        mode = .form
        publish()
    }

    func submitForm() {
        guard currentData.canSubmitForm else { return }
        mode = .complete
        bottomSheet = nil
        selectedKindCandidate = nil
        publish()
    }

    func resetToForm() {
        mode = .form
        bottomSheet = nil
        selectedKindCandidate = nil
        publish()
    }

    private func publish() {
        state = .success(currentData)
    }

    private var currentData: ManualRegistrationViewData {
        let filteredKinds = filteredConsumableKinds()
        return ManualRegistrationViewData(
            mode: mode,
            draft: draft,
            kindSearchQuery: kindSearchQuery,
            consumableKinds: consumableKinds,
            filteredKinds: filteredKinds,
            imageOptions: imageOptions,
            bottomSheet: bottomSheet,
            selectedKindCandidate: selectedKindCandidate
        )
    }

    private func filteredConsumableKinds() -> [ManualConsumableKind] {
        let query = kindSearchQuery.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !query.isEmpty else { return consumableKinds }
        return consumableKinds.filter {
            $0.title.localizedCaseInsensitiveContains(query)
        }
    }
}

enum ManualRegistrationConfig {
    static let itemNameMaxLength = 15
    static let kindNameMaxLength = 15
    static let quantityMinimum = 0
    static let quantityMaximum = 99
}
