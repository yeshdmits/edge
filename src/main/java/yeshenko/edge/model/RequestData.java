package yeshenko.edge.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "request_data")
@Data
public class RequestData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "device_number", nullable = false)
    private Integer deviceNumber;

    @Column(name = "date", nullable = false)
    private Timestamp date;

    @Column(name = "username", nullable = false)
    private String username;
}
