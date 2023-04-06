package ru.practicum.ewm.service.user.dto;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode (of = "id")
public class UserShortDto {

    private Long id;

    private String name;
}
