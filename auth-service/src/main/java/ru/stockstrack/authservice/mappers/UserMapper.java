package ru.stockstrack.authservice.mappers;

import org.mapstruct.*;
import ru.stockstrack.authservice.DTO.*;
import ru.stockstrack.authservice.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Регистрация: DTO → Entity
    @Mapping(target = "login", expression = "java(dto.getLogin().toLowerCase())")
    @Mapping(target = "email", expression = "java(dto.getEmail().toLowerCase())")
    User toEntity(UserRegistrationDTO dto);

    // Просмотр профиля: Entity → DTO
    UserResponseDTO toResponse(User user);

    // Обновление данных: частично применяет поля из DTO в Entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "login", expression = "java(dto.getLogin().toLowerCase())")
    @Mapping(target = "email", expression = "java(dto.getEmail().toLowerCase())")
    void updateUserFromDto(UserUpdateDTO dto, @MappingTarget User user);
}
