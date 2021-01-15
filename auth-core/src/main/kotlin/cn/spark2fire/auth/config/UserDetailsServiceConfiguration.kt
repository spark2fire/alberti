package cn.spark2fire.auth.config

import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.ObjectPostProcessor
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.util.StringUtils
import java.util.regex.Pattern

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(AuthenticationManager::class)
@ConditionalOnBean(ObjectPostProcessor::class)
@ConditionalOnMissingBean(value = [AuthenticationManager::class, AuthenticationProvider::class, UserDetailsService::class], type = ["org.springframework.security.oauth2.jwt.JwtDecoder", "org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector"])
class UserDetailsServiceConfiguration {
    @Bean
    @ConditionalOnMissingBean(type = ["org.springframework.security.oauth2.client.registration.ClientRegistrationRepository"])
    @Lazy
    fun inMemoryUserDetailsManager(properties: SecurityProperties,
                                   passwordEncoder: ObjectProvider<PasswordEncoder?>): InMemoryUserDetailsManager {
        val user = properties.user
        val roles = user.roles
        return InMemoryUserDetailsManager(
                User.withUsername(user.name).password(getOrDeducePassword(user, passwordEncoder.ifAvailable))
                        .roles(*StringUtils.toStringArray(roles)).build())
    }

    private fun getOrDeducePassword(user: SecurityProperties.User, encoder: PasswordEncoder?): String {
        val password = user.password
        if (user.isPasswordGenerated) {
            logger.info(String.format("%n%nUsing generated security password: %s%n", user.password))
        }
        return when {
            PASSWORD_ALGORITHM_PATTERN.matcher(password).matches() -> password
            encoder != null -> encoder.encode(password)
            else -> NOOP_PASSWORD_PREFIX + password
        }
    }

    companion object {
        private const val NOOP_PASSWORD_PREFIX = "{noop}"
        private val PASSWORD_ALGORITHM_PATTERN = Pattern.compile("^\\{.+}.*$")
        private val logger = LogFactory.getLog(UserDetailsServiceConfiguration::class.java)
    }
}
