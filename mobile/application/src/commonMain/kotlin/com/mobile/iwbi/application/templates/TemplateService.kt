package com.mobile.iwbi.application.templates

import com.iwbi.domain.shopping.Template
import com.mobile.iwbi.application.templates.input.TemplateServicePort
import com.mobile.iwbi.application.templates.output.TemplateRepositoryPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TemplateService(
    private val templateRepositoryPort: TemplateRepositoryPort
) : TemplateServicePort {

    override fun observeTemplates(): Flow<List<Template>> {
        return templateRepositoryPort.observeTemplates()
    }

    override suspend fun saveTemplate(template: Template) {
        templateRepositoryPort.saveTemplate(template)
    }

    override suspend fun deleteTemplate(templateName: String) {
        templateRepositoryPort.deleteTemplate(templateName)
    }

    override fun isTemplateExists(templateName: String): Boolean {
        // This is a synchronous check, but we'll need to handle it properly
        // In a real scenario, you might want to make this suspend or use a cached value
        return false // Placeholder - will be handled in the repository
    }
}

