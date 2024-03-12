package ru.nsu.kuklin.pizzeria.io;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WorkerLogger implements ILogger {
    public WorkerLogger(Class<?> classObj, int id) {
        this.className = classObj.getSimpleName();
        this.id = id;
    }

    public void log(String message) {
        LocalDateTime now = LocalDateTime.now();
        System.out.printf("[%s %d][%s]: %s\n", className, id, dtf.format(now), message);
    }
    private final String className;
    private final int id;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
}
