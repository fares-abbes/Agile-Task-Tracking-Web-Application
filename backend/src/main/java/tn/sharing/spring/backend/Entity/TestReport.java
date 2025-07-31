package tn.sharing.spring.backend.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
public class TestReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reportId;

    @OneToOne
    @JsonIgnore
    private Tasks task;

    @ManyToOne
    private Users tester;

    private LocalDate date;
    private String result;
    private String overallRemarks;

    @ElementCollection
    @CollectionTable(name = "test_report_attributes", joinColumns = @JoinColumn(name = "report_id"))
    @MapKeyColumn(name = "attribute_name")
    @Column(name = "is_met")
    private Map<String, Boolean> attributes;

}
