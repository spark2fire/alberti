package com.har01d.userauth.config

import com.har01d.userauth.token.JwtTokenService
import com.har01d.userauth.token.TokenService
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class JwtTokenConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun tokenService(): TokenService = JwtTokenService()
}
