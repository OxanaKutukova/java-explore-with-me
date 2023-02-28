package ru.practicum.ewm.service.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CategoryDto {

    private Long id;

    @NotBlank
    @Size(min = 3, max = 255)
    private String name;
}
