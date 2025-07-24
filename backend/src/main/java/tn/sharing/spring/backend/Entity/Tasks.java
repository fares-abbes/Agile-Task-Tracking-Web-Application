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
public class Tasks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int taskId;
    private String taksName;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String description;
    private Importance importance;
    private Status status;
    @ManyToMany(mappedBy = "tasks",cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            fetch=FetchType.EAGER)
    @JsonIgnore
    private List<Users> users;
    @ManyToOne
    private  Project project;
    @OneToMany
    private List<Validation>validations;
}
