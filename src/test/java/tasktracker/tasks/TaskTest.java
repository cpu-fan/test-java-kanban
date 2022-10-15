package tasktracker.tasks;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    Task task;
    private static final DateTimeFormatter FORMATTER_TEST = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @BeforeEach
    void setUp() {
        task = new Task("name", "desc", "25.08.2023 12:00", 60);
    }

    @AfterAll
    static void afterAll() {
        Task.setCountTaskId(0);
    }

    // Добавьте в тесты проверку новых полей.
    @Test
    void getStartTime() {
        LocalDateTime expected = LocalDateTime.parse("25.08.2023 12:00", FORMATTER_TEST);
        assertEquals(expected, task.getStartTime());
    }

    @Test
    void getEndTime() {
        LocalDateTime expected = task.getStartTime().plusMinutes(task.duration);
        assertEquals(expected, task.getEndTime());
    }

    @Test
    void getDuration() {
        assertEquals(60, task.getDuration());
    }

    @Test
    void setStartTime() {
        LocalDateTime expected = task.setStartTime("25.08.2023 18:00");
        task.startTime = task.setStartTime("25.08.2023 18:00");
        assertEquals(expected, task.getStartTime());
    }
}