# Task Tracker CLI

A simple command-line interface (CLI) application built in Java to track and manage your tasks. This project helps you practice programming skills including working with the filesystem, handling user inputs, and building a simple CLI application.

## Features

- **Add tasks** with descriptions
- **Update task** descriptions
- **Delete tasks** by ID
- **Mark tasks** as in-progress or done
- **List all tasks** or filter by status (todo, in-progress, done)
- **Persistent storage** using JSON file
- **Error handling** for invalid inputs and edge cases

## Requirements

- Java 8 or higher
- No external libraries or frameworks required (uses only native Java libraries)

## Installation

1. Clone or download this project to your local machine
2. Navigate to the project directory
3. Compile the Java files:
   ```bash
   javac *.java
   ```

## Usage

### Basic Commands

The application can be run using either:
- `java TaskTracker <command> [arguments]`
- `task-cli <command> [arguments]` (Windows batch file)

### Command Examples

#### Adding Tasks
```bash
# Add a new task
java TaskTracker add "Buy groceries"
# Output: Task added successfully (ID: 1)

java TaskTracker add "Complete project documentation"
# Output: Task added successfully (ID: 2)
```

#### Updating Tasks
```bash
# Update task description
java TaskTracker update 1 "Buy groceries and cook dinner"
# Output: Task updated successfully
```

#### Deleting Tasks
```bash
# Delete a task by ID
java TaskTracker delete 1
# Output: Task deleted successfully
```

#### Marking Task Status
```bash
# Mark task as in-progress
java TaskTracker mark-in-progress 2
# Output: Task marked as in-progress successfully

# Mark task as done
java TaskTracker mark-done 2
# Output: Task marked as done successfully
```

#### Listing Tasks
```bash
# List all tasks
java TaskTracker list
# Output:
# All Tasks:
# ID | Status      | Description
# ---|-------------|------------
# 1  | todo        | Buy groceries
# 2  | done        | Complete project documentation

# List tasks by status
java TaskTracker list done
java TaskTracker list todo
java TaskTracker list in-progress
```

#### Getting Help
```bash
# Show usage information
java TaskTracker
# Output: Shows all available commands and their usage
```

## Task Properties

Each task contains the following properties:

- **id**: Unique identifier (auto-generated)
- **description**: Task description text
- **status**: Current status (todo, in-progress, done)
- **createdAt**: Date and time when task was created
- **updatedAt**: Date and time when task was last updated

## Data Storage

Tasks are stored in a `tasks.json` file in the current directory. The file is automatically created when you add your first task. The JSON structure looks like this:

```json
[
  {
    "id": 1,
    "description": "Buy groceries",
    "status": "todo",
    "createdAt": "2024-01-15 10:30:00",
    "updatedAt": "2024-01-15 10:30:00"
  },
  {
    "id": 2,
    "description": "Complete project documentation",
    "status": "done",
    "createdAt": "2024-01-15 11:00:00",
    "updatedAt": "2024-01-15 14:30:00"
  }
]
```

## Project Structure

```
TASKTRACKER/
├── TaskTracker.java      # Main CLI application class
├── Task.java            # Task model class
├── task-cli.bat         # Windows batch file for easy execution
├── tasks.json           # Data storage file (created automatically)
└── README.md           # This file
```

## Error Handling

The application handles various error scenarios:

- **Invalid task ID**: When trying to update/delete/mark a non-existent task
- **Missing arguments**: When required arguments are not provided
- **Invalid commands**: When an unknown command is used
- **File I/O errors**: When there are issues reading/writing the JSON file
- **JSON parsing errors**: When the JSON file is corrupted or invalid

## Development

### Key Implementation Details

1. **Native JSON Handling**: Uses only Java's built-in libraries for JSON parsing and generation
2. **File System Operations**: Uses `java.nio.file` for modern file operations
3. **Date/Time Handling**: Uses `java.time.LocalDateTime` for timestamp management
4. **Stream API**: Uses Java 8+ Stream API for filtering and processing tasks
5. **Error Handling**: Comprehensive try-catch blocks for graceful error handling

### Adding New Features

To extend the application, you can:

1. Add new commands in the main switch statement
2. Create new methods for handling specific operations
3. Extend the Task class with additional properties
4. Modify the JSON structure for new data fields

## Testing

Test the application with various scenarios:

1. **Basic operations**: Add, list, update, delete tasks
2. **Status changes**: Mark tasks as in-progress and done
3. **Filtering**: List tasks by different statuses
4. **Error cases**: Try invalid IDs, missing arguments, etc.
5. **Data persistence**: Restart the application and verify tasks are still there

## Troubleshooting

### Common Issues

1. **"java: command not found"**: Make sure Java is installed and in your PATH
2. **"Error: Invalid JSON format"**: The tasks.json file may be corrupted - delete it to start fresh
3. **"Error: Task with ID X not found"**: The task ID doesn't exist - use `list` to see available tasks

### File Permissions

Make sure the application has read/write permissions in the current directory for creating and modifying the `tasks.json` file.

## License

This project is open source and available under the MIT License.

## Contributing

Feel free to contribute to this project by:
- Reporting bugs
- Suggesting new features
- Submitting pull requests
- Improving documentation 