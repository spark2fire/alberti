package cn.spark2fire.auth.config

import cn.spark2fire.auth.handler.UserAuthHandler
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class LoginHandlerConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun userAuthHandler() = UserAuthHandler()
}
