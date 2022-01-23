package cn.spark2fire.auth.handler

import cn.spark2fire.auth.dto.LoginDto

open class UserAuthHandler {
    open fun preLogin(account: LoginDto) {}

    open fun onLoginSuccess(username: String) {}

    open fun onLoginFail(username: String) {}

    open fun onLogoutSuccess(username: String) {}
}
