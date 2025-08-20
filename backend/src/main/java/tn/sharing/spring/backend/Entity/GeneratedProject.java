package tn.sharing.spring.backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "generated_projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    @Column(columnDefinition = "text")
    private String description;

    @Lob
    @Column(columnDefinition = "text")
    private String tasksJson;

    private Long userId; // optional: store owner
    private Long teamId; // optional

    private Instant createdAt;
    private Instant updatedAt;
}
