package com.dondinero.peleadeenanos;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/Peleadores/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/peleas/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/Peleadores").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/peleas").permitAll()
                .requestMatchers(HttpMethod.DELETE,"/api/Peleadores/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/peleas/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        return http.build();
    }

    @Bean
   
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

    converter.setJwtGrantedAuthoritiesConverter(jwt -> {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null) return List.of();

        List<String> roles = (List<String>) realmAccess.get("roles");

        if (roles == null) return List.of();

        return roles.stream()
                .map(SimpleGrantedAuthority::new) // 🔥 NO agregar ROLE_
                .collect(Collectors.toList());
    });

    return converter;
    }
}