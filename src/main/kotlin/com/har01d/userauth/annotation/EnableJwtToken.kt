package com.har01d.userauth.annotation

import com.har01d.userauth.config.JwtTokenConfiguration
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(JwtTokenConfiguration::class)
annotation class EnableJwtToken
