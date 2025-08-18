package tn.sharing.spring.backend.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.sharing.spring.backend.DTOs.UserTaskCountDTO;
import tn.sharing.spring.backend.Entity.Role;
import tn.sharing.spring.backend.Entity.Status;
import tn.sharing.spring.backend.Entity.Team;
import tn.sharing.spring.backend.Entity.Users;
import tn.sharing.spring.backend.Repository.TeamRepo;
import tn.sharing.spring.backend.Repository.UserRepo;
import tn.sharing.spring.backend.Repository.UserRepo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo usersRepo;

    @Autowired
    private TeamRepo teamRepo;

    public Users createUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDate.now());
        return userRepo.save(user);
    }

    public List<Users> getAllUsers() {
        return userRepo.findAll();
    }

    public Optional<Users> getUserById(int id) {
        return userRepo.findById(id);
    }

    public Users updateUser(int id, Users userDetails) {
        return userRepo.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setRole(userDetails.getRole());
            user.setEmail(userDetails.getEmail());
            return userRepo.save(user);
        }).orElse(null);
    }

    @Transactional
    public boolean deleteUser(int id) {
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
            return true;
        }
        return false;
    }

    public Users assignUserToTeam(int userId, int teamId) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        user.setTeam(team);
        return usersRepo.save(user);
    }

    public long getTotalUsers() {
        return usersRepo.count();
    }

    public List<UserTaskCountDTO> getTaskCountsForTeam(int teamId, Status status, Integer month, Integer year) {
        return userRepo.countTasksPerUserInTeam(teamId, status, month, year);
    }

    public List<Users> getDevelopersByTeam(int teamId) {
        return userRepo.findByTeam_TeamIdAndRole(teamId, Role.DEVELOPPER);
    }

    public List<Users> getTestersByTeam(int teamId) {
        return userRepo.findByTeam_TeamIdAndRole(teamId, Role.TESTER);
    }
}
