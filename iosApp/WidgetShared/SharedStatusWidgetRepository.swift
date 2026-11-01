import Shared

actor SharedStatusWidgetRepository: StatusWidgetRepository {
    private let readService = SharedReadService(repositoryProvider: SharedRepositoryProvider())

    func summary() async throws -> StatusWidgetSummary {
        let summary = try await readService.getMyStatusSummary()
        return StatusWidgetSummary(totalCount: Int(summary.totalCount), needReplaceCount: Int(summary.needReplaceCount))
    }

    func buckets() async throws -> StatusWidgetBucketCounts {
        let buckets = try await readService.getBuckets()
        return StatusWidgetBucketCounts(
            dangerCount: Int(buckets.first { $0.bucket == .danger }?.count ?? 0),
            warningCount: Int(buckets.first { $0.bucket == .warning }?.count ?? 0)
        )
    }
}

extension StatusWidgetDataSource {
    static let shared = StatusWidgetDataSource(repository: SharedStatusWidgetRepository(), cache: StatusWidgetCache())
}
