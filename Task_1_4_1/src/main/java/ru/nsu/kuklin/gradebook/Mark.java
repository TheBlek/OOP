package ru.nsu.kuklin.gradebook;

public enum Mark {
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    public int getMark() {
        return mark;
    }

    Mark(int m) {
        mark = m;
    }

    private int mark;
}

