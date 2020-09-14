package cn.har01d.auth.token

import cn.har01d.auth.dto.UserToken

interface TokenService {
    fun extractToken(rawToken: String): UserToken?
    fun encodeToken(username: String, authority: String, rememberMe: Boolean): String
    fun deleteToken(username: String)
}
