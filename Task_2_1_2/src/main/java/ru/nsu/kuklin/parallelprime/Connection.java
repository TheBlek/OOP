package ru.nsu.kuklin.parallelprime;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Connection {
    public SocketChannel channel;
    public ByteBuffer incoming;
    public ByteBuffer outcoming;

    public Connection(SocketChannel channel) {
        this.channel = channel;
        incoming = ByteBuffer.allocate(2048);
        outcoming = ByteBuffer.allocate(2048);
        outcoming.limit(0);
        assert !outcoming.hasRemaining();
    }
}
