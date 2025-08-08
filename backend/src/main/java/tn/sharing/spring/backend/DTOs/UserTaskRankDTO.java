package tn.sharing.spring.backend.DTOs;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTaskRankDTO {
    private int userId;
    private String email;
    private int teamId;
    private int taskCount;

    
    // getters and setters
}