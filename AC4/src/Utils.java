import java.util.Random;

public class Utils {

    public static <T> int getRandomElement(int[] array) {
        Random random = new Random();
        int randomIndex = random.nextInt(array.length);
        return array[randomIndex];
    }
}
