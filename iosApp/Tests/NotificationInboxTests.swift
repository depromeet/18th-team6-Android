import Foundation

private enum TestFailure: Error { case request }

private actor InboxRepositoryFake: NotificationInboxRepository {
    var cards: [NotificationInboxCard]
    var readIds: [Int64] = []
    var failRead = false
    var failLoad = false
    var failAll = false
    var failItem = false
    var heldLoad: CheckedContinuation<[NotificationInboxCard], Error>?
    var holdNextLoad = false
    private var heldCards: [NotificationInboxCard] = []

    init(cards: [NotificationInboxCard]) { self.cards = cards }
    func configure(read: Bool = false, load: Bool = false, all: Bool = false, item: Bool = false) {
        failRead = read
        failLoad = load
        failAll = all
        failItem = item
    }
    func holdLoad() { holdNextLoad = true }
    func hasHeldLoad() -> Bool { heldLoad != nil }
    func releaseLoad() {
        heldLoad?.resume(returning: heldCards)
        heldLoad = nil
    }
    func load() async throws -> [NotificationInboxCard] {
        if failLoad { throw TestFailure.request }
        if holdNextLoad {
            holdNextLoad = false
            heldCards = cards
            return try await withCheckedThrowingContinuation { heldLoad = $0 }
        }
        return cards
    }
    func markRead(notificationId: Int64) async throws -> String {
        readIds.append(notificationId)
        if failRead { throw TestFailure.request }
        cards = cards.map {
            var card = $0
            if card.notificationId == notificationId { card.isRead = true }
            return card
        }
        return "obrit://home"
    }
    func markAllRead() async throws {
        if failAll { throw TestFailure.request }
        cards = cards.map { var card = $0; card.isRead = true; return card }
    }
    func validateItem(id: Int) async throws {
        if failItem { throw TestFailure.request }
    }
}

@main
private struct NotificationInboxTests {
    static func card(_ parent: Int64, _ entry: Int64?, item: Int = 10, read: Bool = false) -> NotificationInboxCard {
        NotificationInboxCard(
            notificationId: parent, entryId: entry, title: "교체 알림", content: "칫솔을 교체해주세요",
            itemName: "칫솔", label: "교체 D+1", nextReplacementDate: "2026-10-30", hasLowStock: true,
            deepLink: "obrit://items/\(item)", createdAt: "2026-10-31T09:00:00", isRead: read
        )
    }

    @MainActor
    static func waitUntil(_ condition: @MainActor () -> Bool) async {
        for _ in 0..<300 {
            if condition() { return }
            try? await Task.sleep(nanoseconds: 5_000_000)
        }
        fatalError("Timed out waiting for state")
    }

    @MainActor
    static func loaded(_ vm: NotificationInboxViewModel) -> [NotificationInboxCard] {
        guard case let .success(cards) = vm.state else { fatalError("Expected successful state") }
        return cards
    }

