package com.mobile.iwbi.infrastructure.templates

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private val Context.templateDataStore: DataStore<Preferences> by preferencesDataStore(name = "templates")

fun createTemplateDataStore(context: Context): DataStore<Preferences> {
    return context.templateDataStore
}

