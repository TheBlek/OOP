package ru.nsu.kuklin.dsl;

import lombok.Data;

@Data
public class TaskRunResult {
    private boolean success;
    private String stdOut;

    public TaskRunResult(boolean b, String string) {
        success = b;
        stdOut = string;
    }
}
