package ru.nsu.kuklin.parallelprime;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static void main(String[] args) throws SocketException {
        int maxConcurrentSegments = 10000;
        int segmentSize = 10000;
        int port = 8091;
        InetAddress ip = null;
        InetAddress localBroadcast = null;
        switch (args.length) {
            case 1:
                try {
                    ip = InetAddress.getByName(args[0]);
                    for (var it = NetworkInterface.getNetworkInterfaces(); it.hasMoreElements(); ) {
                        var netInterface = it.nextElement();
                        for (var address : netInterface.getInterfaceAddresses()) {
                            if (address.getAddress().equals(ip)) {
                                localBroadcast = address.getBroadcast();
                                break;
                            }
                        }
                        if (localBroadcast != null) {
                            break;
                        }
                    }
                    if (localBroadcast == null) {
                        System.out.println("Given address isn't an address on this machine");
                        break;
                    }
                } catch (UnknownHostException e) {
                    System.out.println("IP address specified in the first parameter is invalid");
                    return;
                }
                break;
            case 0:
                var addresses = new ArrayList<InterfaceAddress>();
                for (var it = NetworkInterface.getNetworkInterfaces(); it.hasMoreElements(); ) {
                    addresses.addAll(it.nextElement().getInterfaceAddresses());
                }
                System.out.println("Choose which network to use for broadcast: ");
                for (int i = 0; i < addresses.size(); i++) {
                    System.out.printf("%d: %s\n", i, addresses.get(i));
                }
                var scanner = new Scanner(System.in);
                var interfaceAddr = addresses.get(scanner.nextInt());
                ip = interfaceAddr.getAddress();
                localBroadcast = interfaceAddr.getBroadcast();
                break;
            default:
                System.out.println("Too many parameters");
                return;
        }
        ArrayList<Segment> distributed = new ArrayList<>();
        BlockingQueue<Segment> toDistribute = new ArrayBlockingQueue<>(maxConcurrentSegments);
        ArrayList<Task> tasks = new ArrayList<>();
        // TODO(theblek): broadcast on a startup
        // TODO(theblek): receive broadcasts and connect to that machine
        // TODO(theblek): think about scalability strategies to not have O(N) connections open

        System.out.println(ip + ":" + port);
        var broadcast = getBroadcast(ip, port);
        if (broadcast == null) {
            return;
        }

        var broadcastSocket = new InetSocketAddress(localBroadcast, port);
        try {
            // Broadcast that we entered the network and accepting connections
            System.out.println(broadcastSocket);
            broadcast.send(new DatagramPacket("hello".getBytes(), 5, localBroadcast, port));
        } catch (IOException e) {
            System.out.println("Failed to write to broadcast: " + e);
            return;
        }

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
            Selector selector = null;
            try {
                selector = Selector.open();
            } catch (IOException e) {
                System.out.println("Failed to open selector");
                return;
            }
//            HashMap<SocketAddress, AsynchronousSocketChannel> connections = new HashMap<>();
//            // Start listening for new connections
//            try (var server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(28000))) {
//                server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
//                    @Override
//                    public void completed(AsynchronousSocketChannel result, Void attachment) {
//                        try {
//                            connections.put(result.getRemoteAddress(), result);
//                            System.out.println("New connection from " + result.getRemoteAddress());
//                        } catch (IOException e) {
//                            System.out.println("Failed to get remote addr");
//                        }
//                    }
//
//                    @Override
//                    public void failed(Throwable exc, Void attachment) {}
//                });
//            } catch (IOException e) {
//                System.out.println("Failed to open server socket");
//                return;
//            }

//            SelectionKey broadcastKey = null;
//            try {
//                broadcastKey = broadcast.getChannel().register(selector, SelectionKey.OP_READ);
//            } catch (ClosedChannelException e) {
//                System.out.println("Channel closed?..");
//                return;
//            }

        // Udp listening thread
        new Thread(() -> {
            var receiving = new DatagramPacket(new byte[2048], 2048);
            while (true) {
                try {
                    broadcast.receive(receiving);
                    System.out.println("New user detected: " + receiving.getAddress());
                } catch (IOException e) {
                    System.out.println("Failed to receive broadcast message");
                }
            }
        }).start();
    }

    private static DatagramSocket getBroadcast(InetAddress ip, int port) {
        try {
            var broadcast = new DatagramSocket(new InetSocketAddress(ip, port));
//            broadcast.configureBlocking(false);
            return broadcast;
        } catch (IOException e) {
            System.out.print("Failed to open broadcast datagram socket: " + e);
            return null;
        }
    }
}
