package ru.nsu.kuklin.parallelprime;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
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
                            System.out.println("Taken segment " + s.id + " for self-calculating");
                            s.master = config.ip();
                            toCalculate.put(s);
                        }
                    }
                    Segment s = toCalculate.poll(100, TimeUnit.MILLISECONDS);
                    if (s != null) {
                        // Copy of an array, yeah, ineffective. But whatever. Who cares :)
                        s.hasComposites = (new SequentialDetector()).detect(Arrays.copyOfRange(s.nums, 0, s.numCount));
                        calculated.put(s);
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
                        var segment = new Segment(segmentSize, id, 0, config.ip());
                        int segmentCount = 0;
                        while (fileScanner.hasNextInt()) {
                            segment.nums[segment.numCount] = fileScanner.nextInt();
                            segment.numCount += 1;
                            if (segment.numCount == segmentSize) {
                                toDistribute.add(segment);
                                segmentCount += 1;
                                segment = new Segment(segmentSize, id, segmentCount, config.ip());
                            }
                        }
                        if (segment.numCount > 0) {
                            toDistribute.add(segment);
                            segmentCount += 1;
                        }
                        var segments = new HashSet<Integer>();
                        for (int i = 0; i < segmentCount; i++) {
                            segments.add(i);
                        }
                        tasks.put(id, segments);
                    } catch (FileNotFoundException e) {
                        System.out.println("File not found");
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
            } catch (IOException e) {
                System.out.println("Failed to write to broadcast: " + e);
                return;
            }

            var ackBytes = "a".getBytes();

            var receiving = new DatagramPacket(new byte[2048], 2048);
            while (true) {
                try {
                    broadcast.receive(receiving);
                    String message = new String(receiving.getData(), receiving.getOffset(), receiving.getLength());
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
        new Thread(() -> {
            var broadcast = getBroadcast(8100);
            if (broadcast == null) {
                return;
            }
            var bytes = "hc".getBytes();
            while (true) {
                try {
                    Thread.sleep(1000);
                    resetHealthCheck();
                    try {
                        broadcast.send(new DatagramPacket(bytes, bytes.length, config.broadcast(), port));
                    } catch (IOException e) {
                        System.out.println("Failed to write to broadcast: " + e);
                        return;
                    }
                } catch (InterruptedException e) {
                    System.out.println("Who interrupted my sleep??!");
                    return;
                }
            }
        }).start();

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
            // Self-calculated segments and
            // Calculated segments which master died
            while (!calculated.isEmpty()) {
                if (calculated.peek().master.equals(config.ip())) {
                    try {
                        handleCalculatedSegment(calculated.take(), null);
                    } catch (InterruptedException e) {
                        System.out.println("My take was interrupted");
                    }
                } else if (!connections.containsKey(calculated.peek().master)) {
                    try {
                        calculated.take();
                    } catch (InterruptedException e) {
                        System.out.println("My take was interrupted");
                    }
                } else {
                    break;
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
                InetSocketAddress remote;
                try {
                    remote = (InetSocketAddress) channel.getRemoteAddress();
                    assert remote != null;
                } catch (IOException e) {
                    System.out.println("Failed to get remote address: " + e);
                    continue;
                }

                if (key.isConnectable()) {
                    try {
                        if (!channel.finishConnect()) {
                            key.cancel();
                            continue;
                        }
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
                synchronized(this) {
                    Connection conn = connections.get(remote.getAddress());
                    if (conn == null) {
                        // This remote is dead
                        key.cancel();
                        continue;
                    }

                    if (key.isReadable()) {
                        try {
                            int cnt = channel.read(conn.incoming);
                            System.out.println("Read " + cnt + " bytes from " + remote.getAddress());
                        } catch (IOException e) {
                            System.out.println("Failed to read from channel: " + e);
                            continue;
                        }

                        if (conn.incoming.position() >= 4 && conn.incoming.position() - 4 >= conn.incoming.getInt(0)) {
                            var size = conn.incoming.getInt(0);
                            System.out.println(conn.incoming.position() + " " + size);
                            // Message is fully transmitted
                            try {
                                Segment segment = gson.fromJson(
                                    new String(conn.incoming.array(), 4, size),
                                    Segment.class
                                );
                                if (!segment.master.equals(config.ip())) {
                                    try {
                                        toCalculate.put(segment);
                                    } catch (InterruptedException e) {
                                        System.out.println("Put interrupted...");
                                    }
                                    System.out.println("Received segment " + segment.id + " from " + remote.getAddress() + " with master " + segment.master);
                                } else {
                                    handleCalculatedSegment(segment, remote.getAddress());
                                }
                            } catch (JsonSyntaxException e) {
                                System.out.println("it's not a segment i received: " + e);
                            }
                            var bytes = conn.incoming.array();
                            var left = conn.incoming.position() - 4 - size;
                            conn.incoming.clear();
                            conn.incoming.put(bytes, 4 + size, left);
                            System.out.println(left + " bytes left");
                        }
                    }
                    if (key.isWritable()) {
                        if (!conn.outcoming.hasRemaining()) {
                            Segment data = null;
                            if (!toDistribute.isEmpty()) {
                                try {
                                    data = getSegmentFor(remote.getAddress());
                                } catch (InterruptedException e) {
                                    // Either this remote is already dead
                                    // Or we were interrupted
                                    continue;
                                }
                                addToDistributed(data, remote.getAddress());
                            } else if (!calculated.isEmpty()) {
                                try {
                                    data = calculated.peek();
                                    if (data.master.equals(remote.getAddress())) {
                                        data = calculated.take();
                                    }
                                } catch (InterruptedException e) {
                                    System.out.println("Interrupted while getting a segment");
                                }
                                assert !data.master.equals(config.ip());
                            }
                            if (data == null) {
                                continue;
                            }
                            System.out.println("Sending segment " + data.id + " to " + remote.getAddress());
                            conn.outcoming.clear();
                            conn.outcoming.putInt(0); // First int - length
                            byte[] message = gson.toJson(data).getBytes();
                            conn.outcoming.put(message);
                            conn.outcoming.putInt(0, message.length);
                            conn.outcoming.limit(conn.outcoming.position());
                            conn.outcoming.position(0);
                        }
                        try {
                            int cnt = channel.write(conn.outcoming);
                            System.out.println("Wrote " + cnt + " bytes");
                        } catch (IOException e) {
                            System.out.println("Failed to write to socket: " + e);
                        }
                    }
                }
            }
        }
    }

    private synchronized Segment getSegmentFor(InetAddress remote) throws InterruptedException {
        if (!connections.containsKey(remote)) {
            throw new InterruptedException();
        }
        var data = toDistribute.take();
        System.out.println("Giving a segment " + data.id + " to remote");
        return data;
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
            System.out.println("Connection with " + addr + " is dead. or they are");
            connections.remove(addr);
            for (var segment : distributed.get(addr)) {
                try {
                    toDistribute.put(segment);
                } catch (InterruptedException e) {
                    System.out.println("Redistributing was interrupted");
                }
            }
            distributed.remove(addr);
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

    private synchronized void removeFromDistributed(Segment segment, InetAddress addr) {
        distributed.get(addr).remove(segment);
    }

    private synchronized void addToDistributed(Segment segment, InetAddress addr) {
        if (!distributed.containsKey(addr)) {
            distributed.put(addr, new ArrayList<>());
        }
        distributed.get(addr).add(segment);
    }

    private void handleCalculatedSegment(Segment segment, InetAddress from) {
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
            if (from != null) {
                removeFromDistributed(segment, from);
            }
            if (!tasks.containsKey(segment.jobId)) {
               return;
            }
            var task = tasks.get(segment.jobId);
            task.remove(segment.id);
            System.out.println("Received segment with id = " + segment.id + " from " + from);
            System.out.printf("%d segments left for task %s\n", task.size(), segment.jobId);
            if (task.isEmpty()) {
                System.out.printf("Task %s finished. No composite numbers found\n", segment.jobId);
                tasks.remove(segment.jobId);
            }
        }
    }

    int maxConcurrentSegments = 10000;
    int segmentSize = 100;
    int port = 8091;
    BlockingQueue<InetAddress> newUsers = new ArrayBlockingQueue<>(10);
    volatile HashMap<InetAddress, Connection> connections = new HashMap<>();
    Gson gson = new Gson();
    volatile HashMap<InetAddress, ArrayList<Segment>> distributed = new HashMap<>();
    BlockingQueue<Segment> toCalculate = new ArrayBlockingQueue<>(maxConcurrentSegments);
    BlockingQueue<Segment> calculated = new ArrayBlockingQueue<>(maxConcurrentSegments);
    BlockingQueue<Segment> toDistribute = new ArrayBlockingQueue<>(maxConcurrentSegments);
    Map<UUID, Set<Integer>> tasks = new HashMap<>();
}
