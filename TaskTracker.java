import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;

public class TaskTracker {
    private static final String TASKS_FILE = "tasks.json";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }
        
        String command = args[0].toLowerCase();
        
        try {
            switch (command) {
                case "add":
                    if (args.length < 2) {
                        System.out.println("Error: Task description is required");
                        return;
                    }
                    addTask(args[1]);
                    break;
                    
                case "update":
                    if (args.length < 3) {
                        System.out.println("Error: Task ID and new description are required");
                        return;
                    }
                    updateTask(Integer.parseInt(args[1]), args[2]);
                    break;
                    
                case "delete":
                    if (args.length < 2) {
                        System.out.println("Error: Task ID is required");
                        return;
                    }
                    deleteTask(Integer.parseInt(args[1]));
                    break;
                    
                case "mark-in-progress":
                    if (args.length < 2) {
                        System.out.println("Error: Task ID is required");
                        return;
                    }
                    markTaskStatus(Integer.parseInt(args[1]), "in-progress");
                    break;
                    
                case "mark-done":
                    if (args.length < 2) {
                        System.out.println("Error: Task ID is required");
                        return;
                    }
                    markTaskStatus(Integer.parseInt(args[1]), "done");
                    break;
                    
                case "list":
                    if (args.length > 1) {
                        listTasksByStatus(args[1]);
                    } else {
                        listAllTasks();
                    }
                    break;
                    
                default:
                    System.out.println("Error: Unknown command '" + command + "'");
                    printUsage();
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid task ID. Please provide a valid number.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static void printUsage() {
        System.out.println("Task Tracker CLI - Usage:");
        System.out.println("  java TaskTracker add \"Task description\"");
        System.out.println("  java TaskTracker update <id> \"New description\"");
        System.out.println("  java TaskTracker delete <id>");
        System.out.println("  java TaskTracker mark-in-progress <id>");
        System.out.println("  java TaskTracker mark-done <id>");
        System.out.println("  java TaskTracker list");
        System.out.println("  java TaskTracker list done");
        System.out.println("  java TaskTracker list todo");
        System.out.println("  java TaskTracker list in-progress");
    }
    
    private static void addTask(String description) throws Exception {
        List<Task> tasks = loadTasks();
        int newId = tasks.isEmpty() ? 1 : tasks.stream().mapToInt(Task::getId).max().orElse(0) + 1;
        
        Task newTask = new Task(newId, description, "todo", LocalDateTime.now(), LocalDateTime.now());
        tasks.add(newTask);
        
        saveTasks(tasks);
        System.out.println("Task added successfully (ID: " + newId + ")");
    }
    
    private static void updateTask(int id, String newDescription) throws Exception {
        List<Task> tasks = loadTasks();
        Task task = findTaskById(tasks, id);
        
        if (task == null) {
            System.out.println("Error: Task with ID " + id + " not found");
            return;
        }
        
        task.setDescription(newDescription);
        task.setUpdatedAt(LocalDateTime.now());
        
        saveTasks(tasks);
        System.out.println("Task updated successfully");
    }
    
    private static void deleteTask(int id) throws Exception {
        List<Task> tasks = loadTasks();
        Task task = findTaskById(tasks, id);
        
        if (task == null) {
            System.out.println("Error: Task with ID " + id + " not found");
            return;
        }
        
        tasks.remove(task);
        saveTasks(tasks);
        System.out.println("Task deleted successfully");
    }
    
    private static void markTaskStatus(int id, String status) throws Exception {
        List<Task> tasks = loadTasks();
        Task task = findTaskById(tasks, id);
        
        if (task == null) {
            System.out.println("Error: Task with ID " + id + " not found");
            return;
        }
        
        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        
        saveTasks(tasks);
        System.out.println("Task marked as " + status + " successfully");
    }
    
    private static void listAllTasks() throws Exception {
        List<Task> tasks = loadTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks found");
            return;
        }
        
        System.out.println("All Tasks:");
        System.out.println("ID | Status      | Description");
        System.out.println("---|-------------|------------");
        for (Task task : tasks) {
            System.out.printf("%-2d | %-11s | %s%n", 
                task.getId(), task.getStatus(), task.getDescription());
        }
    }
    
    private static void listTasksByStatus(String status) throws Exception {
        List<Task> tasks = loadTasks();
        List<Task> filteredTasks = tasks.stream()
            .filter(task -> task.getStatus().equals(status))
            .toList();
            
        if (filteredTasks.isEmpty()) {
            System.out.println("No tasks found with status: " + status);
            return;
        }
        
        System.out.println("Tasks with status '" + status + "':");
        System.out.println("ID | Status      | Description");
        System.out.println("---|-------------|------------");
        for (Task task : filteredTasks) {
            System.out.printf("%-2d | %-11s | %s%n", 
                task.getId(), task.getStatus(), task.getDescription());
        }
    }
    
    private static List<Task> loadTasks() throws Exception {
        if (!Files.exists(Paths.get(TASKS_FILE))) {
            return new ArrayList<>();
        }
        
        String content = Files.readString(Paths.get(TASKS_FILE));
        if (content.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Task> tasks = new ArrayList<>();
        
        // Simple JSON parsing for array of task objects
        content = content.trim();
        if (!content.startsWith("[") || !content.endsWith("]")) {
            throw new Exception("Invalid JSON format");
        }
        
        content = content.substring(1, content.length() - 1).trim();
        if (content.isEmpty()) {
            return tasks;
        }
        
        // Split by task objects (simple approach)
        String[] taskStrings = content.split("(?<=}),\\s*(?=\\{)");
        
        for (String taskStr : taskStrings) {
            taskStr = taskStr.trim();
            if (taskStr.startsWith(",")) {
                taskStr = taskStr.substring(1).trim();
            }
            if (taskStr.startsWith("{")) {
                taskStr = taskStr.substring(1);
            }
            if (taskStr.endsWith("}")) {
                taskStr = taskStr.substring(0, taskStr.length() - 1);
            }
            
            Task task = parseTaskFromString(taskStr);
            if (task != null) {
                tasks.add(task);
            }
        }
        
        return tasks;
    }
    
    private static Task parseTaskFromString(String taskStr) throws Exception {
        Map<String, String> fields = new HashMap<>();
        
        // Handle numeric values (id) and string values separately
        Pattern stringPattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"");
        Pattern numberPattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(\\d+)");
        
        Matcher stringMatcher = stringPattern.matcher(taskStr);
        while (stringMatcher.find()) {
            String key = stringMatcher.group(1);
            String value = stringMatcher.group(2);
            fields.put(key, value);
        }
        
        Matcher numberMatcher = numberPattern.matcher(taskStr);
        while (numberMatcher.find()) {
            String key = numberMatcher.group(1);
            String value = numberMatcher.group(2);
            fields.put(key, value);
        }
        
        if (fields.size() < 5) {
            throw new Exception("Invalid task format - missing required fields");
        }
        
        try {
            int id = Integer.parseInt(fields.get("id"));
            String description = fields.get("description");
            String status = fields.get("status");
            LocalDateTime createdAt = LocalDateTime.parse(fields.get("createdAt"), formatter);
            LocalDateTime updatedAt = LocalDateTime.parse(fields.get("updatedAt"), formatter);
            
            return new Task(id, description, status, createdAt, updatedAt);
        } catch (NumberFormatException e) {
            throw new Exception("Invalid task ID format");
        } catch (Exception e) {
            throw new Exception("Error parsing task data: " + e.getMessage());
        }
    }
    
    private static void saveTasks(List<Task> tasks) throws Exception {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            json.append("  {\n");
            json.append("    \"id\": ").append(task.getId()).append(",\n");
            json.append("    \"description\": \"").append(escapeJson(task.getDescription())).append("\",\n");
            json.append("    \"status\": \"").append(task.getStatus()).append("\",\n");
            json.append("    \"createdAt\": \"").append(task.getCreatedAt().format(formatter)).append("\",\n");
            json.append("    \"updatedAt\": \"").append(task.getUpdatedAt().format(formatter)).append("\"\n");
            json.append("  }");
            
            if (i < tasks.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        
        json.append("]\n");
        
        Files.writeString(Paths.get(TASKS_FILE), json.toString());
    }
    
    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    private static Task findTaskById(List<Task> tasks, int id) {
        return tasks.stream()
            .filter(task -> task.getId() == id)
            .findFirst()
            .orElse(null);
    }
} 