package cn.har01d.auth.annotation

import cn.har01d.auth.config.DatabaseTokenConfiguration
import org.springframework.context.annotation.Import

/**
 * Enable Database Token.
 * The default table name is "t_token" and the table will created automatically.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(DatabaseTokenConfiguration::class)
annotation class EnableDatabaseToken
