package ru.nsu.kuklin.dsl;

/**
 * Results of a task for any given student. Contains all the information collected by the program.
 */
public record TaskResult(
    Student student,
    boolean builds,
    int testCount,
    int failCount,
    int skipCount,
    int coverage,
    CheckstyleResult checkstyle,
    boolean softPass,
    boolean hardPass
) {}