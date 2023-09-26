package ru.nsu.kuklin.heapsort;

import java.util.Arrays;
import java.util.Random;
/**
 * Main class with heapsort function.
 */

public class App {
    /**
     * Sorts array using heapsort.
     *
     * @param array Array to sort
     */
    public static void heapsort(int[] array) {
        int[] heap = array.clone();
        for (int i = 0; i < array.length; i++) {
            // Sift up
            int cur = i;
            while (cur != 0 && heap[(cur - 1) / 2] > heap[cur]) {
                int parent = (cur - 1) / 2;
                int tmp = heap[cur];
                heap[cur] = heap[parent];
                heap[parent] = tmp;
                cur = parent;
            }
        }

        // array is a heap now
        int heapLen = heap.length; 
        for (int i = 0; i < heap.length; i++, heapLen--) {
            array[i] = heap[0];
            heap[0] = heap[heapLen - 1];
            // Sift down
            int cur = 0;
            while (cur < heapLen - 1 && 2 * cur + 1 < heapLen) {
                int left = 2 * cur + 1;
                int childValue = heap[left];
                int child = left;
                if (left + 1 < heapLen && heap[left + 1] < childValue) {
                    child = left + 1;
                    childValue = heap[left + 1];
                }
                if (childValue >= heap[cur]) {
                    break;
                }

                int tmp = heap[child];
                heap[child] = heap[cur];
                heap[cur] = tmp;
                cur = child;
            }
        }
    }

    /**
     * Stub main function.
     *
     * @param args Cmd args
     */
    public static void main(String[] args) {
        System.out.println("Hello world");
    }
}
