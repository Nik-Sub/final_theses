package com.iwbi.util.koin.ktx

import kotlin.reflect.KClass
import org.koin.core.KoinApplication
import org.koin.core.qualifier.Qualifier

/**
 * Call [org.koin.core.Koin.declare] from [KoinApplication] context
 */
inline fun <reified T> KoinApplication.declare(
    instance: T,
    qualifier: Qualifier? = null,
    secondaryTypes: List<KClass<*>> = emptyList(),
    allowOverride: Boolean = true,
) {
    koin.declare(instance, qualifier, secondaryTypes, allowOverride)
}
