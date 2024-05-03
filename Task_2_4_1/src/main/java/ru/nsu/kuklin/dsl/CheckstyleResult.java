package ru.nsu.kuklin.dsl;

/**
 * Results of the checkstyle.
 * CLEAN - no warnings.
 * WARNING - some warnings but not much, exit code is still 0.
 * ERROR - many warnings, exit code is 1.
 */
public enum CheckstyleResult {
    ERROR,
    WARNING,
    CLEAN,
}
