package cn.spark2fire.auth.web

import cn.spark2fire.auth.dto.LoginDto
import cn.spark2fire.auth.dto.UserToken
import cn.spark2fire.auth.event.UserLoginEvent
import cn.spark2fire.auth.event.UserLoginFailedEvent
import cn.spark2fire.auth.event.UserLogoutEvent
import cn.spark2fire.auth.exception.UserUnauthorizedException
import cn.spark2fire.auth.token.TokenService
import org.springframework.context.ApplicationEventPublisher
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
                   val publisher: ApplicationEventPublisher,
                   val userDetailsService: UserDetailsService,
                   val tokenService: TokenService) {
    @PostMapping("/login")
    fun login(@RequestBody account: LoginDto): UserToken {
        val user = userDetailsService.loadUserByUsername(account.username)
        if (user != null && passwordEncoder.matches(account.password, user.password)) {
            val authorities = if (user.authorities.isEmpty()) setOf(SimpleGrantedAuthority("ROLE_USER")) else user.authorities
            val token = tokenService.encodeToken(user.username, authorities.first().authority, account.rememberMe)
            publisher.publishEvent(UserLoginEvent(account.username))
            return UserToken(user.username, authorities, token)
        }
        publisher.publishEvent(UserLoginFailedEvent(account.username))
        throw UserUnauthorizedException("用户或密码错误")
    }

    @PostMapping("/logout")
    fun logout() {
        val authentication = SecurityContextHolder.getContext().authentication
        authentication?.let {
            tokenService.deleteToken(it.name)
            publisher.publishEvent(UserLogoutEvent(it.name))
        }
    }

    @GetMapping("/principal")
    fun principal(): Authentication = SecurityContextHolder.getContext().authentication
}
