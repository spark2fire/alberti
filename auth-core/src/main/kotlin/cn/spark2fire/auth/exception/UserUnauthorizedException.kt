package cn.spark2fire.auth.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * The Exception if the Token is invalid.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
class UserUnauthorizedException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
}
