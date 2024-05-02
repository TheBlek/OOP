package ru.nsu.kuklin.dsl;

class Student {
    String name;
    String nickname;
    int group;

    private void methodMissing(String name, Object value) {
        this.metaClass.setAttribute(this, name, value[0])
    }
}
