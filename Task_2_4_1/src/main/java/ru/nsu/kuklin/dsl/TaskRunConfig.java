package ru.nsu.kuklin.dsl;

record TaskRunConfig(String task, boolean excludeTests) {
    public TaskRunConfig(String task) {
        this(task, false);
    }

    public TaskRunConfig withExcludeTests() {
        return new TaskRunConfig(task, true);
    }

}
