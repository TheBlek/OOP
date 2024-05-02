package ru.nsu.kuklin.dsl
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Task {
    String name;
    int maxMark;
    LocalDate softDeadline;
    LocalDate hardDeadline;

    private void methodMissing(String name, Object value) {
        this.metaClass.setAttribute(this, name, value[0])
    }

    private void softDeadline(String date) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.uu")
        softDeadline = LocalDate.parse(date, format)
    }

    private void hardDeadline(String date) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.uu")
        hardDeadline = LocalDate.parse(date, format)
    }
}
