import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

class Main {
    private static final double NANOSECONDS_TO_SECONDS = 1_000_000_000.0;

    public static void main(String[] args) {
        Runnable task = prepareTask();

        // one platform thread == one OS/kernel thread
        // ~1MB stack each, 10k platform threads would exhaust memory
        Supplier<ExecutorService> platformThreadFactory = () -> Executors.newFixedThreadPool(200);
        runAndTime("Platform", platformThreadFactory, task);

        Supplier<ExecutorService> virtualThreadFactory = Executors::newVirtualThreadPerTaskExecutor;
        runAndTime("Virtual", virtualThreadFactory, task);
    }

    private static Runnable prepareTask() {
        return () -> {
            try {
                Thread.sleep(Duration.ofMillis(200)); // simulate network/DB call
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
    }

    private static void runAndTime(String label, Supplier<ExecutorService> factory, Runnable task) {
        System.out.printf("Starting task on %s Threads...%n", label);
        long start = System.nanoTime();
        try (ExecutorService executor = factory.get()) {
            for (int i = 0; i < 10_000; i++) {
                executor.execute(task);
            }
        }
        System.out.printf("Finished task on %s Threads.%n", label);
        System.out.printf("Estimated elapsed time on %s Threads: %.2f s%n", label, getElapsedTimeInSeconds(start));
    }

    private static double getElapsedTimeInSeconds(long start) {
        long elapsedTime = System.nanoTime() - start;
        return (double) elapsedTime / NANOSECONDS_TO_SECONDS;
    }
}
