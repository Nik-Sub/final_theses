package com.mobile.iwbi.presentation

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform