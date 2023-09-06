package heapsort;

public class App {
    /**
     * Sorts array using heapsort
     * @param array Array to sort
     * @return Sorted array
     */
    public static int[] heapsort(int[] array) {
        for (int i = 0; i < array.length; i++) {
            // Sift up
            int cur = i;
            while (cur != 0 && array[(cur - 1) / 2] > array[cur]) {
                int parent = (cur - 1) / 2;
                int tmp = array[cur];
                array[cur] = array[parent];
                array[parent] = tmp;
                cur = parent;
            }
        }

        // array is a heap now
        int[] result = new int[array.length];
        int heap_len = array.length; for (int i = 0; i < array.length; i++, heap_len--) {
            result[i] = array[0];
            array[0] = array[heap_len - 1];
            // Sift down
            int cur = 0;
            while (cur != heap_len && 2 * cur + 1 < heap_len) {
                int left = 2 * cur + 1;
                int childValue = array[left];
                int child = left;
                if (left + 1 < heap_len && array[left + 1] < childValue) {
                    child = left + 1;
                }

                int tmp = array[child];
                array[child] = array[cur];
                array[cur] = tmp;
                cur = child;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int[] res = App.heapsort(new int[]{5, 4, 3, 2, 1});
        for (int value : res) {
            System.out.println(value);
        }
    }
}
