package ru.stockstrack.authservice.service;

import ru.stockstrack.authservice.DTO.LoginRequestDTO;
import ru.stockstrack.authservice.DTO.LoginResponseDTO;
import ru.stockstrack.authservice.DTO.UserRegistrationDTO;
import ru.stockstrack.authservice.DTO.UserResponseDTO;

public interface AuthService {
    UserResponseDTO register(UserRegistrationDTO userRegistrationDTO);
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}