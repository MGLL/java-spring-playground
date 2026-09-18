import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;

class Main {
    private static final List<Product> BASKET = Arrays.asList(
        new Product("Spinach", "Vegetable", 3, 0.1),
        new Product("Chicken", "Meat", 1, 12),
        new Product("Banana", "Fruit", 6, 0.2)
    );

    public static void main(String[] args) {
        // Demo for built-in interface
        builtInFunctionalInterfacesDemo();

        // Demo for built-in interface as lambda
        builtInFunctionalInterfacesAsLambdaDemo();

        // Virtual extension method forEach on collections
        BASKET.forEach(System.out::println);

        // Custom functional interface
        functionalInterfaceDemo();

        // Anonymous class vs Lambda
        anonymousClassVsLambda();

        // `this` demonstration
        new ThisDemo().show();

        // Target typing, lambda has no type of its own, the context give one.
        Function<Integer, Integer> boxedDouble = n -> n * 2;
        IntUnaryOperator primitiveDouble = n -> n * 2; // no boxing
        System.out.printf("%nsame body, two types: %d and %d%n", 
            boxedDouble.apply(21), primitiveDouble.applyAsInt(21));

        // Composition, small named pieces beat one dense lambda: 
        // each part is readable, and reusable on its own.
        Function<Product, String> function = Product::getName;
        Predicate<Product> isMeat = product -> "Meat".equals(product.getCategory());
        Predicate<Product> isScarce = product -> product.getQuantity() < 3;
        Predicate<Product> notScarceMeat = isMeat.and(isScarce).negate();
        Function<Product, String> shout = function.andThen(String::toUpperCase);
        System.out.println();
        for (Product product : BASKET) {
            System.out.printf("%-8s notScarceMeat=%-5b shout=%s%n",
                product.getName(), notScarceMeat.test(product), shout.apply(product));
        }

        // Chaining
        List<Product> sorted = new ArrayList<>(BASKET);
        Comparator<Product> byCategoryThenQuantity = Comparator
            .comparing(Product::getCategory) // primary comparator
            .thenComparingInt(Product::getQuantity) // secondary comparator
            .reversed();
        sorted.sort(byCategoryThenQuantity);
        System.out.println("\nchained comparator: " + sorted);

        // Capture demo
        int threshold = 3;
        Predicate<Product> inStock = product -> product.getQuantity() >= threshold;
        // threshold = 5; // uncomment and the line above stops compiling as threshold would no longer be effectively final
        System.out.println("\ncaptured threshold " + threshold + ": " + inStock.test(BASKET.get(0)));

        // The bound form dereferences its receiver when the reference is created, not when it is called
        Product missing = null;
        try {
            Supplier<String> eager = missing::getName;
            System.out.println("never reached: " + eager.get());
        } catch (NullPointerException e) {
            System.out.println("bound receiver evaluated at creation: " + e);
        }
    }

    private static void builtInFunctionalInterfacesDemo() {
        // Function interface, represent a transformation from one value to another.
        Function<String, Integer> strLength = new Function<String, Integer>() {
            @Override
            public Integer apply(String s) {
                return s.length();
            }
        };
        BASKET.forEach(product -> System.out.println(
            "Product name length: " + strLength.apply(product.getName())));

        // BiFunction, similar to Function interface, but takes two input values.
        BiFunction<Product, Product, Integer> max = new BiFunction<Product, Product, Integer>() {
            @Override 
            public Integer apply(Product firstProduct, Product secondProduct) {
                return Math.max(firstProduct.getQuantity(), secondProduct.getQuantity());
            }
        };
        System.out.println("Max product quantity: " + max.apply(BASKET.get(0), BASKET.get(1)));

        // Consumer interface operates on a single input and returns nothing
        Consumer<List<Product>> printProducts = new Consumer<List<Product>>() {
            @Override 
            public void accept(List<Product> products) {
                System.out.println("Product in basket: ");
                products.forEach(System.out::println);
            }
        };
        printProducts.accept(BASKET);
        
        // Supplier interface produces a value without taking any 
        Supplier<String> idGenerator = new Supplier<String>() {
            @Override
            public String get() {
                return UUID.randomUUID().toString();
            }
        };
        System.out.println("Generate ID: " + idGenerator.get());

        // Predicate interface evaluates a condition and returns a boolean
        Predicate<List<Product>> basketHaveMoreThanFiveItems = new Predicate<List<Product>>() {
            @Override
            public boolean test(List<Product> basket) {
                return basket.size() > 5;
            }
        };
        System.out.println("Basket have more than 5 items: " + basketHaveMoreThanFiveItems.test(BASKET));
        System.out.println("------");
    }

