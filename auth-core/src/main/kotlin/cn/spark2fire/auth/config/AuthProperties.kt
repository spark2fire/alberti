package cn.spark2fire.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cn.spark2fire.auth")
class AuthProperties(
        var idleTimeout: Long = 30,
        var redisPrefix: String = "TOKEN:a:",
        var tableName: String = "t_token",
        var headerName: String = "X-ACCESS-TOKEN",
        var jwt: JwtProperties = JwtProperties()
)

class JwtProperties(
        var secretKey: String = "",
        var issuer: String = "Har01d",
        var subject: String = "auth0",
        var audience: String = "web",
        var rememberDays: Long = 7
)
