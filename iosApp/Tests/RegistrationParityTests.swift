import Combine
import Foundation

private enum TestFailure: Error { case failed }

private final class CompletionSpy: OnboardingCompletionStore {
    var hasCompletedOnboarding = false
    func markCompleted() { hasCompletedOnboarding = true }
}

@MainActor
private final class NotificationSpy: OnboardingNotificationSetup {
    let store: CompletionSpy
    var requested = false
    var continuation: CheckedContinuation<Void, Never>?
    init(store: CompletionSpy) { self.store = store }
    func enableAndRequestPermission() async {
        precondition(store.hasCompletedOnboarding, "Persist completion before asking OS permission")
        requested = true
        await withCheckedContinuation { continuation = $0 }
    }
    func respond() { continuation?.resume(); continuation = nil }
}

@MainActor
private final class OnboardingSpy: OnboardingRepository {
    var calls: [[OnboardingRegistrationRequest]] = []
    var shouldFailSecondBatch = true
    func groups() async throws -> [OnboardingCategoryGroup] { [] }
    func options() async throws -> [OnboardingItemOption] { [] }
    func commonIconURL() async throws -> String { "common-icon" }
    func registerItems(requests: [OnboardingRegistrationRequest]) async throws {
        calls.append(requests)
        if calls.count == 2 && shouldFailSecondBatch { throw TestFailure.failed }
    }
}

@MainActor
private final class ManualSpy: ItemRegistrationWriteRepository {
    var categories: [String] = []
    var items: [ItemRegistrationCreateItemRequest] = []
    var rejectsDuplicate = false
    func createKind(name: String) async throws -> ItemKind {
        categories.append(name)
        return ItemKind(id: 500, title: name, addedCount: 0, imageURL: "common-icon", categoryGroupId: 3)
    }
    func createItem(request: ItemRegistrationCreateItemRequest) async throws {
        if rejectsDuplicate { throw CreateItemError.DuplicatedName() }
        items.append(request)
    }
    func createItems(request: ItemRegistrationCreateItemsRequest) async throws {}
}

private struct CatalogStub: ItemRegistrationCatalogRepository {
    func catalog() async throws -> ItemRegistrationCatalog {
        let kinds = await RegistrationParityTests.kinds
        let groups = await RegistrationParityTests.groups
        return ItemRegistrationCatalog(itemKinds: kinds, imageOptions: [], categoryGroups: groups, existingItemNames: ["칫솔", "칫솔 2"])
    }
}

@main
@MainActor
struct RegistrationParityTests {
    static let groups = [ItemKindGroup(id: 2, name: "주방", displayOrder: 2), ItemKindGroup(id: 1, name: "욕실", displayOrder: 1), ItemKindGroup(id: 4, name: "빈 그룹", displayOrder: 0), ItemKindGroup(id: 3, name: "직접 등록", displayOrder: 3, isUserDefined: true)]
    static let kinds = [ItemKind(id: 1, title: "칫솔", addedCount: 0, imageURL: "", categoryGroupId: 1), ItemKind(id: 2, title: "주방 수건", addedCount: 0, imageURL: "", categoryGroupId: 2)]

    static func manualData(_ vm: ItemRegistrationViewModel) -> ItemRegistrationViewData {
        guard case let .success(data) = vm.state else { preconditionFailure("Expected manual state") }
        return data
    }
    static func onboardingData(_ vm: OnboardingViewModel) -> OnboardingViewData {
        guard case let .success(data) = vm.state else { preconditionFailure("Expected onboarding state") }
        return data
    }
    static func eventually(_ predicate: () -> Bool) async {
        for _ in 0..<1000 {
            if predicate() { return }
            try? await Task.sleep(for: .milliseconds(1))
        }
        preconditionFailure("Async state did not complete")
    }

    static func main() async {
        testNames()
        await testManualRegistration()
        testOnboardingSearch()
        await testBatchRetryAndPermissionOrdering()
        print("PASS: naming, manual validation/reset, committed global search, name-only category creation, onboarding custom selection, 20-item batches/retry, completion-before-permission")
    }

    static func testNames() {
        precondition(ItemRegistrationNameRule.availableName(for: "칫솔", existingNames: ["칫솔", "칫솔 2"]) == "칫솔 1")
        let long = String(repeating: "가", count: 15)
        precondition(ItemRegistrationNameRule.availableName(for: long, existingNames: [long]).count == 15)
        precondition(ItemRegistrationNameRule.error(for: "ㅋ 카ㅌ", existingNames: []) == nil)
        precondition(ItemRegistrationNameRule.error(for: "Brush", existingNames: ["brush"]) == nil)
        precondition(ItemRegistrationNameRule.error(for: "칫솔", existingNames: ["칫솔"]) == ItemRegistrationNameRule.duplicateMessage)
        precondition(ItemRegistrationNameRule.error(for: "가  나", existingNames: []) == "공백은 단어 사이에 한 칸만 입력할 수 있어요")
        precondition(ItemRegistrationNameRule.error(for: "칫솔!", existingNames: []) == "한글 및 영문, 숫자만 입력 가능합니다.")
        precondition(ItemRegistrationNameRule.error(for: long + "가", existingNames: []) == "이름은 15자 이내로 입력해주세요")
    }

