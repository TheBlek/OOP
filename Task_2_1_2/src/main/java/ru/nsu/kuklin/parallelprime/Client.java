package ru.nsu.kuklin.parallelprime;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Client {
    public void run(String[] args) throws SocketException {
        IPConfig config = IPConfig.getFromArgs(args);
        if (config == null) {
            return;
        }
        // TODO(theblek): test bigger segment sizes
        // TODO(theblek): think about scalability strategies to not have O(N) connections open
        // TODO(theblek): ping clients to check if they are alive

        // Server thread
        Selector selector = null;
        try {
            selector = Selector.open();
        } catch (IOException e) {
            System.out.println("Failed to open selector");
            return;
        }

        ServerSocketChannel server = null;
        try {
            server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress(8090));
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            System.out.println("Failed to create server socket channel: " + e);
            return;
        }

        // Compute thread
        new Thread(() -> {
            while (true) {
                try {
                    if (toCalculate.isEmpty()) {
                        Segment s = toDistribute.poll(100, TimeUnit.MILLISECONDS);
                        if (s != null) {
                            s.master = config.ip();
                            toCalculate.put(s);
                        }
                    }
                    Segment s = toCalculate.poll(100, TimeUnit.MILLISECONDS);
                    if (s != null) {
                        System.out.println("Calculating on my own!!!");
                        // Copy of an array, yeah, ineffective. But whatever. Who cares :)
                        s.hasComposites = (new SequentialDetector()).detect(Arrays.copyOfRange(s.nums, 0, s.numCount));
                        calculated.add(s);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        }).start();

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
                        var segment = new Segment(segmentSize, id, config.ip());
                        int segmentCount = 0;
                        while (fileScanner.hasNextInt()) {
                            segment.nums[segment.numCount] = fileScanner.nextInt();
                            segment.numCount += 1;
                            if (segment.numCount == segmentSize) {
                                toDistribute.add(segment);
                                segmentCount += 1;
                                segment = new Segment(segmentSize, id, config.ip());
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

        // Udp listening thread
        new Thread(() -> {
            var broadcast = getBroadcast(port);
            if (broadcast == null) {
                return;
            }

            try {
                // Broadcast that we entered the network and accepting connections
                var bytes = "h".getBytes();
                broadcast.send(new DatagramPacket(bytes, bytes.length, config.broadcast(), port));
                System.out.println("Sent hello broadcast");
            } catch (IOException e) {
                System.out.println("Failed to write to broadcast: " + e);
                return;
            }

            var ackBytes = "a".getBytes();

            var receiving = new DatagramPacket(new byte[2048], 2048);
            while (true) {
                try {
                    broadcast.receive(receiving);
                    var message = new String(receiving.getData());
                    System.out.println("Got message from udp: " + message);
                    System.out.println("Got message from udp: " + message);
                    System.out.println("-----");
                    if (!receiving.getAddress().equals(config.ip())) {
                        switch (message) {
                            case "h" -> {
                                System.out.println("New user detected: " + receiving.getAddress());
                                newUsers.add(receiving.getAddress());
                            }
                            case "hc" ->
                                broadcast.send(new DatagramPacket(ackBytes, ackBytes.length, receiving.getAddress(), port));
                            case "a" -> submitHealthCheck(receiving.getAddress());
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Failed to receive broadcast message");
                }
            }
        }).start();

        // Healthcheck thread
//        new Thread(() -> {
//            var broadcast = getBroadcast(port + 1);
//            if (broadcast == null) {
//                return;
//            }
//            var bytes = "hc".getBytes();
//            while (true) {
//                try {
//                    Thread.sleep(1000);
//                    resetHealthCheck();
//                    try {
//                        broadcast.send(new DatagramPacket(bytes, bytes.length, config.broadcast(), port));
//                    } catch (IOException e) {
//                        System.out.println("Failed to write to broadcast: " + e);
//                        return;
//                    }
//                } catch (InterruptedException e) {
//                    System.out.println("Who interrupted my sleep??!");
//                    return;
//                }
//            }
//        }).start();

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
                    channel.connect(new InetSocketAddress(user, 8090));
                    channel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    System.out.println("Initiated a connection on " + user);
                } catch (IOException e) {
                    System.out.println("Failed to initiate connection: " + e);
                }
            }
            while (!calculated.isEmpty() && !connections.containsKey(calculated.peek().master)) {
                // Calculated segments which master died
                try {
                    calculated.take();
                } catch (InterruptedException e) {
                    System.out.println("My take was interrupted");
                }
            }

            try {
                if (selector.selectNow() == 0) {
                    continue;
                }
            } catch (IOException e) {
                System.out.println("Failed to select: "  + e);
            }

            var iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                var key = iter.next();
                iter.remove();
                if (key.isAcceptable()) {
                    try {
                        var channel = ((ServerSocketChannel) key.channel()).accept();
                        channel.socket().setTcpNoDelay(true);
                        var address = ((InetSocketAddress)channel.getRemoteAddress()).getAddress();
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        submitNewConnection(address, new Connection(channel));
                        System.out.println("Accepted new connection!");
                    } catch (IOException e) {
                        System.out.println("Failed to accept connection: " + e);
                    }
                    continue;
                }
                var channel = (SocketChannel)key.channel();
                InetSocketAddress remote = null;
                try {
                    remote = (InetSocketAddress) channel.getRemoteAddress();
                    assert remote != null;
                } catch (IOException e) {
                    System.out.println("Failed to get remote address: " + e);
                    continue;
                }

                if (key.isConnectable()) {
                    try {
                        channel.finishConnect();
                        channel.socket().setTcpNoDelay(true);
                    } catch (IOException e) {
                        System.out.println("Failed to finish connection: " + e);
                    }
                    submitNewConnection(remote.getAddress(), new Connection(channel));
                    System.out.println("Finished new connection!");
                }
                if (!channel.isConnected()) {
                    continue;
                }
                assert remote.getAddress() != null;
                Connection conn = connections.get(remote.getAddress());
                if (conn == null) {
                    // This remote is dead
                    key.cancel();
                    continue;
                }
                assert conn != null;

                if (key.isReadable()) {
                    try {
                        channel.read(conn.incoming);
                    } catch (IOException e) {
                        System.out.println("Failed to read from channel: " + e);
                        continue;
                    }

                    if (conn.incoming.position() >= 4 && conn.incoming.position() - 4 >= conn.incoming.getInt(0)) {
                        var size = conn.incoming.getInt(0);
                        System.out.println("Trying to iterprete the full message");
                        // Message is fully transmitted
                        try {
                            Segment segment = gson.fromJson(
                                new String(conn.incoming.array(), 4, size),
                                Segment.class
                            );
                            if (!segment.master.equals(config.ip())) {
                                toCalculate.add(segment);
                                System.out.println("Received segment from " + segment.master + ": " + segment);
                            } else {
                                handleCalculatedSegment(segment);
                            }
                        } catch (JsonSyntaxException e) {
                            System.out.println("it's not a segment i received: " + e);
                        }
                        var bytes = conn.incoming.array();
                        var left = conn.incoming.position() - 4 - size;
                        conn.incoming.clear();
                        conn.incoming.put(bytes, 4 + size,  left);
                    }
                }
                if (key.isWritable()) {
                    if (!conn.outcoming.hasRemaining()) {
                        Segment data = null;
                        if (!toDistribute.isEmpty()) {
                            try {
                                data = toDistribute.take();
                                distributed.put(data, remote.getAddress());
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted while getting a segment");
                            }
                        } else if (!calculated.isEmpty()) {
                            try {
                                data = calculated.peek();
                                if (data.master.equals(remote.getAddress())) {
                                    data = calculated.take();
                                }
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted while getting a segment");
                            }
                            if (data.master.equals(config.ip())) {
                                handleCalculatedSegment(data);
                                continue;
                            }
                        }
                        if (data == null) {
                            continue;
                        }
                        conn.outcoming.clear();
                        conn.outcoming.putInt(0); // First int - length
                        byte[] message = gson.toJson(data).getBytes();
                        conn.outcoming.put(message);
                        conn.outcoming.putInt(0, message.length);
                        conn.outcoming.limit(conn.outcoming.position());
                        conn.outcoming.position(0);
                    }
                    try {
                        channel.write(conn.outcoming);
                    } catch (IOException e) {
                        System.out.println("Failed to write to socket: " + e);
                    }
                }
            }
        }
    }

    private synchronized void resetHealthCheck() {
        var toRemove = new ArrayList<InetAddress>();
        for (var entry : connections.entrySet()) {
            if (!entry.getValue().health) {
                toRemove.add(entry.getKey());
            }
            entry.getValue().health = false;
        }
        for (var addr : toRemove) {
            System.out.println("Connection with " + addr + "is dead. or they are");
            connections.remove(addr);
        }
    }

    private synchronized void submitHealthCheck(InetAddress addr) {
        connections.get(addr).health = true;
    }

    private synchronized void submitNewConnection(InetAddress addr, Connection conn) {
        connections.put(addr, conn);
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

    private void handleCalculatedSegment(Segment segment) {
        if (segment.hasComposites) {
            System.out.printf("Task %s finished. Composites found\n", segment.jobId);
//                                    distributed
//                                        .entrySet()
//                                        .stream()
//                                        .filter((s) -> s.getKey().jobId == segment.jobId)
//                                        .forEach((s) -> {
//                                            try {
//                                                var bytes = s.getKey().jobId.toString().getBytes();
//                                                broadcast.send(new DatagramPacket(bytes, bytes.length, s.getValue(), port));
//                                            } catch (IOException e) {
//                                                System.out.println("Failed to send cancellation message");
//                                            }
//                                        });
            // TODO(theblek): cancel the job somehow
        } else {
            segment.hasComposites = false;
            distributed.remove(segment);
            var taskM = tasks.stream().filter((t) -> t.id.equals(segment.jobId)).findFirst();
            if (taskM.isPresent()) {
                var task = taskM.get();
                task.segmentCount -= 1;
                System.out.printf("%d segments left for task %s\n", task.segmentCount, task.id);
                if (task.segmentCount == 0) {
                    System.out.printf("Task %s finished. No composite numbers found\n", task.id);
                }
            }
        }
    }

    int maxConcurrentSegments = 10000;
    int segmentSize = 100;
    int port = 8091;
    BlockingQueue<InetAddress> newUsers = new ArrayBlockingQueue<>(10);
    volatile HashMap<InetAddress, Connection> connections = new HashMap<>();
    Gson gson = new Gson();
    HashMap<Segment, InetAddress> distributed = new HashMap<>();
    BlockingQueue<Segment> toCalculate = new ArrayBlockingQueue<>(maxConcurrentSegments);
    BlockingQueue<Segment> calculated = new ArrayBlockingQueue<>(maxConcurrentSegments);
    BlockingQueue<Segment> toDistribute = new ArrayBlockingQueue<>(maxConcurrentSegments);
    ArrayList<Task> tasks = new ArrayList<>();
}
