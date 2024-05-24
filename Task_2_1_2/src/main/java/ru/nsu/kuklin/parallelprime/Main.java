package ru.nsu.kuklin.parallelprime;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static void main(String[] args) throws SocketException {
        int maxConcurrentSegments = 10000;
        int segmentSize = 100;
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
        // TODO(theblek): extract switch for getting ip and broadcast ip into a function
        // TODO(theblek): test bigger segment sizes
        // TODO(theblek): think about scalability strategies to not have O(N) connections open

        Gson gson = new Gson();
        ArrayList<Segment> distributed = new ArrayList<>();
        BlockingQueue<Segment> toDistribute = new ArrayBlockingQueue<>(maxConcurrentSegments);
        ArrayList<Task> tasks = new ArrayList<>();

        var broadcast = getBroadcast(port);
        if (broadcast == null) {
            return;
        }

        try {
            // Broadcast that we entered the network and accepting connections
            var bytes = "hello".getBytes();
            broadcast.send(new DatagramPacket(bytes, bytes.length, localBroadcast, port));
        } catch (IOException e) {
            System.out.println("Failed to write to broadcast: " + e);
            return;
        }

        // Stdin thread
        new Thread(() -> {
            var scanner = new Scanner(System.in);
            while (true) {
                String line = "";
                try {
                    line = scanner.nextLine();
                } catch (NoSuchElementException ignored) {}
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
        // Just a communication queue. Should not require much capacity
        BlockingQueue<InetAddress> newUsers = new ArrayBlockingQueue<>(10);
        HashMap<InetAddress, Connection> connections = new HashMap<>();

        // Udp listening thread
        final var localIp = ip;
        new Thread(() -> {
            var receiving = new DatagramPacket(new byte[2048], 2048);
            while (true) {
                try {
                    broadcast.receive(receiving);
                    if (!receiving.getAddress().equals(localIp)) {
                        System.out.println("New user detected: " + receiving.getAddress());
                        newUsers.add(receiving.getAddress());
                    }
                } catch (IOException e) {
                    System.out.println("Failed to receive broadcast message");
                }
            }
        }).start();

        ServerSocketChannel server = null;
        try {
            server = ServerSocketChannel.open().bind(new InetSocketAddress(8090));
            server.configureBlocking(false);
        } catch (IOException e) {
            System.out.println("Failed to create server socket channel: " + e);
            return;
        }
        SelectionKey serverKey = null;
        try {
            serverKey = server.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            System.out.println("Channel was closed?... " + e);
            return;
        }
        while (true) {
            while (!newUsers.isEmpty()) {
                InetAddress user;
                try {
                    user = newUsers.take();
                } catch (InterruptedException e) {
                    System.out.println("Failed to take next user: " + e);
                    continue;
                }

                try {
                    SocketChannel channel = SocketChannel.open();
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    channel.connect(new InetSocketAddress(user, 8090));
                    System.out.println("Initiated a connection");
                } catch (IOException e) {
                    System.out.println("Failed to initiate connection: " + e);
                }
            }
            try {
                if (selector.selectNow() == 0) {
                    continue;
                }
            } catch (IOException e) {
                System.out.println("Failed to select: "  + e);
            }
            for (var key : selector.selectedKeys()) {
                if (key.equals(serverKey)) {
                    try {
                        var channel = server.accept();
                        var address = ((InetSocketAddress)channel.getRemoteAddress()).getAddress();
                        connections.put(address, new Connection(channel));
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        System.out.println("New connection!");
                    } catch (IOException e) {
                        System.out.println("Failed to accept connection: " + e);
                    }
                    continue;
                }
                var channel = (SocketChannel)key.channel();
                InetSocketAddress remote = null;
                try {
                    remote = (InetSocketAddress) channel.getRemoteAddress();
                } catch (IOException e) {
                    System.out.println("Failed to get remote address: " + e);
                }
                if (key.isConnectable()) {
                    connections.put(remote.getAddress(), new Connection(channel));
                    System.out.println("New connection!");
                }
                Connection conn = connections.get(remote.getAddress());
                assert conn != null;
                if (key.isReadable()) {
                    try {
                        int cnt = channel.read(conn.incoming);
                        if (cnt > 0) {
                            System.out.println("Read " + cnt);
                        }
                    } catch (IOException e) {
                        System.out.println("Failed to read from channel: " + e);
                        continue;
                    }
                    if (conn.incoming.position() >= 4 && conn.incoming.position() - 4 == conn.incoming.getInt(0)) {
                        System.out.println("Trying to iterprete the full message");
                        // Message is fully transmitted
                        try {
                            Segment s = gson.fromJson(
                                new String(conn.incoming.array(), 4, conn.incoming.position() - 4),
                                Segment.class
                            );
                            System.out.println("Received segment: " + s);
                        } catch (JsonSyntaxException e) {
                            System.out.println("it's not a segment i received: " + e);
                        }
                        conn.incoming.clear();
                    }
                }
                if (key.isWritable()) {
                    System.out.println("There is a writable key");
                    if (conn.outcoming.hasRemaining()) {
                        System.out.println("Updating outcoming buffer");
                        if (toDistribute.isEmpty()) {
                            System.out.println("Nothing to distribute");
                            continue;
                        }
                        try {
                            conn.outcoming.clear();
                            conn.outcoming.putInt(0); // First int - length
                            byte[] message = gson.toJson(toDistribute.take()).getBytes();
                            conn.outcoming.put(message);
                            conn.outcoming.putInt(0, message.length);
                            conn.outcoming.limit(conn.outcoming.position());
                            conn.outcoming.position(0);
                        } catch (InterruptedException e) {
                            System.out.println("Interrupted while getting a segment");
                        }
                    }
                    try {
                        int cnt = channel.write(conn.outcoming);
                        if (cnt > 0) {
                            System.out.println("Read " + cnt);
                        }
                    } catch (IOException e) {
                        System.out.println("Failed to write to socket: " + e);
                    }
                }
            }
        }
    }

    private static DatagramSocket getBroadcast(int port) {
        try {
            var broadcast = new DatagramSocket(port);
            broadcast.setBroadcast(true);
            return broadcast;
        } catch (IOException e) {
            System.out.print("Failed to open broadcast datagram socket: " + e);
            return null;
        }
    }
}
