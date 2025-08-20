package com.gabriel.danael_fernandes_react_manager.api.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/teste")
class TesteController {
    @GetMapping
    fun aaa(): String{
        return "hello world"
    }
}