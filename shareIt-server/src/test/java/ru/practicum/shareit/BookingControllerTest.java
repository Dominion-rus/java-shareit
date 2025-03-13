package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@ExtendWith(SpringExtension.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    void createBooking_ShouldReturn200() throws Exception {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        BookingResponseDto responseDto = new BookingResponseDto(1L, bookingDto.getStart(), bookingDto.getEnd(),
                null, null, BookingStatus.WAITING);

        when(bookingService.createBooking(anyLong(), any(BookingDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void updateBookingStatus_ShouldReturn200() throws Exception {
        BookingResponseDto responseDto = new BookingResponseDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), null, null, BookingStatus.APPROVED);

        when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void getBooking_ShouldReturn200() throws Exception {
        BookingResponseDto responseDto = new BookingResponseDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), null, null, BookingStatus.APPROVED);

        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(responseDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)));
    }

    @Test
    void getBooking_ShouldReturn404_WhenNotFound() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Бронирование не найдено"));

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBookingStatus_ShouldReturn403_WhenNotOwner() throws Exception {
        when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new AccessDeniedException("Access denied"));

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", "2"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserBookings_ShouldReturn200() throws Exception {
        List<BookingResponseDto> bookings = List.of(
                new BookingResponseDto(1L, LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2), null, null, BookingStatus.APPROVED)
        );

        when(bookingService.getUserBookings(anyLong(), anyString())).thenReturn(bookings);

        mockMvc.perform(get("/bookings?state=ALL")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    void getOwnerBookings_ShouldReturn200() throws Exception {
        List<BookingResponseDto> bookings = List.of(
                new BookingResponseDto(2L, LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2), null, null, BookingStatus.WAITING)
        );

        when(bookingService.getOwnerBookings(anyLong(), anyString())).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner?state=ALL")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));
    }

}
