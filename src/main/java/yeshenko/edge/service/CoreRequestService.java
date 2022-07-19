package yeshenko.edge.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import yeshenko.edge.dto.DeviceDto;
import yeshenko.edge.exception.ResponseDataException;

import java.util.List;
import java.util.stream.Collectors;

import static yeshenko.edge.constant.Constants.PATH_SERIAL_NUMBER;

@Service
@Slf4j
public class CoreRequestService {

    private final WebClient webClient;

    @Autowired
    public CoreRequestService(WebClient.Builder webClientBuilder, @Value("${core.url}") String baseUrl) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        log.info("Base Core Url: {}", baseUrl);
    }

    public DeviceDto findBySerialNumber(String serialNumber) {
        try {

            return webClient.get()
                    .uri(PATH_SERIAL_NUMBER, serialNumber)
                    .retrieve()
                    .bodyToMono(DeviceDto.class)
                    .block();
        } catch (RuntimeException e) {
            log.error("Could not retrieve data from core service: {}", e.getMessage());
            throw new ResponseDataException("Could not retrieve data from core service");
        }
    }

    public List<DeviceDto> findAllByModel(String model) {
        ResponseEntity<List<DeviceDto>> listResponseEntity = webClient.get()
                .retrieve()
                .toEntityList(DeviceDto.class)
                .blockOptional()
                .orElseThrow(() -> new ResponseDataException("Could not fetch data from Core service"));

        if (!listResponseEntity.hasBody() || listResponseEntity.getBody() == null) {
            log.info("Body of Response Entity not exists");
            throw new ResponseDataException("Body of Response Entity not exists");
        }

        return listResponseEntity.getBody().stream()
                .filter(device -> model.equalsIgnoreCase(device.getModel()))
                .collect(Collectors.toList());
    }

    public void processRequest(DeviceDto device) {
        try {
            String response = webClient.post()
                    .body(Mono.just(device), DeviceDto.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("Web Client Response: {}", response);
        } catch (WebClientException e) {
            log.warn("Could not execute request for device, serialNumber: {}, message: {}",
                    device.getSerialNumber(), e.getMessage());
        }
    }
}
