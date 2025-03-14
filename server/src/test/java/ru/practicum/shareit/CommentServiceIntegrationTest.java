package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User user;
    private User anotherUser;
    private Item item;
    private CommentDto commentDto;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User(null, "John Doe", "john@example.com"));
        anotherUser = userRepository.save(new User(null, "Alice Smith", "alice@example.com"));
        item = itemRepository.save(new Item(null, "Laptop", "A powerful laptop", true, user, null));
        booking = bookingRepository.save(new Booking(null, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, user, BookingStatus.APPROVED));
        commentDto = new CommentDto(null, "Great item!", "John Doe", LocalDateTime.now());
    }

    @Test
    void addComment_ShouldSaveToDatabase() {
        CommentDto savedComment = commentService.addComment(user.getId(), item.getId(), commentDto);

        assertThat(savedComment, notNullValue());
        assertThat(savedComment.getText(), is("Great item!"));
        assertThat(savedComment.getAuthorName(), is("John Doe"));
    }

    @Test
    void addComment_ShouldThrowException_WhenUserDidNotBookItem() {
        Exception exception = assertThrows(ValidateException.class, () ->
                commentService.addComment(anotherUser.getId(), item.getId(), commentDto));

        assertThat(exception.getMessage(), is("Комментарий может оставить только арендатор"));
    }

    @Test
    void addComment_ShouldThrowException_WhenItemNotFound() {
        Exception exception = assertThrows(NotFoundException.class, () ->
                commentService.addComment(user.getId(), 999L, commentDto)); // Несуществующий товар

        assertThat(exception.getMessage(), is("Вещь не найдена"));
    }

    @Test
    void addComment_ShouldThrowException_WhenUserNotFound() {
        Exception exception = assertThrows(NotFoundException.class, () ->
                commentService.addComment(999L, item.getId(), commentDto)); // Несуществующий пользователь

        assertThat(exception.getMessage(), is("Пользователь не найден"));
    }

    @Test
    void addComment_ShouldSetCorrectTime() {
        CommentDto savedComment = commentService.addComment(user.getId(), item.getId(), commentDto);

        assertThat(savedComment.getCreated(), notNullValue());
        assertThat(savedComment.getCreated(), lessThanOrEqualTo(LocalDateTime.now()));
    }
}
