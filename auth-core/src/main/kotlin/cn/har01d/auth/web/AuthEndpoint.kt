package cn.har01d.auth.web

import cn.har01d.auth.dto.LoginDto
import cn.har01d.auth.dto.UserToken
import cn.har01d.auth.exception.UserUnauthorizedException
import cn.har01d.auth.token.TokenService
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@FrameworkEndpoint
@ResponseBody
@RequestMapping("/accounts")
class AuthEndpoint(val passwordEncoder: PasswordEncoder,
                   val userDetailsService: UserDetailsService,
                   val tokenService: TokenService) {
    @PostMapping("/login")
    fun login(@RequestBody account: LoginDto): UserToken {
        val user = userDetailsService.loadUserByUsername(account.username)
        if (user != null && passwordEncoder.matches(account.password, user.password)) {
            val authorities = if (user.authorities.isEmpty()) setOf(SimpleGrantedAuthority("ROLE_USER")) else user.authorities
            val token = tokenService.encodeToken(user.username, authorities.first().authority, account.rememberMe)
            return UserToken(user.username, authorities, token)
        }
        throw UserUnauthorizedException("用户或密码错误")
    }

    @PostMapping("/logout")
    fun logout() {
        val authentication = SecurityContextHolder.getContext().authentication
        authentication?.let {
            tokenService.deleteToken(it.name)
        }
    }

    @GetMapping("/principal")
    fun principal(): Authentication = SecurityContextHolder.getContext().authentication
}
