package ru.practicum.shareitgateway.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareitgateway.booking.dto.BookingResponseDto;


import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(message = "Имя вещи не должно быть пустым")
    private String name;

    @NotBlank(message = "Описание вещи не должно быть пустым")
    private String description;

    @NotNull(message = "Доступность вещи обязательна")
    private Boolean available;

    private Long requestId;

    private BookingResponseDto lastBooking;
    private BookingResponseDto nextBooking;
    private List<CommentDto> comments;
}
