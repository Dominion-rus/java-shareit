package ru.practicum.shareitgateway.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareitgateway.config.AppConfig;
import ru.practicum.shareitgateway.user.dto.UserDto;
import ru.practicum.shareitgateway.user.dto.UserPatchDto;


@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final RestTemplate restTemplate;
    private final AppConfig appConfig;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        String url = appConfig.getFullUrl("/users");
        log.info("Отправка запроса на сервер: {}", url);

        try {
            return restTemplate.postForEntity(url, userDto, Object.class);
        } catch (HttpClientErrorException e) {
            return handleHttpClientErrorException(e);
        } catch (Exception e) {
            return handleUnexpectedException(e, "POST");
        }
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

        try {
            return restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, Object.class);
        } catch (HttpClientErrorException e) {
            return handleHttpClientErrorException(e);
        } catch (Exception e) {
            return handleUnexpectedException(e, "PATCH");
        }
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

    private ResponseEntity<Object> handleHttpClientErrorException(HttpClientErrorException e) {
        log.warn("Ошибка от сервера: статус={}, сообщение={}", e.getStatusCode(), e.getResponseBodyAsString());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity
                .status(e.getStatusCode())
                .headers(responseHeaders)
                .body(e.getResponseBodyAsString());
    }

    private ResponseEntity<Object> handleUnexpectedException(Exception e, String method) {
        log.error("Неожиданная ошибка при {}-запросе: {}", method, e.getMessage(), e);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .headers(responseHeaders)
                .body("{\"success\":false,\"error\":\"Internal error\",\"message\":\"Произошла ошибка на шлюзе\"}");
    }
}