    private static void builtInFunctionalInterfacesAsLambdaDemo() {
        Function<Product, Integer> strLength = product -> product.getName().length();
        BASKET.forEach(product -> System.out.println(
            "(lambda) Product name length: " + strLength.apply(product)));

        BiFunction<Product, Product, Integer> max = (firstProduct, secondProduct) -> 
            Math.max(firstProduct.getQuantity(), secondProduct.getQuantity());
        System.out.println("(lambda) Max product quantity: " + max.apply(BASKET.get(0), BASKET.get(1)));

        Consumer<List<Product>> printProducts = products -> {
            System.out.println("(lambda) Product in basket: ");
            products.forEach(System.out::println);
        };
        printProducts.accept(BASKET);

        Supplier<String> idGenerator = () -> UUID.randomUUID().toString();
        System.out.println("(lambda) Generated ID: " + idGenerator.get());

        Predicate<List<Product>> basketHaveMoreThanFiveItems = basket -> basket.size() > 5;
        System.out.println("(lambda) Basket have more than 5 items: " + basketHaveMoreThanFiveItems.test(BASKET));
        System.out.println("------");
    }

    private static void functionalInterfaceDemo() {
        BasketCalculator calc = new BasketCalculator();
        BasketCalculationOperation count = products -> products.stream().mapToDouble(p -> p.getQuantity()).sum();
        BasketCalculationOperation sumPrice = products -> products.stream().mapToDouble(p -> p.getQuantity() * p.getPrice()).sum();
        System.out.printf("Count quantity: %.2f %n", calc.calculate(BASKET, count));
        System.out.printf("Sum price: %.2f %n", calc.calculate(BASKET, sumPrice));

        // example with factory
        TriFunction<String, String, Integer, Product> factory = Product::new;
        System.out.println("custom interface factory: " + factory.apply("Broom", "Apparel", 2));

    }

    private static void anonymousClassVsLambda() {
        // Anonymous class: five lines
        Comparator<Product> byNameAnonymous = new Comparator<Product>() {
            @Override
            public int compare(Product left, Product right) {
                return left.getName().compareTo(right.getName());
            }
        };
        // Lambda:
        Comparator<Product> byNameLambda = (left, right) -> left.getName().compareTo(right.getName());
        List<Product> sorted = new ArrayList<>(BASKET);
        sorted.sort(byNameAnonymous);
        System.out.println("anonymous class: " + sorted);
        sorted.sort(byNameLambda);
        System.out.println("lambda: " + sorted);
    }
}

class Product {
    private final String name;
    private final String category;
    private final int quantity;
    private final double unitPrice;

    public Product(String name, String category, int quantity) {
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unitPrice = 0.0;
    }

    public Product(String name, String category, int quantity, double unitPrice) {
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getName() { return name; }

    public String getCategory() { return category; }

    public int getQuantity() { return quantity; }

    public double getPrice() { return unitPrice; }

    @Override
    public String toString() {
        return name + " x" + quantity;
    }
}

@FunctionalInterface
interface BasketCalculationOperation {
    double apply(List<Product> products);
}

@FunctionalInterface
interface TriFunction<A, B, C, R> {
    R apply(A a, B b, C c);
}

class BasketCalculator {
    public Double calculate(List<Product> products, BasketCalculationOperation operation) {
        return operation.apply(products);
    }
}

class ThisDemo {
    void show() {
        // A lambda introduces no new scope: `this` is still the enclosing ThisDemo
        Runnable lambda = () -> System.out.println("\n`this` inside a lambda: " + this);

        // An anonymous class is a new instance, so `this` is that instance.
        // Reaching the enclosing one needs ThisDemo.this.
        Runnable anonymous = new Runnable() {
            @Override
            public void run() {
                System.out.println("`this` inside anonymous class: " + this);
                System.out.println("the enclosing instance: " + ThisDemo.this);
            }
        };
        lambda.run();
        anonymous.run();
    }

    @Override
    public String toString() {
        return "ThisDemo instance";
    }
}
