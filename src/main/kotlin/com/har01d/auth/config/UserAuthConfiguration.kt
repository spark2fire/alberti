package com.har01d.auth.config

import com.har01d.auth.token.InMemoryTokenService
import com.har01d.auth.token.TokenFilter
import com.har01d.auth.token.TokenService
import com.har01d.auth.web.AuthEndpoint
import com.har01d.auth.web.FrameworkEndpointHandlerMapping
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import javax.servlet.http.HttpServletResponse

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(value = [AuthProperties::class])
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
    fun authEndpoint(passwordEncoder: PasswordEncoder, userDetailsService: UserDetailsService, tokenService: TokenService): AuthEndpoint {
        return AuthEndpoint(passwordEncoder, userDetailsService, tokenService)
    }

    @Bean
    fun mapping() = FrameworkEndpointHandlerMapping()

    @Bean
    fun tokenFilter(tokenService: TokenService, properties: AuthProperties): FilterRegistrationBean<TokenFilter> {
        val registration = FilterRegistrationBean<TokenFilter>()
        registration.filter = TokenFilter(tokenService, properties)
        return registration
    }

    @Bean
    @ConditionalOnMissingBean
    fun tokenService(properties: AuthProperties): TokenService = InMemoryTokenService(properties)
}
