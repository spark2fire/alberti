package cn.spark2fire.auth.token

import cn.spark2fire.auth.dto.UserToken

interface TokenService {
    fun extractToken(rawToken: String): UserToken?
    fun encodeToken(username: String, authority: String, rememberMe: Boolean): String
    fun deleteToken(username: String)
}
