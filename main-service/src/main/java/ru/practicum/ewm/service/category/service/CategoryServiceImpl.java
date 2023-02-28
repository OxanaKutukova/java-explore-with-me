package ru.practicum.ewm.service.category.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.category.model.Category;
import ru.practicum.ewm.service.category.model.CategoryMapper;
import ru.practicum.ewm.service.category.repository.CategoryRepository;
import ru.practicum.ewm.service.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(Long categoryId) {
        final Category category = getCategoryById(categoryId);

        return CategoryMapper.toCategoryDto(category);
    }

    @Transactional
    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        final Category category = CategoryMapper.toCategory(categoryDto);
        final Category categoryS = categoryRepository.save(category);

        return CategoryMapper.toCategoryDto(categoryS);
    }

    @Transactional
    @Override
    public CategoryDto update(Long categoryId, CategoryDto categoryDto) {
        final Category category = CategoryMapper.toCategory(categoryDto);
        final Category categoryU = getCategoryById(categoryId);
        if (category.getName() != null) {
            categoryU.setName(category.getName());
        }
        final Category categorySaved = categoryRepository.save(categoryU);

        return CategoryMapper.toCategoryDto(categorySaved);
    }

    @Transactional
    @Override
    public void delete(Long categoryId) {
        throwIfNotExistCategory(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    private Category getCategoryById(Long categoryId) {
        return  categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + categoryId + " не найдена"));
    }

    private void throwIfNotExistCategory(Long categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + categoryId + " не найдена"));
    }

}
