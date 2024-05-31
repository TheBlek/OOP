package ru.nsu.kuklin.parallelprime;

import lombok.Data;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.UUID;

@Data
class Segment implements Serializable {
    UUID jobId;
    int id;
    int numCount;
    int[] nums;
    InetAddress master;
    boolean hasComposites = false;

    public Segment(int size, UUID jobId, int id, InetAddress master) {
        nums = new int[size];
        numCount = 0;
        this.master = master;
        this.jobId = jobId;
        this.id = id;
    }
}