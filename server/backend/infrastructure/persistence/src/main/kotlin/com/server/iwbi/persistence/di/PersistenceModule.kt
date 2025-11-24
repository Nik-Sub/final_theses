package com.server.iwbi.persistence.di

import com.server.iwbi.application.friends.output.FriendRepositoryPort
import com.server.iwbi.application.shoppingnotes.output.ShoppingNotesRepositoryPort
import com.server.iwbi.persistence.config.DatabaseConfig
import com.server.iwbi.persistence.config.DatabaseInitializer
import com.server.iwbi.persistence.database.DatabaseManager
import com.server.iwbi.persistence.database.DatabaseProvider
import com.server.iwbi.persistence.friends.FriendRepository
import com.server.iwbi.persistence.shoppingnotes.ShoppingNotesRepository
import org.koin.dsl.module

val persistenceModule = module {
    single { DatabaseConfig() }

    single {
        val databaseConfig = get<DatabaseConfig>()
        val database = DatabaseInitializer.initialize(databaseConfig)
        DatabaseManager(database)
    }

    single { DatabaseProvider(get()) }

    single<ShoppingNotesRepositoryPort> { ShoppingNotesRepository(get()) }

    single<FriendRepositoryPort> { FriendRepository() }
}
