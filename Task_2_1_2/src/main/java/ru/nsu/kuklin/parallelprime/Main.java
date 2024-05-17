package ru.nsu.kuklin.parallelprime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static void main(String[] args) {
        int maxConcurrentSegments = 10000;
        int segmentSize = 10000;
        ArrayList<Segment> distributed = new ArrayList<>();
        BlockingQueue<Segment> toDistribute = new ArrayBlockingQueue<>(maxConcurrentSegments);
        ArrayList<Task> tasks = new ArrayList<>();

        // Stdin thread
        new Thread(() -> {
            var scanner = new Scanner(System.in);
            while (true) {
                var line = scanner.nextLine();
                if (line.startsWith("/job")) {
                    var filename = line.split(" ", 2)[1];
                    try {
                        var fileScanner = new Scanner(new File(filename));
                        var id = java.util.UUID.randomUUID();
                        var segment = new Segment(segmentSize, id);
                        int segmentCount = 0;
                        while (fileScanner.hasNextInt()) {
                            segment.nums[segment.numCount] = fileScanner.nextInt();
                            segment.numCount += 1;
                            if (segment.numCount == segmentSize) {
                                toDistribute.add(segment);
                                segmentCount += 1;
                                segment = new Segment(segmentSize, id);
                            }
                        }
                        if (segment.numCount > 0) {
                            toDistribute.add(segment);
                            segmentCount += 1;
                        }
                        tasks.add(new Task(id, segmentCount));
                    } catch (FileNotFoundException e) {
                        System.out.println("File not found");
                    }
                } else if (line.startsWith("/status")) {
                    for (var segment : toDistribute) {
                        System.out.println(segment);
                    }
                } else if (line.startsWith("/quit")) {
                    return;
                }
            }
        }).start();

        // Server thread
        new Thread(() -> {
            Selector selector = null;
            try {
                selector = Selector.open();
            } catch (IOException e) {
                System.out.println("Failed to open selector");
                return;
            }
            HashMap<SocketAddress, AsynchronousSocketChannel> connections = new HashMap<>();
            // Start listening for new connections
            try (var server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(28000))) {
                server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
                    @Override
                    public void completed(AsynchronousSocketChannel result, Void attachment) {
                        try {
                            connections.put(result.getRemoteAddress(), result);
                            System.out.println("New connection from " + result.getRemoteAddress());
                        } catch (IOException e) {
                            System.out.println("Failed to get remote addr");
                        }
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {}
                });
            } catch (IOException e) {
                System.out.println("Failed to open server socket");
                return;
            }

            DatagramChannel broadcast = null;
            try {
                broadcast = DatagramChannel.open().bind(new InetSocketAddress("0.0.0.0", 29000));
                broadcast.configureBlocking(false);
            } catch (IOException e) {
                System.out.print("Failed to open broadcast datagram socket");
                return;
            }

            try {
                broadcast.register(selector, SelectionKey.OP_READ);
            } catch (ClosedChannelException e) {
                System.out.println("Channel closed?..");
            }
        }).start();
    }
}
