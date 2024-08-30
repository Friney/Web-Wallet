package ru.web.wallet.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.web.wallet.api.dto.UserCreateRequest;
import ru.web.wallet.api.dto.UserDto;
import ru.web.wallet.api.dto.UserPatchRequest;
import ru.web.wallet.core.entity.User;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface UserMapper {
    UserDto map(User user);

    List<UserDto> map(List<User> users);

    @Mapping(target = "id", ignore = true)
    User map(UserCreateRequest userPatchRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "password", ignore = true)
    User map(@MappingTarget User user, UserPatchRequest userPatchRequest);
}
