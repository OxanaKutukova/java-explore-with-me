package ru.practicum.ewm.service.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CategoryDto {

    private Long id;

    @NotBlank
    private String name;
}