    static func testManualRegistration() async {
        let writer = ManualSpy()
        let vm = ItemRegistrationViewModel(catalogRepository: CatalogStub(), writeRepository: writer)
        await eventually { if case .success = vm.state { true } else { false } }
        precondition(manualData(vm).draft.quantity == 1)
        precondition(manualData(vm).visibleGroups.map(\.id) == [1, 2, 3])
        vm.selectKind(kinds[0])
        precondition(manualData(vm).draft.itemName == "칫솔 1")
        vm.updateItemName("내 칫솔")
        vm.selectKind(kinds[1])
        precondition(manualData(vm).draft.itemName == "내 칫솔", "Preserve the user-edited name when switching consumables")
        vm.openKindSheet()
        vm.updateKindSearchQuery("  수건  ")
        precondition(manualData(vm).filteredKinds.count == 2, "Typing must not filter the catalog")
        precondition(manualData(vm).kindSearchSuggestions == ["주방 수건"])
        vm.submitKindSearch()
        precondition(manualData(vm).filteredKinds.map(\.id) == [2])
        vm.clearKindSearch()
        precondition(manualData(vm).filteredKinds.count == 2)
        vm.updateKindSearchQuery("새 소모품")
        vm.submitKindSearch()
        vm.createSearchItem()
        precondition(manualData(vm).bottomSheet == .directKind && manualData(vm).draft.directKindName == "새 소모품")
        precondition(manualData(vm).canSubmitDirectKind, "No representative icon required")
        vm.submitDirectKind()
        await eventually { writer.categories.count == 1 && !manualData(vm).isProcessing }
        precondition(writer.items.isEmpty, "Creating a category must not create an item yet")
        precondition(manualData(vm).draft.selectedKind?.id == 500 && manualData(vm).bottomSheet == nil)
        precondition(manualData(vm).draft.itemName == "내 칫솔")
        vm.submitForm()
        await eventually { vm.effect == .registered("내 칫솔") }
        precondition(writer.items.first?.quantity == 1 && writer.items.first?.lastReplacementPeriod == nil)
        precondition(manualData(vm).draft.selectedKind == nil && manualData(vm).draft.itemName.isEmpty && manualData(vm).draft.quantity == 1)
        vm.clearEffect()
        vm.selectKind(kinds[0])
        vm.updateItemName("다른 이름")
        writer.rejectsDuplicate = true
        vm.submitForm()
        await eventually { vm.effect != nil }
        precondition(manualData(vm).itemNameError == ItemRegistrationNameRule.duplicateMessage && !manualData(vm).canSubmitForm)
    }

    static func testOnboardingSearch() {
        let options = (1...4).map { OnboardingItemOption(id: $0, title: "칫솔 \($0)", addedCount: 0, imageURL: "", categoryGroupId: $0 % 2 + 1) }
        let vm = OnboardingViewModel(initialOptions: options, initialGroups: [OnboardingCategoryGroup(id: 1, name: "욕실", displayOrder: 1), OnboardingCategoryGroup(id: 2, name: "주방", displayOrder: 2)])
        vm.startOnboarding()
        vm.toggleOption(options[0])
        vm.updateSearchQuery("칫솔")
        precondition(onboardingData(vm).searchSuggestions.count == 3)
        precondition(onboardingData(vm).submittedSearchQuery == nil)
        vm.submitSearch()
        precondition(onboardingData(vm).searchResults.count == 4)
        vm.selectGroup(onboardingData(vm).visibleGroups[1])
        precondition(onboardingData(vm).selectedCount == 1 && onboardingData(vm).submittedSearchQuery == nil)
        vm.updateSearchQuery("새 수세미")
        vm.submitSearch()
        vm.addSearchItem()
        let custom = onboardingData(vm).selectedOptions.first(where: \.isNew)!
        precondition(onboardingData(vm).selectedGroup?.isUserDefined == true && onboardingData(vm).selectedCount == 2)
        vm.updateSearchQuery("새 수세미")
        vm.submitSearch()
        precondition(onboardingData(vm).searchResults.isEmpty && onboardingData(vm).newItemNameError == ItemRegistrationNameRule.duplicateMessage)
        vm.toggleOption(custom)
        precondition(!onboardingData(vm).visibleGroups.contains(where: \.isUserDefined) && onboardingData(vm).selectedGroup?.name == "욕실")
        vm.updateSearchQuery("금지!")
        vm.submitSearch()
        vm.addSearchItem()
        precondition(!onboardingData(vm).options.contains(where: \.isNew))
    }

    static func testBatchRetryAndPermissionOrdering() async {
        let repo = OnboardingSpy()
        let store = CompletionSpy()
        let notification = NotificationSpy(store: store)
        let options = (1...44).map { OnboardingItemOption(id: $0, title: "소모품 \($0)", addedCount: 0, imageURL: "", categoryGroupId: 1) }
        let vm = OnboardingViewModel(repository: repo, initialOptions: options, initialGroups: [OnboardingCategoryGroup(id: 1, name: "욕실", displayOrder: 1)], completionStore: store, notificationSetup: notification)
        vm.startOnboarding()
        for option in options { vm.toggleOption(option) }
        vm.updateSearchQuery("새 소모품")
        vm.submitSearch()
        vm.addSearchItem()
        vm.moveNext()
        await eventually { vm.effect != nil }
        precondition(repo.calls.map(\.count) == [20, 20])
        precondition(!store.hasCompletedOnboarding && !notification.requested)
        precondition(onboardingData(vm).selectedCount == 45 && !onboardingData(vm).isProcessing)
        vm.clearEffect()
        repo.shouldFailSecondBatch = false
        vm.moveNext()
        await eventually { notification.requested }
        precondition(repo.calls.map(\.count) == [20, 20, 20, 5], "Retry must not resubmit the first completed batch")
        precondition(repo.calls.last?.last?.categoryId == nil && repo.calls.last?.last?.newCategoryName == "새 소모품")
        precondition(store.hasCompletedOnboarding && vm.effect == nil && onboardingData(vm).isProcessing)
        notification.respond()
        await eventually { vm.effect == .registered(45) }
    }
}
