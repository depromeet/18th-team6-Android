import Foundation

struct StatusWidgetSnapshot: Codable, Sendable {
    let userId: Int64
    let updatedAt: Date
    let state: StatusWidgetState
}

protocol StatusWidgetCaching: Sendable {
    func userId() async -> Int64?
    func load() async -> StatusWidgetSnapshot?
    func save(_ state: StatusWidgetState, for userId: Int64, at date: Date) async -> Bool
}

actor StatusWidgetCache: StatusWidgetCaching {
    private let defaults: UserDefaults?
    private let snapshotKey = "obrit.statusWidget.snapshot"

    init(groupIdentifier: String? = Bundle.main.object(forInfoDictionaryKey: "OBRitAppGroupIdentifier") as? String) {
        if let groupIdentifier, !groupIdentifier.isEmpty, !groupIdentifier.hasPrefix("$(") {
            defaults = UserDefaults(suiteName: groupIdentifier)
        } else {
            defaults = nil
        }
    }

    func userId() -> Int64? {
        guard let value = defaults?.object(forKey: "userId") as? NSNumber, value.int64Value > 0 else { return nil }
        return value.int64Value
    }

    func load() -> StatusWidgetSnapshot? {
        guard let userId = userId(), let data = defaults?.data(forKey: snapshotKey),
              let snapshot = try? JSONDecoder().decode(StatusWidgetSnapshot.self, from: data),
              snapshot.userId == userId else { return nil }
        return snapshot
    }

    func save(_ state: StatusWidgetState, for userId: Int64, at date: Date) -> Bool {
        guard self.userId() == userId else { return false }
        let snapshot = StatusWidgetSnapshot(userId: userId, updatedAt: date, state: state)
        guard let data = try? JSONEncoder().encode(snapshot) else { return false }
        defaults?.set(data, forKey: snapshotKey)
        return true
    }
}
