package com.har01d.userauth.token

import com.har01d.userauth.config.AuthProperties
import com.har01d.userauth.dto.UserToken
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.security.Key
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ConcurrentHashMap


class JwtTokenService(private val properties: AuthProperties) : TokenService {
    private val repository: ConcurrentHashMap<String, Token> = ConcurrentHashMap<String, Token>()
    private val key: Key = getKey()
    private fun getKey(): Key {
        return if (properties.secretKey.isNotEmpty()) {
            Keys.hmacShaKeyFor(properties.secretKey.toByteArray())
        } else {
            Keys.secretKeyFor(SignatureAlgorithm.HS512)
        }
    }

    override fun extractToken(rawToken: String): UserToken? {
        val jws = Jwts.parserBuilder()
                .setSigningKey(key)
                .requireIssuer(ISSUER)
                .requireSubject(SUBJECT)
                .requireAudience(AUDIENCE)
                .build()
                .parseClaimsJws(rawToken)
        val username = jws.body.get("username", String::class.java)
        val authority = jws.body.get("authority", String::class.java)
        return UserToken(username, setOf(SimpleGrantedAuthority(authority)), rawToken)
    }

    override fun encodeToken(username: String, authority: String, rememberMe: Boolean): String {
        val now = Instant.now()
        val expire = if (rememberMe) now.plus(7, ChronoUnit.DAYS) else now.plus(IDLE_TIMEOUT, ChronoUnit.MINUTES)
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuer(ISSUER)
                .setSubject(SUBJECT)
                .setAudience(AUDIENCE)
                .claim("username", username)
                .claim("authority", authority)
                .claim("rememberMe", rememberMe)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expire))
                .signWith(key)
                .compact()
    }

    override fun deleteToken(username: String) {
        repository.remove(username)
    }

    companion object {
        private const val ISSUER = "Har01d"
        private const val SUBJECT = "auth0"
        private const val AUDIENCE = "web"
        private const val IDLE_TIMEOUT = 30L
    }
}
