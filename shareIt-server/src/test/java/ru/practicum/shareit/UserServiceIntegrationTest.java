package ru.practicum.shareit;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


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
    }

    @Test
    void getAllUsers_ShouldReturnList() {
        userService.createUser(userDto);
        List<UserDto> users = userService.getAllUsers();

        assertThat(users, hasSize(1));
        assertThat(users.get(0).getName(), is("John Doe"));
    }
}

