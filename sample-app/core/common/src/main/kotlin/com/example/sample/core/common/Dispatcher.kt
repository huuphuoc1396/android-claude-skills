package com.example.sample.core.common

import javax.inject.Qualifier

/** Inject a [kotlinx.coroutines.CoroutineDispatcher] with `@Dispatcher(SampleDispatchers.IO)`; never use `Dispatchers.IO` directly. */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: SampleDispatchers)

enum class SampleDispatchers {
    Default,
    IO,
}
