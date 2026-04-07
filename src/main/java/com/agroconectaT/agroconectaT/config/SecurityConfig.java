package com.agroconectaT.agroconectaT.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 1. ZONA PÚBLICA (Solo Home y Formularios de Registro/Login)
                .requestMatchers("/css/**", "/img/**", "/js/**", "/webjars/**", "/favicon.ico").permitAll()
                .requestMatchers("/", "/index", "/home").permitAll()
                .requestMatchers("/auth/**", "/login", "/logout", "/register").permitAll()
                .requestMatchers("/error", "/simular-error", "/ruta-404-test").permitAll()
                
                // =========================================================
                // EXCLUSIVIDAD DE ROLES (LA MAGIA)
                // =========================================================
                // 2. SOLO ADMINISTRADOR: Control total del sistema
                .requestMatchers("/admin/**", "/usuarios/**").hasRole("ADMIN")
                
                // 3. SOLO CAMPESINO: Gestión de su finca y sus productos
                .requestMatchers("/productos/**").hasRole("CAMPESINO")
                
                // 4. SOLO COMPRADOR: Ver el catálogo y comprar
                .requestMatchers("/catalogo/**", "/carrito/**", "/pedidos/**", "/pagos/**").hasRole("COMPRADOR")
                
                // Cualquier otra cosa, debe estar autenticado
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/login")
                // Aquí usamos el manejador de éxito para redirigir según el rol
                .successHandler(customAuthenticationSuccessHandler)
                .failureUrl("/auth/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/auth/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );
        return http.build();
    }
    
    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }

    @Bean
    public org.springframework.security.core.userdetails.UserDetailsService userDetailsService(
            com.agroconectaT.agroconectaT.usuario.UsuarioRepository usuarioRepository) {
        return username -> {
            var usuarioOpt = usuarioRepository.findByCorreo(username);
            if (usuarioOpt.isEmpty()) throw new org.springframework.security.core.userdetails.UsernameNotFoundException("Usuario no encontrado");
            var u = usuarioOpt.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(u.getCorreo()).password(u.getContrasena()).roles(u.getRol()).build();
        };
    }
}