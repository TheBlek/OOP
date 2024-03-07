package ru.nsu.kuklin.pizzeria;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    private static State state;

    public static void main(String[] args) throws IOException {
        state = new State(100);
        ArrayList<Thread> workers = new ArrayList<>();
        WorkerProvider[] factories = { new DefaultBakerProvider(state, 5) };
        for (var factory : factories){
            for (var worker : factory) {
                workers.add(new Thread(worker));
                workers.getLast().start();
            }
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        br.readLine();
        System.out.print("Enter String");
        String s = br.readLine();
        System.out.print("Enter Integer:");

        for (var worker : workers) {
            worker.interrupt();
        }
    }
}