import Combine
import Foundation

enum NotificationInboxViewState: Equatable {
    case loading
    case loadFailed
    case success([NotificationInboxCard])
}

struct NotificationInboxNavigation: Equatable {
    let id = UUID()
    let destination: NotificationDestination
    let resetToHome: Bool
}

@MainActor
final class NotificationInboxViewModel: ObservableObject {
    @Published private(set) var state: NotificationInboxViewState = .loading
    @Published private(set) var unreadCount = 0
    @Published private(set) var isProcessing = false
    @Published private(set) var navigation: NotificationInboxNavigation?
    @Published var message: String?

    private let repository: any NotificationInboxRepository
    private var cards: [NotificationInboxCard] = []
    private var hasLoaded = false
    private var isRefreshing = false
    private var needsRefresh = false
    private var revision = 0

    init(repository: any NotificationInboxRepository) {
        self.repository = repository
    }

    func refresh() async {
        guard !isRefreshing else {
            needsRefresh = true
            return
        }
        isRefreshing = true
        let initialRevision = revision
        defer {
            isRefreshing = false
            if needsRefresh {
                needsRefresh = false
                Task { await refresh() }
            }
        }
        do {
            let loaded = try await repository.load()
            guard !Task.isCancelled else { return }
            guard initialRevision == revision else {
                needsRefresh = true
                return
            }
            cards = loaded.sorted {
                let first = $0.date ?? .distantPast
                let second = $1.date ?? .distantPast
                return first == second ? $0.id > $1.id : first > second
            }
            hasLoaded = true
            publishCards()
        } catch {
            guard !Task.isCancelled else { return }
            if !hasLoaded { state = .loadFailed }
            else { message = "알림을 불러오지 못했어요. 다시 시도해주세요." }
        }
    }

    func select(_ card: NotificationInboxCard) {
        open(notificationId: card.notificationId, deepLink: card.deepLink, resetToHome: false)
    }

    func open(notificationId: Int64?, deepLink: String?, resetToHome: Bool) {
        guard !isProcessing else { return }
        isProcessing = true
        revision += 1
        Task {
            defer { isProcessing = false }
            var destinationLink = deepLink
            if let notificationId {
                do {
                    let readLink = try await repository.markRead(notificationId: notificationId)
                    // A bundled response points home; an inbox card retains its own item link.
                    if destinationLink == nil { destinationLink = readLink }
                    cards = cards.map {
                        var card = $0
                        if card.notificationId == notificationId { card.isRead = true }
                        return card
                    }
                    revision += 1
                    if hasLoaded { publishCards() }
                } catch {
                    message = "알림을 읽음 처리하지 못했어요. 다시 시도해주세요."
                    return
                }
            }
            guard let link = destinationLink, let destination = NotificationDestination(deepLink: link) else {
                message = "알림의 이동 경로를 확인할 수 없어요."
                navigate(.home, resetToHome: true)
                return
            }
            if case let .item(id) = destination {
                do { try await repository.validateItem(id: id) }
                catch {
                    message = "소모품이 삭제되었거나 정보를 조회할 수 없어요."
                    navigate(.home, resetToHome: true)
                    return
                }
            }
            navigate(destination, resetToHome: resetToHome)
            if !hasLoaded { await refresh() }
        }
    }

    func markAllRead() {
        guard !isProcessing, unreadCount > 0 else { return }
        isProcessing = true
        revision += 1
        Task {
            defer { isProcessing = false }
            do {
                try await repository.markAllRead()
                cards = cards.map {
                    var card = $0
                    card.isRead = true
                    return card
                }
                revision += 1
                publishCards()
            } catch {
                message = "전체 읽음 처리에 실패했어요. 다시 시도해주세요."
            }
        }
    }

    func clearNavigation() { navigation = nil }

    private func navigate(_ destination: NotificationDestination, resetToHome: Bool) {
        navigation = NotificationInboxNavigation(destination: destination, resetToHome: resetToHome)
    }

    private func publishCards() {
        unreadCount = Set(cards.filter { !$0.isRead }.map(\.notificationId)).count
        state = .success(cards)
    }
}
