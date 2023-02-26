package ru.practicum.ewm.service.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.service.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto create(NewCompilationDto compilationDto);

    CompilationDto update(Long compId, UpdateCompilationDto compilationDto);

    void delete(Long compId);

    List<CompilationDto> getAll(boolean pinned, Pageable pageable);

    CompilationDto getById(Long compId);
}
