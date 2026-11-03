import Foundation
import Shared

protocol OnboardingRepository {
    func groups() async throws -> [OnboardingCategoryGroup]
    func options() async throws -> [OnboardingItemOption]
    func commonIconURL() async throws -> String
    func registerItems(requests: [OnboardingRegistrationRequest]) async throws
}

actor SharedOnboardingRepository: OnboardingRepository {
    private let readService: SharedReadService
    private let writeService: SharedWriteService

    init(
        readService: SharedReadService,
        writeService: SharedWriteService
    ) {
        self.readService = readService
        self.writeService = writeService
    }

    func groups() async throws -> [OnboardingCategoryGroup] {
        let groups = try await readService.getCategoryGroups()
        return groups.map { group in
            OnboardingCategoryGroup(
                id: Int(clamping: group.id),
                name: group.type.name == "USER" ? "직접 등록" : group.name,
                displayOrder: Int(group.displayOrder),
                isUserDefined: group.type.name == "USER"
            )
        }
    }

    func options() async throws -> [OnboardingItemOption] {
        let categories = try await readService.getCategories()
        return categories.map { category in
            OnboardingItemOption(
                id: Int(clamping: category.id),
                title: category.name,
                addedCount: Int(category.itemCount),
                imageURL: category.iconUrl,
                categoryGroupId: category.categoryGroupId.map { Int(clamping: $0.int64Value) }
            )
        }
    }

    func commonIconURL() async throws -> String {
        try await readService.getCategoryIcons().first { $0.id == 1 }?.url ?? ""
    }

    func registerItems(requests: [OnboardingRegistrationRequest]) async throws {
        let params = requests.map { request in
            CreateItemParams(
                categoryId: request.categoryId.map { KotlinLong(longLong: Int64($0)) },
                name: request.name,
                spareQuantity: KotlinInt(int: 0),
                lastReplacementPeriod: nil,
                replacementIntervalDays: nil,
                newCategoryName: request.newCategoryName,
                newCategoryDefaultReplacementIntervalDays: nil
            )
        }
        _ = try await writeService.createItems(params: params, receiptImageUrl: nil)
    }
}

struct OnboardingSampleRepository: OnboardingRepository {
    func groups() async throws -> [OnboardingCategoryGroup] {
        OnboardingDefaults.groups
    }

    func options() async throws -> [OnboardingItemOption] {
        OnboardingDefaults.options
    }

    func commonIconURL() async throws -> String { "" }

    func registerItems(requests: [OnboardingRegistrationRequest]) async throws {
    }
}
