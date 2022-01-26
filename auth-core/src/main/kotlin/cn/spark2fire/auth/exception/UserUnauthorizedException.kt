package cn.spark2fire.auth.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * The Exception if the Token is invalid.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
class UserUnauthorizedException : RuntimeException {
    val code: Int

    constructor(message: String, code: Int = 0) : super(message) {
        this.code = code
    }

    constructor(message: String, code: Int, cause: Throwable?) : super(message, cause) {
        this.code = code
    }
}
