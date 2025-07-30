package tn.sharing.spring.backend.DTOs;

import lombok.Data;

@Data
public class TaskAssignmentRequest {
    private int taskId;
    private int developerId;
    private int testerId;

    // Remove projectId field and its getter/setter
}