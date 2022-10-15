package tasktracker.managers;

import tasktracker.historymanager.HistoryManager;
import tasktracker.historymanager.InMemoryHistoryManager;
import tasktracker.taskmanager.FileBackedTaskManager;
import tasktracker.taskmanager.InMemoryTaskManager;
import tasktracker.taskmanager.TaskManager;

import java.io.File;

public final class Managers {

    private static final File FILE_PATH = new File("src/main/resources/fileForSave.csv");

    private Managers() {}

    public static TaskManager getDefaultFile() {
        return new FileBackedTaskManager(FILE_PATH);
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static File getFilePath() {
        return FILE_PATH;
    }
}
