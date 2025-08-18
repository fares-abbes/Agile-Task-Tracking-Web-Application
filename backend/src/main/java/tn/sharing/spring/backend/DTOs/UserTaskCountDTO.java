package tn.sharing.spring.backend.DTOs;

public class UserTaskCountDTO {
    private int userId;
    private String email;
    private Integer teamId;
    private long taskCount;

    public UserTaskCountDTO(int userId, String email, Integer teamId, long taskCount) {
        this.userId = userId;
        this.email = email;
        this.teamId = teamId;
        this.taskCount = taskCount;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }
    public long getTaskCount() { return taskCount; }
    public void setTaskCount(long taskCount) { this.taskCount = taskCount; }
}