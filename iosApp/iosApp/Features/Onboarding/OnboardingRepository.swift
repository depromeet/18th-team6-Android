import Foundation
import Shared

protocol OnboardingRepository {
    func groups() async throws -> [OnboardingCategoryGroup]
    func options() async throws -> [OnboardingItemOption]
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
                name: group.name,
                displayOrder: Int(group.displayOrder)
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

    func registerItems(requests: [OnboardingRegistrationRequest]) async throws {
        let params = requests.map { request in
            CreateItemParams(
                categoryId: KotlinLong(longLong: Int64(request.categoryId)),
                name: request.name,
                spareQuantity: KotlinInt(int: 0),
                lastReplacementPeriod: nil,
                replacementIntervalDays: nil,
                newCategoryName: nil,
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

    func registerItems(requests: [OnboardingRegistrationRequest]) async throws {
    }
}
