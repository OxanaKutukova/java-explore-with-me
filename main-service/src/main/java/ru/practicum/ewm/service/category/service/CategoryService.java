package ru.practicum.ewm.service.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.service.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(CategoryDto categoryDto);

    CategoryDto update(Long categoryId, CategoryDto categoryDto);

    void delete(Long categoryId);

    List<CategoryDto> getAll(Pageable pageable);

    CategoryDto getById(Long categoryId);
}
