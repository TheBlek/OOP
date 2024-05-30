package ru.nsu.kuklin.parallelprime;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
public class Task {
    final UUID id;
    int segmentCount;

    Task(UUID id, int segmentCount) {
        this.id = id;
        this.segmentCount = segmentCount;
    }
}
