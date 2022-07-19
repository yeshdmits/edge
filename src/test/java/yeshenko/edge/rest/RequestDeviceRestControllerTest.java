package yeshenko.edge.rest;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import yeshenko.edge.dto.DeviceDto;
import yeshenko.edge.model.RequestData;
import yeshenko.edge.repository.RequestDataRepository;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static yeshenko.edge.constant.Constants.MEDIA_TYPE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@WireMockTest(httpPort = 8081)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RequestDeviceRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RequestDataRepository requestDataRepository;

    @Test
    public void testGetBySerialNumber() {
        String serialNumber = "C36B7811";

        stubFor(get(urlEqualTo("/api/".concat(serialNumber)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("device_C36B7811.json")));

        DeviceDto responseBody = webTestClient.get()
                .uri("/api/device/{serialNumber}", serialNumber)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DeviceDto.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals(responseBody.getSerialNumber(), serialNumber);

    }

    @Test
    public void testGetBySerialNumber_ThrowException() {
        String serialNumber = "C36B7811";

        stubFor(get(urlEqualTo("/api/".concat(serialNumber)))
                .willReturn(aResponse()
                        .withStatus(500)));

        webTestClient.get()
                .uri("/api/device/{serialNumber}", serialNumber)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void testGetAllByModel() {

        stubFor(get(urlEqualTo("/api"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("all_devices.json")));

        String model = "MOD1";

        List<DeviceDto> responseBody = webTestClient.get()
                .uri("/api/json/{model}", model)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DeviceDto.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(responseBody);
        Assertions.assertFalse(responseBody.isEmpty());
        Assertions.assertEquals(3, responseBody.size());
    }

    @Test
    public void testGetAllByModelCSV() {

        stubFor(get(urlEqualTo("/api"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("all_devices.json")));

        String model = "MOD1";

        byte[] responseBody = webTestClient.get()
                .uri("/api/csv/{model}", model)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBody();

        Assertions.assertNotNull(responseBody);
    }

    @Test
    @Order(1)
    public void testUploadCsvFile() {
        stubFor(post(urlEqualTo("/api"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("device_C36B7811.json")));


        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();

        multipartBodyBuilder.part("file", new ClassPathResource("__files/to_load.csv"))
                .header("Content-Type", MEDIA_TYPE);

        RequestData responseBody = webTestClient.post()
                .uri("/api/upload")
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(RequestData.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals(responseBody.getDate().getTime(),
                requestDataRepository.getById(responseBody.getId()).getDate().getTime());
    }

    @Test
    public void testUploadCsvFile_ReturnBadRequest() {
        stubFor(post(urlEqualTo("/api"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("device_C36B7811.json")));


        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();

        multipartBodyBuilder.part("file", new ClassPathResource("__files/to_load.csv"))
                .contentType(MediaType.MULTIPART_FORM_DATA);

        webTestClient.post()
                .uri("/api/upload")
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange()
                .expectStatus().isBadRequest();


    }

    @Test
    @Order(2)
    public void testGetAllUpdatedFiles() {
        List<RequestData> responseBody = webTestClient.get()
                .uri("/api")
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBodyList(RequestData.class)
                .returnResult()
                .getResponseBody();

        List<RequestData> all = requestDataRepository
                .findAll();

        Assertions.assertNotNull(responseBody);
        Assertions.assertFalse(responseBody.isEmpty());
        Assertions.assertEquals(responseBody.size(), requestDataRepository.findAll().size());
    }
}