package cn.spark2fire.auth.token

import cn.spark2fire.auth.config.AuthProperties
import cn.spark2fire.auth.dto.UserToken
import cn.spark2fire.auth.exception.UserUnauthorizedException
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
    private val repository: ConcurrentHashMap<String, Token> = ConcurrentHashMap()
    private val key: Key = getKey()
    private fun getKey(): Key {
        return if (properties.jwt.secretKey.isNotEmpty()) {
            Keys.hmacShaKeyFor(properties.jwt.secretKey.toByteArray())
        } else {
            Keys.secretKeyFor(SignatureAlgorithm.HS512)
        }
    }

    override fun extractToken(rawToken: String): UserToken {
        try {
            val jws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .requireIssuer(properties.jwt.issuer)
                    .requireSubject(properties.jwt.subject)
                    .requireAudience(properties.jwt.audience)
                    .build()
                    .parseClaimsJws(rawToken)
            val username = jws.body.get("username", String::class.java)
            val authority = jws.body.get("authority", String::class.java)
            return UserToken(username, setOf(SimpleGrantedAuthority(authority)), rawToken)
        } catch (e: Exception) {
            throw UserUnauthorizedException("Token失效", e)
        }
    }

    override fun encodeToken(username: String, authority: String, rememberMe: Boolean): String {
        val now = Instant.now()
        val expire = if (rememberMe) now.plus(properties.jwt.rememberDays, ChronoUnit.DAYS) else now.plus(properties.idleTimeout, ChronoUnit.MINUTES)
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuer(properties.jwt.issuer)
                .setSubject(properties.jwt.subject)
                .setAudience(properties.jwt.audience)
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
}
