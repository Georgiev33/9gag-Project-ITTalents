package com.ittalens.gag.model.exceptions;

import com.ittalens.gag.model.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BadRequestException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    private ErrorDTO handleBadRequest(Exception ex){
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage(ex.getMessage());
        errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDTO.setLocalDateTime(LocalDateTime.now());
        return errorDTO;
    }

    @ExceptionHandler(value = {NotFoundException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    private ErrorDTO handleNotFound(Exception ex){
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage(ex.getMessage());
        errorDTO.setStatus(HttpStatus.NOT_FOUND.value());
        errorDTO.setLocalDateTime(LocalDateTime.now());
        return errorDTO;
    }
}
