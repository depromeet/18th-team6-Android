import Foundation
import Shared

struct AppDependencies {
    let refreshCenter: AppRefreshCenter
    let makeHomeViewModel: @MainActor () -> HomeViewModel
    let makeHomeListTabViewModel: @MainActor () -> HomeListTabViewModel
    let makeSearchViewModel: @MainActor () -> SearchViewModel
    let makeItemRegistrationViewModel: @MainActor () -> ItemRegistrationViewModel
    let makeItemDetailViewModel: @MainActor (Int) -> ItemDetailViewModel
    let makeItemDetailEditViewModel: @MainActor (Int) -> ItemDetailEditViewModel

    static var live: AppDependencies {
        let refreshCenter = AppRefreshCenter()
        let repositoryProvider = SharedRepositoryProvider()
        let readService = SharedReadService(repositoryProvider: repositoryProvider)
        let writeService = SharedWriteService(repositoryProvider: repositoryProvider)
        let itemReadRepository = SharedItemReadRepository(
            readService: readService,
            writeService: writeService
        )
        let itemRegistrationWriteRepository = SharedItemRegistrationWriteRepository(writeService: writeService)
        let searchRecentKeywordStore = UserDefaultsSearchRecentKeywordStore()

        return AppDependencies(
            refreshCenter: refreshCenter,
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
            makeItemRegistrationViewModel: {
                ItemRegistrationViewModel(
                    catalogRepository: itemReadRepository,
                    writeRepository: itemRegistrationWriteRepository
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
            makeHomeViewModel: {
                HomeViewModel(initialDashboard: HomeDashboard.empty)
            },
            makeHomeListTabViewModel: {
                HomeListTabViewModel()
            },
            makeSearchViewModel: {
                SearchViewModel()
            },
            makeItemRegistrationViewModel: {
                ItemRegistrationViewModel()
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
