package ru.practicum.ewm.service.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode (of = "id")

public class UserDto {
    private Long id;

    @NotBlank
    @Size(min = 1, max = 100)
    private String name;

    @Email
    @NotBlank
    @Size(min = 1, max = 100)
    private String email;
}