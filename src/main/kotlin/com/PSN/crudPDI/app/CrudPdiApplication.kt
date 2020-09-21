package com.PSN.crudPDI.app

import com.PSN.crudPDI.filters.AuthFilter
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableAutoConfiguration
@ComponentScan(basePackages = ["com.PSN.crudPDI"])
@ConfigurationPropertiesScan(basePackages = ["com.PSN.crudPDI.filters"])
@EnableJpaRepositories(basePackages = ["com.PSN.crudPDI.repository"])
@EntityScan(basePackages = ["com.PSN.crudPDI.model"])
class CrudPsnApplication

fun main(args: Array<String>) {
    runApplication<CrudPsnApplication>(*args)

    @Bean
    fun filterRegistrationBean(): FilterRegistrationBean<AuthFilter> {
        val registrationBean = FilterRegistrationBean<AuthFilter>()
        val authFilter = AuthFilter()
        registrationBean.filter = authFilter
        registrationBean.addUrlPatterns("/CrudPDI/*")
        return registrationBean
    }
}
