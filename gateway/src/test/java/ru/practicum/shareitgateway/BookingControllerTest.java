package ru.practicum.shareitgateway;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareitgateway.booking.BookingController;
import ru.practicum.shareitgateway.config.AppConfig;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void updateBookingStatus_ShouldReturnResponseEntity() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        when(appConfig.getFullUrl(anyString())).thenReturn("http://localhost:8080/bookings/1?approved=true");
        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(new ResponseEntity<>("{}", HttpStatus.OK));

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void updateBookingStatus_ShouldHandleError() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        when(appConfig.getFullUrl(anyString())).thenReturn("http://localhost:8080/bookings/1?approved=true");
        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN, "Forbidden"));

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getBooking_ShouldReturnResponseEntity() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        when(appConfig.getFullUrl(anyString())).thenReturn("http://localhost:8080/bookings/1");
        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(new ResponseEntity<>("{}", HttpStatus.OK));

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }


    @Test
    void getUserBookings_ShouldReturnResponseEntity() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        when(appConfig.getFullUrl(anyString())).thenReturn("http://localhost:8080/bookings?state=ALL");
        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(new ResponseEntity<>("{}", HttpStatus.OK));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsForOwner_ShouldReturnResponseEntity() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        when(appConfig.getFullUrl(anyString())).thenReturn("http://localhost:8080/bookings/owner?state=ALL");
        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(new ResponseEntity<>("{}", HttpStatus.OK));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsForOwner_ShouldHandleNotFound() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        when(appConfig.getFullUrl(anyString())).thenReturn("http://localhost:8080/bookings/owner?state=ALL");
        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found"));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL"))
                .andExpect(status().isNotFound());
    }
}
