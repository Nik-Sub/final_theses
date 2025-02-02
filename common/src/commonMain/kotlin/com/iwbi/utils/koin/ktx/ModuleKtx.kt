package com.iwbi.utils.koin.ktx

import org.koin.core.module.Module

inline fun <reified T, reified U> Module.singleUsing(crossinline block: (U) -> T) {
    single { block(get()) }
}