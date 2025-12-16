package com.mobile.iwbi.application.templates.input

import com.iwbi.domain.shopping.Template
import kotlinx.coroutines.flow.Flow

interface TemplateServicePort {
    fun observeTemplates(): Flow<List<Template>>
    suspend fun saveTemplate(template: Template)
    suspend fun deleteTemplate(templateName: String)
    fun isTemplateExists(templateName: String): Boolean
}

