package com.har01d.userauth.token

import com.har01d.userauth.dto.UserToken
import com.har01d.userauth.exception.UserUnauthorizedException
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*
import java.util.concurrent.TimeUnit

class RedisTokenService(val redisTemplate: StringRedisTemplate) : TokenService {
    override fun extractToken(rawToken: String): UserToken? {
        val token = String(Base64.getDecoder().decode(rawToken))
        var username: String? = null
        var authority: String? = null
        var rememberMe = false
        val parts = token.split(":").toTypedArray()
        if (parts.size == 4) {
            username = parts[0]
            authority = parts[1]
            rememberMe = "1" == parts[2]
        }
        if (username != null) {
            val key = TOKEN_PREFIX + username
            val accessToken: String? = redisTemplate.opsForValue().get(key)
            if (rawToken == accessToken) {
                if (!rememberMe) {
                    redisTemplate.expire(key, IDLE_TIMEOUT, TimeUnit.MINUTES)
                }
                return UserToken(username, setOf(SimpleGrantedAuthority(authority)), token)
            } else {
                throw UserUnauthorizedException("Token失效")
            }
        }
        return null
    }

    override fun encodeToken(username: String, authority: String, rememberMe: Boolean): String {
        var token: String = username + ":" + authority + ":" + (if (rememberMe) 1 else 0) + ":" + UUID.randomUUID()
        token = Base64.getEncoder().encodeToString(token.toByteArray())
        val key = TOKEN_PREFIX + username
        redisTemplate.opsForValue().set(key, token)
        if (!rememberMe) {
            redisTemplate.expire(key, IDLE_TIMEOUT, TimeUnit.MINUTES)
        }
        return token
    }

    override fun deleteToken(username: String) {
        redisTemplate.delete(TOKEN_PREFIX + username)
    }

    companion object {
        private const val TOKEN_PREFIX = "TOKEN:c:"
        private const val IDLE_TIMEOUT = 30L
    }
}
