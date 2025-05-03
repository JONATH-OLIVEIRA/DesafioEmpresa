package br.com.empresa.gerenciamento_usuarios.config;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; 
import org.springframework.http.HttpMethod;
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
            .csrf(csrf -> csrf.disable()) // 🔄 Desabilita CSRF para facilitar testes

            .authorizeHttpRequests(auth -> {
                // 🔓 Liberando acesso às rotas públicas
                auth.requestMatchers(HttpMethod.GET, "/usuarios", "/usuarios/**").permitAll();
                auth.requestMatchers(HttpMethod.GET, "/usuarios/cadastro").permitAll();
                auth.requestMatchers("/usuarios/cadastro", "/usuarios/salvar").permitAll();
                auth.requestMatchers(HttpMethod.POST, "/usuarios/salvar").permitAll();
                auth.requestMatchers(HttpMethod.GET, "/enderecos/buscar").permitAll();
                auth.requestMatchers(HttpMethod.POST, "/enderecos/buscar").permitAll();
                auth.requestMatchers("/enderecos", "/enderecos/**").permitAll();
                auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll();
                
                // 🔓 Permitir acesso às páginas Thymeleaf e arquivos estáticos
                auth.requestMatchers("/templates/**", "/css/**", "/js/**", "/images/**").permitAll();

                // 🔐 Bloquear demais rotas, exigindo autenticação (vem por último)
                auth.anyRequest().authenticated();
            })

            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 🔄 Evita bloqueios indesejados

            .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class); // 🔄 Mantendo autenticação JWT

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