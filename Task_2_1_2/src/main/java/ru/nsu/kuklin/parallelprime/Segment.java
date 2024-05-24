package ru.nsu.kuklin.parallelprime;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
class Segment implements Serializable {
    UUID jobId;
    int numCount;
    int[] nums;
//    String client;

    public Segment(int size, UUID jobId) {
        nums = new int[size];
        numCount = 0;
//        client = "";
        this.jobId = jobId;
    }
}