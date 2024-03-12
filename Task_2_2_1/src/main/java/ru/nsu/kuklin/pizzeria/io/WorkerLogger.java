package ru.nsu.kuklin.pizzeria.io;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WorkerLogger implements ILogger {
    public WorkerLogger(Class<?> classObj, String name) {
        this.className = classObj.getSimpleName();
        this.name = name;
    }

    public void log(String message) {
        LocalDateTime now = LocalDateTime.now();
        System.out.printf("[%s %s][%s]: %s\n", className, name, dtf.format(now), message);
    }
    private final String className;
    private final String name;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
}
