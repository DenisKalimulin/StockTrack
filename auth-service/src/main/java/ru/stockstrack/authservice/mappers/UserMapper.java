package ru.stockstrack.authservice.mappers;

import org.mapstruct.*;
import ru.stockstrack.authservice.DTO.*;
import ru.stockstrack.authservice.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Регистрация: DTO → Entity
    User toEntity(UserRegistrationDTO dto);

    // Просмотр профиля: Entity → DTO
    UserResponseDTO toResponse(User user);

    // Обновление данных: частично применяет поля из DTO в Entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserUpdateDTO dto, @MappingTarget User user);
}