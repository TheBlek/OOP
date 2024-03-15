package ru.nsu.kuklin.pizzeria.io;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementation of ILogger interface that log the message from a particular worker.
 */
public class WorkerLogger implements ILogger {
    /**
     * Construct from worker's class and name.
     */
    public WorkerLogger(Class<?> classObj, String name) {
        this.className = classObj.getSimpleName();
        this.name = name;
    }

    @Override
    public void log(String message) {
        LocalDateTime now = LocalDateTime.now();
        System.out.printf("[%s %s][%s]: %s\n", className, name, dtf.format(now), message);
    }
    private final String className;
    private final String name;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static WorkerLogger instance;
}
