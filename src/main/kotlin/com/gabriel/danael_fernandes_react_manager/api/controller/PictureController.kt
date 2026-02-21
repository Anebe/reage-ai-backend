package com.gabriel.danael_fernandes_react_manager.api.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class PictureController(
    @Value("\${file.upload-dir}") private val uploadDir: String
) : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // TRADUÇÃO:
        // "Quando uma requisição chegar para a URL '/pictures/qualquer-coisa.jpg'..."
        registry.addResourceHandler("/pictures/**")
            // "...procure por 'qualquer-coisa.jpg' na pasta física configurada em 'file.upload-dir'"
            .addResourceLocations("file:$uploadDir")
    }
}