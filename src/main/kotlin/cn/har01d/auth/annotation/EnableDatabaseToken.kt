package cn.har01d.auth.annotation

import cn.har01d.auth.config.DatabaseTokenConfiguration
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(DatabaseTokenConfiguration::class)
annotation class EnableDatabaseToken
