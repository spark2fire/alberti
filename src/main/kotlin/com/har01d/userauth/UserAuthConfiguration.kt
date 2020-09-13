package com.har01d.userauth

import com.har01d.userauth.token.*
import com.har01d.userauth.web.FrameworkEndpointHandlerMapping
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import javax.servlet.http.HttpServletResponse


@Configuration(proxyBeanMethods = false)
class UserAuthConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun authenticationEntryPoint(): AuthenticationEntryPoint {
        return AuthenticationEntryPoint { _, response, _ ->
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
        }
    }

    @Bean
    @ConditionalOnMissingBean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun mapping() = FrameworkEndpointHandlerMapping()

    @Bean
    fun tokenFilter(tokenService: TokenService): FilterRegistrationBean<TokenFilter> {
        val registration = FilterRegistrationBean<TokenFilter>()
        registration.filter = TokenFilter(tokenService)
        return registration
    }

    @Bean
    @ConditionalOnMissingBean
    fun tokenService(redisTemplate: ObjectProvider<StringRedisTemplate>, repository: ObjectProvider<TokenRepository>): TokenService {
        return when {
            redisTemplate.ifAvailable != null -> {
                RedisTokenService(redisTemplate.ifAvailable!!)
            }
            repository.ifAvailable != null -> {
                DatabaseTokenService(repository.ifAvailable!!)
            }
            else -> {
                throw RuntimeException("缺少依赖")
            }
        }
    }
}
