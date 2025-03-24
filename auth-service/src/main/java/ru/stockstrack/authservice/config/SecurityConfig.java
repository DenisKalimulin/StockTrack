package ru.stockstrack.authservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     Менеджер аутентификации (используется в AuthService)

    DaoAuthenticationProvider - это стандартный провайдер Spring Security, который:
    1) Сам вызывает UserDetailsService.loadUserByUsername(...)
    2) Сравнивает введённый пароль с сохранённым (используя BCryptPasswordEncoder)

    userDetailsService - это сервис, который реализовывается, чтобы Spring могу найти
    пользователя по login или email

    ProviderManager - оболочка вокруг DaoAuthenticationProvider
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       BCryptPasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    /*
     Конфигурация безопасности
     Без токена можно посещать страницы -/auth/login и /auth/register
     Остальные страницы только после аутентификации
     httpBasic - временная заглушка, чтобы модно было тестировать через Postman, позже заменить на JWT-фильтр
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register", "/auth/login").permitAll()  // открытые
                        .anyRequest().authenticated()                                 // остальные — защищённые
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults()); // временно, можно убрать после тестов

        return http.build();
    }
}