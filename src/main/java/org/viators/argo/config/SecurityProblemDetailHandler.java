package org.viators.argo.config;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;

/**
 * Produces RFC 9457 ProblemDetail responses for security exceptions.
 *
 * <p>Security exceptions (401, 403) are raised in the filter chain,
 * <strong>before</strong> the DispatcherServlet. This means they bypass
 * {@code @RestControllerAdvice} entirely. To produce ProblemDetail
 * responses for these, we implement Spring Security's entry point
 * and access denied handler interfaces and write the JSON directly.
 *
 * <p>Both handlers are inner implementations to keep the security
 * error handling cohesive in one file.
 */
@Component
@RequiredArgsConstructor
public class SecurityProblemDetailHandler {

    private final ObjectMapper objectMapper;


    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (HttpServletRequest request,
                HttpServletResponse response,
                AuthenticationException ex) -> {
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Authentication is required to access this resource"
            );
            problem.setTitle("Unauthorized");
            problem.setType(URI.create("https://api.ops.com/errors/unauthorized"));
            problem.setInstance(URI.create(request.getRequestURI()));
            problem.setProperty("errorCode", "UNAUTHORIZED");
            problem.setProperty("timestamp", Instant.now());

            writeProblemDetail(response, HttpStatus.UNAUTHORIZED, problem);
        };
    }

    /**
     * Handles 403 Forbidden — valid token but insufficient permissions.
     */
    public AccessDeniedHandler accessDeniedHandler() {
        return (HttpServletRequest request,
                HttpServletResponse response,
                AccessDeniedException ex) -> {

            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource"
            );
            problem.setTitle("Forbidden");
            problem.setType(URI.create("https://api.ops.com/errors/forbidden"));
            problem.setInstance(URI.create(request.getRequestURI()));
            problem.setProperty("errorCode", "FORBIDDEN");
            problem.setProperty("timestamp", Instant.now());

            writeProblemDetail(response, HttpStatus.FORBIDDEN, problem);
        };
    }

    /**
     * Writes a ProblemDetail as JSON to the HTTP response.
     *
     * <p>We write directly because security exceptions bypass the
     * Spring MVC pipeline — there's no content negotiation or
     * message converter chain available at this point.
     */
    private void writeProblemDetail(HttpServletResponse response,
                                    HttpStatus status,
                                    ProblemDetail problem) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), problem);
    }
}
