package ru.nsu.kuklin.dsl;

public record TaskRunConfig(String task, boolean excludeTests, boolean getStdOut, boolean clean) {
    public TaskRunConfig(String task) {
        this(task, false, false, false);
    }

    public TaskRunConfig withClean() {
        return new TaskRunConfig(task, excludeTests, getStdOut, true);
    }

    public TaskRunConfig withExcludeTests() {
        return new TaskRunConfig(task, true, getStdOut, clean);
    }

    public TaskRunConfig withGetStdOut() {
        return new TaskRunConfig(task, excludeTests, true, clean);
    }
}
