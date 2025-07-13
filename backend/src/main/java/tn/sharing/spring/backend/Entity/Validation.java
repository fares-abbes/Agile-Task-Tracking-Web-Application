package tn.sharing.spring.backend.Entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
public class Validation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int validationId;

    private String status;
    private String reason;
    private LocalDate date;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Users validator;

}
