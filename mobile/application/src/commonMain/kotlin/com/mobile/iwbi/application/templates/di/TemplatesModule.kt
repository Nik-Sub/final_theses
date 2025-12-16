package com.mobile.iwbi.application.templates.di

import com.mobile.iwbi.application.templates.TemplateService
import com.mobile.iwbi.application.templates.input.TemplateServicePort
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val templatesModule = module {
    factoryOf(::TemplateService) bind TemplateServicePort::class
}

