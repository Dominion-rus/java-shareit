package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        item = itemRepository.save(new Item(null, "Laptop", "Powerful laptop", true, owner, null));

        bookingDto = new BookingDto(item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
    }

    @Test
    void createBooking_ShouldSaveToDatabase() {
        BookingResponseDto savedBooking = bookingService.createBooking(user.getId(), bookingDto);

        assertThat(savedBooking, notNullValue());
        assertThat(savedBooking.getItem().getId(), equalTo(item.getId()));
        assertThat(savedBooking.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void createBooking_ShouldThrowException_WhenEndDateBeforeStartDate() {
        BookingDto invalidBooking = new BookingDto(item.getId(), LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1));

        Exception exception = assertThrows(ValidateException.class, () ->
                bookingService.createBooking(user.getId(), invalidBooking));

        assertThat(exception.getMessage(), containsString("Дата начала бронирования должна " +
                "быть раньше даты окончания"));
    }

    @Test
    void updateBookingStatus_ShouldApproveBooking() {
        BookingResponseDto savedBooking = bookingService.createBooking(user.getId(), bookingDto);
        BookingResponseDto updatedBooking = bookingService.updateBookingStatus(owner.getId(), savedBooking.getId(), true);

        assertThat(updatedBooking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void updateBookingStatus_ShouldRejectBooking() {
        BookingResponseDto savedBooking = bookingService.createBooking(user.getId(), bookingDto);
        BookingResponseDto updatedBooking = bookingService.updateBookingStatus(owner.getId(), savedBooking.getId(), false);

        assertThat(updatedBooking.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void updateBookingStatus_ShouldThrowException_WhenNotOwner() {
        BookingResponseDto savedBooking = bookingService.createBooking(user.getId(), bookingDto);

        Exception exception = assertThrows(AccessDeniedException.class, () ->
                bookingService.updateBookingStatus(user.getId(), savedBooking.getId(), true));

        assertThat(exception.getMessage(), containsString("Подтвердить бронирование может только владелец"));
    }

    @Test
    void getBooking_ShouldReturnBooking_WhenBookerOrOwner() {
        BookingResponseDto savedBooking = bookingService.createBooking(user.getId(), bookingDto);

        BookingResponseDto foundBookingAsBooker = bookingService.getBooking(user.getId(), savedBooking.getId());
        BookingResponseDto foundBookingAsOwner = bookingService.getBooking(owner.getId(), savedBooking.getId());

        assertThat(foundBookingAsBooker, notNullValue());
        assertThat(foundBookingAsOwner, notNullValue());
    }

    @Test
    void getBooking_ShouldThrowException_WhenNotOwnerOrBooker() {
        User otherUser = userRepository.save(new User(null, "Random User", "random@example.com"));
        BookingResponseDto savedBooking = bookingService.createBooking(user.getId(), bookingDto);

        Exception exception = assertThrows(AccessDeniedException.class, () ->
                bookingService.getBooking(otherUser.getId(), savedBooking.getId()));

        assertThat(exception.getMessage(), containsString("Доступ запрещён"));
    }

    @Test
    void getUserBookings_ShouldReturnAllBookings() {
        BookingResponseDto savedBooking = bookingService.createBooking(user.getId(), bookingDto);

        List<BookingResponseDto> bookings = bookingService.getUserBookings(user.getId(), "ALL");

        assertThat(bookings, not(empty()));
        assertThat(bookings.size(), is(1));
        assertThat(bookings.get(0).getId(), is(savedBooking.getId()));
    }

    @Test
    void getUserBookings_ShouldReturnFutureBookings() {
        BookingResponseDto savedBooking = bookingService.createBooking(user.getId(), bookingDto);

        List<BookingResponseDto> bookings = bookingService.getUserBookings(user.getId(), "FUTURE");

        assertThat(bookings, not(empty()));
        assertThat(bookings.size(), is(1));
        assertThat(bookings.get(0).getId(), is(savedBooking.getId()));
    }

}
