package cn.spark2fire.auth.token

import cn.spark2fire.auth.config.AuthProperties
import cn.spark2fire.auth.dto.UserToken
import cn.spark2fire.auth.exception.UserUnauthorizedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StreamUtils
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class TokenFilter(private val tokenService: TokenService, private val properties: AuthProperties) :
    OncePerRequestFilter() {
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
            sendError(response, e)
        }
    }

    private fun sendError(response: HttpServletResponse, e: UserUnauthorizedException) {
        val body = "{\"message\":\"${e.message}\",\"code\":${e.code}}"
        response.contentType = "application/json"
        response.status = 401
        StreamUtils.copy(body.toByteArray(), response.outputStream)
    }

    private fun getToken(request: HttpServletRequest): String? {
        var token = request.getHeader(properties.headerName)
        if (token == null || token.isEmpty()) {
            token = request.getParameter(properties.headerName)
        }
        return token
    }

    private fun buildAuthentication(token: String): Authentication? {
        try {
            val userToken: UserToken = tokenService.extractToken(token) ?: return null
            return UsernamePasswordAuthenticationToken(userToken.name, "", userToken.authorities)
        } catch (e: UserUnauthorizedException) {
            throw e
        } catch (e: Exception) {
            logger.warn("Token失效", e)
            throw UserUnauthorizedException("Token失效", 40100, e)
        }
    }
}
