import Foundation
import OSLog

enum AppLog {
    private static let subsystem = Bundle.main.bundleIdentifier ?? "OBRit"

    static let homeViewModel = Logger(subsystem: subsystem, category: "HomeViewModel")
    static let homeListTabViewModel = Logger(subsystem: subsystem, category: "HomeListTabViewModel")
    static let searchViewModel = Logger(subsystem: subsystem, category: "SearchViewModel")
    static let itemRegistrationViewModel = Logger(subsystem: subsystem, category: "ItemRegistrationViewModel")
    static let itemDetailViewModel = Logger(subsystem: subsystem, category: "ItemDetailViewModel")
    static let swiftRepository = Logger(subsystem: subsystem, category: "SwiftRepositoryAdapter")

    static func enter(_ logger: Logger, _ event: String, _ details: String = "") {
        logger.info("[enter] \(event, privacy: .public) \(details, privacy: .public)")
        debugPrint(status: "enter", event: event, details: details)
    }

    static func success(_ logger: Logger, _ event: String, _ details: String = "") {
        logger.info("[success] \(event, privacy: .public) \(details, privacy: .public)")
        debugPrint(status: "success", event: event, details: details)
    }

    static func failure(_ logger: Logger, _ event: String, _ error: Error, _ details: String = "") {
        logger.error("[failure] \(event, privacy: .public) \(details, privacy: .public) error=\(String(describing: error), privacy: .public)")
        debugPrint(status: "failure", event: event, details: "\(details) error=\(String(describing: error))")
    }

    private static func debugPrint(status: String, event: String, details: String) {
        #if DEBUG
            let suffix = details.isEmpty ? "" : " \(details)"
            print("[OBRit][Swift][\(status)] \(event)\(suffix)")
        #endif
    }
}
