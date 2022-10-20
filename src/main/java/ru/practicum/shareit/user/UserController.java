package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        return userService.getById(id);
    }

    @PostMapping
    public UserDto save(@Valid @RequestBody UserDto userDto) {
        return userService.add(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable long id) {
        return userService.update(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userService.delete(id);
    }
}
