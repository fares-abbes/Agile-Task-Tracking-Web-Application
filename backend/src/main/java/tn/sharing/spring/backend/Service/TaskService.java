package tn.sharing.spring.backend.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.sharing.spring.backend.DTOs.TaskAssignmentRequest;
import tn.sharing.spring.backend.DTOs.TaskCreationRequest;
import tn.sharing.spring.backend.DTOs.UserTaskRankDTO;
import tn.sharing.spring.backend.Entity.*;
import tn.sharing.spring.backend.Repository.ProjectRepo;
import tn.sharing.spring.backend.Repository.TasksRepo;
import tn.sharing.spring.backend.Repository.UserRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        // No need to check projectId - task already has its project

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

    public Tasks assignTaskToTester(int teamLeadId, TaskAssignmentRequest request) {
        // Check if the user is a team lead
        Optional<Users> teamLeadOpt = userRepo.findById(teamLeadId);
        if (teamLeadOpt.isEmpty() || teamLeadOpt.get().getRole() != Role.TEAMLEAD) {
            return null;
        }

        // Check if tester exists
        Optional<Users> testerOpt = userRepo.findById(request.getTesterId());
        if (testerOpt.isEmpty() || testerOpt.get().getRole() != Role.TESTER) {
            return null;
        }
        
        // Check if task exists
        Optional<Tasks> taskOpt = tasksRepo.findById(request.getTaskId());
        if (taskOpt.isEmpty()) {
            return null;
        }
        
        Tasks task = taskOpt.get();
        // Task already has its project - no need to set it again

        // Assign to tester
        Users tester = testerOpt.get();
        List<Users> assignees = task.getUsers();
        if (assignees == null) {
            assignees = new ArrayList<>();
        }
        assignees.add(tester);
        task.setUsers(assignees);
        
        // Update tester's tasks
        List<Tasks> testerTasks = tester.getTasks();
        if (testerTasks == null) {
            testerTasks = new ArrayList<>();
        }
        testerTasks.add(task);
        tester.setTasks(testerTasks);

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
    public List<Tasks> getTasksByDeveloper(int developerId) {
        return tasksRepo.findByUsers_Id(developerId);
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

    public List<Tasks> getTasksByTeamLead(int teamLeadId) {
        return tasksRepo.findByProject_TeamLead_Id(teamLeadId);
    }

    public List<Tasks> getTasksByTester(int testerId) {
        return tasksRepo.findByUsers_Id(testerId);
    }

    public Tasks updateTaskStatusByTester(int testerId, int taskId, Status newStatus) {
        // Check if the user is a tester
        Optional<Users> testerOpt = userRepo.findById(testerId);
        if (testerOpt.isEmpty() || testerOpt.get().getRole() != Role.TESTER) {
            return null;
        }

        // Check if task exists
        Optional<Tasks> taskOpt = tasksRepo.findById(taskId);
        if (taskOpt.isEmpty()) {
            return null;
        }

        Tasks task = taskOpt.get();
        
        // Check if the tester is assigned to this task
        boolean isAssigned = task.getUsers().stream()
                .anyMatch(user -> user.getId() == testerId && user.getRole() == Role.TESTER);
        
        if (!isAssigned) {
            return null; // Tester is not assigned to this task
        }

        // Only allow Approved or NotApproved status updates by testers
        if (newStatus != Status.Approved && newStatus != Status.NotApproved) {
            return null;
        }

        // Update task status
        task.setStatus(newStatus);

        // Save and return updated task
        return tasksRepo.save(task);
    }

    public Tasks updateTaskStatus(int taskId, Status newStatus) {
        Optional<Tasks> taskOpt = tasksRepo.findById(taskId);
        if (taskOpt.isEmpty()) {
            return null;
        }
        Tasks task = taskOpt.get();
        task.setStatus(newStatus);
        return tasksRepo.save(task);
    }

    public List<Tasks> filterTasksByStatusAndImportance(List<Tasks> tasks, Status status, Importance importance) {
        return tasks.stream()
            .filter(task -> (status == null || task.getStatus() == status))
            .filter(task -> (importance == null || task.getImportance() == importance))
            .collect(Collectors.toList());
    }

    public List<UserTaskRankDTO> rankTeamMembersByTasksDoneThisMonth() {
        List<Object[]> results = tasksRepo.rankTeamMembersByTasksDoneThisMonth();
        List<UserTaskRankDTO> stats = new ArrayList<>();
        for (Object[] row : results) {
            stats.add(new UserTaskRankDTO(
                (Integer) row[0],
                (String) row[1],
                (Integer) row[2],
                ((Long) row[3]).intValue()
            ));
        }
        return stats;
    }
}
