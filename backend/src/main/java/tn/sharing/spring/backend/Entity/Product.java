package tn.sharing.spring.backend.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;
    private String serialNumber;
    private String location;
    private LocalDate installationDate;

    @ElementCollection
    private List<String> models;

    private String status;
    private LocalDate lastModified;
    @ManyToOne
    @JsonIgnore
    
    private Users user;
}
