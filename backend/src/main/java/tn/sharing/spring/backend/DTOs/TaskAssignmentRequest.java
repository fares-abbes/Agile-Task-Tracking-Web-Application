package tn.sharing.spring.backend.DTOs;

import lombok.Data;
import tn.sharing.spring.backend.Entity.Importance;
import tn.sharing.spring.backend.Entity.Status;
import java.time.LocalDate;

@Data
public class TaskAssignmentRequest {
    private int taskId;
    private int developerId;
    private int projectId;
}
