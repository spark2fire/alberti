package com.har01d.userauth.dto

import org.springframework.security.core.GrantedAuthority

data class UserToken(val username: String, val authorities: Collection<GrantedAuthority>, val token: String)
