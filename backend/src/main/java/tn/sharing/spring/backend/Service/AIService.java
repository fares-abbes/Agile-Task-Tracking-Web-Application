package tn.sharing.spring.backend.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import tn.sharing.spring.backend.Entity.Users;
import tn.sharing.spring.backend.Service.UserService;
import tn.sharing.spring.backend.Entity.Role;
import tn.sharing.spring.backend.Entity.Status;

import java.util.*;

@Service
public class AIService {

    @Value("${GEMINI_API_KEY}")
    private String geminiApiKey;

    private final UserService userService;

    public AIService(UserService userService) {
        this.userService = userService;
    }

    // 1. Generate tasks from job description using Gemini LLM
    public List<Map<String, Object>> generateTasksFromDescription(String description) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + geminiApiKey;

        String prompt = "Given this project description, list all implementation tasks required for the project. For each task, assign a complexity level from 1 (simple) to 4 (very complex). Return ONLY a JSON array like: [{\"task\": \"Design database schema\", \"description\": \"Design the database tables and relationships.\", \"complexity\": 3}, ...]. Do not add any explanation or commentary.";
        String fullPrompt = prompt + "\n\nProject description:\n" + description;

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(Map.of("parts", List.of(Map.of("text", fullPrompt))))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        List<Map<String, Object>> tasks = new ArrayList<>();
        Map body = response.getBody();
        if (body != null && body.containsKey("candidates")) {
            List candidates = (List) body.get("candidates");
            if (!candidates.isEmpty()) {
                Map candidate = (Map) candidates.get(0);
                Map content = (Map) candidate.get("content");
                List parts = (List) content.get("parts");
                if (!parts.isEmpty()) {
                    Map part = (Map) parts.get(0);
                    String text = (String) part.get("text");
                    System.out.println("Gemini raw response: " + text); // Debug print
                    // Try to parse as JSON
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        tasks = mapper.readValue(text, List.class);
                    } catch (Exception e) {
                        System.out.println("Gemini response is not valid JSON: " + text);
                        // Try to extract JSON array from the text
                        int start = text.indexOf("[");
                        int end = text.lastIndexOf("]");
                        if (start != -1 && end != -1 && end > start) {
                            String jsonArray = text.substring(start, end + 1);
                            try {
                                ObjectMapper mapper = new ObjectMapper();
                                tasks = mapper.readValue(jsonArray, List.class);
                            } catch (Exception ex2) { // <-- renamed from 'ex' to 'ex2'
                                System.out.println("Failed to parse extracted JSON array: " + jsonArray);
                                // Manual parsing fallback for bullet or numbered lists
                                if (text != null && !text.isEmpty()) {
                                    String[] lines = text.split("\n");
                                    for (String line : lines) {
                                        line = line.trim();
                                        if (line.isEmpty()) continue;
                                        // Example: "- Authentication: Implement login and registration. Complexity: 1"
                                        if (line.startsWith("-") || line.matches("^\\d+\\..*")) {
                                            String[] lineParts = line.split("Complexity:");
                                            String taskPart = lineParts[0].replace("-", "").replaceFirst("^\\d+\\.", "").trim();
                                            String[] taskDescSplit = taskPart.split(":", 2);
                                            String taskName = taskDescSplit[0].trim();
                                            String taskDesc = taskDescSplit.length > 1 ? taskDescSplit[1].trim() : "";
                                            int complexity = 1;
                                            if (lineParts.length > 1) {
                                                try {
                                                    complexity = Integer.parseInt(lineParts[1].replaceAll("[^0-9]", ""));
                                                } catch (Exception ex3) { // <-- unique name for inner catch
                                                    complexity = 1;
                                                }
                                            }
                                            Map<String, Object> task = new HashMap<>();
                                            task.put("task", taskName);
                                            task.put("description", taskDesc);
                                            task.put("complexity", complexity);
                                            tasks.add(task);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return tasks;
    }

    // 2. Assign each task to a convenient developer and tester in the team
    public List<Map<String, Object>> assignTasksToTeam(List<Map<String, Object>> tasks, int teamId) {
            List<Users> developers = userService.getDevelopersByTeam(teamId);
        List<Users> testers = userService.getTestersByTeam(teamId);

        List<Map<String, Object>> assignments = new ArrayList<>();
        int devIndex = 0, testerIndex = 0;

        for (Map<String, Object> task : tasks) {
            Users developer = developers.get(devIndex % developers.size());
            Users tester = testers.get(testerIndex % testers.size());
            devIndex++; testerIndex++;

            assignments.add(Map.of(
                "task", task,
                "developer", developer,
                "tester", tester
            ));
        }
        return assignments;
    }
}
