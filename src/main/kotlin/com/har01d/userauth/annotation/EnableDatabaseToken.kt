package com.har01d.userauth.annotation

import com.har01d.userauth.config.DatabaseTokenConfiguration
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(DatabaseTokenConfiguration::class)
annotation class EnableDatabaseToken
