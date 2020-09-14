package cn.har01d.auth.token

import java.time.Instant

class Token(val username: String, val token: String, var activeTime: Instant)
