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
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int projectId;
    private String status;
    private LocalDate lastModified;
    @ManyToOne
    @JsonIgnore
    private Users user;
    @ManyToOne
    @JsonIgnore
    private Client client;
    @OneToMany(mappedBy = "project")
    List<Tasks> tasks;
}
