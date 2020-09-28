package cn.har01d.auth.config

import cn.har01d.auth.token.DatabaseTokenService
import cn.har01d.auth.token.TokenService
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

@Configuration(proxyBeanMethods = false)
class DatabaseTokenConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun tokenService(jdbcTemplate: JdbcTemplate, properties: AuthProperties): TokenService {
        jdbcTemplate.execute("create table if not exists ${properties.tableName} (username varchar(255) primary key,token varchar(255) not null,activeTime datetime not null,createTime datetime not null)")
        return DatabaseTokenService(jdbcTemplate, properties)
    }
}
