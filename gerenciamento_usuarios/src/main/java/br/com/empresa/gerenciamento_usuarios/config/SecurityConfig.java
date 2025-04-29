package br.com.empresa.gerenciamento_usuarios.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import br.com.empresa.gerenciamento_usuarios.Service.JwtService;
import br.com.empresa.gerenciamento_usuarios.auth.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtService jwtService;

    @Autowired
    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Configuração de PasswordEncoder ativada com BCrypt.");
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Iniciando configuração de SecurityFilterChain.");

        http
            // Configuração de CORS
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                corsConfig.setAllowedOrigins(List.of("http://localhost:5173", "http://example.com")); // Domínios permitidos
                corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                corsConfig.setAllowCredentials(true); // Permitir envio de credenciais (cookies e cabeçalhos)
                corsConfig.setAllowedHeaders(List.of("*")); // Permitir todos os cabeçalhos
                logger.info("Configuração de CORS ativada.");
                return corsConfig;
            }))
            // Desabilitar CSRF
            .csrf(csrf -> {
                csrf.disable();
                logger.info("CSRF desabilitado.");
            })
            // Configuração de autenticação e autorização
            .authorizeHttpRequests(auth -> {
                // **Permitir acesso ao Swagger UI**
                auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll();

                // **Rotas públicas** (Cadastro de usuários)
                auth.requestMatchers("/usuarios").permitAll();

                // **Rotas protegidas por autenticação**
                auth.requestMatchers("/usuarios/**").authenticated(); // Exige autenticação
                auth.requestMatchers("/dashboard/**").hasAuthority("ADMINISTRADOR"); // Exige permissão de administrador

                // Todas as demais rotas exigem autenticação
                auth.anyRequest().authenticated();
                logger.info("Configuração de autorização finalizada.");
            })
            // Gerenciamento de sessão: JWT é stateless
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Filtro de autenticação JWT
            .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173", "http://example.com")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true);
                logger.info("CORS configurado para integração com o front-end.");
            }
        };
    }
}