package yeshenko.edge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yeshenko.edge.dto.DeviceDto;
import yeshenko.edge.exception.FileCreatingException;
import yeshenko.edge.exception.FileNotValidException;
import yeshenko.edge.model.RequestData;
import yeshenko.edge.repository.RequestDataRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static yeshenko.edge.constant.Constants.HEADERS;
import static yeshenko.edge.constant.Constants.MEDIA_TYPE;
import static yeshenko.edge.constant.Constants.MESSAGE_TEMPLATE;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestDataService {

    private final CoreRequestService coreRequestService;

    private final RequestDataRepository requestDataRepository;

    private final EmailService emailService;

    public void composeCsvFile(HttpServletResponse response, String model) {

        try (CSVPrinter printer = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT
                .builder().setHeader(HEADERS).build())) {
            for (DeviceDto device : coreRequestService.findAllByModel(model)) {
                printer.printRecord(device.getSerialNumber(), device.getModel(), device.getDescription());
            }
        } catch (IOException e) {
            throw new FileCreatingException("An exception occurred while creating the file.");
        }
    }

    public List<RequestData> getFiles() {
        return requestDataRepository.findAll();
    }

    public boolean hasCSVFormat(MultipartFile file) {
        return MEDIA_TYPE.equals(file.getContentType());
    }

    public RequestData processRequestLoadDevices(MultipartFile file, String username) {
        if (!hasCSVFormat(file)) {
            log.error("Media type not equals csv format.");
            throw new FileNotValidException("File must be send with csv format");
        }
        List<CSVRecord> csvRecords = getCsvRecords(file);

        RequestData requestData = new RequestData();
        requestData.setDeviceNumber(0);
        csvRecords.forEach(record -> {
            DeviceDto device = new DeviceDto(record.get(0), record.get(1), record.get(2));
            requestData.setDeviceNumber(requestData.getDeviceNumber() + 1);
            coreRequestService.processRequest(device);

        });


        requestData.setDate(Timestamp.valueOf(LocalDateTime.now()));
        requestData.setFilename(file.getOriginalFilename());
        requestData.setUsername(username);

        emailService.send(String.format(
                MESSAGE_TEMPLATE,
                requestData.getFilename(),
                requestData.getDeviceNumber(),
                requestData.getDate()
        ));

        return requestDataRepository.save(requestData);
    }

    private List<CSVRecord> getCsvRecords(MultipartFile file) {
        try (InputStreamReader in = new InputStreamReader(file.getInputStream())) {
            return CSVFormat.DEFAULT.builder()
                    .setHeader(HEADERS)
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(in)
                    .getRecords();
        } catch (IOException e) {
            log.error("Could not parse input file: {}", e.getMessage());
            throw new FileNotValidException("Could not read the file. Csv format must be with default values");
        }
    }
}
