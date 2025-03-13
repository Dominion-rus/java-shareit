package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@Transactional
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User user;
    private User owner;
    private Item item;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User(null, "John Doe", "john@example.com"));
        owner = userRepository.save(new User(null, "Alice Smith", "alice@example.com"));
        item = itemRepository.save(new Item(null, "Laptop", "Powerful laptop", true, owner,
                null));
        bookingDto = new BookingDto(item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
    }

    @Test
    void createBooking_ShouldSaveToDatabase() {
        BookingResponseDto savedBooking = bookingService.createBooking(user.getId(), bookingDto);

        assertThat(savedBooking, notNullValue());
        assertThat(savedBooking.getItem().getId(), equalTo(item.getId()));
    }
}

