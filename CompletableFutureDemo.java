import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class CompletableFutureDemo {

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        // Крок 1: Асинхронно згенерувати масив з 10 випадкових цілих чисел
        CompletableFuture<List<Integer>> initialArrayFuture = CompletableFuture.supplyAsync(() -> {
            long start = System.currentTimeMillis();
            List<Integer> array = new ArrayList<>();
            IntStream.range(0, 10).forEach(i -> array.add(ThreadLocalRandom.current().nextInt(1, 101)));
            sleep(500); // Імітація часу обчислення
            System.out.println("Початковий масив: " + array);
            System.out.println("Час, витрачений на створення початкового масиву: " + (System.currentTimeMillis() - start) + "мс");
            return array;
        });

        // Крок 2: Асинхронно збільшити кожен елемент масиву на 5
        CompletableFuture<List<Integer>> incrementedArrayFuture = initialArrayFuture.thenApplyAsync(array -> {
            long start = System.currentTimeMillis();
            List<Integer> incrementedArray = new ArrayList<>();
            array.forEach(element -> incrementedArray.add(element + 5));
            sleep(500); // Імітація часу обчислення
            System.out.println("Збільшений масив: " + incrementedArray);
            System.out.println("Час, витрачений на збільшення масиву: " + (System.currentTimeMillis() - start) + "мс");
            return incrementedArray;
        });

        // Крок 3: Асинхронно знайти факторіал суми елементів обох масивів
        CompletableFuture<Void> factorialFuture = incrementedArrayFuture.thenCombineAsync(initialArrayFuture, (incrementedArray, initialArray) -> {
            long start = System.currentTimeMillis();
            int sumInitial = initialArray.stream().mapToInt(Integer::intValue).sum();
            int sumIncremented = incrementedArray.stream().mapToInt(Integer::intValue).sum();
            int totalSum = sumInitial + sumIncremented;
            long factorial = calculateFactorial(totalSum);
            sleep(500); // Імітація часу обчислення
            System.out.println("Сума початкового масиву: " + sumInitial);
            System.out.println("Сума збільшеного масиву: " + sumIncremented);
            System.out.println("Факторіал загальної суми (" + totalSum + "): " + factorial);
            System.out.println("Час, витрачений на обчислення факторіалу: " + (System.currentTimeMillis() - start) + "мс");
            return null;
        });

        // Очікування завершення всіх завдань
        factorialFuture.join();

        System.out.println("Загальний час виконання: " + (System.currentTimeMillis() - startTime) + "мс");
    }

    private static long calculateFactorial(int number) {
        long result = 1;
        for (int i = 2; i <= number; i++) {
            result *= i;
        }
        return result;
    }

    private static void sleep(int millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
