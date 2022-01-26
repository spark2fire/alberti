package cn.spark2fire.auth.token

import cn.spark2fire.auth.config.AuthProperties
import cn.spark2fire.auth.dto.UserToken
import cn.spark2fire.auth.exception.UserUnauthorizedException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class InMemoryTokenService(private val properties: AuthProperties) : TokenService {
    private val repository: ConcurrentHashMap<String, Token> = ConcurrentHashMap<String, Token>()
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
            val now = Instant.now()
            val accessToken = repository.get(username)
            if (accessToken != null && rawToken == accessToken.token && accessToken.activeTime.plus(properties.idleTimeout, ChronoUnit.MINUTES).isAfter(now)) {
                if (!rememberMe) {
                    accessToken.activeTime = Instant.now()
                    repository[username] = accessToken
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
        repository[username] = Token(username, token, Instant.now())
        return token
    }

    override fun deleteToken(username: String) {
        repository.remove(username)
    }
}
