package tasktracker.tasks;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    Epic epic;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    void setUp() {
        epic = new Epic("epic name", "epic desc");
        subtask1 = new Subtask("subtask1", "subtask1 desc", epic);
        subtask2 = new Subtask("subtask2", "subtask2 desc", epic);
    }

    @AfterAll
    static void afterAll() {
        Task.setCountTaskId(0);
    }

    // Пустой список подзадач
    @Test
    void calculateEpicStatusWhenMapOfSubtasksIsEmpty() {
        epic.clearSubtask();
        assertEquals(TaskStatuses.NEW, epic.getStatus());
    }

    // Все подзадачи со статусом NEW
    @Test
    void calculateEpicStatusWhenAllSubtaskIsStatusNew() {
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        assertEquals(TaskStatuses.NEW, epic.getStatus());
    }

    // Все подзадачи со статусом DONE
    @Test
    void calculateEpicStatusWhenAllSubtaskIsStatusDone() {
        subtask1 = new Subtask(subtask1.getId(), "subtask1", "status done", TaskStatuses.DONE, epic);
        subtask2 = new Subtask(subtask2.getId(), "subtask2", "status done", TaskStatuses.DONE, epic);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        assertEquals(TaskStatuses.DONE, epic.getStatus());
    }

    // Подзадачи со статусами NEW и DONE
    @Test
    void calculateEpicStatusShouldReturnStatusIsInProgressWhenSubtaskIsStatusNewAndDone() {
        subtask1 = new Subtask(subtask1.getId(), "subtask1", "status done", TaskStatuses.NEW, epic);
        subtask2 = new Subtask(subtask2.getId(), "subtask2", "status done", TaskStatuses.DONE, epic);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        assertEquals(TaskStatuses.IN_PROGRESS, epic.getStatus());
    }

    // Подзадачи со статусами NEW и DONE
    @Test
    void calculateEpicStatusWhenAllSubtaskIsStatusInProgress() {
        subtask1 = new Subtask(subtask1.getId(), "subtask1", "status done",
                TaskStatuses.IN_PROGRESS, epic);
        subtask2 = new Subtask(subtask2.getId(), "subtask2", "status done",
                TaskStatuses.IN_PROGRESS, epic);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        assertEquals(TaskStatuses.IN_PROGRESS, epic.getStatus());
    }
}