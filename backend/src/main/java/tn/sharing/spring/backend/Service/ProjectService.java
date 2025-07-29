package tn.sharing.spring.backend.Service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.sharing.spring.backend.Entity.*;
import tn.sharing.spring.backend.Repository.ClientRepo;
import tn.sharing.spring.backend.Repository.ProjectRepo;
import tn.sharing.spring.backend.Repository.TestReportRepo;
import tn.sharing.spring.backend.Repository.UserRepo;

import java.util.List;
import java.util.Optional;

import static tn.sharing.spring.backend.Entity.Status.TODO;

@AllArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepo projectRepo;
    private final UserRepo userRepo;
    private final TestReportRepo testReportRepo;
    private final ClientRepo clientRepo;

    
    public Project addProject(Project project, int userId, int clientId) {
        
            
        // Find and set the client by ID
        Optional<Client> clientOpt = clientRepo.findById(clientId);
        if (clientOpt.isEmpty()) {
            return null;
        }
        
        // Set the user (quality user who created the project)
        Optional<Users> userOpt = userRepo.findById(userId);
        if (userOpt.isPresent()) {
            project.setTeamLead(userOpt.get());
        }
        
        // Set the client
        project.setClient(clientOpt.get());
        project.setStatus("TODO");
        
        return projectRepo.save(project);
    }

    public boolean deleteProject(int projectId) {
        if (projectRepo.existsById(projectId)) {
            projectRepo.deleteById(projectId);
            return true;
        }
        return false;
    }

    public Project updateProject(int projectId, Project projectDetails) {
        return projectRepo.findById(projectId).map(project -> {
            project.setTasks(projectDetails.getTasks());
            project.setStatus(projectDetails.getStatus());
            project.setProjectName(projectDetails.getProjectName());
            project.setEndDate(projectDetails.getEndDate());
            project.setDescription(projectDetails.getDescription());


            return projectRepo.save(project);
        }).orElse(null);
    }

    public List<Project> getAllProjects() {
        return projectRepo.findAll();
    }

    public Optional<Project> getProjectById(int projectId) {
        return projectRepo.findById(projectId);
    }

    public List<Project> getProjectsByTeamLead(int teamLeadId) {
        return projectRepo.findByTeamLead_Id(teamLeadId);
    }
}
