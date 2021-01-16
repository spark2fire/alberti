package cn.spark2fire.auth.event

import org.springframework.context.ApplicationEvent

abstract class UserEvent(username: String) : ApplicationEvent(username)
