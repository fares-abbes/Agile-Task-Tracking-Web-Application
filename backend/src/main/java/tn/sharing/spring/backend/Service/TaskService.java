package tn.sharing.spring.backend.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.sharing.spring.backend.DTOs.ProjectProgressDTO;
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
    private final ProjectRepo projectRepo;
    private final TasksRepo tasksRepo;
    private final UserRepo userRepo;

    /**
     * For a given team lead (user id), return each project he leads with:
     * - total tasks
     * - count of tasks with status DONE or APPROVED
     * - percentage (doneApproved / total * 100)
     */
    public List<ProjectProgressDTO> getProjectProgressForTeamLead(int teamLeadId) {
        List<Project> projects = projectRepo.findByTeamLead_Id(teamLeadId);
        if (projects == null || projects.isEmpty()) {
            return List.of();
        }

        List<ProjectProgressDTO> result = new ArrayList<>();
        List<Status> targetStatuses = List.of(Status.DONE, Status.Approved);

        for (Project p : projects) {
            // adjust getter if Project entity uses a different id/name field (projectId / id / name / projectName)
            int projId = p.getProjectId(); // change to getId() if needed
            String projName = p.getProjectName() != null ? p.getProjectName() : ("Project " + projId); // adjust getter if needed

            // try optimized repo if available, otherwise fetch all tasks and filter
            List<Tasks> projectTasks = tasksRepo.findByProject_ProjectId(projId);
            int total = projectTasks == null ? 0 : projectTasks.size();

            int doneApprovedCount = 0;
            if (total > 0) {
                doneApprovedCount = (int) projectTasks.stream()
                        .filter(t -> t.getStatus() != null && (t.getStatus() == Status.DONE || t.getStatus() == Status.Approved))
                        .count();
            }

            double percentage = total == 0 ? 0.0 : (100.0 * doneApprovedCount / total);

            result.add(new ProjectProgressDTO(projId, projName, total, doneApprovedCount, percentage));
        }

        return result;
    }

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

 
    /**
     * Return, for a given team id, the number of tasks marked DONE assigned to each team member.
     * - teamId: id of the team
     * - returns a list of UserTaskRankDTO where the 4th field is the count of DONE tasks assigned to that user
     *
     * Note: requires UserRepo to expose findByTeam_Id(int teamId) and TasksRepo to expose
     * findByUsers_IdInAndStatus(List<Integer> userIds, Status status).
     */
    public List<UserTaskRankDTO> rankTeamMembersByTasksDoneThisMonth(int teamId) {
        // get members of the team
        List<Users> members = userRepo.findByTeam_TeamId(teamId);
        if (members == null || members.isEmpty()) {
            return List.of();
        }

        // determine team lead id (first user in team with role TEAMLEAD)
        int teamLeadId = members.stream()
                .filter(u -> u.getRole() == Role.TEAMLEAD)
                .map(Users::getId)
                .findFirst()
                .orElse(0);

        // collect member ids
        List<Integer> memberIds = members.stream().map(Users::getId).collect(Collectors.toList());

        // fetch tasks assigned to these members that are marked DONE or Approved
        List<Status> statuses = List.of(Status.DONE, Status.Approved);
        List<Tasks> doneTasks = tasksRepo.findByUsers_IdInAndStatusIn(memberIds, statuses);
        if (doneTasks == null || doneTasks.isEmpty()) {
            // return zero counts for each member
            List<UserTaskRankDTO> emptyStats = new ArrayList<>();
            for (Users m : members) {
                emptyStats.add(new UserTaskRankDTO(m.getId(), m.getUsername(), teamId, 0, teamLeadId));
            }
            return emptyStats;
        }

        // count tasks per member (a task assigned to multiple members counts for each assigned member)
        java.util.Map<Integer, Integer> counts = new java.util.HashMap<>();
        for (Tasks t : doneTasks) {
            if (t.getUsers() == null) continue;
            for (Users u : t.getUsers()) {
                if (memberIds.contains(u.getId())) {
                    counts.put(u.getId(), counts.getOrDefault(u.getId(), 0) + 1);
                }
            }
        }

        // build DTO list (preserve all members even if count == 0)
        List<UserTaskRankDTO> stats = new ArrayList<>();
        for (Users m : members) {
            int cnt = counts.getOrDefault(m.getId(), 0);
            stats.add(new UserTaskRankDTO(m.getId(), m.getUsername(), teamId, cnt, teamLeadId));
        }

        return stats;
    }

    

    // counts for projects led by teamLeadId: TODO, IN_PROGRESS, NOT_APPROVED
    public long countPendingByTeamLead(int teamLeadId) {
        List<Status> pending = List.of(Status.TODO, Status.IN_PROGRESS, Status.NotApproved);
        return tasksRepo.countByProject_TeamLead_IdAndStatusIn(teamLeadId, pending);
    }

    // counts for projects led by teamLeadId: APPROVED, DONE
    public long countCompletedByTeamLead(int teamLeadId) {
        List<Status> completed = List.of(Status.Approved, Status.DONE);
        return tasksRepo.countByProject_TeamLead_IdAndStatusIn(teamLeadId, completed);
    }

    // counts for projects led by teamLeadId with importance CRITICAL or HIGH
    public long countHighPriorityByTeamLead(int teamLeadId) {
        List<Importance> imps = List.of(Importance.CRITICAL, Importance.HIGH);
        return tasksRepo.countByProject_TeamLead_IdAndImportanceIn(teamLeadId, imps);
    }

    /**
     * Count number of team members for the team of the given teamLead (by user id).
     * - finds the team of the provided teamLead user and returns the number of users in that team.
     * - returns 0 if user or team not found.
     */
    public long countTeamMembersByTeamLead(int teamLeadId) {
        Optional<Users> leadOpt = userRepo.findById(teamLeadId);
        if (leadOpt.isEmpty()) {
            return 0L;
        }
        Users lead = leadOpt.get();
        if (lead.getTeam() == null) {
            return 0L;
        }
        int teamId = lead.getTeam().getTeamId();
        return userRepo.countByTeam_TeamId(teamId);
    }

    /**
     * Return all tasks for projects led by the given teamLeadId excluding tasks with Status.APPROVED.
     */
    public List<Tasks> getTasksForTeamLeadExcludingApproved(int teamLeadId) {
        return tasksRepo.findByProject_TeamLead_IdAndStatusNot(teamLeadId, Status.Approved);
    }
}
