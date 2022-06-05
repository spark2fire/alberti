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
        /**
         * The secret key to encode JWT token, at least 256 bit.
         */
        var secretKey: String = "",
        /**
         * The JWT token issuer.
         */
        var issuer: String = "spark2fire.cn",
        /**
         * The JWT token subject.
         */
        var subject: String = "auth0",
        /**
         * The JWT token audience.
         */
        var audience: String = "web",
        /**
         * The JWT token expire time, 30 days by default.
         */
        var rememberDays: Long = 30
)
