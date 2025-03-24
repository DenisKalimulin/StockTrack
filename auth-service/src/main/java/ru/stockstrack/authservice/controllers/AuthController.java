package ru.stockstrack.authservice.controllers;

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
public class AuthController {
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody
                                                        UserRegistrationDTO userRegistrationDTO) {
        UserResponseDTO userResponseDTO = authService.register(userRegistrationDTO);

        logger.info("Новый пользователь зарегистрирован");

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody
                                                      LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginResponseDTO = authService.login(loginRequestDTO);

        logger.info("Пользователь вошел в систему");

        return ResponseEntity.ok(loginResponseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser() {
        return ResponseEntity.ok("Выход выполнен. Удалите токен на клиенте.");
    }
}