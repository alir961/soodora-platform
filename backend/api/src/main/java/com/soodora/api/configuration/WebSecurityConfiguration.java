package com.soodora.api.configuration;

import com.soodora.api.auth.JwtRequestFilter;
import com.soodora.service.user.UserService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration {

    private final JwtRequestFilter jwtAuthFilter;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Value("${application.url:localhost}")
    private String applicationUrl;

    @Value("${administration.url:localhost}")
    private String administrationUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/status",
                                "/auth/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources",
                                "/swagger-resources/**")
                        .permitAll()
                        .requestMatchers(GET, "/users/*").permitAll()
                        .requestMatchers(GET, "/users/*/followers").permitAll()
                        .requestMatchers(GET, "/users/*/following").permitAll()
                        .requestMatchers(GET, "/users/*/posts").permitAll()
                        .requestMatchers(GET, "/users/*/reposts").permitAll()
                        .requestMatchers(GET, "/users/*/audios").permitAll()
                        .requestMatchers(GET, "/users/*/products").permitAll()
                        .requestMatchers(GET, "/posts").permitAll()
                        .requestMatchers(GET, "/posts/*").permitAll()
                        .requestMatchers(GET, "/posts/*/likes").permitAll()
                        .requestMatchers(GET, "/posts/*/reposts").permitAll()
                        .requestMatchers(GET, "/posts/*/comments").permitAll()
                        .requestMatchers(GET, "/posts/*/comments/*/likes").permitAll()
                        .requestMatchers(GET, "/products/*").permitAll()
                        .requestMatchers(GET, "/link-hubs").permitAll()
                        .requestMatchers(GET, "/link-hubs/**").permitAll()
                        .requestMatchers("/auth/google").permitAll()
                        .requestMatchers("/payments/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .sessionManagement(withDefaults())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        config.setAllowCredentials(true);
        config.addAllowedOrigin(applicationUrl);
        config.addAllowedOrigin(administrationUrl);
        config.addAllowedOrigin("capacitor://localhost");
        config.addAllowedOrigin("https://staging.soodora.com");
        config.addAllowedOrigin("https://app.soodora.com");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
