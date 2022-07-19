package yeshenko.edge.rest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import yeshenko.edge.dto.DeviceDto;
import yeshenko.edge.model.RequestData;
import yeshenko.edge.service.CoreRequestService;
import yeshenko.edge.service.RequestDataService;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static yeshenko.edge.constant.Constants.ATTACHMENT_FILENAME;
import static yeshenko.edge.constant.Constants.DATE_TIME_PATTERN;
import static yeshenko.edge.constant.Constants.UNDERLINE;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class RequestDeviceRestController {

    private final CoreRequestService coreRequestService;
    private final RequestDataService requestDataService;

    @GetMapping("/device/{serialNumber}")
    public DeviceDto read(@PathVariable String serialNumber) {
        return coreRequestService.findBySerialNumber(serialNumber);
    }

    @GetMapping("/json/{model}")
    public List<DeviceDto> getDevicesAsJsonFile(@PathVariable String model) {
        return coreRequestService.findAllByModel(model);
    }

    @GetMapping("/csv/{model}")
    public void getDevicesAsCsvFile(@PathVariable String model, HttpServletResponse response) {

        String filename = model.concat(UNDERLINE)
                .concat(new SimpleDateFormat(DATE_TIME_PATTERN).format(new Date()))
                .concat(".csv");

        log.info("Filename:{}", filename);

        response.setContentType("text/csv");

        response.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME.concat(filename));

        requestDataService.composeCsvFile(response, model);
    }

    @PostMapping(path = "/upload/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "oauth2")
    public RequestData uploadDevices(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal Jwt principal) {
        String username = String.valueOf(principal.getClaims().get("preferred_username"));
        log.info("Authenticated login: {}", username);
        return requestDataService.processRequestLoadDevices(file, username);
    }

    @GetMapping("/request/")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "oauth2")
    public List<RequestData> getUploadedFiles() {
        return requestDataService.getFiles();
    }
}
