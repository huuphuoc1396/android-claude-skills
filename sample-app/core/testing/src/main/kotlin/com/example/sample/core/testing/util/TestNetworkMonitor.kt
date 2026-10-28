package com.example.sample.core.testing.util

import com.example.sample.core.data.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** Test double for [NetworkMonitor], online by default; flip it with [setOnline]. */
class TestNetworkMonitor : NetworkMonitor {
    private val online = MutableStateFlow(true)

    override val isOnline: Flow<Boolean> = online

    fun setOnline(isOnline: Boolean) {
        online.value = isOnline
    }
}
