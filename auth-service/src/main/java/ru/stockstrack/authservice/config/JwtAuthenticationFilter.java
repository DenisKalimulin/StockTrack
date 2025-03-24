package ru.stockstrack.authservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.stockstrack.authservice.serviceImpl.CustomUserDetailsService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization"); // Получаем заголовок авторизации

        String token = null;
        String login = null;

        if(authHeader != null && authHeader.startsWith("Bearer ")) { // Проверка, что заголовок начинается с Bearer
            token = authHeader.substring(7);
            if(jwtTokenProvider.validateToken(token)) { // Проверка, что токен не испорчен
                login = jwtTokenProvider.getLoginFromToken(token); // Достаём login из sub внутри токена — это имя пользователя
            }
        }
        if(login != null && SecurityContextHolder.getContext().getAuthentication() == null) { // Проверяем, не авторизован ли уже пользователь
            var userDetails = userDetailsService.loadUserByUsername(login); // Загружаем пользователя из БД
            var authToken = new UsernamePasswordAuthenticationToken( // Создаём объект Authentication, чтобы Spring понимал, кто пользователь
                    userDetails, // Кто пользователь
                    null, // Пароль не нужен
                    userDetails.getAuthorities() // Роли, если есть
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken); // Устанавливаем авторизованного пользователя в контекст безопасности Spring
        }
        filterChain.doFilter(request, response); // передаём управление дальше по цепочке фильтров (иначе запрос "зависнет").
    }
}
