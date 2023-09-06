#!/usr/bin/env sh

javadoc -d docs -sourcepath ./app/src/main/java heapsort
mkdir -p build
javac ./app/src/main/java/heapsort/App.java -d build
cd build
java heapsort.App

