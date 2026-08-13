package com.appzoi.appzoi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.appzoi.appzoi.repository.UsuarioRepositorio;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UsuarioRepositorio usuarioRepository
    ) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/beneficios",
                                "/proposito",
                                "/valores",
                                "/registro",
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/uploads/**"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/dueno/**", "/mascotas/**").hasAnyRole("DUENO", "ADMIN")
                        .requestMatchers("/veterinario/**").hasAnyRole("VETERINARIO", "ADMIN")
                        .requestMatchers("/seleccionar-perfil").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            String username = authentication.getName();
                            usuarioRepository.findByEmail(username).ifPresentOrElse(usuario -> {
                                try {
                                    if ("ADMIN".equals(usuario.getTipoPerfil())
                                            || AdminUserSeeder.ADMIN_EMAIL.equalsIgnoreCase(username)) {
                                        response.sendRedirect("/admin");
                                    } else if ("VETERINARIO".equals(usuario.getTipoPerfil())) {
                                        response.sendRedirect("/veterinario/home");
                                    } else if ("DUENO".equals(usuario.getTipoPerfil())) {
                                        response.sendRedirect("/dueno/home");
                                    } else {
                                        response.sendRedirect("/seleccionar-perfil");
                                    }
                                } catch (Exception exception) {
                                    throw new RuntimeException(exception);
                                }
                            }, () -> {
                                try {
                                    response.sendRedirect("/seleccionar-perfil");
                                } catch (Exception exception) {
                                    throw new RuntimeException(exception);
                                }
                            });
                        })
                        .failureHandler((request, response, exception) -> {
                            request.getSession().setAttribute(
                                    "errorLogin",
                                    "El correo o la contraseña no son correctos."
                            );
                            String correo = request.getParameter("username");
                            if (correo != null && correo.length() <= 120) {
                                request.getSession().setAttribute("loginEmail", correo.trim());
                            }
                            response.sendRedirect("/login");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                .exceptionHandling(exception -> exception.accessDeniedPage("/acceso-denegado"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
