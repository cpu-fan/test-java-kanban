package tasktracker.historymanager;

import tasktracker.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    // Хэш-таблица для хранения связки id и узлов
    private final Map<Integer, Node> nodeMap = new HashMap<>();

    private Node head;
    private Node tail;

    public void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
    }

    @Override
    public void addToHistory(Task task) {
        if (nodeMap.containsKey(task.getId())) {
            remove(task.getId());
            nodeMap.remove(task.getId());
        }
        linkLast(task);
        nodeMap.put(task.getId(), tail);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        Node node = head;
        while (node != null) {
            historyList.add(node.task);
            node = node.next;
        }
        return historyList;
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.get(id);
        removeNode(node);
    }

    private void removeNode(Node node) {
        if (node == null) return;
        
        final Node prevLink = node.prev;
        final Node nextLink = node.next;

        if (prevLink == null) {
            head = nextLink;
        } else {
            prevLink.next = nextLink;
            node.next = null; // обнуляем ссылку
        }

        if (nextLink == null) {
            tail = prevLink;
        } else {
            nextLink.prev = prevLink;
            node.prev = null; // обнуляем ссылку
        }

        node.task = null;
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "nodeMap=" + nodeMap +
                '}';
    }

    public static class Node {
        private Node prev;
        private Task task;
        private Node next;

        public Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }
}
