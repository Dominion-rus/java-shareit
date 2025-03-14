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
        } catch (HttpClientErrorException.Conflict e) {
            log.warn("Ошибка 409: {}", e.getResponseBodyAsString());
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .headers(responseHeaders)
                    .body(e.getResponseBodyAsString());
        } catch (HttpClientErrorException.BadRequest e) {
            log.warn("Ошибка 400: {}", e.getResponseBodyAsString());

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .headers(responseHeaders)
                    .body(e.getResponseBodyAsString());
        } catch (HttpClientErrorException e) {
            log.warn("Ошибка от сервера: статус={}, сообщение={}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Неожиданная ошибка при отправке POST-запроса: {}", e.getMessage(), e);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(responseHeaders)
                    .body("{\"success\":false,\"error\":\"Internal error\",\"message\":\"Произошла ошибка на шлюзе\"}");
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
        } catch (HttpClientErrorException.Conflict e) {
            log.warn("Ошибка 409: {}", e.getResponseBodyAsString());

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .headers(responseHeaders)
                    .body(e.getResponseBodyAsString());
        } catch (HttpClientErrorException e) {
            log.warn("Ошибка от сервера: статус={}, сообщение={}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Неожиданная ошибка при отправке PATCH-запроса: {}", e.getMessage(), e);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(responseHeaders)
                    .body("{\"success\":false,\"error\":\"Internal error\",\"message\":\"Произошла ошибка на шлюзе\"}");
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

}
