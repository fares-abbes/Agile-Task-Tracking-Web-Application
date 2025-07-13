package tn.sharing.spring.backend.Service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.sharing.spring.backend.Entity.Users;
import tn.sharing.spring.backend.Repository.UserRepo;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class    UserService {
private  final UserRepo userRepo;
private  final PasswordEncoder passwordEncoder;
    public Users createUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }
    public List<Users> getAllUsers() {
        return userRepo.findAll();
    }

    public Optional<Users> getUserById(Long id) {
        return userRepo.findById(id);
    }

    public Users updateUser(Long id, Users userDetails) {
        return userRepo.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setRole(userDetails.getRole());
            user.setEmail(userDetails.getEmail());
            user.setPassword(userDetails.getPassword());
            user.setCreatedAt(userDetails.getCreatedAt());
            return userRepo.save(user);
        }).orElse(null);
    }

    public boolean deleteUser(Long id) {
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
            return true;
        }
        return false;
    }
}
