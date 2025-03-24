package ru.stockstrack.authservice.service;

import ru.stockstrack.authservice.DTO.UserResponseDTO;
import ru.stockstrack.authservice.DTO.UserUpdateDTO;

public interface UserService {
    UserResponseDTO updateUser(UserUpdateDTO userUpdateDTO);
    UserResponseDTO getCurrentUserProfile();
    void deleteCurrentUser();
}