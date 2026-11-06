import Foundation

struct StatusWidgetSummary: Sendable {
    let totalCount: Int
    let needReplaceCount: Int
}

struct StatusWidgetBucketCounts: Sendable {
    let dangerCount: Int
    let warningCount: Int
}

protocol StatusWidgetRepository: Sendable {
    func summary() async throws -> StatusWidgetSummary
    func buckets() async throws -> StatusWidgetBucketCounts
}

actor StatusWidgetDataSource {
    private let repository: any StatusWidgetRepository
    private let cache: any StatusWidgetCaching
    private var inFlight: Task<StatusWidgetState?, Never>?
    private var recentUserId: Int64?
    private var recentAttempt: Date?
    private var recentResult: StatusWidgetState?

    init(repository: any StatusWidgetRepository, cache: any StatusWidgetCaching) {
        self.repository = repository
        self.cache = cache
    }

    func refresh(force: Bool = false) async -> StatusWidgetState? {
        if let inFlight { return await inFlight.value }
        guard let userId = await cache.userId() else { return nil }
        if !force {
            if recentUserId == userId, let recentAttempt, Date().timeIntervalSince(recentAttempt) < 30 {
                return recentResult
            }
            if let snapshot = await cache.load(), Date().timeIntervalSince(snapshot.updatedAt) < 30 {
                return snapshot.state
            }
        }
        if let inFlight { return await inFlight.value }
        let task = Task { await fetch(userId: userId) }
        inFlight = task
        defer { inFlight = nil }
        return await withTaskCancellationHandler {
            await task.value
        } onCancel: {
            task.cancel()
        }
    }

    private func fetch(userId: Int64) async -> StatusWidgetState? {
        let result: StatusWidgetState?
        do {
            async let summary = repository.summary()
            async let buckets = repository.buckets()
            let loadedSummary = try await summary
            let state: StatusWidgetState
            if loadedSummary.totalCount == 0 {
                // The summary alone confirms an empty account, even if buckets are unavailable.
                state = StatusWidgetState(dangerCount: 0, warningCount: 0, goodRatio: 1, isEmpty: true)
            } else {
                let loadedBuckets = try await buckets
                state = StatusWidgetState(
                    dangerCount: loadedBuckets.dangerCount,
                    warningCount: loadedBuckets.warningCount,
                    goodRatio: 1 - Double(loadedSummary.needReplaceCount) / Double(max(loadedSummary.totalCount, 1))
                )
            }
            try Task.checkCancellation()
            if await cache.save(state, for: userId, at: Date()) { result = state }
            else { result = await cache.load()?.state }
        } catch {
            result = await cache.load()?.state
        }
        guard await cache.userId() == userId else {
            recentUserId = nil
            recentAttempt = nil
            recentResult = nil
            return await cache.load()?.state
        }
        recentUserId = userId
        recentAttempt = Date()
        recentResult = result
        return result
    }
}
