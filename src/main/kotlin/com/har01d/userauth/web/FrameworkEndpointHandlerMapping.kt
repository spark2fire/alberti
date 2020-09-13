package com.har01d.userauth.web

import org.springframework.core.annotation.AnnotationUtils
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.Method

class FrameworkEndpointHandlerMapping : RequestMappingHandlerMapping() {
    override fun isHandler(beanType: Class<*>): Boolean {
        return AnnotationUtils.findAnnotation(beanType, FrameworkEndpoint::class.java) != null
    }

    override fun getMappingForMethod(method: Method, handlerType: Class<*>): RequestMappingInfo? {
        return super.getMappingForMethod(method, handlerType)
    }
}
