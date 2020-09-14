package cn.har01d.auth.annotation

import cn.har01d.auth.config.JwtTokenConfiguration
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(JwtTokenConfiguration::class)
annotation class EnableJwtToken
