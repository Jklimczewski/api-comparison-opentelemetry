package pl.project.benchmark.common.service;

import org.springframework.stereotype.Service;
import pl.project.benchmark.common.dto.UserDto;
import pl.project.benchmark.common.exception.ResourceNotFoundException;
import pl.project.benchmark.common.mapper.UserMapper;
import pl.project.benchmark.common.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto getUser(Long id) {

        return userRepository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + id
                        ));
    }
}
