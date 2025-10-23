package com.mobile.iwbi.application.shoppingnotes.di

import com.mobile.iwbi.application.shoppingnotes.ShoppingNotesService
import com.mobile.iwbi.application.shoppingnotes.input.ShoppingNotesServicePort

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val shoppingNotesModule = module {
    singleOf(::ShoppingNotesService) bind ShoppingNotesServicePort::class
}