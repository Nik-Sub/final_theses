package com.mobile.iwbi.application.templates.output

import com.iwbi.domain.shopping.Template
import kotlinx.coroutines.flow.Flow

interface TemplateRepositoryPort {
    fun observeTemplates(): Flow<List<Template>>
    suspend fun saveTemplate(template: Template)
    suspend fun deleteTemplate(templateName: String)
    suspend fun getAllTemplates(): List<Template>
}

