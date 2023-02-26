package ru.practicum.ewm.service.compilation.model;

import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.service.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.model.EventMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        List<EventShortDto> eventsShort = compilation.getEvents()
                                            .stream()
                                            .map(EventMapper::toEventShortDto)
                                            .collect(Collectors.toList());

        return CompilationDto
                .builder()
                .id(compilation.getId())
                .events(eventsShort)
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .build();
    }

    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();

        for (int i = 0; i < newCompilationDto.getEvents().size(); i++) {
            events.add(new Event());
            events.get(i).setId(newCompilationDto.getEvents().get(i));
        }

        return Compilation
                .builder()
                .id(newCompilationDto.getId())
                .events(events)
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.isPinned())
                .build();
    }

    public static Compilation toCompilation(UpdateCompilationDto updateCompilationDto) {
        List<Event> events = new ArrayList<>();

        for (int i = 0; i < updateCompilationDto.getEvents().size(); i++) {
            events.add(new Event());
            events.get(i).setId(updateCompilationDto.getEvents().get(i));
        }

        return Compilation
                .builder()
                .id(updateCompilationDto.getId())
                .events(events)
                .title(updateCompilationDto.getTitle())
                .pinned(updateCompilationDto.isPinned())
                .build();
    }
}
