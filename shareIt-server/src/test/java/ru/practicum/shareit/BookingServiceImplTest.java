package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "John Doe", "john@example.com");
        owner = new User(2L, "Alice Smith", "alice@example.com");
        item = new Item(1L, "Laptop", "Powerful laptop", true, owner, null);
        booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, user,
                BookingStatus.WAITING);
        bookingDto = new BookingDto(item.getId(), booking.getStart(), booking.getEnd());
    }

    @Test
    void createBooking_ShouldReturnBookingResponseDto() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(Mockito.<Booking>any())).thenReturn(booking);

        BookingResponseDto result = bookingService.createBooking(user.getId(), bookingDto);

        assertThat(result, notNullValue());
        assertThat(result.getItem().getId(), is(item.getId()));
        assertThat(result.getStatus(), is(BookingStatus.WAITING));
    }

    @Test
    void createBooking_WhenItemNotAvailable_ShouldThrowException() {
        item.setAvailable(false);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ValidateException exception = assertThrows(
                ValidateException.class,
                () -> bookingService.createBooking(user.getId(), bookingDto)
        );

        assertThat(exception.getMessage(), containsString("Вещь недоступна для бронирования"));
    }

    @Test
    void updateBookingStatus_ShouldApproveBooking() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(Mockito.<Booking>any())).thenReturn(booking);

        BookingResponseDto result = bookingService.updateBookingStatus(owner.getId(), booking.getId(), true);

        assertThat(result.getStatus(), is(BookingStatus.APPROVED));
    }

    @Test
    void updateBookingStatus_ShouldRejectBooking() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(Mockito.<Booking>any())).thenReturn(booking);

        BookingResponseDto result = bookingService.updateBookingStatus(owner.getId(), booking.getId(), false);

        assertThat(result.getStatus(), is(BookingStatus.REJECTED));
    }

    @Test
    void updateBookingStatus_ShouldThrowException_WhenNotOwner() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> bookingService.updateBookingStatus(3L, booking.getId(), true)
        );

        assertThat(exception.getMessage(), containsString("Подтвердить бронирование может только владелец"));
    }

    @Test
    void getBooking_ShouldReturnBooking_WhenOwnerOrBooker() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingResponseDto resultAsBooker = bookingService.getBooking(user.getId(), booking.getId());
        BookingResponseDto resultAsOwner = bookingService.getBooking(owner.getId(), booking.getId());

        assertThat(resultAsBooker, notNullValue());
        assertThat(resultAsOwner, notNullValue());
    }

    @Test
    void getBooking_ShouldThrowException_WhenUnauthorized() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> bookingService.getBooking(3L, booking.getId())
        );

        assertThat(exception.getMessage(), containsString("Доступ запрещён"));
    }

    @Test
    void getUserBookings_ShouldReturnListOfBookings() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdOrderByStartDesc(user.getId()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getUserBookings(user.getId(), "ALL");

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getStatus(), is(BookingStatus.WAITING));
    }

    @Test
    void getUserBookings_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getUserBookings(user.getId(), "ALL")
        );

        assertThat(exception.getMessage(), containsString("Пользователь не найден"));
    }

    @Test
    void getOwnerBookings_ShouldThrowException_WhenUserHasNoItems() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(owner.getId())).thenReturn(List.of());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getOwnerBookings(owner.getId(), "ALL")
        );

        assertThat(exception.getMessage(), containsString("У пользователя нет вещей, доступ запрещен"));
    }

    @Test
    void getOwnerBookings_ShouldReturnListOfBookings() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(owner.getId())).thenReturn(List.of(item));
        when(bookingRepository.findByItemOwnerAndState(List.of(item), "ALL")).thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getOwnerBookings(owner.getId(), "ALL");

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getStatus(), is(BookingStatus.WAITING));
    }


}

