package com.har01d.userauth.annotation

import com.har01d.userauth.config.RedisTokenConfiguration
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(RedisTokenConfiguration::class)
annotation class EnableRedisToken
