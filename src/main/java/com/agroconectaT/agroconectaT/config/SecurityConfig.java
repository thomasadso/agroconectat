package com.agroconectaT.agroconectaT.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 1. RECURSOS PÚBLICOS
                .requestMatchers("/css/**", "/img/**", "/js/**", "/webjars/**", "/favicon.ico").permitAll()
                
                // 2. PÁGINAS PÚBLICAS
                .requestMatchers("/", "/index", "/home").permitAll()
                .requestMatchers("/auth/**", "/login", "/logout", "/register").permitAll()
                
                // 3. RUTAS DE ERROR Y DEMOSTRACIÓN (Aquí estaba el fallo)
                // Permitimos la ruta trampa "/ruta-404-test" explícitamente
                .requestMatchers("/error", "/simular-error", "/ruta-404-test").permitAll()
                
                // 4. ZONA PRIVADA (Catálogo público)
                .requestMatchers("/catalogo/**").permitAll() 
                
                // 5. PERMISOS POR ROL
                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/productos/nuevo", "/productos/editar/**", "/productos/eliminar/**").hasAnyRole("ADMIN", "CAMPESINO")
                .requestMatchers("/pedidos/**", "/pagos/**").authenticated()
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/login")
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