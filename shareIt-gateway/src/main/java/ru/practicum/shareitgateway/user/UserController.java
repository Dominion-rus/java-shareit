package ru.practicum.shareitgateway.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareitgateway.config.AppConfig;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;


@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final RestTemplate restTemplate;
    private final AppConfig appConfig;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        String url = appConfig.getFullUrl("/users");
        log.info("Отправка запроса на сервер: {}", url);
        return restTemplate.postForEntity(url, userDto, Object.class);

    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        String url = appConfig.getFullUrl("/users/" + userId);
        log.info("Отправка запроса на сервер: {}", url);
        return restTemplate.getForEntity(url, Object.class);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        String url = appConfig.getFullUrl("/users/");
        log.info("Отправка запроса на сервер: {}", url);
        return restTemplate.getForEntity(url, Object.class);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserPatchDto userPatchDto) {

        String url = appConfig.getFullUrl("/users/" + userId);
        log.info("PATCH-запрос на сервер: {}, тело: {}", url, userPatchDto);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<UserPatchDto> requestEntity = new HttpEntity<>(userPatchDto, headers);

        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, Object.class);
        log.info("Ответ сервера: {}", response);
        return response;
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {

        String url = appConfig.getFullUrl("/users/" + userId);
        log.info("DELETE-запрос на сервер: {}", url);

        return restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                Object.class
        );
    }

}
