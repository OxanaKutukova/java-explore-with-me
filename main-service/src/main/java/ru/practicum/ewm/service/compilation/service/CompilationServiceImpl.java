package ru.practicum.ewm.service.compilation.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.service.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.service.compilation.model.Compilation;
import ru.practicum.ewm.service.compilation.model.CompilationMapper;
import ru.practicum.ewm.service.compilation.repository.CompilationRepository;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.exception.NotFoundException;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        final Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        final Compilation compilationS = compilationRepository.save(compilation);

        if (compilationS.getEvents().size() > 0) {
            compilationS.getEvents().replaceAll(event -> eventRepository.findById(event.getId())
                    .orElseThrow(() -> new NotFoundException("Событие не найдено или недоступно")));

        }

        return CompilationMapper.toCompilationDto(compilationS);
    }

    @Transactional
    @Override
    public CompilationDto update(Long compId, UpdateCompilationDto compilationDto) {
        final Compilation compilation = getCompilationById(compId);
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        if ((compilationDto.isPinned() && !compilation.isPinned()) ||
                (!compilationDto.isPinned() && compilation.isPinned())) {
            compilation.setPinned(compilationDto.isPinned());
        }

        if (!compilationDto.getEvents().isEmpty()) {
            compilationRepository.deleteEventCompilation(compId);
            List<Event> events = new ArrayList<>();

            if (compilationDto.getEvents().size() > 0) {
                for (int i = 0; i < compilationDto.getEvents().size(); i++) {
                    events.add(getEventById(compilationDto.getEvents().get(i)));
                }
            }

            compilation.setEvents(events);
        }
        final Compilation compilationS = compilationRepository.save(compilation);

        return CompilationMapper.toCompilationDto(compilationS);

    }

    @Transactional
    @Override
    public void delete(Long compId) {
        throwIfNotExistCompilation(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getAll(boolean pinned, Pageable pageable) {
        return compilationRepository.findAllByPinnedIs(pinned, pageable)
                .stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getById(Long compId) {

        final Compilation compilation = getCompilationById(compId);

        return CompilationMapper.toCompilationDto(compilation);
    }

    private void throwIfNotExistCompilation(Long compilationId) {
        compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Подборка событий с id=" + compilationId + " не найдена"));
    }

    private Compilation getCompilationById(Long compilationId) {
        return  compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + compilationId + " не найдена"));
    }

    private Event getEventById(Long eventId) {
        return  eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено или недоступно"));
    }
}
