package ru.nsu.kuklin.parallelprime;

import lombok.Data;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.UUID;

@Data
class Segment implements Serializable {
    UUID jobId;
    int numCount;
    int[] nums;
    InetAddress master;
    boolean hasPrimes;

    public Segment(int size, UUID jobId, InetAddress master) {
        nums = new int[size];
        numCount = 0;
        this.master = master;
        this.jobId = jobId;
    }
}