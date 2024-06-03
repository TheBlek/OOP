package ru.nsu.kuklin.parallelprime;

import java.net.*;

public class Main {
    public static void main(String[] args) throws SocketException {
        var client = new Client();
        client.run(args);
    }
}
