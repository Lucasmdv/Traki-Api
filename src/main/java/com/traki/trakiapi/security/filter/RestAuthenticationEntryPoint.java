package com.traki.trakiapi.security.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String errorMessage;
        if (authException instanceof BadCredentialsException) {
            errorMessage = "Credenciales inválidas";
        } else if (authException instanceof DisabledException) {
            errorMessage = "Cuenta deshabilitada";
        } else if (authException instanceof LockedException) {
            errorMessage = "Cuenta bloqueada";
        } else if (authException instanceof AccountExpiredException) {
            errorMessage = "Cuenta expirada";
        } else if (authException instanceof CredentialsExpiredException) {
            errorMessage = "Credenciales expiradas";
        } else if (authException instanceof InsufficientAuthenticationException) {
            errorMessage = "Autenticación insuficiente";
        } else if (authException instanceof AuthenticationServiceException) {
            errorMessage = "Error en el servicio de autenticación";
        } else {
            errorMessage = "Error de autenticación: " + authException.getMessage();
        }

        String jsonResponse = String.format("{\"error\": \"%s\"," +
                        "\"status\": %d, \"path\": \"%s\"}",
                errorMessage,
                HttpServletResponse.SC_UNAUTHORIZED,
                request.getRequestURI());
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
