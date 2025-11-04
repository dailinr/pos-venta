package com.dailin.api_posventa.controller;

import java.net.URI;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.dailin.api_posventa.dto.request.SaveProduct;
import com.dailin.api_posventa.dto.response.ApiError;
import com.dailin.api_posventa.dto.response.GetProduct;
import com.dailin.api_posventa.exception.ObjectNotFoundException;
import com.dailin.api_posventa.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<GetProduct>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<GetProduct> findOneById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findOneById(id));
    }

    @PostMapping
    public ResponseEntity<GetProduct> createOne(
        @RequestBody @Valid SaveProduct saveDto, HttpServletRequest request
    ){
        GetProduct productCreated = productService.createOne(saveDto);
        String baseUrl = request.getRequestURL().toString();

        // localizacion para el producto recien creado 
        URI newLocation = URI.create(baseUrl + "/" + productCreated.id());

        return ResponseEntity.created(newLocation).body(productCreated);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<GetProduct> updatedOneById(
        @PathVariable Long id, @RequestBody @Valid SaveProduct saveDto
    ){

        try {
            GetProduct productUpdated = productService.updtedOneById(id, saveDto);
            return ResponseEntity.ok(productUpdated);
        } 
        catch (ObjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteOneById(@PathVariable Long id){

        try {
            productService.deleteOneById(id);
            return ResponseEntity.noContent().build();
        } 
        catch (ObjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Metodo para manejar excepciones
    @ExceptionHandler({
        Exception.class,
        ObjectNotFoundException.class,
        MethodArgumentTypeMismatchException.class, // tipo de argumento no coinciden
        MethodArgumentNotValidException.class // el arg no es valido (según jakarta validation)
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
        else {
            return this.handleException(exception, request, response, timestamp);
        }

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
