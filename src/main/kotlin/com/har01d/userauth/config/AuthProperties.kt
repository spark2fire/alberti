package com.har01d.userauth.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "com.har01d.auth")
class AuthProperties(
        var idleTimeout: Long = 30,
        var secretKey: String = "",
        var redisPrefix: String = "TOKEN:a:",
        var headerName: String = "X-ACCESS-TOKEN"
)
