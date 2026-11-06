import Foundation

private enum TestFailure: Error { case request }

private actor WidgetCacheFake: StatusWidgetCaching {
    var owner: Int64? = 1
    var snapshot: StatusWidgetSnapshot?
    var writes = 0

    init(state: StatusWidgetState? = nil) {
        if let state { snapshot = StatusWidgetSnapshot(userId: 1, updatedAt: .distantPast, state: state) }
    }
    func changeOwner(_ id: Int64?) { owner = id }
    func userId() -> Int64? { owner }
    func load() -> StatusWidgetSnapshot? { snapshot?.userId == owner ? snapshot : nil }
    func save(_ state: StatusWidgetState, for userId: Int64, at date: Date) -> Bool {
        guard owner == userId else { return false }
        snapshot = StatusWidgetSnapshot(userId: userId, updatedAt: date, state: state)
        writes += 1
        return true
    }
}

private actor WidgetRepositoryFake: StatusWidgetRepository {
    var summaryCalls = 0
    var bucketCalls = 0
    var failSummary = false
    var failBuckets = false
    var total = 10
    var needReplace = 3
    var hold = false
    var summaryWait: CheckedContinuation<Void, Never>?
    var bucketWait: CheckedContinuation<Void, Never>?

    func configure(summaryFailure: Bool = false, bucketFailure: Bool = false, total: Int = 10, needReplace: Int = 3, hold: Bool = false) {
        failSummary = summaryFailure
        failBuckets = bucketFailure
        self.total = total
        self.needReplace = needReplace
        self.hold = hold
    }
    func bothStarted() -> Bool { summaryWait != nil && bucketWait != nil }
    func release() {
        summaryWait?.resume()
        bucketWait?.resume()
        summaryWait = nil
        bucketWait = nil
    }
    func counts() -> (Int, Int) { (summaryCalls, bucketCalls) }
    func summary() async throws -> StatusWidgetSummary {
        summaryCalls += 1
        if hold { await withCheckedContinuation { summaryWait = $0 } }
        if failSummary { throw TestFailure.request }
        return StatusWidgetSummary(totalCount: total, needReplaceCount: needReplace)
    }
    func buckets() async throws -> StatusWidgetBucketCounts {
        bucketCalls += 1
        if hold { await withCheckedContinuation { bucketWait = $0 } }
        if failBuckets { throw TestFailure.request }
        // Counts intentionally differ from needReplaceCount.
        return StatusWidgetBucketCounts(dangerCount: 5, warningCount: 2)
    }
}

@main
private struct StatusWidgetDataSourceTests {
    static func waitForBoth(_ repo: WidgetRepositoryFake) async {
        for _ in 0..<300 {
            if await repo.bothStarted() { return }
            try? await Task.sleep(nanoseconds: 5_000_000)
        }
        fatalError("Both API requests must start in parallel")
    }

    static func main() async {
        let repo = WidgetRepositoryFake()
        let cache = WidgetCacheFake()
        let source = StatusWidgetDataSource(repository: repo, cache: cache)
        let state = await source.refresh(force: true)
        precondition(state?.dangerCount == 5 && state?.warningCount == 2)
        precondition(state?.goodRatio == 0.7 && state?.filledDotCount == 21)
        let snapshot = await cache.load()!
        let encoded = try! JSONEncoder().encode(snapshot)
        let decoded = try! JSONDecoder().decode(StatusWidgetSnapshot.self, from: encoded)
        precondition(decoded.userId == 1 && decoded.state.filledDotCount == 21)
        _ = await source.refresh()
        let counts = await repo.counts()
        precondition(counts.0 == 1 && counts.1 == 1, "Reuse just-saved data when reloading a timeline")

        for failure in [true, false] {
            await repo.configure(summaryFailure: failure, bucketFailure: !failure)
            let fallback = await source.refresh(force: true)
            precondition(fallback?.filledDotCount == 21)
            let writes = await cache.writes
            precondition(writes == 1, "Do not replace cache on partial failure")
        }
        let emptyCache = WidgetCacheFake()
        let failed = StatusWidgetDataSource(repository: repo, cache: emptyCache)
        let failedState = await failed.refresh(force: true)
        precondition(failedState == nil, "No successful cache means failure UI")
        await repo.configure(total: 0, needReplace: 0)
        let empty = await source.refresh(force: true)
        precondition(empty?.isEmpty == true && empty?.dangerCount == 0 && empty?.warningCount == 0)
        let savedEmpty = await cache.load()
        precondition(savedEmpty?.state.isEmpty == true, "Replace previous success data with the empty state")
        await repo.configure(bucketFailure: true, total: 0, needReplace: 0)
        let emptyWithoutBuckets = await source.refresh(force: true)
        precondition(emptyWithoutBuckets?.isEmpty == true, "Zero totalCount is authoritative even when buckets fail")
        let freshEmptyCache = WidgetCacheFake()
        let freshEmptySource = StatusWidgetDataSource(repository: repo, cache: freshEmptyCache)
        let initialEmpty = await freshEmptySource.refresh(force: true)
        precondition(initialEmpty?.isEmpty == true, "Empty state must work without a previous cache")

        let noIdentityRepo = WidgetRepositoryFake()
        let noIdentityCache = WidgetCacheFake()
        await noIdentityCache.changeOwner(nil)
        let noIdentity = StatusWidgetDataSource(repository: noIdentityRepo, cache: noIdentityCache)
        let missing = await noIdentity.refresh(force: true)
        precondition(missing == nil)
        let noIdentityCounts = await noIdentityRepo.counts()
        precondition(noIdentityCounts.0 == 0 && noIdentityCounts.1 == 0)

        let concurrentRepo = WidgetRepositoryFake()
        await concurrentRepo.configure(hold: true)
        let concurrentCache = WidgetCacheFake()
        let concurrent = StatusWidgetDataSource(repository: concurrentRepo, cache: concurrentCache)
        let first = Task { await concurrent.refresh(force: true) }
        await waitForBoth(concurrentRepo)
        let second = Task { await concurrent.refresh(force: true) }
        try? await Task.sleep(nanoseconds: 20_000_000)
        await concurrentRepo.release()
        let firstResult = await first.value
        let secondResult = await second.value
        precondition(firstResult?.filledDotCount == 21 && secondResult?.filledDotCount == 21)
        let concurrentCounts = await concurrentRepo.counts()
        precondition(concurrentCounts.0 == 1 && concurrentCounts.1 == 1, "Coalesce retry requests")

        let switchedRepo = WidgetRepositoryFake()
        await switchedRepo.configure(hold: true)
        let switchedCache = WidgetCacheFake()
        let switched = StatusWidgetDataSource(repository: switchedRepo, cache: switchedCache)
        let obsolete = Task { await switched.refresh(force: true) }
        await waitForBoth(switchedRepo)
        await switchedCache.changeOwner(2)
        await switchedRepo.release()
        let obsoleteResult = await obsolete.value
        precondition(obsoleteResult == nil, "Never display another user's completed request")
        let switchedWrites = await switchedCache.writes
        precondition(switchedWrites == 0)
        print("PASS: parallel APIs, separate counts/ratio, success cache, partial failures, missing identity, empty state, retries, identity switch")
    }
}
