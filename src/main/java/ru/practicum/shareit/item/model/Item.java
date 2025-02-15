package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Имя или логин не может быть пустым")
    @NotNull(message = "Поле 'name' не может быть пустым")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Описание не может быть пустым")
    @NotNull(message = "Поле 'description' не может быть пустым")
    private String description;

    @Column(nullable = false)
    @NotNull(message = "Поле 'available' не может быть пустым")
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}
