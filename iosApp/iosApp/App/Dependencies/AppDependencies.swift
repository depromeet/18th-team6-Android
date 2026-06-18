import Foundation
import Shared

struct AppDependencies {
    let refreshCenter: AppRefreshCenter
    let onboardingCompletionStore: OnboardingCompletionStore
    let makeHomeViewModel: @MainActor () -> HomeViewModel
    let makeHomeListTabViewModel: @MainActor () -> HomeListTabViewModel
    let makeSearchViewModel: @MainActor () -> SearchViewModel
    let makeOnboardingViewModel: @MainActor () -> OnboardingViewModel
    let makeItemRegistrationViewModel: @MainActor () -> ItemRegistrationViewModel
    let makeReceiptAnalyzeViewModel: @MainActor () -> ReceiptAnalyzeViewModel
    let makeItemDetailViewModel: @MainActor (Int) -> ItemDetailViewModel
    let makeItemDetailEditViewModel: @MainActor (Int) -> ItemDetailEditViewModel

    static var live: AppDependencies {
        let refreshCenter = AppRefreshCenter()
        let repositoryProvider = SharedRepositoryProvider()
        let readService = SharedReadService(repositoryProvider: repositoryProvider)
        let writeService = SharedWriteService(repositoryProvider: repositoryProvider)
        let receiptService = SharedReceiptService(repositoryProvider: repositoryProvider)
        let itemReadRepository = SharedItemReadRepository(
            readService: readService,
            writeService: writeService
        )
        let itemRegistrationWriteRepository = SharedItemRegistrationWriteRepository(writeService: writeService)
        let searchRecentKeywordStore = UserDefaultsSearchRecentKeywordStore()

        return AppDependencies(
            refreshCenter: refreshCenter,
            onboardingCompletionStore: UserDefaultsOnboardingCompletionStore(),
            makeHomeViewModel: {
                HomeViewModel(
                    repository: SharedHomeDashboardRepository(readService: readService)
                )
            },
            makeHomeListTabViewModel: {
                HomeListTabViewModel(
                    repository: SharedHomeListTabRepository(readService: readService)
                )
            },
            makeSearchViewModel: {
                SearchViewModel(
                    repository: SharedHomeListTabRepository(readService: readService),
                    recentKeywordStore: searchRecentKeywordStore
                )
            },
            makeOnboardingViewModel: {
                OnboardingViewModel(
                    repository: SharedOnboardingRepository(
                        readService: readService,
                        writeService: writeService
                    ),
                    initialOptions: []
                )
            },
            makeItemRegistrationViewModel: {
                ItemRegistrationViewModel(
                    catalogRepository: itemReadRepository,
                    writeRepository: itemRegistrationWriteRepository
                )
            },
            makeReceiptAnalyzeViewModel: {
                ReceiptAnalyzeViewModel(
                    repository: SharedReceiptAnalyzeRepository(receiptService: receiptService)
                )
            },
            makeItemDetailViewModel: { itemId in
                ItemDetailViewModel(itemId: itemId, repository: itemReadRepository)
            },
            makeItemDetailEditViewModel: { itemId in
                ItemDetailEditViewModel(itemId: itemId, repository: itemReadRepository)
            }
        )
    }

    static var preview: AppDependencies {
        return AppDependencies(
            refreshCenter: AppRefreshCenter(),
            onboardingCompletionStore: PreviewOnboardingCompletionStore(hasCompletedOnboarding: true),
            makeHomeViewModel: {
                HomeViewModel(initialDashboard: HomeDashboard.empty)
            },
            makeHomeListTabViewModel: {
                HomeListTabViewModel()
            },
            makeSearchViewModel: {
                SearchViewModel()
            },
            makeOnboardingViewModel: {
                OnboardingViewModel()
            },
            makeItemRegistrationViewModel: {
                ItemRegistrationViewModel()
            },
            makeReceiptAnalyzeViewModel: {
                ReceiptAnalyzeViewModel()
            },
            makeItemDetailViewModel: { itemId in
                ItemDetailViewModel(itemId: itemId)
            },
            makeItemDetailEditViewModel: { itemId in
                ItemDetailEditViewModel(itemId: itemId)
            }
        )
    }
}
