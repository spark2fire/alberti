package cn.spark2fire.auth.config

import cn.spark2fire.auth.token.InMemoryTokenService
import cn.spark2fire.auth.token.TokenFilter
import cn.spark2fire.auth.token.TokenService
import cn.spark2fire.auth.web.AuthEndpoint
import cn.spark2fire.auth.web.FrameworkEndpointHandlerMapping
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationEventPublisher
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

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(WebSecurityConfigurerAdapter::class)
    @ConditionalOnMissingBean(WebSecurityConfigurerAdapter::class)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    class SpringBootWebSecurityConfiguration {
        @Configuration(proxyBeanMethods = false)
        @Order(SecurityProperties.BASIC_AUTH_ORDER)
        internal class DefaultWebSecurityConfigurer : WebSecurityConfigurerAdapter() {
            override fun configure(http: HttpSecurity) {
                http.authorizeRequests { requests -> requests.anyRequest().authenticated() }
                http
                        .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .and()
                        .csrf().disable()
                        .formLogin().disable()
                        .logout().disable()
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authEndpoint(passwordEncoder: PasswordEncoder, publisher: ApplicationEventPublisher, userDetailsService: UserDetailsService, tokenService: TokenService): AuthEndpoint {
        return AuthEndpoint(passwordEncoder, publisher, userDetailsService, tokenService)
    }

    @Bean
    fun mapping() = FrameworkEndpointHandlerMapping()

    @Bean
    fun tokenFilter(tokenService: TokenService, properties: AuthProperties) = TokenFilter(tokenService, properties)

    @Bean
    @ConditionalOnMissingBean
    fun tokenService(properties: AuthProperties): TokenService = InMemoryTokenService(properties)
}
