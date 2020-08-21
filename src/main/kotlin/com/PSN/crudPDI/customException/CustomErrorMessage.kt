package com.PSN.crudPDI.customException

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.*
import javax.persistence.EntityNotFoundException

/*
@ControllerAdvice é uma anotação que permite centralizar as exceptions nesta classe.
Quando ocorre alguma exceção o spring "entra" nesta classe e procura pelo Handler com a exceção correspondente
O motivo de haver apenas um método era porque quando ocorria uma exceção ele exibia código 500 e também o trace.
Agora quando ocorre esse erro ele exibe 404 junto com a mensgagem "O Id buscado não existe".
 */

@ControllerAdvice
class CustomErrorMessage : ResponseEntityExceptionHandler()
{
    @ExceptionHandler(EntityNotFoundException::class)
    fun handlerEntityNotFound(ex: Exception, request: WebRequest?): ResponseEntity<Any?> {
        val details: MutableList<String> = ArrayList()
        details.add("O Id buscado não existe!")
        val error = ErrorResponse(Date(), 404, details)
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Exception::class)
    fun handlerBadRequest(ex: Exception, request: WebRequest?): ResponseEntity<Any?> {
        val details: MutableList<String> = ArrayList()
        details.add("Ocorreu um erro em sua requisição, verifique sua sintaxe!")
        val error = ErrorResponse(Date(), 400, details)
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }
}
