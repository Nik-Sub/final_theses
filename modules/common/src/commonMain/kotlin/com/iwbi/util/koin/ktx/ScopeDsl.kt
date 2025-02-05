package com.iwbi.util.koin.ktx

import org.koin.dsl.ScopeDSL

inline fun <reified T, reified U> ScopeDSL.scopedUsing(crossinline block: (U) -> T) {
    scoped { block(get()) }
}

inline fun <reified T, reified U, reified V> ScopeDSL.scopedUsing(crossinline block: (U, V) -> T) {
    scoped { block(get(), get()) }
}

inline fun <reified T, reified U, reified V, reified W> ScopeDSL.scopedUsing(crossinline block: (U, V, W) -> T) {
    scoped { block(get(), get(), get()) }
}
