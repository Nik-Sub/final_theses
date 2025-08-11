package com.mobile.iwbi.domain.store

import kotlinx.serialization.Serializable

@Serializable
data class Store(
    val name: String,
    val mapOfTheStore: String
)