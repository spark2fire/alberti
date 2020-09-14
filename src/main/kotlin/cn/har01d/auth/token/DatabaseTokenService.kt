package cn.har01d.auth.token

import cn.har01d.auth.config.AuthProperties
import cn.har01d.auth.dto.UserToken
import cn.har01d.auth.exception.UserUnauthorizedException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class DatabaseTokenService(private val jdbcTemplate: JdbcTemplate, private val properties: AuthProperties) : TokenService {
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
            val now = Timestamp.from(Instant.now())
            val accessToken = jdbcTemplate.query("SELECT token,activeTime FROM ${properties.tableName} WHERE username=?",
                    RowMapper { rs, _ ->
                        Token(username, rs.getString("token"), rs.getTimestamp("activeTime").toInstant())
                    }, username).firstOrNull()
            if (accessToken != null && rawToken == accessToken.token && accessToken.activeTime.plus(properties.idleTimeout, ChronoUnit.MINUTES).isAfter(now.toInstant())) {
                if (!rememberMe) {
                    jdbcTemplate.update("UPDATE ${properties.tableName} SET activeTime=? WHERE username=?", now, username)
                }
                return UserToken(username, setOf(SimpleGrantedAuthority(authority)), token)
            } else {
                throw UserUnauthorizedException("Token失效")
            }
        }
        return null
    }

    override fun encodeToken(username: String, authority: String, rememberMe: Boolean): String {
        deleteToken(username)
        var token: String = username + ":" + authority + ":" + (if (rememberMe) 1 else 0) + ":" + UUID.randomUUID()
        token = Base64.getEncoder().encodeToString(token.toByteArray())
        val now = Timestamp.from(Instant.now())
        jdbcTemplate.update("INSERT INTO ${properties.tableName} (username,token,activeTime,createTime) VALUES (?,?,?,?)", username, token, now, now)
        return token
    }

    override fun deleteToken(username: String) {
        jdbcTemplate.update("DELETE FROM ${properties.tableName} WHERE username=?", username)
    }
}

