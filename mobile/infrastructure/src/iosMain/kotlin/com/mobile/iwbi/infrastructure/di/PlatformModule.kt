package com.mobile.iwbi.infrastructure.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.mobile.iwbi.application.templates.output.TemplateRepositoryPort
import com.mobile.iwbi.infrastructure.templates.TemplateRepository
import com.mobile.iwbi.infrastructure.templates.createTemplateDataStore
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformInfrastructureModule: Module = module {
    single<DataStore<Preferences>> {
        createTemplateDataStore()
    }

    single<TemplateRepositoryPort> {
        TemplateRepository(dataStore = get())
    }
}

