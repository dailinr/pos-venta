package com.dailin.api_posventa.exception;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.dailin.api_posventa.dto.response.ApiError;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // Metodo para manejar excepciones
    @ExceptionHandler({
        Exception.class,
        ObjectNotFoundException.class,
        MethodArgumentTypeMismatchException.class, // tipo de argumento no coinciden
        MethodArgumentNotValidException.class, // el arg no es valido (según jakarta validation)
        HttpRequestMethodNotSupportedException.class, // 405: method http no permitido
        HttpMediaTypeNotSupportedException.class, // formato no soportado (solo JSON)
        HttpMessageNotReadableException.class // formato de datos ilegible
    })
    public ResponseEntity<ApiError> handleGenericException(
        Exception exception,
        HttpServletRequest request,
        HttpServletResponse response
    ){

        ZoneId zoneId = ZoneId.of("America/Bogota");
        LocalDateTime timestamp = LocalDateTime.now(zoneId); // hora actual

        if(exception instanceof ObjectNotFoundException objectNotFoundException) {
            return this.handleObjectNotFoundException(objectNotFoundException, request, response, timestamp);
        }
        else if(exception instanceof MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
            return this.handleMethodArgumentTypeMismatchException(methodArgumentTypeMismatchException, request, response, timestamp);
        }
        else if(exception instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            return this.handleMethodArgumentNotValidException(methodArgumentNotValidException, request, response, timestamp);
        }
        else if(exception instanceof HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException) {
            return this.handleHttpRequestMethodNotSupportedException(httpRequestMethodNotSupportedException, request, response, timestamp);
        }
        else if(exception instanceof HttpMediaTypeNotSupportedException httpMediaTypeNotSupportedException){
            return this.handleHttpMediaTypeNotSupportedException(httpMediaTypeNotSupportedException, request, response, timestamp);
        }
        else if(exception instanceof HttpMessageNotReadableException httpMessageNotReadableException){
            return this.handleHttpMessageNotReadableException(httpMessageNotReadableException, request, response, timestamp);
        }
        else {
            return this.handleException(exception, request, response, timestamp);
        }
    }

    private ResponseEntity<ApiError> handleHttpMessageNotReadableException(
        HttpMessageNotReadableException httpMessageNotReadableException, 
        HttpServletRequest request, HttpServletResponse response, LocalDateTime timestamp
    ) {
        int httpStatus = HttpStatus.BAD_REQUEST.value(); 
         
        ApiError apiError = new ApiError(
            httpStatus, 
            request.getRequestURL().toString(), 
            request.getMethod(), 
            "Opps! Error leyendo el mensaje del cuerpo de la solicitud HTTP. "+
            "Asegurate de que la solicitud esté en un formato correcto y contenga datos validos.", 
            httpMessageNotReadableException.getMessage(), 
            timestamp, 
            null
        );

        return ResponseEntity.status(httpStatus).body(apiError);
    }

    private ResponseEntity<ApiError> handleHttpMediaTypeNotSupportedException(
        HttpMediaTypeNotSupportedException httpMediaTypeNotSupportedException, 
        HttpServletRequest request, HttpServletResponse response, LocalDateTime timestamp
    ) {
        int httpStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(); 
         
        ApiError apiError = new ApiError(
            httpStatus, 
            request.getRequestURL().toString(), 
            request.getMethod(), 
            "Tipo de media no soportado. El servidor no está habilitado para procesar "+
            "la entidad requerida en el formato proporcionado en la solicitud. "+
            "Los tipos de media soportados son: " +httpMediaTypeNotSupportedException.getSupportedMediaTypes() + 
            " y tú enviaste: "+ httpMediaTypeNotSupportedException.getContentType(), 
            httpMediaTypeNotSupportedException.getMessage(), 
            timestamp, 
            null
        );

        return ResponseEntity.status(httpStatus).body(apiError);
    }

    private ResponseEntity<ApiError> handleHttpRequestMethodNotSupportedException(
        HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException, 
        HttpServletRequest request, HttpServletResponse response, LocalDateTime timestamp
    ) {
       
        int httpStatus = HttpStatus.METHOD_NOT_ALLOWED.value(); 
         
        ApiError apiError = new ApiError(
            httpStatus, 
            request.getRequestURL().toString(), 
            request.getMethod(), 
            "Opps! Método no permitido. Revise el método HTTP de tu solicitud.", 
            httpRequestMethodNotSupportedException.getMessage(), 
            timestamp, 
            null
        );

        return ResponseEntity.status(httpStatus).body(apiError);
    }

    private ResponseEntity<ApiError> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException methodArgumentNotValidException, HttpServletRequest request,
        HttpServletResponse response, LocalDateTime timestamp
    ) {

        int httpStatus = HttpStatus.BAD_REQUEST.value(); 

        // Lista con los detalles de errores de validacion (jakarta validation)
        List<ObjectError> errors = methodArgumentNotValidException.getAllErrors();
        List<String> details = errors.stream().map(error -> {
            // se comprueba que cada error sea una instancia FieldErrors (campos definidos en la entidad)
            if(error instanceof FieldError fieldError) {
                // devuelve el nombre del atributo/campo que no se validó y su mensaje
                return fieldError.getField() + ": "+ fieldError.getDefaultMessage();
            }
            return error.getDefaultMessage();
        }).toList(); // se convierte en una list<String> con los detalles
         
        ApiError apiError = new ApiError(
            httpStatus, 
            request.getRequestURL().toString(), 
            request.getMethod(), 
            "El contenido de la solicitud es invalido o tiene parametros incompletos. "+
            "Por favor, verifica la información requerida antes de volver a intentar.", 
            methodArgumentNotValidException.getMessage(), 
            timestamp, 
            details
        );

        return ResponseEntity.status(httpStatus).body(apiError); 
    }

    private ResponseEntity<ApiError> handleException(Exception exception, HttpServletRequest request,
        HttpServletResponse response, LocalDateTime timestamp
    ) {
        int httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value(); 
         
        ApiError apiError = new ApiError(
            httpStatus, 
            request.getRequestURL().toString(), 
            request.getMethod(), 
            "Opps! Algo ocurrió en el servidor. Por favor intentalo más tarde.", 
            exception.getMessage(), 
            timestamp, 
            null
        );

        return ResponseEntity.status(httpStatus).body(apiError);
    }

    private ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException methodArgumentTypeMismatchException, HttpServletRequest request,
        HttpServletResponse response, LocalDateTime timestamp
    ) {
        int httpStatus = HttpStatus.BAD_REQUEST.value();

        Object valueRejected = methodArgumentTypeMismatchException.getValue(); // devuelve el valor del argumento - lo saca como obj porque desconoce su type
        String propertyName = methodArgumentTypeMismatchException.getName(); // devuelve el nombre de la propiedad (Args)

        ApiError apiError = new ApiError(
            httpStatus, 
            request.getRequestURL().toString(), 
            request.getMethod(), 
            "Solicitud invalida. El valor ingresado '"+valueRejected+"' no es el tipo de dato esperado por el "+propertyName, 
            methodArgumentTypeMismatchException.getMessage(), 
            timestamp, 
            null
        );

        return ResponseEntity.status(httpStatus).body(apiError);
    }

    private ResponseEntity<ApiError> handleObjectNotFoundException(ObjectNotFoundException objectNotFoundException,
        HttpServletRequest request, HttpServletResponse response, LocalDateTime timestamp
    ) {
        int httpStatus = HttpStatus.NOT_FOUND.value(); 
         
        ApiError apiError = new ApiError(
            httpStatus, 
            request.getRequestURL().toString(), 
            request.getMethod(), 
            "Lo sientimos, la información requerida no pudo ser encontrada. Por favor, revise la URL o intente otra busqueda.", 
            objectNotFoundException.getMessage(), 
            timestamp, 
            null
        );

        return ResponseEntity.status(httpStatus).body(apiError);
    }

}
