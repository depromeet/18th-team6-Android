import Foundation

struct NotificationInboxCard: Identifiable, Equatable {
    let notificationId: Int64
    let entryId: Int64?
    let title: String
    let content: String?
    let itemName: String?
    let label: String?
    let nextReplacementDate: String?
    let hasLowStock: Bool
    let deepLink: String
    let createdAt: String
    var isRead: Bool

    var id: String { "\(notificationId):\(entryId.map(String.init) ?? "legacy")" }
    var date: Date? { NotificationInboxDate.date(from: createdAt) }
}

enum NotificationDestination: Equatable {
    case home
    case item(Int)

    init?(deepLink: String) {
        guard let url = URLComponents(string: deepLink), url.scheme?.lowercased() == "obrit",
              url.user == nil, url.password == nil, url.port == nil,
              url.query == nil, url.fragment == nil else { return nil }
        let parts = url.path.split(separator: "/", omittingEmptySubsequences: true)
        switch url.host?.lowercased() {
        case "home" where parts.isEmpty:
            self = .home
        case "items" where parts.count == 1:
            guard let id = Int(parts[0]), id > 0 else { return nil }
            self = .item(id)
        default:
            return nil
        }
    }
}

enum NotificationInboxDate {
    static func date(from value: String) -> Date? {
        let iso = ISO8601DateFormatter()
        iso.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        if let date = iso.date(from: value) { return date }
        iso.formatOptions = [.withInternetDateTime]
        if let date = iso.date(from: value) { return date }
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        for format in ["yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd'T'HH:mm:ss"] {
            formatter.dateFormat = format
            if let date = formatter.date(from: value) { return date }
        }
        return nil
    }

    static func groupLabel(for date: Date?, now: Date = Date(), calendar: Calendar = .current) -> String {
        guard let date else { return "이전 알림" }
        if calendar.isDate(date, inSameDayAs: now) { return "오늘" }
        if let yesterday = calendar.date(byAdding: .day, value: -1, to: now),
           calendar.isDate(date, inSameDayAs: yesterday) { return "어제" }
        let formatter = DateFormatter()
        formatter.calendar = calendar
        formatter.dateFormat = "yyyy.MM.dd"
        return formatter.string(from: date)
    }

    static func relativeTime(for date: Date?, now: Date = Date()) -> String {
        guard let date else { return "" }
        let minutes = max(0, Int(now.timeIntervalSince(date) / 60))
        if minutes < 1 { return "방금 전" }
        if minutes < 60 { return "\(minutes)분 전" }
        if minutes < 1440 { return "\(minutes / 60)시간 전" }
        return "\(minutes / 1440)일 전"
    }
}
