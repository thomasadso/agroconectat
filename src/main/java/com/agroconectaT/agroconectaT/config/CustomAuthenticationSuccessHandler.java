package com.agroconectaT.agroconectaT.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Extraemos los roles del usuario que acaba de iniciar sesión
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        // Redirección exclusiva por rol
        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin/dashboard");
        } else if (roles.contains("ROLE_CAMPESINO")) {
            response.sendRedirect("/productos");
        } else if (roles.contains("ROLE_COMPRADOR")) {
            response.sendRedirect("/catalogo");
        } else {
            response.sendRedirect("/");
        }
    }
}