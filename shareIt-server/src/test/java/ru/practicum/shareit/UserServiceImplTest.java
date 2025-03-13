package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private UserPatchDto userPatchDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "John Doe", "john@example.com");
        userDto = new UserDto(1L, "John Doe", "john@example.com");
        userPatchDto = new UserPatchDto("Updated Name", "updated@example.com");
    }

    @Test
    void createUser_ShouldReturnUserDto() {
        when(userRepository.save(Mockito.<User>any())).thenReturn(user);

        UserDto result = userService.createUser(userDto);

        assertThat(result, notNullValue());
        assertThat(result.getId(), is(user.getId()));
        assertThat(result.getName(), is(user.getName()));
    }

    @Test
    void getUserById_ShouldReturnUserDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(user.getId());

        assertThat(result, notNullValue());
        assertThat(result.getId(), is(user.getId()));
        assertThat(result.getEmail(), is(user.getEmail()));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUserDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(Mockito.<User>any())).thenReturn(user);

        UserDto result = userService.updateUser(user.getId(), userPatchDto);

        assertThat(result, notNullValue());
        assertThat(result.getName(), is(userPatchDto.getName()));
        assertThat(result.getEmail(), is(userPatchDto.getEmail()));
    }

    @Test
    void deleteUser_ShouldCallRepositoryDeleteById() {
        userService.deleteUser(user.getId());

        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test
    void getUserById_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(999L)
        );

        assertThat(exception.getMessage(), is("Пользователь не найден"));
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> result = userService.getAllUsers();

        assertThat(result, is(empty()));
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        List<User> users = List.of(
                new User(1L, "John Doe", "john@example.com"),
                new User(2L, "Jane Doe", "jane@example.com")
        );

        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> result = userService.getAllUsers();

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getName(), is("John Doe"));
        assertThat(result.get(1).getName(), is("Jane Doe"));
    }

    @Test
    void updateUser_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(999L, userPatchDto)
        );

        assertThat(exception.getMessage(), is("Пользователь не найден"));
    }


}

