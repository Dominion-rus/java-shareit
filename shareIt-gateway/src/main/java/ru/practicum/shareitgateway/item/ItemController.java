package ru.practicum.shareitgateway.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareitgateway.config.AppConfig;
import ru.practicum.shareitgateway.item.dto.ItemDto;
import ru.practicum.shareitgateway.item.dto.ItemPatchDto;
import ru.practicum.shareitgateway.item.dto.CommentDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final RestTemplate restTemplate;
    private final AppConfig appConfig;

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemDto itemDto) {

        String url = appConfig.getFullUrl("/items");
        log.info("POST-запрос на сервер: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<ItemDto> requestEntity = new HttpEntity<>(itemDto, headers);

        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody ItemPatchDto itemPatchDto) {

        String url = appConfig.getFullUrl("/items/" + itemId);
        log.info("PATCH-запрос на сервер: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<ItemPatchDto> requestEntity = new HttpEntity<>(itemPatchDto, headers);

        return restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, Object.class);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {

        String url = appConfig.getFullUrl("/items/" + itemId);
        log.info("GET-запрос на сервер: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, Object.class);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(
            @RequestHeader("X-Sharer-User-Id") Long userId) {

        String url = appConfig.getFullUrl("/items");
        log.info("GET-запрос на сервер: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, Object.class);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {

        String url = appConfig.getFullUrl("/items/search?text=" + text);
        log.info("GET-запрос на сервер: {}", url);

        return restTemplate.getForEntity(url, Object.class);
    }


    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentDto) {

        String url = appConfig.getFullUrl("/items/" + itemId + "/comment");
        log.info("POST-запрос на сервер: {}, тело: {}", url, commentDto);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<CommentDto> requestEntity = new HttpEntity<>(commentDto, headers);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);
            log.info("Ответ от сервера: статус={}, тело={}", response.getStatusCode(), response.getBody());
            return response;
        } catch (HttpClientErrorException.BadRequest e) {
            log.warn("Ошибка 400: {}", e.getResponseBodyAsString());

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .headers(responseHeaders)
                    .body(e.getResponseBodyAsString()); // Передаем тело ошибки без изменений
        } catch (HttpClientErrorException e) {
            log.warn("Ошибка от сервера: статус={}, сообщение={}", e.getStatusCode(), e.getResponseBodyAsString());

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);

            return ResponseEntity
                    .status(e.getStatusCode())
                    .headers(responseHeaders)
                    .body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Неожиданная ошибка при POST-запросе: {}", e.getMessage(), e);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(responseHeaders)
                    .body("{\"error\":\"Произошла ошибка на шлюзе\"}");
        }
    }

}

