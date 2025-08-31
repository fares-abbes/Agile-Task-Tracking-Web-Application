package tn.sharing.spring.backend.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.sharing.spring.backend.Entity.Team;
import tn.sharing.spring.backend.Entity.Users;
import tn.sharing.spring.backend.Service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("createuser")
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping("/getusersList")
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/getuser/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable int id) {
        Optional<Users> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/updateuser/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable int id, @RequestBody Users user) {
        Users updated = userService.updateUser(id, user);
        if (updated == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/deleteuser/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        if (userService.deleteUser(id))
            return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/users/{userId}/assign-team/{teamId}")
    public ResponseEntity<?> assignUserToTeam(@PathVariable int userId, @PathVariable int teamId) {
        Users updatedUser = userService.assignUserToTeam(userId, teamId);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{userId}/team")
    public ResponseEntity<?> getUserTeam(@PathVariable int userId) {
        try {
            Team team = userService.getUserTeam(userId);
            if (team != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("teamId", team.getTeamId());
                response.put("teamName", team.getName());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.ok(Map.of("message", "User is not assigned to any team"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


}
