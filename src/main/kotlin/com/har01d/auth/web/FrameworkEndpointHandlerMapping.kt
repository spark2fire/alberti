package com.har01d.auth.web

import org.springframework.core.annotation.AnnotationUtils
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

class FrameworkEndpointHandlerMapping : RequestMappingHandlerMapping() {
    override fun isHandler(beanType: Class<*>): Boolean {
        return AnnotationUtils.findAnnotation(beanType, FrameworkEndpoint::class.java) != null
    }
}
