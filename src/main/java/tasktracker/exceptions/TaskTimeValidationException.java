package tasktracker.exceptions;

import tasktracker.tasks.Task;

public class TaskTimeValidationException extends RuntimeException {
    Task task;

    public TaskTimeValidationException(String message) {
        super(message);
    }

    public TaskTimeValidationException(String message, Task task) {
        super(message);
        this.task = task;
    }

    public TaskTimeValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "id: " + task.getId() + ", name: " + task.getName() + ", startTime: "
                + task.getStartTimeInFormat();
    }
}
