package ru.practicum.ewm.service.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.service.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.service.compilation.service.CompilationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class CompilationController {

    private final CompilationService compilationService;

    //Добавить подборку событий
    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody NewCompilationDto compilationDto) {
        log.info("MainServer: Добавить подборку: {} ", compilationDto);
        CompilationDto resCompilationDto = compilationService.create(compilationDto);
        log.info("MainServer: Подборка: {} добавлена.", resCompilationDto);

        return resCompilationDto;
    }

    //Изменить подборку событий
    @PatchMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto update(@RequestBody UpdateCompilationDto compilationDto,
                                @PathVariable Long compId) {
        log.info("MainServer: Изменить подборку событий с id: {} на {}", compId, compilationDto);
        CompilationDto resCompilationDto = compilationService.update(compId, compilationDto);
        log.info("MainServer: Обновлена подборка событий с id = {}, следующими данными: {}", compId, resCompilationDto);

        return resCompilationDto;
    }

    //Удалить подборку событий
    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {
        log.info("MainServer: Удалить подборку событий с id {} ", compId);
        compilationService.delete(compId);
        log.info("MainServer: Удалена подборка событий с id {}", compId);
    }

    //Получить все подборки событий
    @GetMapping("/compilations")
    public List<CompilationDto> getAll(@RequestParam(value = "pinned", required = false) boolean pinned,
                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("MainServer: Получить все подборки событий");
        final Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        List<CompilationDto> allCompilations = compilationService.getAll(pinned, pageable);
        log.info("MainServer: Получен список всех подборок событий. Результат = {}", allCompilations);

        return allCompilations;
    }

    //Получить подборку событий по Id
    @GetMapping("/compilations/{compId}")
    public CompilationDto getById(@PathVariable Long compId) {
        log.info("MainServer: Получить подборку событий по id = {}", compId);
        CompilationDto compilationDto = compilationService.getById(compId);
        log.info("MainServer: Получена подборка событий по id = {}. Результат = {}", compId, compilationDto);

        return compilationDto;
    }
}
