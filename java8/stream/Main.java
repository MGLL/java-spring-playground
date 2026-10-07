import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Main {
    private static final List<Product> BASKET = Arrays.asList(
        new Product("Banana", "Fruits", 7),
        new Product("Beef", "Meat", 1),
        new Product("Chicken", "Meat", 2),
        new Product("Lettuce", "Vegetables", 2),
        new Product("Spinach", "Vegetables", 20),
        new Product("Apple", "Fruits", 3)
    );

    private static final List<List<String>> ORDERS = Arrays.asList(
        Arrays.asList("Spinach", "Chicken"),
        Arrays.asList("Banana")
    );

    public static void main(String[] args) {
        streamComparisonWithLoop();
        streamLaziness();
        streamShortCircuiting();
        streamMapAndFlatMap();
        streamReduce();
        streamCollectors();
        streamPartitioning();
        streamPrimitive();
        streamSingleUse();
    }

    private static void streamComparisonWithLoop() {
        // Loop approach
        List<String> productNames = new ArrayList<>();
        for(int i = 0; i < BASKET.size(); i++) {
            Product product = BASKET.get(i);
            if(product.getQuantity() >= 3){
                productNames.add(product.getName().toUpperCase());
            }
        }
        System.out.println(productNames);

        // Stream approach
        List<String> streamedProductNames = BASKET.stream()
            .filter(product -> product.getQuantity() >= 3) // select
            .map(Product::getName) // transform: extract name
            .map(String::toUpperCase) // transform: uppercase
            .collect(Collectors.toList()); // collect result into a List
        System.out.println(streamedProductNames);
    }

    private static void streamLaziness() {
        Stream<String> pipeline = BASKET.stream()
            .peek(product -> System.out.println("source -> " + product.getName()))
            .filter(product -> product.getQuantity() >= 3)
            .map(Product::getName)
            .peek(name -> System.out.println("kept -> " + name)); // nothing run
        System.out.println("Pipeline is prepared.");
        pipeline.collect(Collectors.toList()); // run the pipeline with terminal operation '.collect()'
        System.out.println("\nDone executing.");
    }

    private static void streamShortCircuiting() {
        // Stop when the limit is reached
        System.out.println("limit(5): " + Stream.iterate(0, n -> n + 1)
            .limit(5)
            .collect(Collectors.toList())); // return [0, 1, 2, 3, 4]
        // or when the first element is found.
        System.out.println("findFirst(): " + Stream.iterate(1, n -> n * 2)
            .filter(n -> n > 8)
            .findFirst()); // return Optional[16]
    }

    private static void streamMapAndFlatMap() {
        // map: one element in, one element out, a list stays a list
        System.out.println("\nmap, order sizes: " + ORDERS.stream()
            .map(List::size)
            .collect(Collectors.toList()));
        // flatMap: each element becomes a stream, all of them flattened into one
        System.out.println("flatMap, all items: " + ORDERS.stream()
            .flatMap(List::stream)
            .collect(Collectors.toList()));
    }

    private static void streamReduce() {
        // With an identity, the result is never empty (no Optional)
        System.out.println("\nreduce with identity 0: " + BASKET.stream()
            .map(Product::getQuantity)
            .reduce(0, Integer::sum));

        // Without one, an empty stream would have no result (Optional)
        // compare to find largest item in the basket
        Optional<Product> largestItem = BASKET.stream()
            .reduce((left, right) -> left.getQuantity() >= right.getQuantity() ? left : right);
        System.out.println("reduce without identity: " + largestItem);

        // Alternative
        System.out.println("(alternative) reduce without identity: " + BASKET.stream()
            .max(Comparator.comparingInt(Product::getQuantity)));
    }

    private static void streamCollectors() {
        // Collectors: groupingBy takes a downstream collector of its own
        Map<String, Long> countByCategory = BASKET.stream()
            .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));
        System.out.println("\ndownstream counting: " + countByCategory);

        Map<String, Integer> quantityByCategory = BASKET.stream()
            .collect(Collectors.groupingBy(Product::getCategory, Collectors.summingInt(Product::getQuantity)));
        System.out.println("downstream summingInt: " + quantityByCategory);

        // two products share the category "Meat", so the two-argument toMap has two
        // values for one key and refuses to pick between them
        try {
            BASKET.stream().collect(Collectors.toMap(Product::getCategory, Product::getName));
        } catch (IllegalStateException e) {
            System.out.println("toMap without a merge: " + e);
        }
        // the third argument is the merge function, and it says what to do instead
        System.out.println("toMap with a merge: " + BASKET.stream()
            .collect(Collectors.toMap(Product::getCategory, Product::getName, (kept, discarded) -> kept)));
    }

    private static void streamPartitioning() {
        Predicate<Product> isPlentiful = product -> product.getQuantity() >= 3;

        // partitioningBy splits on a predicate, so the keys are always exactly false and true
        Map<Boolean, List<Product>> byPlenty = BASKET.stream()
            .collect(Collectors.partitioningBy(isPlentiful));
        System.out.println("\npartitioningBy: " + byPlenty);
        System.out.println("the false side: " + byPlenty.get(false));

        // like groupingBy, it takes a downstream collector of its own
        System.out.println("partitioningBy + counting: " + BASKET.stream()
            .collect(Collectors.partitioningBy(isPlentiful, Collectors.counting())));

        // both keys are there even when one side matches nothing, which is the
        // difference from grouping on a boolean: groupingBy omits the key it never saw
        Predicate<Product> isHuge = product -> product.getQuantity() > 100;
        System.out.println("partitioningBy keys, no match: " + BASKET.stream()
            .collect(Collectors.partitioningBy(isHuge))
            .keySet()); // [false, true], and get(true) is an empty list, never null
        System.out.println("groupingBy keys, no match: " + BASKET.stream()
            .collect(Collectors.groupingBy(isHuge::test))
            .keySet()); // [false]
    }

    private static void streamPrimitive() {
        // an IntStream does not box, and adds sum() and summaryStatistics()
        System.out.println("\nIntStream sum: " + BASKET.stream()
            .mapToInt(Product::getQuantity)
            .sum());
        IntSummaryStatistics stats = BASKET.stream()
            .mapToInt(Product::getQuantity)
            .summaryStatistics(); // count, sum, min, average and max in a single pass
        System.out.println("one pass: " + stats);
    }

    private static void streamSingleUse() {
        Stream<Product> consumed = BASKET.stream();
        System.out.println("\nfirst terminal operation: " + consumed.count());
        try {
            consumed.count();
        } catch (IllegalStateException e) {
            System.out.println("second terminal operation: " + e);
        }
    }
}

class Product {
    private final String name;
    private final String category;
    private final int quantity;

    Product(String name, String category, int quantity) {
        this.name = name;
        this.category = category;
        this.quantity = quantity;
    }

    public String getName() { return name; }

    public String getCategory() { return category; }

    public int getQuantity() { return quantity; }

    @Override
    public String toString() {
        return name + " x" + quantity;
    }
}
