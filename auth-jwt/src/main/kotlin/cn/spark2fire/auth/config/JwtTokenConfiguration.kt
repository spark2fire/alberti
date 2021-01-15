package cn.spark2fire.auth.config

import cn.spark2fire.auth.token.JwtTokenService
import cn.spark2fire.auth.token.TokenService
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class JwtTokenConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun tokenService(properties: AuthProperties): TokenService = JwtTokenService(properties)
}
