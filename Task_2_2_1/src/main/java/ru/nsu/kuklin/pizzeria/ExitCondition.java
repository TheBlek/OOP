package ru.nsu.kuklin.pizzeria;

/**
 * Interface for blocking methods that pizzeria might want to wait for.
 */
public interface ExitCondition {
    /**
     * This call should block until arbitrary condition is met.
     */
    void waitForExitCondition();
}
