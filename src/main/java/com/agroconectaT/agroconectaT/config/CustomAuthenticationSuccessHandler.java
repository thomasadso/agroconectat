package com.agroconectaT.agroconectaT.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException, ServletException {
        var roles = auth.getAuthorities().stream().map(Object::toString).toList();
        if (roles.contains("ROLE_ADMIN")) response.sendRedirect("/usuarios");
        else if (roles.contains("ROLE_CAMPESINO")) response.sendRedirect("/productos");
        else response.sendRedirect("/catalogo");
    }
}