    @MainActor
    static func main() async {
        for invalid in ["https://example.com", "obrit://items/-1", "obrit://items/0", "obrit://items/1/2", "obrit://items/abc", "obrit://unknown", "obrit://user@home", "obrit://items/1?redirect=x"] {
            precondition(NotificationDestination(deepLink: invalid) == nil)
        }
        precondition(NotificationDestination(deepLink: "obrit://home") == .home)
        precondition(NotificationDestination(deepLink: "obrit://items/10") == .item(10))
        precondition(NotificationInboxDate.date(from: "2026-10-31T09:00:00") != nil)
        precondition(NotificationInboxDate.date(from: "2026-10-31T09:00:00.123Z") != nil)

        let opens = NotificationOpenCenter()
        opens.enqueue(userInfo: ["notificationId": "12", "deepLink": "obrit://home"])
        opens.enqueue(userInfo: ["notificationId": NSNumber(value: 13), "deepLink": "obrit://items/30"])
        precondition(opens.takeFirst()?.notificationId == 12)
        precondition(opens.takeFirst()?.notificationId == 13)
        precondition(opens.takeFirst() == nil)

        let cards = [card(12, 31), card(12, 32, item: 20), card(13, nil, item: 30), card(14, 40, read: true)]
        precondition(Set(cards.map(\.id)).count == 4)
        let repo = InboxRepositoryFake(cards: cards)
        let vm = NotificationInboxViewModel(repository: repo)
        await vm.refresh()
        precondition(vm.unreadCount == 2, "Count parents, not cards; loading must not mark read")
        let initialReadIds = await repo.readIds
        precondition(initialReadIds.isEmpty)
        vm.select(cards[1])
        vm.select(cards[1])
        await waitUntil { !vm.isProcessing }
        precondition(vm.unreadCount == 1)
        precondition(loaded(vm).filter { $0.notificationId == 12 }.allSatisfy(\.isRead))
        precondition(loaded(vm).first { $0.notificationId == 13 }?.isRead == false)
        precondition(vm.navigation?.destination == .item(20), "Preserve card link instead of parent's home link")
        let readIds = await repo.readIds
        precondition(readIds == [12], "Repeated taps must not create concurrent reads")
        vm.clearNavigation()

        await repo.configure(read: true)
        vm.select(cards[2])
        await waitUntil { !vm.isProcessing }
        precondition(vm.unreadCount == 1 && vm.navigation == nil && vm.message != nil)
        await repo.configure(all: true)
        vm.markAllRead()
        await waitUntil { !vm.isProcessing }
        precondition(vm.unreadCount == 1)
        await repo.configure()
        vm.markAllRead()
        await waitUntil { !vm.isProcessing }
        precondition(vm.unreadCount == 0 && loaded(vm).allSatisfy(\.isRead))

        let failedItemRepo = InboxRepositoryFake(cards: [card(15, nil)])
        let failedItemVM = NotificationInboxViewModel(repository: failedItemRepo)
        await failedItemVM.refresh()
        await failedItemRepo.configure(item: true)
        failedItemVM.select(card(15, nil))
        await waitUntil { !failedItemVM.isProcessing }
        precondition(failedItemVM.navigation?.destination == .home && failedItemVM.message != nil)
        precondition(failedItemVM.unreadCount == 0)

        let pushRepo = InboxRepositoryFake(cards: cards)
        let pushVM = NotificationInboxViewModel(repository: pushRepo)
        await pushVM.refresh()
        pushVM.open(notificationId: 12, deepLink: "obrit://home", resetToHome: true)
        await waitUntil { !pushVM.isProcessing }
        precondition(pushVM.unreadCount == 1 && pushVM.navigation?.destination == .home)
        precondition(pushVM.navigation?.resetToHome == true)

        let staleRepo = InboxRepositoryFake(cards: [card(16, 50)])
        let staleVM = NotificationInboxViewModel(repository: staleRepo)
        await staleVM.refresh()
        await staleRepo.holdLoad()
        let refreshTask = Task { await staleVM.refresh() }
        for _ in 0..<100 {
            if await staleRepo.hasHeldLoad() { break }
            try? await Task.sleep(nanoseconds: 5_000_000)
        }
        staleVM.select(card(16, 50))
        await waitUntil { !staleVM.isProcessing }
        await staleRepo.releaseLoad()
        await refreshTask.value
        try? await Task.sleep(nanoseconds: 50_000_000)
        precondition(staleVM.unreadCount == 0 && loaded(staleVM).allSatisfy(\.isRead), "Stale reads cannot restore unread state")

        let emptyRepo = InboxRepositoryFake(cards: [])
        let emptyVM = NotificationInboxViewModel(repository: emptyRepo)
        await emptyRepo.configure(load: true)
        await emptyVM.refresh()
        precondition(emptyVM.state == .loadFailed)
        await emptyRepo.configure()
        await emptyVM.refresh()
        precondition(loaded(emptyVM).isEmpty && emptyVM.unreadCount == 0)

        let badgeRepo = InboxRepositoryFake(cards: (1...100).map { card(Int64($0), nil) })
        let badgeVM = NotificationInboxViewModel(repository: badgeRepo)
        await badgeVM.refresh()
        precondition(badgeVM.unreadCount == 100)
        print("PASS: parent badge count, bundled/legacy cards, read-all, failed reads, destinations, push taps, stale refresh, retry")
    }
}
