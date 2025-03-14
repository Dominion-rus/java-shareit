package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;


public class BookingMapper {
    public static BookingResponseDto toBookingDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDto(booking.getItem(), null, null, null),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus()
        );
    }

}
