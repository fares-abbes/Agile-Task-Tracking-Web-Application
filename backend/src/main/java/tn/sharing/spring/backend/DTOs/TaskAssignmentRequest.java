package tn.sharing.spring.backend.DTOs;

import lombok.Data;
import tn.sharing.spring.backend.Entity.Importance;
import tn.sharing.spring.backend.Entity.Status;
import java.time.LocalDate;

@Data
public class TaskAssignmentRequest {
    private String taskName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Importance importance;
    private Status status;
    private int developerId;
    private int projectId;
}
