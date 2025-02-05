package com.iwbi.util.koin

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent

abstract class BaseKoinComponent : KoinComponent {
    protected abstract val application: KoinApplication

    final override fun getKoin(): Koin = application.koin
}
