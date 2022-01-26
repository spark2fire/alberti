package cn.spark2fire.auth.token

import cn.spark2fire.auth.config.AuthProperties
import cn.spark2fire.auth.dto.UserToken
import cn.spark2fire.auth.exception.UserUnauthorizedException
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*
import java.util.concurrent.TimeUnit

class RedisTokenService(private val redisTemplate: StringRedisTemplate, private val properties: AuthProperties) : TokenService {
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
            val key = properties.redisPrefix + username
            val accessToken: String? = redisTemplate.opsForValue().get(key)
            if (rawToken == accessToken) {
                if (!rememberMe) {
                    redisTemplate.expire(key, properties.idleTimeout, TimeUnit.MINUTES)
                }
                return UserToken(username, setOf(SimpleGrantedAuthority(authority)), token)
            } else {
                throw UserUnauthorizedException("Token失效", 40100)
            }
        }
        return null
    }

    override fun encodeToken(username: String, authority: String, rememberMe: Boolean): String {
        var token: String = username + ":" + authority + ":" + (if (rememberMe) 1 else 0) + ":" + UUID.randomUUID()
        token = Base64.getEncoder().encodeToString(token.toByteArray())
        val key = properties.redisPrefix + username
        redisTemplate.opsForValue().set(key, token)
        if (!rememberMe) {
            redisTemplate.expire(key, properties.idleTimeout, TimeUnit.MINUTES)
        }
        return token
    }

    override fun deleteToken(username: String) {
        redisTemplate.delete(properties.redisPrefix + username)
    }
}
