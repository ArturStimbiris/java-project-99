package hexlet.code.handler;

import hexlet.code.exception.LabelNotFoundException;
import hexlet.code.exception.RsaKeyLoadingException;
import hexlet.code.exception.SentryTestException;
import hexlet.code.exception.TaskNotFoundException;
import hexlet.code.exception.TaskStatusDeletionException;
import hexlet.code.exception.TaskStatusNotFoundException;
import hexlet.code.exception.UserDeletionException;
import hexlet.code.exception.UserNotFoundException;
import hexlet.code.exception.LabelDeletionException;
import io.sentry.Sentry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        Sentry.captureException(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException e) {
        Sentry.captureException(e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        Sentry.captureException(e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<String> handleTaskNotFoundException(TaskNotFoundException e) {
        Sentry.captureException(e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(TaskStatusNotFoundException.class)
    public ResponseEntity<String> handleTaskStatusNotFoundException(TaskStatusNotFoundException e) {
        Sentry.captureException(e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(LabelNotFoundException.class)
    public ResponseEntity<String> handleLabelNotFoundException(LabelNotFoundException e) {
        Sentry.captureException(e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(UserDeletionException.class)
    public ResponseEntity<String> handleUserDeletionException(UserDeletionException e) {
        Sentry.captureException(e);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(TaskStatusDeletionException.class)
    public ResponseEntity<String> handleTaskStatusDeletionException(TaskStatusDeletionException e) {
        Sentry.captureException(e);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(RsaKeyLoadingException.class)
    public ResponseEntity<String> handleRsaKeyLoadingException(RsaKeyLoadingException e) {
        Sentry.captureException(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(SentryTestException.class)
    public ResponseEntity<String> handleSentryTestException(SentryTestException e) {
        Sentry.captureException(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        Sentry.captureException(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(LabelDeletionException.class)
    public ResponseEntity<String> handleLabelDeletionException(LabelDeletionException e) {
        Sentry.captureException(e);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
