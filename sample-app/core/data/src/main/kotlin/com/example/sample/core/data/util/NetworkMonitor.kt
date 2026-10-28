package com.example.sample.core.data.util

import kotlinx.coroutines.flow.Flow

/** Whether the device can reach the internet; screens use it to refresh when the connection returns. */
interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}
