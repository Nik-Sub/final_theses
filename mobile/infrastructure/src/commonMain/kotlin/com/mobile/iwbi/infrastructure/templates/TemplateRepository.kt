package com.mobile.iwbi.infrastructure.templates

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.iwbi.domain.shopping.Template
import com.mobile.iwbi.application.authentication.output.AuthenticationProviderPort
import com.mobile.iwbi.application.templates.output.TemplateRepositoryPort
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString

class TemplateRepository(
    private val dataStore: DataStore<Preferences>,
    private val authProvider: AuthenticationProviderPort
) : TemplateRepositoryPort {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private fun getTemplatesKey(userId: String) = stringPreferencesKey("templates_$userId")

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeTemplates(): Flow<List<Template>> {
        return authProvider.observeCurrentUser().flatMapLatest { user ->
            if (user == null) {
                flowOf(emptyList())
            } else {
                val key = getTemplatesKey(user.uid)
                dataStore.data.map { preferences ->
                    val templatesJson = preferences[key] ?: "[]"
                    try {
                        json.decodeFromString(ListSerializer(Template.serializer()), templatesJson)
                    } catch (e: Exception) {
                        println("Error deserializing templates for user ${user.uid}: ${e.message}")
                        emptyList()
                    }
                }
            }
        }
    }

    override suspend fun saveTemplate(template: Template) {
        val user = authProvider.observeCurrentUser().first() ?: return
        val key = getTemplatesKey(user.uid)

        dataStore.edit { preferences ->
            val currentTemplates = getAllTemplatesFromPreferences(preferences, key).toMutableList()

            // Remove existing template with the same name (case-insensitive)
            currentTemplates.removeAll { it.name.equals(template.name, ignoreCase = true) }

            // Add the new template
            currentTemplates.add(template)

            // Save back to preferences
            val templatesJson = json.encodeToString(ListSerializer(Template.serializer()), currentTemplates)
            preferences[key] = templatesJson
        }
    }

    override suspend fun deleteTemplate(templateName: String) {
        val user = authProvider.observeCurrentUser().first() ?: return
        val key = getTemplatesKey(user.uid)

        dataStore.edit { preferences ->
            val currentTemplates = getAllTemplatesFromPreferences(preferences, key).toMutableList()

            // Remove template with matching name (case-insensitive)
            currentTemplates.removeAll { it.name.equals(templateName, ignoreCase = true) }

            // Save back to preferences
            val templatesJson = json.encodeToString(ListSerializer(Template.serializer()), currentTemplates)
            preferences[key] = templatesJson
        }
    }

    override suspend fun getAllTemplates(): List<Template> {
        return observeTemplates().first()
    }

    private fun getAllTemplatesFromPreferences(preferences: Preferences, key: Preferences.Key<String>): List<Template> {
        val templatesJson = preferences[key] ?: "[]"
        return try {
            json.decodeFromString(ListSerializer(Template.serializer()), templatesJson)
        } catch (e: Exception) {
            println("Error deserializing templates: ${e.message}")
            emptyList()
        }
    }
}

