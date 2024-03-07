package ru.nsu.kuklin.pizzeria;

public class WorkerLogger implements ILogger {
    public WorkerLogger(Class<?> classObj, int id) {
        this.classObj = classObj;
        this.id = id;
    }

    public void log(String message) {
        System.out.printf("[%s %d]: %s\n", classObj.getSimpleName(), id, message);
    }
    private final Class<?> classObj;
    private final int id;
}
