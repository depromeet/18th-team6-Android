import Foundation
import Shared

struct AppDependencies {
    let makeHomeViewModel: @MainActor () -> HomeViewModel
    let makeHomeListTabViewModel: @MainActor () -> HomeListTabViewModel
    let makeItemRegistrationViewModel: @MainActor () -> ItemRegistrationViewModel
    let makeItemDetailViewModel: @MainActor (Int) -> ItemDetailViewModel

    static var live: AppDependencies {
        let readService = SharedReadService(repositoryProvider: SharedRepositoryProvider())
        let itemReadRepository = SharedItemReadRepository(readService: readService)

        return AppDependencies(
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
            makeItemRegistrationViewModel: {
                ItemRegistrationViewModel(catalogRepository: itemReadRepository)
            },
            makeItemDetailViewModel: { itemId in
                ItemDetailViewModel(itemId: itemId, repository: itemReadRepository)
            }
        )
    }

    static var preview: AppDependencies {
        return AppDependencies(
            makeHomeViewModel: {
                HomeViewModel()
            },
            makeHomeListTabViewModel: {
                HomeListTabViewModel()
            },
            makeItemRegistrationViewModel: {
                ItemRegistrationViewModel()
            },
            makeItemDetailViewModel: { itemId in
                ItemDetailViewModel(itemId: itemId)
            }
        )
    }
}
