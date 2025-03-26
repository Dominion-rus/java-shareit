package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.comment.service.CommentServiceImpl;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Item item;
    private Comment comment;
    private CommentDto commentDto;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User(1L, "John Doe", "john@example.com");
        item = new Item(1L, "Laptop", "A powerful laptop", true, user, null);
        comment = new Comment(1L, "Great item!", item, user, LocalDateTime.now());
        commentDto = new CommentDto(null, "Great item!", "John Doe", LocalDateTime.now());
        booking = new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, user,
                BookingStatus.APPROVED);
    }

    @Test
    void addComment_ShouldReturnCommentDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastCompletedBooking(
                anyLong(), anyLong(), eq(BookingStatus.APPROVED), Mockito.any(LocalDateTime.class)))
                .thenReturn(booking);

        when(commentRepository.save(Mockito.<Comment>any())).thenReturn(comment);

        CommentDto result = commentService.addComment(user.getId(), item.getId(), commentDto);

        assertThat(result, notNullValue());
        assertThat(result.getText(), is("Great item!"));
        assertThat(result.getAuthorName(), is("John Doe"));
    }

    @Test
    void addComment_WhenUserNotBookedItem_ShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastCompletedBooking(anyLong(), anyLong(), eq(BookingStatus.APPROVED),
                Mockito.any(LocalDateTime.class)))
                .thenReturn(null);

        ValidateException exception = assertThrows(
                ValidateException.class,
                () -> commentService.addComment(user.getId(), item.getId(), commentDto)
        );

        assertThat(exception.getMessage(), containsString("Комментарий может оставить только арендатор"));
    }

    @Test
    void addComment_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> commentService.addComment(user.getId(), item.getId(), commentDto)
        );

        assertThat(exception.getMessage(), containsString("Пользователь не найден"));
    }

    @Test
    void addComment_WhenItemNotFound_ShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> commentService.addComment(user.getId(), item.getId(), commentDto)
        );

        assertThat(exception.getMessage(), containsString("Вещь не найдена"));
    }

    @Test
    void addComment_WhenBookingNotApproved_ShouldThrowException() {
        booking.setStatus(BookingStatus.WAITING);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findLastCompletedBooking(anyLong(), anyLong(), eq(BookingStatus.APPROVED),
                Mockito.any(LocalDateTime.class)))
                .thenReturn(null);

        ValidateException exception = assertThrows(
                ValidateException.class,
                () -> commentService.addComment(user.getId(), item.getId(), commentDto)
        );

        assertThat(exception.getMessage(), containsString("Комментарий может оставить только арендатор"));
    }
}

