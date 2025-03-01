package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByItemOwnerId(Long ownerId);

    // Найти все бронирования пользователя (сортировка по дате начала)
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    // Найти все бронирования владельца вещи (сортировка по дате начала)
    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    // Найти текущие бронирования пользователя
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start <= :now AND b.end >= :now")
    List<Booking> findCurrentBookings(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    // Найти прошедшие бронирования пользователя
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end < :now")
    List<Booking> findPastBookings(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    // Найти будущие бронирования пользователя
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > :now")
    List<Booking> findFutureBookings(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    // Если нужно найти все бронирования, связанные с владельцем вещи
    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    // Найти последнее бронирование для предмета
    Booking findFirstByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime now);

    // Найти следующее бронирование для предмета
    Booking findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);

    List<Booking> findByItemIdOrderByStartDesc(Long itemId);

    Booking findFirstByItemIdAndBookerIdAndStatusAndEndBeforeOrderByStartDesc(
            Long itemId, Long bookerId, BookingStatus status, LocalDateTime end);

}

