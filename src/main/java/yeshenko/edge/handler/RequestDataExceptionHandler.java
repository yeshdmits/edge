package yeshenko.edge.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import yeshenko.edge.exception.FileCreatingException;
import yeshenko.edge.exception.FileNotValidException;
import yeshenko.edge.exception.ResponseDataException;

@RestControllerAdvice
public class RequestDataExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = FileNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleIOException(RuntimeException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(),
                HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = ResponseDataException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> handleResponseException(RuntimeException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(),
                HttpStatus.NO_CONTENT, request);
    }

    @ExceptionHandler(value = FileCreatingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleCreatingFileException(RuntimeException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
