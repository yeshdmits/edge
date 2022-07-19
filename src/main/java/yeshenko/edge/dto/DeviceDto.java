package yeshenko.edge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceDto {
    private String serialNumber;
    private String model;
    private String description;
}
