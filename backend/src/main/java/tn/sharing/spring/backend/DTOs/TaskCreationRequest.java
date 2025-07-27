package tn.sharing.spring.backend.DTOs;

import lombok.Data;
import tn.sharing.spring.backend.Entity.Importance;

import java.time.LocalDate;

@Data
public class TaskCreationRequest {
    private String taskName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Importance importance;
}
