package ru.practicum.shareitgateway.user.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPatchDto {
    private String name;

    @Email(message = "Некорректный email")
    private String email;
}
