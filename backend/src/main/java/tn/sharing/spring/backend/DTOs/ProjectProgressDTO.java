package tn.sharing.spring.backend.DTOs; // <- if package name differs, adjust accordingly

public class ProjectProgressDTO {
    private int projectId;
    private String projectName;
    private int totalTasks;
    private int doneApprovedCount;
    private double percentage; // 0..100

    public ProjectProgressDTO(int projectId, String projectName, int totalTasks, int doneApprovedCount, double percentage) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.totalTasks = totalTasks;
        this.doneApprovedCount = doneApprovedCount;
        this.percentage = percentage;
    }

    // getters / setters
    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public int getTotalTasks() { return totalTasks; }
    public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }

    public int getDoneApprovedCount() { return doneApprovedCount; }
    public void setDoneApprovedCount(int doneApprovedCount) { this.doneApprovedCount = doneApprovedCount; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
}