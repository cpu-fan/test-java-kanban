package tasktracker.tasks;

import tasktracker.exceptions.TaskTimeValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected TaskStatuses status;
    protected static int countTaskId;
    protected LocalDateTime startTime;
    protected long duration;
    protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    // Конструктор с возможностью указать время старта и длительность задачи
    public Task(String name, String description, String startTime, int duration) {
        id = ++countTaskId;
        this.name = name;
        this.description = description;
        this.startTime = setStartTime(startTime);
        this.duration = duration;
        this.status = TaskStatuses.NEW; // все новые задачи создаются по умолчанию со статусом NEW
    }

    // Конструктор для новых задач со счетчиком для id.
    public Task(String name, String description) {
        id = ++countTaskId;
        this.name = name;
        this.description = description;
        this.status = TaskStatuses.NEW; // все новые задачи создаются по умолчанию со статусом NEW
    }

    // Конструктор для обновления задач без счетчика и с обновлением статуса (с 6-го спринта и для создания из файла).
    public Task(int id, String name, String description, TaskStatuses status) {
        this(name, description);
        this.id = id;
        this.status = status;
    }

    // Конструктор для обновления задач без счетчика и с обновлением статуса (с 7-го спринта с добавлением времени).
    public Task(int id, String name, String description, TaskStatuses status, String startTime, int duration) {
        this(id, name, description, status);
        this.startTime = setStartTime(startTime);
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    // Создан для задания id при создании задачи по http
    public void setId(int id) {
        this.id = id;
    }

    public TaskTypes getType() {
        return TaskTypes.TASK;
    }

    public String getName() {
        return name;
    }

    public TaskStatuses getStatus() {
        return status;
    }

    public void setStatus(TaskStatuses taskStatus) {
        this.status = taskStatus;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStartTime() {
        return this.startTime == null ? null : startTime;
    }

    public LocalDateTime getEndTime() {
        return this.startTime == null ? null : startTime.plusMinutes(duration);
    }

    // Следующие два метода используются для обратного формирования времени в строку для хранения
    // в toString() и последующего использования этого формата для записи в файл и чтения из него
    public String getStartTimeInFormat() {
        return this.startTime == null ? null : this.startTime.format(FORMATTER);
    }

    public String getEndTimeInFormat() {
        return this.startTime == null ? null : startTime.plusMinutes(duration).format(FORMATTER);
    }

    public long getDuration() {
        return this.duration;
    }

    public static void setCountTaskId(int countTaskId) {
        Task.countTaskId = countTaskId;
    }

    protected LocalDateTime setStartTime(String startTimeStr) {
        /* Данная проверка сделана, т.к. этот метод используется еще и при чтении из файла, где есть
        таски без времени у которых значение заполнено как null */
        if (startTimeStr.equals("null")) {
            return null;
        }
        LocalDateTime startTimeLDT = LocalDateTime.parse(startTimeStr, FORMATTER);
        if (startTimeLDT.isBefore(LocalDateTime.now())) {
            throw new TaskTimeValidationException("Время начала выполнения задачи не должно " +
                    "быть раньше текущего времени");
        }
        return startTimeLDT;
    }

    public int getCountTaskId() {
        return countTaskId;
    }

    public static DateTimeFormatter getFormatter() {
        return FORMATTER;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,",
                getId(),
                getType(),
                getName(),
                getStatus(),
                getDescription(),
                getStartTimeInFormat(),
                getDuration());
    }
}
