package com.har01d.userauth.token

import com.har01d.userauth.dto.UserToken

interface TokenService {
    fun extractToken(rawToken: String): UserToken?
    fun encodeToken(username: String, authority: String, rememberMe: Boolean): String
    fun deleteToken(username: String)
}
