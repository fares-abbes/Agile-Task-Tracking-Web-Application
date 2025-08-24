package tn.sharing.spring.backend.DTOs;

public class UserTaskRankDTO {
    private int userId;
    private String username;
    private int teamId;
    private int tasksDone;
    private int teamLeadId; // new field

    public UserTaskRankDTO(int userId, String username, int teamId, int tasksDone, int teamLeadId) {
        this.userId = userId;
        this.username = username;
        this.teamId = teamId;
        this.tasksDone = tasksDone;
        this.teamLeadId = teamLeadId;
    }

    // getters / setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public int getTasksDone() { return tasksDone; }
    public void setTasksDone(int tasksDone) { this.tasksDone = tasksDone; }

    public int getTeamLeadId() { return teamLeadId; }
    public void setTeamLeadId(int teamLeadId) { this.teamLeadId = teamLeadId; }
}