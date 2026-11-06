package com.obrit.obrit.di

import com.obrit.obrit.shared.model.notifications.Notification

/** Swift-facing inbox API that unwraps repository results. */
class SharedNotificationInboxService(
    private val repositoryProvider: SharedRepositoryProvider = SharedRepositoryProvider(),
) {
    @Throws(Throwable::class)
    suspend fun getNotifications(): List<Notification> = repositoryProvider.notificationRepository().getNotifications().getOrThrow()

    @Throws(Throwable::class)
    suspend fun markAsRead(notificationId: Long): String =
        repositoryProvider.notificationRepository().markAsRead(notificationId).getOrThrow()

    @Throws(Throwable::class)
    suspend fun markAllAsRead() {
        repositoryProvider.notificationRepository().markAllAsRead().getOrThrow()
    }
}
