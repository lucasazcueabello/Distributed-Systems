import java.util.Random;

private class Main {
    public static void main(String[] args) {
        int threadId;
        int[] arrayOfInts = new int[10000];
        Random random = new Random();
        Exercise3 ex3 = new Exercise3();
        Exercise7 ex7 = new Exercise7();
        Exercise8 ex8 = new Exercise8();


        for (int i = 0; i < arrayOfInts.length; i++) {
            arrayOfInts[i] = random.nextInt(Integer.MAX_VALUE); // Generates random positive integers
        }

        /*
        Exercise 3
         */
        try {
            ex3.threadSearch(arrayOfInts);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        /*
        Exercise 4
         */
        System.out.println("Ex4");
        threadId = ParallelSearchArray.ParallelSearchDivide(arrayOfInts[260], arrayOfInts, 50);
        System.out.println("Thread Id:" + threadId);
        /*
        Exercise 5
         */
        threadId = ParallelSearchArray.ParallelSearch(arrayOfInts[260], arrayOfInts, 50);
        System.out.println("Thread Id:" + threadId);

        /*
        Exercise 7
         */
        int[] numbers = new int[]{45, 78, 12, 22, 35, 78, 53};
        long startTime = System.nanoTime();
        ex7.threadMergeSort(numbers);
        long endTime = System.nanoTime();
        long elapsedTimeNanos = endTime - startTime;
        double elapsedTimeMillis = (double) elapsedTimeNanos / 1_000_000; // Convert nanoseconds to milliseconds
        System.out.println("(Thread)Elapsed time: " + elapsedTimeMillis + " milliseconds");

        /*
        Exercise 8
         */
        startTime = System.nanoTime();
        ex8.mergeSort(numbers);
        endTime = System.nanoTime();
        elapsedTimeNanos = endTime - startTime;
        elapsedTimeMillis = (double) elapsedTimeNanos / 1_000_000; // Convert nanoseconds to milliseconds
        System.out.println("Elapsed time: " + elapsedTimeMillis + " milliseconds");

    }
}
