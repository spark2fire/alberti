package com.har01d.userauth.config

import com.har01d.userauth.token.InMemoryTokenService
import com.har01d.userauth.token.TokenFilter
import com.har01d.userauth.token.TokenService
import com.har01d.userauth.web.FrameworkEndpointHandlerMapping
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
    fun tokenService(): TokenService = InMemoryTokenService()
}
