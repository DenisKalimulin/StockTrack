package ru.stockstrack.authservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.stockstrack.authservice.DTO.UserResponseDTO;
import ru.stockstrack.authservice.DTO.UserUpdateDTO;
import ru.stockstrack.authservice.service.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Методы для управления аккаунтом")
public class UserController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Operation(summary = "Обновить профиль пользователя", description = "Позволяет текущему пользователю обновить свой профиль")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Профиль успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {

        UserResponseDTO updatedUser = userService.updateUser(userUpdateDTO);
        logger.info("Профиль пользователя был успешно обновлен");

        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Получение профиля пользователя", description = "Возвращает профиль текущего авторизованного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Профиль пользователя получен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        logger.info("Запрос на получение профиля");
        UserResponseDTO userResponseDTO = userService.getCurrentUserProfile();
        return ResponseEntity.ok(userResponseDTO);
    }

    @Operation(summary = "Удалить пользователя", description = "Удаляет текущего пользователя из системы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Профиль успешно удален"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteUser() {
        userService.deleteCurrentUser();
        logger.info("Пользователь был успешно удален из системы");
        return ResponseEntity.ok("Профиль удален");
    }
}