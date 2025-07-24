package tn.sharing.spring.backend.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.sharing.spring.backend.DTOs.TaskAssignmentRequest;
import tn.sharing.spring.backend.Entity.*;
import tn.sharing.spring.backend.Repository.ProjectRepo;
import tn.sharing.spring.backend.Repository.TasksRepo;
import tn.sharing.spring.backend.Repository.UserRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class TaskService {

    private final TasksRepo tasksRepo;
    private final UserRepo userRepo;
    private final ProjectRepo projectRepo;

    /**
     * Allows a team lead to assign a task to a developer
     * 
     * @param teamLeadId ID of the team lead assigning the task
     * @param request    Task assignment details
     * @return The created task if successful, null otherwise
     */
    public Tasks assignTaskToDeveloper(int teamLeadId, TaskAssignmentRequest request) {
        // Check if the user is a team lead
        Optional<Users> teamLeadOpt = userRepo.findById(teamLeadId);
        if (teamLeadOpt.isEmpty() || teamLeadOpt.get().getRole() != Role.TEAMLEAD) {
            return null;
        }

        // Check if developer exists
        Optional<Users> developerOpt = userRepo.findById(request.getDeveloperId());
        if (developerOpt.isEmpty() || developerOpt.get().getRole() != Role.DEVELOPPER) {
            return null;
        }

        // Check if project exists
        Optional<Project> projectOpt = projectRepo.findById(request.getProjectId());
        if (projectOpt.isEmpty()) {
            return null;
        }

        // Create task
        Tasks task = new Tasks();
        task.setTaksName(request.getTaskName());
        task.setDescription(request.getDescription());
        task.setDateDebut(request.getStartDate());
        task.setDateFin(request.getEndDate());
        task.setImportance(request.getImportance());
        task.setStatus(request.getStatus() != null ? request.getStatus() : Status.TODO);
        task.setProject(projectOpt.get());

        // Assign to developer
        Users developer = developerOpt.get();
        List<Users> assignees = new ArrayList<>();
        assignees.add(developer);
        task.setUsers(assignees);

        // Update developer's tasks
        List<Tasks> developerTasks = developer.getTasks();
        if (developerTasks == null) {
            developerTasks = new ArrayList<>();
        }
        developerTasks.add(task);
        developer.setTasks(developerTasks);

        // Save task
        return tasksRepo.save(task);
    }

    /**
     * Get all tasks assigned to a user
     * 
     * @param userId ID of the user
     * @return List of tasks assigned to the user
     */
    public List<Tasks> getTasksByUserId(int userId) {
        return tasksRepo.findByUsers_Id(userId);
    }

    /**
     * Get all tasks in a project
     * 
     * @param projectId ID of the project
     * @return List of tasks in the project
     */
    public List<Tasks> getTasksByProjectId(int projectId) {
        return tasksRepo.findByProject_ProjectId(projectId);
    }
}
