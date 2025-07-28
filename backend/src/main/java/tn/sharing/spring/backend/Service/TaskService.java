package tn.sharing.spring.backend.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.sharing.spring.backend.DTOs.TaskAssignmentRequest;
import tn.sharing.spring.backend.DTOs.TaskCreationRequest;
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
        
        // Check if task exists
        Optional<Tasks> taskOpt = tasksRepo.findById(request.getTaskId());
        if (taskOpt.isEmpty()) {
            return null;
        }
        
        Tasks task = taskOpt.get();
        
        // Check if project exists and matches the task's project
        if (request.getProjectId() > 0) {
            Optional<Project> projectOpt = projectRepo.findById(request.getProjectId());
            if (projectOpt.isEmpty()) {
                return null;
            }
            task.setProject(projectOpt.get());
        }

        // Assign to developer
        Users developer = developerOpt.get();
        List<Users> assignees = task.getUsers();
        if (assignees == null) {
            assignees = new ArrayList<>();
        }
        assignees.add(developer);
        task.setUsers(assignees);
        
        // Update task status to IN_PROGRESS when assigned
        task.setStatus(Status.IN_PROGRESS);

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

    public List<Tasks> getTasksByUserId(int userId) {
        return tasksRepo.findByUsers_Id(userId);
    }

    public List<Tasks> getTasksByProjectId(int projectId) {
        return tasksRepo.findByProject_ProjectId(projectId);
    }
    
    public Tasks createTask(int teamLeadId, int projectId, TaskCreationRequest request) {
        // Check if the user is a team lead
        Optional<Users> teamLeadOpt = userRepo.findById(teamLeadId);
        if (teamLeadOpt.isEmpty() || teamLeadOpt.get().getRole() != Role.TEAMLEAD) {
            return null;
        }

        // Check if project exists
        Optional<Project> projectOpt = projectRepo.findById(projectId);
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
        // Always set status to TODO when a task is created (not assigned yet)
        task.setStatus(Status.TODO);
        task.setProject(projectOpt.get());

        // Save task
        return tasksRepo.save(task);
    }

    public List<Tasks> getTasksByDeveloperAndStatus(int developerId, Status status) {
        return tasksRepo.findByUsers_IdAndStatus(developerId, status);
    }
    
    public List<Tasks> getAllTasks() {
        return tasksRepo.findAll();
    }

    /**
     * Get tasks for a developer filtered by importance
     * 
     * @param developerId ID of the developer
     * @param importance Importance level to filter by
     * @return List of tasks matching the criteria
     */
    public List<Tasks> getTasksByDeveloperAndImportance(int developerId, Importance importance) {
        return tasksRepo.findByUsers_IdAndImportance(developerId, importance);
    }
}
