package tn.sharing.spring.backend.DTOs;

public class UserTaskCountDTO {
    private int userId;
    private String username;
    private Integer teamId;
    private long taskCount;

    public UserTaskCountDTO(int userId, String username, Integer teamId, long taskCount) {
        this.userId = userId;
        this.username = username;
        this.teamId = teamId;
        this.taskCount = taskCount;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String email) { this.username = email; }
    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }
    public long getTaskCount() { return taskCount; }
    public void setTaskCount(long taskCount) { this.taskCount = taskCount; }
}