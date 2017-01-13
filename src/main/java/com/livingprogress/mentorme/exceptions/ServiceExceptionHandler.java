package com.livingprogress.mentorme.exceptions;

import com.livingprogress.mentorme.aop.LogAspect;
import com.livingprogress.mentorme.utils.Helper;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * The exception handler.
 */
@EnableWebMvc
@ControllerAdvice
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle controller exception.
     * @param exception the exception.
     * @return the error response entity.
     */
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity<Object> handleControllerException(Throwable exception) {        
        Helper.logException(LogAspect.LOGGER, "com.livingprogress.mentorme.exceptions"
                + ".ServiceExceptionHandler#handleControllerException", exception);

        String stackTrace = ExceptionUtils.getStackTrace(exception);
        LogAspect.LOGGER.error(stackTrace, exception);

        HttpStatus status;
        if (exception instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (exception instanceof EntityNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (exception instanceof AccessDeniedException) {
            status = HttpStatus.FORBIDDEN;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return buildErrorResponse(status, exception);
    }

    /**
     * Handle internal exception with custom error response.
     * @param ex the exception.
     * @param headers the http header.
     * @param status the http status
     * @param request the web request.
     * @return the error response entity
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute("javax.servlet.error.exception", ex, 0);
        }
        return buildErrorResponse(status, ex);
    }

    /**
     * Build error response.
     * @param status the http status.
     * @param ex the exception.
     * @return the error response entity with code and message.
     */
    private static ResponseEntity<Object> buildErrorResponse(HttpStatus status, Throwable ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("code", status.value());
        responseBody.put("message", !Helper.isNullOrEmpty(ex.getMessage()) ? ex.getMessage()
                : "Unexpected error");
        return new ResponseEntity<>(responseBody, status);
    }
}
