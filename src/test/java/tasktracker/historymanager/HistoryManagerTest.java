package tasktracker.historymanager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.managers.Managers;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private HistoryManager history;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void setUp() {
        history = Managers.getDefaultHistory();
        task = new Task("task name", "task desc");
        epic = new Epic("epic name", "epic desc");
        subtask = new Subtask("subtask name", "subtask desc", epic, "23.08.2023 16:00", 60);
    }

    @AfterAll
    static void afterAll() {
        Task.setCountTaskId(0);
    }

    @Test
    void addToHistory() {
        history.addToHistory(task);
        final List<Task> historyList = history.getHistory();
        assertNotNull(historyList,"История пустая.");
        assertEquals(1, historyList.size(), "В истории должна хранится 1 задача.");
    }

    @Test
    void getHistory() {
        final List<Task> historyList = history.getHistory();
        assertNotNull(historyList, "История пустая.");
        assertTrue(historyList.isEmpty(), "История пустая.");
    }

    @Test
    void duplicationInHistory() {
        history.addToHistory(task);
        history.addToHistory(task);
        final List<Task> historyList = history.getHistory();
        assertNotNull(historyList, "История пустая.");
        assertEquals(1, historyList.size(), "В истории должна хранится 1 задача.");
    }

    @Test
    void removeFirst() {
        history.addToHistory(task);
        history.addToHistory(epic);
        history.addToHistory(subtask);

        List<Task> historyList = history.getHistory();
        assertNotNull(historyList, "История пустая.");
        assertEquals(3, historyList.size(), "В истории должно хранятся 3 задачи.");

        history.remove(task.getId());
        historyList = history.getHistory();
        assertNotNull(historyList, "История не пустая.");
        assertEquals(2, historyList.size(), "В истории после удаления должно остаться 2 задачи.");
        assertEquals(epic, historyList.get(0), "В истории должен остаться эпик.");
        assertEquals(subtask, historyList.get(1), "В истории должна остаться сабтаска.");
    }

    @Test
    void removeMiddle() {
        history.addToHistory(task);
        history.addToHistory(epic);
        history.addToHistory(subtask);

        List<Task> historyList = history.getHistory();
        assertNotNull(historyList, "История пустая.");
        assertEquals(3, historyList.size(), "В истории должно хранятся 3 задачи.");

        history.remove(epic.getId());
        historyList = history.getHistory();
        assertNotNull(historyList, "История пустая.");
        assertEquals(2, historyList.size(), "В истории после удаления должно остаться 2 задачи.");
        assertEquals(task, historyList.get(0), "В истории должна остаться таска.");
        assertEquals(subtask, historyList.get(1), "В истории должна остаться сабтаска.");
    }

    @Test
    void removeLast() {
        history.addToHistory(task);
        history.addToHistory(epic);
        history.addToHistory(subtask);

        List<Task> historyList = history.getHistory();
        assertNotNull(historyList, "История пустая.");
        assertEquals(3, historyList.size(), "В истории должно хранятся 3 задачи.");

        history.remove(subtask.getId());
        historyList = history.getHistory();
        assertNotNull(historyList, "История пустая.");
        assertEquals(2, historyList.size(), "В истории после удаления должно остаться 2 задачи.");
        assertEquals(task, historyList.get(0), "В истории должна остаться таска.");
        assertEquals(epic, historyList.get(1), "В истории должен остаться эпик.");
    }
}