package tn.sharing.spring.internshipprojectsharing.Entity;
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
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int checklistId;

    @ManyToOne
    private TestReport report;

    @ManyToOne
    private Users admin;

    private String message;
    private LocalDate createdAt;
}




