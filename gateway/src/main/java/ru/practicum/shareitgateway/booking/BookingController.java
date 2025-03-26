package ru.practicum.shareitgateway.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareitgateway.booking.dto.BookingDto;
import ru.practicum.shareitgateway.config.AppConfig;

@RestController
@RequestMapping("/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final RestTemplate restTemplate;
    private final AppConfig appConfig;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody BookingDto bookingDto) {

        String url = appConfig.getFullUrl("/bookings");
        log.info("POST-запрос на сервер: {}, тело: {}", url, bookingDto);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<BookingDto> requestEntity = new HttpEntity<>(bookingDto, headers);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);
            log.info("Ответ от сервера: статус={}, тело={}", response.getStatusCode(), response.getBody());
            return response;
        } catch (HttpClientErrorException e) {
            log.warn("Ошибка от сервера: статус={}, сообщение={}", e.getStatusCode(), e.getResponseBodyAsString());

            // Возвращаем статус сервера (например, 404)
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Неожиданная ошибка при отправке POST-запроса: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка на шлюзе");
        }
    }


    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved) {

        String url = appConfig.getFullUrl("/bookings/" + bookingId + "?approved=" + approved);
        log.info("PATCH-запрос на сервер: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(ownerId));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, Object.class);
            log.info("Ответ от сервера: статус={}, тело={}", response.getStatusCode(), response.getBody());
            return response;
        } catch (HttpClientErrorException.Forbidden e) {
            log.warn("Ошибка 403: {}", e.getResponseBodyAsString());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getResponseBodyAsString());
        } catch (HttpClientErrorException e) {
            log.warn("Ошибка от сервера: статус={}, сообщение={}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Неожиданная ошибка при PATCH-запросе: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка на шлюзе");
        }
    }


    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {

        String url = appConfig.getFullUrl("/bookings/" + bookingId);
        log.info("GET-запрос на сервер: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, Object.class);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state) {

        String url = appConfig.getFullUrl("/bookings?state=" + state);
        log.info("GET-запрос на сервер: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, Object.class);
    }


    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state) {

        String url = appConfig.getFullUrl("/bookings/owner?state=" + state);
        log.info("GET-запрос на сервер: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Object.class);
            log.info("Ответ от сервера: статус={}, тело={}", response.getStatusCode(), response.getBody());
            return response;

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Ошибка 404: {}", e.getResponseBodyAsString());

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .headers(responseHeaders)
                    .body(e.getResponseBodyAsString());

        } catch (HttpClientErrorException e) {
            log.warn("Ошибка от сервера: статус={}, сообщение={}", e.getStatusCode(), e.getResponseBodyAsString());

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);

            return ResponseEntity
                    .status(e.getStatusCode())
                    .headers(responseHeaders)
                    .body(e.getResponseBodyAsString());

        } catch (Exception e) {
            log.error("Неожиданная ошибка при GET-запросе: {}", e.getMessage(), e);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(responseHeaders)
                    .body("{\"error\":\"Произошла ошибка на шлюзе\"}");
        }
    }


}


