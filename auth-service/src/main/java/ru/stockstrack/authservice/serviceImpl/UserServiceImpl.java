package ru.stockstrack.authservice.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.stockstrack.authservice.DTO.UserResponseDTO;
import ru.stockstrack.authservice.DTO.UserUpdateDTO;
import ru.stockstrack.authservice.exception.UserAlreadyExistsException;
import ru.stockstrack.authservice.exception.UserNotFoundException;
import ru.stockstrack.authservice.mappers.UserMapper;
import ru.stockstrack.authservice.model.User;
import ru.stockstrack.authservice.repository.UserRepository;
import ru.stockstrack.authservice.security.SecurityUtils;
import ru.stockstrack.authservice.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Transactional
    @Override
    public UserResponseDTO updateUser(UserUpdateDTO userUpdateDTO) {
        String userLogin = SecurityUtils.getCurrentLogin();
        logger.info("Обновление профиля пользователя");

        User user = findUserByLogin(userLogin);

        checkUserUniqueness(userUpdateDTO.getLogin(), userUpdateDTO.getEmail());

        // Обновляем только непустые поля
        userMapper.updateUserFromDto(userUpdateDTO, user);
        if(userUpdateDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }

        User savedUser = userRepository.save(user);
        logger.info("Профиль пользователя обновлен");

        return userMapper.toResponse(savedUser);
    }

    @Transactional
    @Override
    public UserResponseDTO getCurrentUserProfile() {
        String userLogin = SecurityUtils.getCurrentLogin();
        logger.info("Получение профиля текущего пользователя");
        User user = findUserByLogin(userLogin);

        return userMapper.toResponse(user);
    }

    @Transactional
    @Override
    public void deleteCurrentUser() {
        String userLogin = SecurityUtils.getCurrentLogin();
        logger.info("Удаление пользователя");

        User user = findUserByLogin(userLogin);
        userRepository.delete(user);

        logger.info("Пользователь удален");
    }

    /**
     * Получение пользователя по логину.
     *
     * @param login логин пользователя.
     * @return найденный пользователь.
     * @throws UserNotFoundException если пользователь с таким логином не найден.
     */
    private User findUserByLogin(String login) {
        return userRepository.findByLogin(login.toLowerCase())
                .orElseThrow(() -> {
                    logger.warn("Пользователь не найден");
                    return new UserNotFoundException("Пользователь с таким логином " + login + " не найден");
                });
    }

    /**
     * Проверка уникальности логина и email.
     * Если логин или email уже заняты, выбрасывает исключение.
     *
     * @param login логин пользователя.
     * @param email email пользователя.
     * @throws UserAlreadyExistsException если логин или email уже заняты.
     */
    private void checkUserUniqueness(String login, String email) {
        if (login != null && userRepository.findByLogin(login.toLowerCase()).isPresent()) {
            logger.warn("Попытка регистрации с уже существующим логином");
            throw new UserAlreadyExistsException("Логин уже используется");
        }
        if (email != null && userRepository.findByEmail(email.toLowerCase()).isPresent()) {
            logger.warn("Попытка регистрации с уже существующим email");
            throw new UserAlreadyExistsException("Email уже используется");
        }
    }
}