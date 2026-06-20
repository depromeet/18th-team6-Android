import Foundation
import Shared

protocol OnboardingRepository {
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

    func options() async throws -> [OnboardingItemOption] {
        let categories = try await readService.getCategories()
        return categories.map { category in
            OnboardingItemOption(
                id: Int(clamping: category.id),
                title: category.name,
                addedCount: Int(category.itemCount),
                imageURL: category.iconUrl
            )
        }
    }

    func registerItems(requests: [OnboardingRegistrationRequest]) async throws {
        for request in requests {
            _ = try await writeService.createItem(
                categoryId: Int64(request.categoryId),
                name: request.name,
                count: KotlinInt(int: Int32(request.quantity)),
                lastReplacementPeriod: request.replacementPeriod.apiRawValue,
                replacementIntervalDays: nil
            )
        }
    }
}

struct OnboardingSampleRepository: OnboardingRepository {
    func options() async throws -> [OnboardingItemOption] {
        OnboardingDefaults.options
    }

    func registerItems(requests: [OnboardingRegistrationRequest]) async throws {
    }
}
