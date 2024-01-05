import java.util.Arrays;

public class ParallelSearchArray {

    public static int ParallelSearchDivide(int toSearch, int[] Array, int NumThreads){
        int [][] dividedArray = divideArray(NumThreads, Array);
        /*for (int [] array: dividedArray) {
            System.out.println(Arrays.toString(array));
        }*/

        Multithreading[] threads = new Multithreading[NumThreads];

        long time = System.nanoTime();
        for (int i = 0; i < NumThreads; i++) {
            threads[i] = new Multithreading(toSearch, dividedArray[i]);
            threads[i].start();
        }

        for (int i = 0; i < NumThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        long threadId = Multithreading.threadId;
        System.out.println("Time: " + (System.nanoTime() - time));
        return (int) threadId;

    }

    public static int ParallelSearch(int toSearch, int[] Array, int NumThreads){
        Multithreading[] threads = new Multithreading[NumThreads];

        long time = System.nanoTime();
        for (int i = 0; i < NumThreads; i++) {
            threads[i] = new Multithreading(toSearch, Array);
            threads[i].start();
        }

        for (int i = 0; i < NumThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        long threadId = Multithreading.threadId;
        System.out.println("Time: " + (System.nanoTime() - time));
        return (int) threadId;

    }

    public static int[][] divideArray(int numThreads, int[] array) {
        int[][] dividedArray = new int[numThreads][];
        int partSize = array.length / numThreads;
        int remainder = array.length % numThreads;

        int startIndex = 0;
        for (int i = 0; i < numThreads; i++) {
            int currentPartSize = partSize + (i < remainder ? 1 : 0);
            dividedArray[i] = new int[currentPartSize];

            System.arraycopy(array, startIndex, dividedArray[i], 0, currentPartSize);

            startIndex += currentPartSize;
        }

        return dividedArray;
    }

}

class Multithreading extends Thread {
    public static long threadId = -1;

    int toSearch;
    int[] array;

    public Multithreading(int toSearch, int[] array){
        this.toSearch = toSearch;
        this.array = array;
    }
    public void run()
    {
        try {
            for (int element: array) {
                if (threadId > -1){
                    break;
                }
                if (element == toSearch) {
                    threadId = Thread.currentThread().getId();
                    break;
                }
            }


        }
        catch (Exception e) {
            // Throwing an exception
            System.out.println("Exception is caught");
        }
    }
}
