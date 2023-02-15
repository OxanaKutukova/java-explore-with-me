package ru.practicum.ewm.client.stats;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import ru.practicum.ewm.dto.stats.EndPointHitDto;
import ru.practicum.ewm.dto.stats.ViewStatsDto;
import ru.practicum.ewm.dto.stats.ViewStatsParamDto;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;


@Slf4j
public class StatsClient {

    private final HttpClient httpClient;
    private final String application;
    private final String statsServiceUri;
    private final ObjectMapper json;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public StatsClient(@Value("${stats-service.uri}") String statsServiceUri,
                        @Value("${spring.application.name}") String application,
                        ObjectMapper json) {
         this.application = application;
         this.statsServiceUri = statsServiceUri;
         this.json = json;
         this.httpClient = HttpClient.newBuilder()
                 .connectTimeout(Duration.ofSeconds(3))
                 .build();
    }

    public void addHit(HttpServletRequest request) {

        EndPointHitDto endPointHitDto = EndPointHitDto.builder()
                .app(application)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(dateTimeFormatter))
                .build();
        try {
            HttpRequest.BodyPublisher bodyPublisher = HttpRequest
                    .BodyPublishers
                    .ofString(json.writeValueAsString(endPointHitDto));
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(statsServiceUri + "/hit"))
                    .POST(bodyPublisher)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .build();
            HttpResponse<Void> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.discarding());

        } catch (Exception e) {

        }
    }

    public List<ViewStatsDto> getStatsHit(ViewStatsParamDto viewStatsParamDto) {
        try {
            String queryString = toQueryString(viewStatsParamDto);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(statsServiceUri + "/stats" + queryString))
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (HttpStatus.valueOf(response.statusCode()).is2xxSuccessful()) {
                return json.readValue(response.body(), new TypeReference<>(){});
            }

        } catch (Exception e) {
            log.error("Не удалось получить статистику по запросу: " +  viewStatsParamDto, e);
        }

        return Collections.emptyList();
    }

    private String toQueryString(ViewStatsParamDto viewStatsParamDto) {
         String start = viewStatsParamDto.getStart();
         String end = viewStatsParamDto.getEnd();

         String queryString = String.format("?start=%s&end=%s&unique=%b",
                 start, end, viewStatsParamDto.isUnique());
         if (viewStatsParamDto.getUris().length > 0) {
             queryString += "&uris=" + String.join(",", viewStatsParamDto.getUris());
         }

         return queryString;
    }

}
