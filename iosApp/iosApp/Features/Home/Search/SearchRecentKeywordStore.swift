import Foundation

protocol SearchRecentKeywordStore {
    func loadKeywords() -> [String]
    func saveKeywords(_ keywords: [String])
}

struct UserDefaultsSearchRecentKeywordStore: SearchRecentKeywordStore {
    private let userDefaults: UserDefaults
    private let key: String

    init(
        userDefaults: UserDefaults = .standard,
        key: String = "com.obrit.search.recentKeywords"
    ) {
        self.userDefaults = userDefaults
        self.key = key
    }

    func loadKeywords() -> [String] {
        userDefaults.stringArray(forKey: key) ?? []
    }

    func saveKeywords(_ keywords: [String]) {
        userDefaults.set(keywords, forKey: key)
    }
}
