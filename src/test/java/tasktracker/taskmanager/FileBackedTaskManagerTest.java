package tasktracker.taskmanager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.exceptions.ManagerSaveException;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileBackedTaskManagerTest {
    private static final File FILE_PATH = new File("src/test/resources/fileForSaveTest.csv");
    TaskManager manager;
    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void setUp() {
        manager = new FileBackedTaskManager(FILE_PATH);
        task = new Task("name", "desc");
        epic = new Epic("name", "desc");
        subtask = new Subtask("name", "desc", epic);
        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subtask);
    }

    @AfterEach
    void tearDown() {
        Task.setCountTaskId(0);
    }

    /* Дополнительно для FileBackedTasksManager — проверка работы по сохранению и восстановлению состояния.
    Граничные условия:
    a. Пустой список задач.
    b. Эпик без подзадач.
    c. Пустой список истории. */

    // Позитивный кейс
    @Test
    void save() throws IOException {
        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());
        String expected = "id,type,name,status,description,startTime,duration,epic\n" +
                "1,TASK,name,NEW,desc,null,0,\n" +
                "2,EPIC,name,NEW,desc,null,0,\n" +
                "3,SUBTASK,name,NEW,desc,null,0,2\n" +
                "\n" +
                "1,2,3";
        String actual = Files.readString(FILE_PATH.toPath());
        assertEquals(expected, actual);
    }

    // Пустой список задач
    @Test
    void saveWhenIsEmpty() throws IOException {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();

        String expected = "id,type,name,status,description,startTime,duration,epic\n\n";
        String actual = Files.readString(FILE_PATH.toPath());

        assertEquals(expected, actual);
    }

    // Эпик без подзадач
    @Test
    void saveWhenEpicWithoutSubtasks() throws IOException {
        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());
        manager.deleteAllSubtasks();

        String expected = "id,type,name,status,description,startTime,duration,epic\n" +
                "1,TASK,name,NEW,desc,null,0,\n" +
                "2,EPIC,name,NEW,desc,null,0,\n" +
                "\n" +
                "1,2";
        String actual = Files.readString(FILE_PATH.toPath());

        assertEquals(expected, actual);
    }

    // Пустой список истории
    @Test
    void saveWhenHistoryListIsEmpty() throws IOException {
        String expected = "id,type,name,status,description,startTime,duration,epic\n" +
                "1,TASK,name,NEW,desc,null,0,\n" +
                "2,EPIC,name,NEW,desc,null,0,\n" +
                "3,SUBTASK,name,NEW,desc,null,0,2\n" +
                "\n";
        String actual = Files.readString(FILE_PATH.toPath());

        assertEquals(expected, actual);
    }

    @Test
    void loadFromFile() {
        TaskManager manager2 = FileBackedTaskManager.loadFromFile(FILE_PATH);
        assertEquals(manager.toString(), manager2.toString());
    }

    @Test
    void loadFromFileWhenIsEmpty() {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();

        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(FILE_PATH));
    }

    @Test
    void loadFromFileWhenHistoryListIsEmpty() {
        String expected = "1,TASK,name,NEW,desc,null,0,\n" +
                "2,EPIC,name,NEW,desc,null,0,\n" +
                "3,SUBTASK,name,NEW,desc,null,0,2\n";
        TaskManager manager2 = FileBackedTaskManager.loadFromFile(FILE_PATH);
        assertEquals(expected, manager2.toString());
    }
}