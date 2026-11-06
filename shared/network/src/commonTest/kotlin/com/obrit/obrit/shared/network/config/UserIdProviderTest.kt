package com.obrit.obrit.shared.network.config

import com.obrit.obrit.shared.network.request.user.RegisterUserRequest
import com.obrit.obrit.shared.network.response.user.RegisterUserResponse
import com.obrit.obrit.shared.network.source.UserRemoteDataSource
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UserIdProviderTest {
    @Test
    fun crossProcessStorageIsReadForEveryRequest() {
        val storage = FakeStorage(17L, supportsInMemoryCache = false)
        val remote = FakeRemote()
        val provider = DefaultUserIdProvider(FakeUuid, remote, storage)
        assertEquals(17L, call { provider.get() })
        storage.userId = 23L
        assertEquals(23L, call { provider.get() })
        assertEquals(0, remote.registrations)
    }

    @Test
    fun missingWidgetIdentityCannotRegisterAnotherUser() {
        val storage = FakeStorage(null, supportsInMemoryCache = false, requiresExistingIdentity = true)
        val remote = FakeRemote()
        val provider = DefaultUserIdProvider(FakeUuid, remote, storage)
        assertFailsWith<IllegalStateException> { call { provider.get() } }
        assertEquals(0, remote.registrations)
    }

    @Test
    fun regularAppKeepsItsExistingRegistrationAndMemoryCacheBehavior() {
        val storage = FakeStorage(null)
        val remote = FakeRemote()
        val provider = DefaultUserIdProvider(FakeUuid, remote, storage)
        assertEquals(42L, call { provider.get() })
        assertEquals(42L, storage.userId)
        storage.userId = 99L
        assertEquals(42L, call { provider.get() })
        assertEquals(1, remote.registrations)
    }

    private class FakeStorage(
        var userId: Long?,
        override val supportsInMemoryCache: Boolean = true,
        private val requiresExistingIdentity: Boolean = false,
    ) : UserIdStorage {
        override fun load(): Long? = if (requiresExistingIdentity) checkNotNull(userId) else userId

        override fun save(userId: Long) {
            this.userId = userId
        }
    }

    private object FakeUuid : DeviceUuidProvider {
        override fun get(): String = "device"
    }

    internal class FakeRemote : UserRemoteDataSource {
        var registrations = 0

        override suspend fun register(request: RegisterUserRequest): RegisterUserResponse {
            registrations += 1
            return RegisterUserResponse(userId = 42L, uuid = request.value)
        }
    }

    /** All fake operations complete synchronously, so no coroutine runtime is needed here. */
    private fun <T> call(block: suspend () -> T): T {
        var outcome: Result<T>? = null
        block.startCoroutine(
            object : Continuation<T> {
                override val context = EmptyCoroutineContext

                override fun resumeWith(result: Result<T>) {
                    outcome = result
                }
            },
        )
        return checkNotNull(outcome).getOrThrow()
    }
}
