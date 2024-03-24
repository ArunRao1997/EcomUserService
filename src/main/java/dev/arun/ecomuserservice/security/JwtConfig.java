//package dev.arun.ecomuserservice.security;
//
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//
//@Configuration
//public class JwtConfig {
//
//    @Value("${my.app.jwt.secret}") // Inject from environment variable or secure store
//    private String jwtSecret;
//
//    @Bean
//    public SecretKey jwtSecretKey() {
//        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
//    }
//}
