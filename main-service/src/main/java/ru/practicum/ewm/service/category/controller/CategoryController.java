package ru.practicum.ewm.service.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.category.service.CategoryService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryController {

    @Autowired
    private final CategoryService categoryService;

    //Добавить категорию
    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("MainServer: Добавить категорию: {} ", categoryDto);
        CategoryDto resCategoryDto = categoryService.create(categoryDto);
        log.info("MainServer: Категория: {} добавлена.", resCategoryDto);

        return resCategoryDto;
    }

    //Изменить категорию
    @PatchMapping("/admin/categories/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto update(@Valid @RequestBody CategoryDto categoryDto,
                              @PathVariable Long categoryId) {
        log.info("MainServer: Изменить категорию с id: {} на {}", categoryId, categoryDto);
        CategoryDto resCategoryDto = categoryService.update(categoryId, categoryDto);
        log.info("MainServer: Обновлена категория с id = {}, следующими данными: {}", categoryId, resCategoryDto);

        return resCategoryDto;
    }

    //Удалить категорию
    @DeleteMapping("/admin/categories/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long categoryId) {
        log.info("MainServer: Удалить категорию с id {} ", categoryId);
        categoryService.delete(categoryId);
        log.info("MainServer: Удалена категория с id {}", categoryId);
    }

    //Получить список всех категорий
    @GetMapping("/categories")
    public List<CategoryDto> getAll(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("MainServer: Получить список всех категорий");
        final Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        List<CategoryDto> allCategories = categoryService.getAll(pageable);
        log.info("MainServer: Получен список всех категорий. Результат = {}", allCategories);

        return allCategories;
    }

    //Получить категорию по Id
    @GetMapping("/categories/{categoryId}")
    public CategoryDto getById(@PathVariable Long categoryId) {
        log.info("MainServer: Получить категорию с id = {}", categoryId);
        CategoryDto categoryDto = categoryService.getById(categoryId);
        log.info("MainServer: Получена вещь с id = {}. Результат = {}", categoryId, categoryDto);

        return categoryDto;
    }

}
