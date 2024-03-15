package ru.nsu.kuklin.pizzeria;

import ru.nsu.kuklin.pizzeria.worker.Worker;

public record RunningWorker(Thread thread, Worker worker) {
}
