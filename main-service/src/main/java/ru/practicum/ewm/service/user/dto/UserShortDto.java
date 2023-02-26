package ru.practicum.ewm.service.user.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode (of = "id")
public class UserShortDto {

    @NotBlank
    private Long id;

    @NotBlank
    private String name;
}
