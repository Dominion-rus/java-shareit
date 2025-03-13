package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
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
}

