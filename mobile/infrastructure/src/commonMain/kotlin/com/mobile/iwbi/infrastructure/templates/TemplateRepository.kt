package com.mobile.iwbi.infrastructure.templates

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.iwbi.domain.shopping.Template
import com.mobile.iwbi.application.templates.output.TemplateRepositoryPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer

class TemplateRepository(
    private val dataStore: DataStore<Preferences>
) : TemplateRepositoryPort {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    companion object {
        private val TEMPLATES_KEY = stringPreferencesKey("templates")
    }

    override fun observeTemplates(): Flow<List<Template>> {
        return dataStore.data.map { preferences ->
            val templatesJson = preferences[TEMPLATES_KEY] ?: "[]"
            try {
                json.decodeFromString(ListSerializer(Template.serializer()), templatesJson)
            } catch (e: Exception) {
                println("Error deserializing templates: ${e.message}")
                emptyList()
            }
        }
    }

    override suspend fun saveTemplate(template: Template) {
        dataStore.edit { preferences ->
            val currentTemplates = getAllTemplatesFromPreferences(preferences).toMutableList()

            // Remove existing template with the same name (case-insensitive)
            currentTemplates.removeAll { it.name.equals(template.name, ignoreCase = true) }

            // Add the new template
            currentTemplates.add(template)

            // Save back to preferences
            val templatesJson = json.encodeToString(ListSerializer(Template.serializer()), currentTemplates)
            preferences[TEMPLATES_KEY] = templatesJson
        }
    }

    override suspend fun deleteTemplate(templateName: String) {
        dataStore.edit { preferences ->
            val currentTemplates = getAllTemplatesFromPreferences(preferences).toMutableList()

            // Remove template with matching name (case-insensitive)
            currentTemplates.removeAll { it.name.equals(templateName, ignoreCase = true) }

            // Save back to preferences
            val templatesJson = json.encodeToString(ListSerializer(Template.serializer()), currentTemplates)
            preferences[TEMPLATES_KEY] = templatesJson
        }
    }

    override suspend fun getAllTemplates(): List<Template> {
        return observeTemplates().first()
    }

    private fun getAllTemplatesFromPreferences(preferences: Preferences): List<Template> {
        val templatesJson = preferences[TEMPLATES_KEY] ?: "[]"
        return try {
            json.decodeFromString(ListSerializer(Template.serializer()), templatesJson)
        } catch (e: Exception) {
            println("Error deserializing templates: ${e.message}")
            emptyList()
        }
    }
}

