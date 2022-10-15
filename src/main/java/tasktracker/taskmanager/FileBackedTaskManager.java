package tasktracker.taskmanager;

import tasktracker.exceptions.ManagerSaveException;
import tasktracker.tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File fileName;

    public FileBackedTaskManager(File fileName) {
        this.fileName = fileName;
    }

    // Метод для сохранения тасок в файл
    public void save() {
        try (Writer fw = new FileWriter(fileName)) {
            fw.write("id,type,name,status,description,startTime,duration,epic\n");
            fw.write(String.format("%s", super.toString()));
            fw.write("\n");
            fw.write(String.format("%s", historyToString()));
        } catch (IOException e) {
            throw new ManagerSaveException("Что-то пошло не так.");
        }
    }

    public String historyToString() {
        StringBuilder sb = new StringBuilder();
        int historySize = getHistory().size();
        for (int i = 0; i < historySize; i++) {
            if (i == getHistory().size() - 1) {
                sb.append(getHistory().get(i).getId());
                continue;
            }
            sb.append(getHistory().get(i).getId() + ",");
        }
        return sb.toString();
    }

    // Метод для парсинга тасок из файла
    public Task fromString(String line) {
        String[] elem = line.split(",");
        final int id = Integer.parseInt(elem[0]);
        final TaskTypes type = TaskTypes.valueOf(elem[1]);
        final String name = elem[2];
        final TaskStatuses status = TaskStatuses.valueOf(elem[3]);
        final String description = elem[4];
        final String startTime = elem[5];
        final int duration = Integer.parseInt(elem[6]);

        switch (type) {
            case TASK:
                return new Task(id, name, description, status, startTime, duration);
            case EPIC:
                return new Epic(id, name, description, status, startTime, duration);
            case SUBTASK:
                /* Здесь для epicId оставил как было, без говорящего названия, т.к. возникает ArrayIndexOutOfBoundsException
                при считывании TASK и EPIC, потому что у них нет такой колонки. А когда настает очередь SUBTASK все ок,
                т.к. у него 5 элемент имеется. */
                return new Subtask(id, name, description, status, Integer.parseInt(elem[7]), startTime, duration);
        }
        throw new ManagerSaveException("Не удалось прочитать таску типа " + type);
    }

    // Метод для парсинга id тасок из файла
    public static List<Integer> historyFromString(String line) {
        return Stream.of(line.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    // Метод для выгрузки тасок из файла в объект менеджера
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try {
            String text = Files.readString(file.toPath()); // Сохраняем текст из файла в переменную text
            String[] lines = text.split(System.lineSeparator()); // И разбиваем текст на линии (таски)
            for (int i = 1; i < lines.length; i++) {
                if (lines[i].equals("")) {
                    fillHistory(lines[i + 1], taskManager); // наполняем историю
                    break;
                }
                switch (lines[i].split(",")[1]) {
                    case "TASK":
                        Task task = taskManager.fromString(lines[i]);
                        taskManager.createTask(task);
                        break;
                    case "EPIC":
                        Task epic = taskManager.fromString(lines[i]);
                        taskManager.createEpic((Epic) epic);
                        break;
                    case "SUBTASK":
                        Task subtask = taskManager.fromString(lines[i]);
                        taskManager.createSubtask((Subtask) subtask);
                }
            }
            updateCounter(taskManager); // обновляем счетчик для идентификаторов тасок
        } catch (IOException | RuntimeException e) {
            throw new ManagerSaveException("Что-то пошло не так.");
        }
        return taskManager;
    }

    // Метод для наполнения истории из файла
    public static void fillHistory(String line, FileBackedTaskManager taskManager) {
        for (Integer id : historyFromString(line)) {
            if (taskManager.getMapOfTasks().containsKey(id)) {
                taskManager.getTaskById(id);
            } else if (taskManager.getMapOfEpics().containsKey(id)) {
                taskManager.getEpicById(id);
            } else {
                taskManager.getSubtaskById(id);
            }
        }
    }

    // Метод для обновления счетчика id тасок после выгрузки тасок из файла
    public static void updateCounter(FileBackedTaskManager taskManager) {
        List<Integer> ids = new ArrayList<>(taskManager.getMapOfTasks().keySet());
        ids.addAll(taskManager.getMapOfEpics().keySet());
        ids.addAll(taskManager.getMapOfSubtasks().keySet());
        Task.setCountTaskId(Collections.max(ids));
    }

    // Ниже группа переопределённых методов класса родителя с добавлением метода сохранения тасок в файл
    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        save();
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        super.removeSubtaskById(subtaskId);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }
}
