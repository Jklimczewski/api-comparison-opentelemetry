package pl.project.benchmark.rest.controller;

import org.springframework.web.bind.annotation.*;
import pl.project.benchmark.common.dto.UserDto;
import pl.project.benchmark.common.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserDto getUser(
            @PathVariable Long id
    ) {
        return userService.getUser(id);
    }
}
