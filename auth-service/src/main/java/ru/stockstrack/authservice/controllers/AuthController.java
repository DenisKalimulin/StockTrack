package ru.stockstrack.authservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.stockstrack.authservice.DTO.LoginRequestDTO;
import ru.stockstrack.authservice.DTO.LoginResponseDTO;
import ru.stockstrack.authservice.DTO.UserRegistrationDTO;
import ru.stockstrack.authservice.DTO.UserResponseDTO;
import ru.stockstrack.authservice.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Методы для регистрации, входа в систему и выхода из нее")
public class AuthController {
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Operation(summary = "Регистрация нового пользователя", description = "Создает нового пользователя в системе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации запроса")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody
                                                        UserRegistrationDTO userRegistrationDTO) {
        UserResponseDTO userResponseDTO = authService.register(userRegistrationDTO);

        logger.info("Новый пользователь зарегистрирован");

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

    @Operation(summary = "Вход в систему",
            description = "Аутентифицирует пользователя и возвращает JWT-токен для доступа к защищённым ресурсам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход в систему"),
            @ApiResponse(responseCode = "401", description = "Неверный логин или пароль")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody
                                                      LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginResponseDTO = authService.login(loginRequestDTO);

        logger.info("Пользователь вошел в систему");

        return ResponseEntity.ok(loginResponseDTO);
    }

    @Operation(summary = "Выход из системы", description = "Завершает сеанс пользователя на клиентской стороне. " +
            "Сервер не хранит токены.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный выход из системы")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser() {
        return ResponseEntity.ok("Выход выполнен. Удалите токен на клиенте.");
    }
}