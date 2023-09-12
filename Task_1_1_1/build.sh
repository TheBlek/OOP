#!/usr/bin/env sh

javadoc -d docs ./app/src/main/java/ru.nsu.kuklin.heapsort/App.java
mkdir -p build
javac ./app/src/main/java/heapsort/App.java -d build
cd build
java heapsort.App

