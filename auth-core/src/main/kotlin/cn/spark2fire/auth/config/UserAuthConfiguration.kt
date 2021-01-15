package cn.spark2fire.auth.config

import cn.spark2fire.auth.token.InMemoryTokenService
import cn.spark2fire.auth.token.TokenFilter
import cn.spark2fire.auth.token.TokenService
import cn.spark2fire.auth.web.AuthEndpoint
import cn.spark2fire.auth.web.FrameworkEndpointHandlerMapping
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
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

    @Order(90)
    @Configuration
    class DefaultWebSecurityConfigurer : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .csrf().disable()
                    .formLogin().disable()
                    .logout().disable()
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
