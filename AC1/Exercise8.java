import java.util.Arrays;
import java.util.concurrent.*;

public class Exercise8 {
    public Exercise8 () {}
    public int[] mergeSort(int[] array){
        if (array == null || array.length < 2) {
            return array;  // Base case: array is already sorted or empty
        }

        int mid = array.length / 2;

        // Split the array into two halves
        int[] left = Arrays.copyOfRange(array, 0, mid);
        int[] right = Arrays.copyOfRange(array, mid, array.length);

        int[] leftSorted = mergeSort(left);
        int[] rightSorted = mergeSort(right);

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