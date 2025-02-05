package com.iwbi.util.koin.ktx

import org.koin.core.module.Module

inline fun <reified T, reified U> Module.singleUsing(crossinline block: (U) -> T) {
    single { block(get()) }
}

inline fun <reified T, reified U, reified V> Module.singleUsing(crossinline block: (U, V) -> T) {
    single { block(get(), get()) }
}

inline fun <reified T, reified U, reified V, reified W> Module.singleUsing(crossinline block: (U, V, W) -> T) {
    single { block(get(), get(), get()) }
}
