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
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(user.getId())
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
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.getAllUsers();

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getName(), is("John Doe"));
    }

    @Test
    void deleteUser_ShouldCallRepositoryDeleteById() {
        userService.deleteUser(user.getId());

        verify(userRepository, times(1)).deleteById(user.getId());
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
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(user.getId(), userPatchDto)
        );

        assertThat(exception.getMessage(), is("Пользователь не найден"));
    }

    @Test
    void updateUser_ShouldUpdateOnlyName_WhenEmailIsNull() {
        UserPatchDto patchDto = new UserPatchDto("New Name", null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(Mockito.<User>any())).thenReturn(user);

        UserDto result = userService.updateUser(user.getId(), patchDto);

        assertThat(result, notNullValue());
        assertThat(result.getName(), is("New Name"));
        assertThat(result.getEmail(), is("john@example.com"));
    }

    @Test
    void updateUser_ShouldUpdateOnlyEmail_WhenNameIsNull() {
        UserPatchDto patchDto = new UserPatchDto(null, "new@example.com");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(Mockito.<User>any())).thenReturn(user);

        UserDto result = userService.updateUser(user.getId(), patchDto);

        assertThat(result, notNullValue());
        assertThat(result.getName(), is("John Doe"));
        assertThat(result.getEmail(), is("new@example.com"));
    }

    @Test
    void updateUser_ShouldNotChangeAnything_WhenAllFieldsAreNull() {
        UserPatchDto patchDto = new UserPatchDto(null, null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(Mockito.<User>any())).thenReturn(user);

        UserDto result = userService.updateUser(user.getId(), patchDto);

        assertThat(result, notNullValue());
        assertThat(result.getName(), is("John Doe"));
        assertThat(result.getEmail(), is("john@example.com"));
    }
}
