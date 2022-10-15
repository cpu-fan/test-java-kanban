package tasktracker.main;

import tasktracker.api.HttpTaskServer;
import tasktracker.api.KVServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new KVServer().start();
    }
}
