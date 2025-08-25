package com.gabriel.danael_fernandes_react_manager.api.controller

import com.gabriel.danael_fernandes_react_manager.core.BusinessRuleException
import com.gabriel.danael_fernandes_react_manager.core.ResourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    // Captura a exceção mais específica primeiro
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(ex: ResourceNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(ex.message ?: "Não encontrado", HttpStatus.NOT_FOUND.value())
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    // Captura exceções de negócio
    @ExceptionHandler(BusinessRuleException::class)
    fun handleBusinessRule(ex: BusinessRuleException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(ex.message ?: "Violação da(s) regra(s)", HttpStatus.UNPROCESSABLE_ENTITY.value())
        return ResponseEntity(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY)
    }

}

data class ErrorResponse(val message: String, val status: Int)