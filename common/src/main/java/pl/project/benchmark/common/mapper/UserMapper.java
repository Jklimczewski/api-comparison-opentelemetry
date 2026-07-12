package pl.project.benchmark.common.mapper;

import pl.project.benchmark.common.dto.UserDto;
import pl.project.benchmark.common.persistence.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
