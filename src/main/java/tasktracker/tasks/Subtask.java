package tasktracker.tasks;

public class Subtask extends Task {
    private String epicName;
    private final int epicId;

    public Subtask(int subtaskId, String name, String description, TaskStatuses status, Epic epic,
                   String startTime, int duration) {
        super(subtaskId, name, description, status);
        this.epicName = epic.name;
        this.epicId = epic.id;
        this.startTime = setStartTime(startTime);
        this.duration = duration;
        epic.addSubtask(this);
    }

    public Subtask(String name, String description, Epic epic, String startTime, int duration) {
        super(name, description);
        this.epicName = epic.name;
        this.epicId = epic.id;
        this.startTime = setStartTime(startTime);
        this.duration = duration;
        epic.addSubtask(this);
    }

    // Конструктор для создания новой подзадачи и помещения в эпик.
    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epicName = epic.name;
        this.epicId = epic.id;
        epic.addSubtask(this);
    }

    // Конструктор для обновления подзадачи.
    public Subtask(int subtaskId, String name, String description, TaskStatuses status, Epic epic) {
        super(subtaskId, name, description, status);
        this.epicName = epic.name;
        this.epicId = epic.id;
    }

    // Конструктор для сохранения сабтаски из строки.
    public Subtask(int subtaskId, String name, String description, TaskStatuses status, int epicId) {
        super(subtaskId, name, description, status);
        this.epicId = epicId;
    }

    // Конструктор для сохранения сабтаски из строки.
    public Subtask(int subtaskId, String name, String description, TaskStatuses status, int epicId,
                   String startTime, int duration) {
        this(subtaskId, name, description, status, epicId);
        this.startTime = setStartTime(startTime);
        this.duration = duration;
    }

    public String getEpicName() {
        return epicName;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskTypes getType() {
        return TaskTypes.SUBTASK;
    }

    // Унаследованный toString от класса Task с добавлением в конец id эпика, в котором содержится эта сабтаска
    @Override
    public String toString() {
        return super.toString() + getEpicId();
    }
}
