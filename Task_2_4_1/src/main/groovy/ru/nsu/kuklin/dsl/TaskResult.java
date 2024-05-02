package ru.nsu.kuklin.dsl;

public record TaskResult(Student student, boolean builds, int testCount, int failCount, int skipCount, int coverage, CheckstyleResult checkstyle) {}