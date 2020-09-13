package com.har01d.userauth.token

import com.har01d.userauth.config.AuthProperties
import com.har01d.userauth.dto.UserToken
import com.har01d.userauth.exception.UserUnauthorizedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class TokenFilter(private val tokenService: TokenService, private val properties: AuthProperties) : OncePerRequestFilter() {
    override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain
    ) {
        try {
            val token = getToken(request)
            token?.let {
                val authentication = buildAuthentication(token)
                SecurityContextHolder.getContext().authentication = authentication
            }
            filterChain.doFilter(request, response)
        } catch (e: UserUnauthorizedException) {
            response.sendError(401, e.message)
        }
    }

    private fun getToken(request: HttpServletRequest): String? {
        var token = request.getHeader(properties.headerName)
        if (token == null || token.isEmpty()) {
            token = request.getParameter(properties.headerName)
        }
        return token
    }

    private fun buildAuthentication(token: String): Authentication? {
        val userToken: UserToken = tokenService.extractToken(token) ?: return null
        return UsernamePasswordAuthenticationToken(userToken.username, "", userToken.authorities)
    }
}
