package tasktracker.taskmanager;

import tasktracker.exceptions.NonExistentTaskException;
import tasktracker.exceptions.TaskTimeValidationException;
import tasktracker.historymanager.HistoryManager;
import tasktracker.managers.Managers;
import tasktracker.tasks.Epic;
import tasktracker.tasks.Subtask;
import tasktracker.tasks.Task;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {

    // Хранение каждого типа задачи в отдельной коллекции
    private final HashMap<Integer, Task> mapOfTasks = new HashMap<>();
    private final HashMap<Integer, Epic> mapOfEpics = new HashMap<>();
    private final HashMap<Integer, Subtask> mapOfSubtasks = new HashMap<>();

    // Хранение истории просмотра задач
    private final HistoryManager history = Managers.getDefaultHistory();

    // Приоритизированный список задач
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparingInt(Task::getId));

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    public void taskTimeValidation(Task task) {
        for (Task prioritizedTask : prioritizedTasks) {
            if (prioritizedTask.getStartTime() == null || task.getStartTime() == null) {
                break;
            }
            if (task.getId() == prioritizedTask.getId()) {
                continue;
            }
            if (prioritizedTask.getEndTime().isAfter(task.getStartTime())
                    && task.getEndTime().isAfter(prioritizedTask.getStartTime())) {
                throw new TaskTimeValidationException("Задача с таким временем старта уже существует. " +
                        "Проигнорированная задача - ", task);
            }
        }
    }

    // Методы для помещения созданной задачи в коллекцию своего типа
    @Override
    public void createTask(Task task) {
        taskTimeValidation(task); // насколько я понял, ты хотел, чтобы таска с одинаковым временем не добавлялась и в мапу?)
        mapOfTasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void createEpic(Epic epic) {
        mapOfEpics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        taskTimeValidation(subtask);
        mapOfSubtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
    }

    // Методы для получения задачи по ее идентификатору из соответствующей коллекции
    @Override
    public Task getTaskById(int id) {
        if (mapOfTasks.containsKey(id)) {
            history.addToHistory(mapOfTasks.get(id));
            return mapOfTasks.get(id);
        } else {
            System.out.println("Задачи с таким идентификатором не существует");
            return null;
        }
    }

    @Override
    public Epic getEpicById(int id) {
        if (mapOfEpics.containsKey(id)) {
            history.addToHistory(mapOfEpics.get(id));
            return mapOfEpics.get(id);
        } else {
            System.out.println("Эпика с таким идентификатором не существует");
            return null;
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (mapOfSubtasks.containsKey(id)) {
            history.addToHistory(mapOfSubtasks.get(id));
            return mapOfSubtasks.get(id);
        } else {
            System.out.println("Подзадачи с таким идентификатором не существует");
            return null;
        }
    }

    // Методы для получения списка всех задач из соответствующей коллекции
    @Override
    public ArrayList<Task> getListAllTasks() {
        return new ArrayList<>(mapOfTasks.values());
    }

    @Override
    public ArrayList<Epic> getListAllEpics() {
        return new ArrayList<>(mapOfEpics.values());
    }

    @Override
    public ArrayList<Subtask> getListAllSubtasks() {
        return new ArrayList<>(mapOfSubtasks.values());
    }

    // Геттеры для мап тасок всех типов
    protected HashMap<Integer, Task> getMapOfTasks() {
        return mapOfTasks;
    }

    protected HashMap<Integer, Epic> getMapOfEpics() {
        return mapOfEpics;
    }

    protected HashMap<Integer, Subtask> getMapOfSubtasks() {
        return mapOfSubtasks;
    }

    // Методы для обновления задач соответствующей коллекции
    @Override
    public void updateTask(Task task) {
        if (task == null) {
            throw new NonExistentTaskException("Задачи с таким идентификатором не существует");
        }

        final Task oldTask = mapOfTasks.get(task.getId());
        if (oldTask == null) {
            throw new NonExistentTaskException("Задачи с таким идентификатором не существует");
        }

        prioritizedTasks.remove(oldTask);
        taskTimeValidation(task);
        mapOfTasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            throw new NonExistentTaskException("Подзадачи с таким идентификатором не существует");
        }
        mapOfEpics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new NonExistentTaskException("Подзадачи с таким идентификатором не существует");
        }

        int oldEpicId = mapOfSubtasks.get(subtask.getId()).getEpicId(); // получаем и сохраняем старый эпик
        boolean isNewEpic = subtask.getEpicId() != oldEpicId; // проверка на новый эпик
        if (isNewEpic) {
            mapOfEpics.get(oldEpicId).deleteSubtask(subtask.getId()); // удаление подзадачи из старого эпика
        }
        mapOfEpics.get(subtask.getEpicId()).addSubtask(subtask); // обновляем подзадачу в ее эпике и пересчитываем статус

        Subtask oldSubtask = mapOfSubtasks.get(subtask.getId());
        if (oldSubtask == null) {
            return;
        }

        prioritizedTasks.remove(oldSubtask);
        taskTimeValidation(subtask);
        mapOfSubtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
    }

    // Методы для удаления задачи по идентификатору соответствующей коллекции
    @Override
    public void removeTaskById(int taskId) {
        if (mapOfTasks.containsKey(taskId)) {
            history.remove(taskId); // удаляем задачу из истории
            prioritizedTasks.remove(mapOfTasks.get(taskId));
            mapOfTasks.remove(taskId); // удаляем саму задачу
        } else {
            throw new NonExistentTaskException("Задачи с таким идентификатором не существует");
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        if (mapOfEpics.containsKey(epicId)) {
            history.remove(epicId);
            mapOfEpics.remove(epicId); // удаляем сам эпик из таблицы эпиков

            // удаляем подзадачи связанные с этим эпиком со списка подзадач
            ArrayList<Integer> subtaskIds = new ArrayList<>(); // отдельный список для id подзадач удаленного эпика
            for (Subtask value : mapOfSubtasks.values()) {
                if (value.getEpicId() == epicId) {
                    subtaskIds.add(value.getId());
                }
            }

            for (Integer subtaskId : subtaskIds) {
                history.remove(subtaskId); // удаляем подзадачи из истории
                prioritizedTasks.remove(mapOfSubtasks.get(subtaskId));
                mapOfSubtasks.remove(subtaskId); // проходимся по списку подзадач и удаляем собранные id на строке 107
            }
            /* Примечание: эти приседания с дополнительным списком ArrayList<Integer> subtaskIds для id подзадач
            * удаленного эпика сделаны, чтобы избежать ошибки ConcurrentModificationException, которая возникает когда
            * я сразу пытаюсь использовать на 107 строке конструкцию mapOfSubtasks.remove(value.getId()); */
        } else {
            throw new NonExistentTaskException("Эпика с таким идентификатором не существует");
        }
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        if (mapOfSubtasks.containsKey(subtaskId)) {
            int epicId = mapOfSubtasks.get(subtaskId).getEpicId(); // получаем id эпика, в котором содержится подзадача
            mapOfEpics.get(epicId).deleteSubtask(subtaskId); // удаляем эту подзадачу в ее эпике и пересчитываем статус эпика
            prioritizedTasks.remove(mapOfSubtasks.get(subtaskId));
            mapOfSubtasks.remove(subtaskId); // удаляем саму подзадачу
            history.remove(subtaskId); // удаляем подзадачу из истории
        } else {
            throw new NonExistentTaskException("Эпика с таким идентификатором не существует");
        }
    }

    // Методы для удаления всех задач в соответствующей коллекции
    @Override
    public void deleteAllTasks() {
        // удаляем все таски из истории
        for (Integer taskId : mapOfTasks.keySet()) {
            history.remove(taskId);
            prioritizedTasks.remove(mapOfTasks.get(taskId));
        }
        mapOfTasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        // удаляем все эпики и их сабтаски из истории
        for (Subtask subtask : mapOfSubtasks.values()) {
            history.remove(subtask.getEpicId());
            history.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        mapOfEpics.clear();
        mapOfSubtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        // удаляем все сабтаски из истории
        for (Integer subtaskId : mapOfSubtasks.keySet()) {
            history.remove(subtaskId);
            prioritizedTasks.remove(mapOfSubtasks.get(subtaskId));
        }
        mapOfSubtasks.clear(); // очищаем сам список сабтасок
        // также очищаем подзадачи в эпиках
        for (Epic epic : mapOfEpics.values()) {
            epic.getEpicSubtasks().clear();
        }
    }

    // Метод для получения списка всех подзадач определённого эпика.
    @Override
    public HashMap<Integer, Subtask> getListAllSubtasksByEpic(Epic epic) {
        return epic.getEpicSubtasks();
    }

    // Метод для получения списка истории просмотренных задач
    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }


    @Override
    public List<Task> getAllTasksAllTypes() {
        return Stream.of(mapOfTasks.values(), mapOfEpics.values(), mapOfSubtasks.values())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Task value : mapOfTasks.values()) {
            sb.append(value + "\n");
        }
        for (Task value : mapOfEpics.values()) {
            sb.append(value + "\n");
        }
        for (Task value : mapOfSubtasks.values()) {
            sb.append(value + "\n");
        }
        return sb.toString();
    }
}
