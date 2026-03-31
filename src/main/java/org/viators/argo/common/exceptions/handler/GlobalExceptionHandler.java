package org.viators.argo.common.exceptions.handler;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.viators.argo.common.exceptions.*;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ERROR_TYPE_BASE = "http://api.argo.com/errors";

    // ═══════════════════════════════════════════════════════════════
    //  Custom Application Exceptions
    // ═══════════════════════════════════════════════════════════════

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        log.debug("Resource not found {}", ex.getMessage());
        return buildProblemDetail(HttpStatus.NOT_FOUND, "Resource Not Found", "not-found", ex);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicateResource(DuplicateResourceException ex) {
        log.warn("Duplicate resource: {}", ex.getMessage());
        return buildProblemDetail(HttpStatus.CONFLICT, "Duplicate Resource", "duplicate-resource", ex);
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ProblemDetail handleBusinessValidation(BusinessValidationException ex) {
        log.debug("Business validation failed: {}", ex.getMessage());
        return buildProblemDetail(HttpStatus.BAD_REQUEST, "Business Validation Failed", "business-validation", ex);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {
        log.warn("Invalid credentials attempt");
        return buildProblemDetail(HttpStatus.UNAUTHORIZED, "Invalid Credentials", "invalid-credentials", ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return buildProblemDetail(HttpStatus.FORBIDDEN, "Access Denied", "access-denied", ex);
    }

    /**
     * Handles invalid state transitions (e.g., cancelling an already shipped order).
     *
     * @param ex the invalid state exception
     * @return 409 Conflict with ProblemDetail body
     */
    @ExceptionHandler(InvalidStateException.class)
    public ProblemDetail handleInvalidState(InvalidStateException ex) {
        log.debug("Invalid state: {}", ex.getMessage());
        return buildProblemDetail(HttpStatus.CONFLICT, "Invalid State Transition", "invalid-state", ex);
    }

    // ═══════════════════════════════════════════════════════════════
    //  Validation Exceptions
    // ═══════════════════════════════════════════════════════════════

    /**
     * Customizes the ProblemDetail for Bean Validation failures ({@code @Valid}).
     *
     * <p>The parent class already handles this exception. We override to add
     * structured field-level errors as an extension property.
     *
     * <p><strong>Why override instead of a new @ExceptionHandler?</strong>
     * The parent already declares a handler for this exception. Adding a separate
     * {@code @ExceptionHandler} would cause ambiguity. Overriding the protected
     * method is the clean path.
     */
    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {

        log.debug("Validation failed with {} errors", ex.getErrorCount());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "One or more fields failed validation"
        );
        problemDetail.setTitle("Validation failed");
        problemDetail.setType(URI.create(ERROR_TYPE_BASE.concat("validation-failed")));
        problemDetail.setProperty("errorCode", ErrorCodeEnum.VALIDATION_FAILED);

        List<FieldErrorDetail> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError ->
                new FieldErrorDetail(
                    fieldError.getField(),
                    fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value",
                    fieldError.getRejectedValue()
                )
            )
            .toList();

        problemDetail.setProperty("fieldErrors", fieldErrors);
        return ResponseEntity.badRequest().body(problemDetail);
    }

    // ═══════════════════════════════════════════════════════════════
    //  Security Exceptions
    // ═══════════════════════════════════════════════════════════════

    /**
     * Handles Spring Security's AccessDeniedException.
     *
     * <p>Fires when {@code @PreAuthorize} fails for an authenticated user.
     * Note: 401 Unauthorized (no token / invalid token) is handled by
     * {@code SecurityProblemDetailHandler} in the security filter chain,
     * not here — those exceptions never reach the DispatcherServlet.
     *
     * @param ex the Spring Security access denied exception
     * @return 403 Forbidden with ProblemDetail body
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ProblemDetail handleSpringSecurityAccessDenied(
        org.springframework.security.access.AccessDeniedException ex) {

        log.warn("Spring Security access denied");

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.FORBIDDEN,
            "You do not have permission to access this resource"
        );
        problem.setTitle("Forbidden");
        problem.setType(URI.create(ERROR_TYPE_BASE + "forbidden"));
        problem.setProperty("errorCode", ErrorCodeEnum.ACCESS_DENIED);

        return problem;
    }

    // ═══════════════════════════════════════════════════════════════
    //  Data Access Exceptions
    // ═══════════════════════════════════════════════════════════════

    /**
     * Handles database constraint violations that slip past service-layer validation.
     *
     * <p>Not covered by {@link ResponseEntityExceptionHandler}.
     * Acts as a safety net for unique constraint, foreign key, and
     * other database-level integrity violations.
     *
     * <p><strong>Important:</strong> Never expose SQL details to the client.
     * The root cause is logged server-side only.
     *
     * @param ex the data integrity violation exception
     * @return 409 Conflict with ProblemDetail body
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Data integrity violation - Cause: {}", ex.getMostSpecificCause().getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "A data integrity constraint was violated. "
                + "Please check your request for duplicate or invalid references."
        );
        problem.setTitle("Data Integrity Violation");
        problem.setType(URI.create(ERROR_TYPE_BASE + "data-integrity-violation"));
        problem.setProperty("errorCode", ErrorCodeEnum.DATA_INTEGRITY_VIOLATION);

        return problem;
    }

    // ═══════════════════════════════════════════════════════════════
    //  Catch-All
    // ═══════════════════════════════════════════════════════════════

    /**
     * Safety net for any unhandled exception.
     *
     * <p>If an exception reaches here, it's a bug or unhandled edge case.
     * Log everything for debugging, return nothing specific to the client.
     *
     * @param ex the unhandled exception
     * @return 500 Internal Server Error with ProblemDetail body
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unexpected error - Exception: {}", ex.getClass().getName(), ex);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred. Please try again later."
        );
        problem.setTitle("Internal Server Error");
        problem.setType(URI.create(ERROR_TYPE_BASE + "internal-error"));
        problem.setProperty("errorCode", ErrorCodeEnum.INTERNAL_SERVER_ERROR);

        return problem;
    }

    // ═══════════════════════════════════════════════════════════════
    //  Global Enrichment Hook
    // ═══════════════════════════════════════════════════════════════

    /**
     * Central hook that ALL exception handlers funnel through.
     *
     * <p>Adds properties that should appear on every error response.
     * Both inherited handlers (Spring MVC exceptions) and our custom
     * handlers route through this method.
     */
    protected ResponseEntity<Object> handleExceptionInternal(
        @NonNull Exception ex,
        Object body,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode statusCode,
        @NonNull WebRequest request
    ) {
        if (body instanceof ProblemDetail problemDetail) {
            problemDetail.setProperty("timestamp", Instant.now());
        }

        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    private ProblemDetail buildProblemDetail(HttpStatus status,
                                             String title,
                                             String typeKey,
                                             BaseException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problem.setTitle(title);
        problem.setType(URI.create(ERROR_TYPE_BASE.concat(typeKey)));
        problem.setProperty("errorCode", ex.getErrorCode().name());

        return problem;
    }

    private record FieldErrorDetail(
        String field,
        String message,
        Object rejectedValue
    ) {
    }
}
