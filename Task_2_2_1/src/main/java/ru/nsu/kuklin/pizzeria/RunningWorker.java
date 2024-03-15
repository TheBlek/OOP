package ru.nsu.kuklin.pizzeria;

import ru.nsu.kuklin.pizzeria.worker.Worker;

/**
 * Started worker representation.
 */
public record RunningWorker(Thread thread, Worker worker) {}
