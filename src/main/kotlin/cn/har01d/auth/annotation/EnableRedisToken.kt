package cn.har01d.auth.annotation

import cn.har01d.auth.config.RedisTokenConfiguration
import org.springframework.context.annotation.Import

/**
 * Enable Redis Token.
 * The Redis server is required.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(RedisTokenConfiguration::class)
annotation class EnableRedisToken
