package ru.practicum.ewm.service.category.model;

import ru.practicum.ewm.service.category.dto.CategoryDto;

public class CategoryMapper {
    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto
                .builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toCategory(CategoryDto categoryDto) {
        return Category
                .builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

}
