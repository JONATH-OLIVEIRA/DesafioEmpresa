package br.com.empresa.gerenciamento_usuarios.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private final Key secretKey;

    // Injeta a chave secreta configurada em application.properties
    public JwtService(@Value("${jwt.secret-key}") String secretKeyBase64) {
        if (secretKeyBase64 == null || secretKeyBase64.isBlank()) {
            throw new IllegalStateException("A propriedade 'jwt.secret-key' não está configurada ou está inválida.");
        }

        try {
            this.secretKey = new SecretKeySpec(
                Base64.getDecoder().decode(secretKeyBase64),
                SignatureAlgorithm.HS256.getJcaName()
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("A propriedade 'jwt.secret-key' contém um valor inválido. Certifique-se de que seja uma string Base64 válida.", e);
        }

        logger.info("JWT Service inicializado com sucesso.");
    }

    // Geração de Token JWT
    public String generateToken(String email, List<String> roles) {
        Instant now = Instant.now(); // Momento atual

        String token = Jwts.builder()
                .setSubject(email) // Define o "subject" como o email
                .claim("roles", roles) // Adiciona roles ao token
                .setIssuedAt(Date.from(now)) // Converte Instant para Date
                .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS))) // Expira em 1 hora
                .signWith(secretKey) // Assina o token com a chave secreta
                .compact();

        logger.info("Token gerado para email [{}] com roles [{}]: {}", email, roles, token);
        return token;
    }

    // Valida o Token JWT
    public boolean validateToken(String token) {
        try {
            logger.info("Token recebido para validação: {}", token);
            getAllClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            logger.error("Erro na validação do token: {}", e.getMessage());
            return false;
        }
    }

    // Extrai Claims de um Token JWT
    public Claims getAllClaimsFromToken(String token) {
        JwtParserBuilder parserBuilder = Jwts.parserBuilder();
        return parserBuilder
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extrai o email (Subject) do Token
    public String extractEmail(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    // Extrai as Roles do Token
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (List<String>) claims.get("roles");
    }
}