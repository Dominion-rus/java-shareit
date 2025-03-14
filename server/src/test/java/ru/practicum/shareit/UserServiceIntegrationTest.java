package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(null, "John Doe", "john@example.com");
    }

    @Test
    void createUser_ShouldSaveToDatabase() {
        UserDto savedUser = userService.createUser(userDto);

        assertThat(savedUser, notNullValue());
        assertThat(savedUser.getId(), notNullValue());
        assertThat(savedUser.getName(), is("John Doe"));
        assertThat(savedUser.getEmail(), is("john@example.com"));
    }

    @Test
    void getAllUsers_ShouldReturnList() {
        userService.createUser(userDto);
        List<UserDto> users = userService.getAllUsers();

        assertThat(users, hasSize(1));
        assertThat(users.get(0).getName(), is("John Doe"));
    }

    @Test
    void getUserById_ShouldReturnUser() {
        UserDto savedUser = userService.createUser(userDto);

        UserDto foundUser = userService.getUserById(savedUser.getId());

        assertThat(foundUser, notNullValue());
        assertThat(foundUser.getId(), is(savedUser.getId()));
        assertThat(foundUser.getName(), is("John Doe"));
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        Exception exception = assertThrows(NotFoundException.class, () ->
                userService.getUserById(999L));

        assertThat(exception.getMessage(), containsString("Пользователь не найден"));
    }

    @Test
    void updateUser_ShouldModifyUserData() {
        UserDto savedUser = userService.createUser(userDto);
        UserPatchDto userPatchDto = new UserPatchDto("Updated Name", "updated@example.com");

        UserDto updatedUser = userService.updateUser(savedUser.getId(), userPatchDto);

        assertThat(updatedUser.getName(), is("Updated Name"));
        assertThat(updatedUser.getEmail(), is("updated@example.com"));
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        UserPatchDto userPatchDto = new UserPatchDto("Updated Name", "updated@example.com");

        Exception exception = assertThrows(NotFoundException.class, () ->
                userService.updateUser(999L, userPatchDto));

        assertThat(exception.getMessage(), containsString("Пользователь не найден"));
    }

    @Test
    void deleteUser_ShouldRemoveUserFromDatabase() {
        UserDto savedUser = userService.createUser(userDto);
        userService.deleteUser(savedUser.getId());

        Exception exception = assertThrows(NotFoundException.class, () ->
                userService.getUserById(savedUser.getId()));

        assertThat(exception.getMessage(), containsString("Пользователь не найден"));
    }
}
