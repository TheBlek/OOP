#!/usr/bin/env sh

mkdir -p build
javac ./app/src/main/java/task_1_1_1/App.java -d build
cd build
java task_1_1_1.App

