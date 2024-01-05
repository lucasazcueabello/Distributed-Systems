import java.util.Arrays;
import java.util.concurrent.*;

public class Exercise7 {
    public Exercise7 () {}
    public int[] threadMergeSort(int[] array){
        if (array == null || array.length < 2) {
            return array;  // Base case: array is already sorted or empty
        }

        int mid = array.length / 2;

        // Split the array into two halves
        int[] left = Arrays.copyOfRange(array, 0, mid);
        int[] right = Arrays.copyOfRange(array, mid, array.length);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Callable<int[]> leftSortTask = () -> threadMergeSort(left);
        Callable<int[]> rightSortTask = () -> threadMergeSort(right);

        Future<int[]> leftSortResult = executor.submit(leftSortTask);
        Future<int[]> rightSortResult = executor.submit(rightSortTask);

        int[] leftSorted, rightSorted;
        try {
            leftSorted = leftSortResult.get();
            rightSorted = rightSortResult.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return array; // Return the original array in case of an error
        }

        executor.shutdown();

        return merge(array, leftSorted, rightSorted);
    }

    private int[] merge(int[] arr, int[] left, int[] right) {
        int i = 0, j = 0, k = 0;

        // Merge left and right arrays into arr
        while (i < left.length && j < right.length) {
            if (left[i] <= right[j]) {
                arr[k++] = left[i++];
            } else {
                arr[k++] = right[j++];
            }
        }

        while (i < left.length) {
            arr[k++] = left[i++];
        }

        while (j < right.length) {
            arr[k++] = right[j++];
        }
        System.out.println(Arrays.toString(arr));
        return arr;
    }
}