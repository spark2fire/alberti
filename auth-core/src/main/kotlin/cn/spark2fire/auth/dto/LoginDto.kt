package cn.spark2fire.auth.dto

data class LoginDto(val username: String, val password: String, val rememberMe: Boolean, val captcha: String?)
