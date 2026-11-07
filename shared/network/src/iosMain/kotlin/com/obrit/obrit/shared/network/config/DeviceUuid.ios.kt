package com.obrit.obrit.shared.network.config

import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val deviceUuidModule: Module =
    module {
        single<DeviceUuidProvider> { IosDeviceUuidProvider() }
    }
