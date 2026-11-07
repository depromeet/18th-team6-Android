package com.obrit.feature.notification

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 앱을 보고 있는 중에 푸시가 도착했다고 알림함에 알린다. iOS의 `notificationInboxDidReceive`와 같은 역할이다.
 *
 * 알림함이 떠 있을 때만 받아서 새로고침한다. 떠 있지 않으면 다음에 들어올 때 어차피 다시 불러온다.
 */
class NotificationReceivedSignal {
    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val events: SharedFlow<Unit> = _events.asSharedFlow()

    fun notifyReceived() {
        _events.tryEmit(Unit)
    }
}
