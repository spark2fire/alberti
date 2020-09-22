package cn.har01d.auth.dto

import org.springframework.security.core.GrantedAuthority

data class UserToken(val name: String, val authorities: Collection<GrantedAuthority>, val token: String)
