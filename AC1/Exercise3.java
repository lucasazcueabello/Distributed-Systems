import java.util.*;

public class Exercise3 {
    private LinkedList<Integer> list;
    private Scanner scanner;
    private Integer number;
    private Boolean found;

    public Exercise3 () {
        this.scanner = new Scanner(System.in);
        this.list = new LinkedList<>();

        this.found = false;
    }


    public void threadSearch(int[] values) throws InterruptedException {

        for (int value : values) {
            this.list.add(value);
        }

        System.out.println("Enter a position to find");
        int position = scanner.nextInt();
        number = this.list.get(position);

        Iterator<Integer> iteratorBackward = this.list.descendingIterator();
        Iterator<Integer> iteratorForward = this.list.listIterator();

        Thread thread1 = new Thread(()->{
            int aux = 0;
            while(iteratorForward.hasNext() && !this.found){
                aux = iteratorForward.next();
                this.found = aux == this.number;
                if (found && aux == this.number) System.out.println("Forward iterator has found the number " + this.number);
            }
        });
        Thread thread2 = new Thread(()->{
            int aux = 0;
            while(iteratorBackward.hasNext() && !this.found){
                aux = iteratorBackward.next();
                this.found = aux == this.number;
                if (this.found && aux == this.number) System.out.println("Backward iterator has found the number " + this.number);
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }

}

