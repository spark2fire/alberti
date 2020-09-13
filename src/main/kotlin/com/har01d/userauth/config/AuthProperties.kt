package com.har01d.userauth.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "com.har01d.auth")
class AuthProperties(var secretKey: String = "")
