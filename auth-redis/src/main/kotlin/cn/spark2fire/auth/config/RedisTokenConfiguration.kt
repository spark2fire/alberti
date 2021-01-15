package cn.spark2fire.auth.config

import cn.spark2fire.auth.token.RedisTokenService
import cn.spark2fire.auth.token.TokenService
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate

@Configuration(proxyBeanMethods = false)
class RedisTokenConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun tokenService(redisTemplate: StringRedisTemplate, properties: AuthProperties): TokenService = RedisTokenService(redisTemplate, properties)
}
