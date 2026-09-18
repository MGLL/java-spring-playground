# Lambda Expressions ([JEP 126](https://openjdk.org/jeps/126))
*Introduced in Project Lambda.*

Finalized in Java 8, together with the default methods ([JEP 126](https://openjdk.org/jeps/126) again, then named *virtual extension methods*) that let `Collection` gain `stream()` and `forEach` without breaking every existing implementor. 

It's part of **Java support for functional-style programming**, alongside *functional interfaces*. These constructs let us express **behavior** as values and pass it around with minimal boilerplate (a pattern powerful with Stream API).

It is one of the most important new features introduced in the Java language.

## Note
### Lambda expressions
Lambda expressions are also called **closure** or **anonymous functions**. A lambda introduces **no new scope**. **Its parameters and locals live in the enclosing method's scope**, so a name already used there cannot be reused. What a lambda can capture is limited to **effectively final** local variables, meaning locals are never reassigned after initialization. The value is copied at capture time.

- A lambda expression is an anonymous function in a way that it is defined **without an explicit return type declaration, access modifiers, or a name**. It's a shortcut to define a method directly where it's used.
- A lambda expression allows you to **encapsulate a block of code so that it can be passed to other processes**. It serves as a syntactic shortcut for anonymous inner classes implementing an interface with a single abstract method. This type of interface is known as a **functional interface**.
- When the compiler evaluates the lambda expression, it infers the type based on the functional interface. This allows it to obtain information about the parameters used, the return type, and the exceptions that may be thrown.

#### Syntax
One of the advantages of lambda expression is to have an easy syntax. It's composed of three parts:
- a **set of parameters**, ranging from none to several
- the **"->" operator**
- the **body of the function**

And it follows to main formats: 
- (parameters) -> expression;
- (parameters) -> { processing; }

```java
(product) -> product.getName().length();

(products) -> {
    System.out.println("Product in basket: ");
    Products.forEach(System.out::println);
};
```

#### Parameters
Also, the parameters of the expression should respect some rules:
- An expression can have no parameters, a single parameter, or multiple parameters.
- Parameter types can be explicitly declared or inferred by the compiler based on the context in which the expression is used.
- The parameters are enclosed in parentheses, with each separated by a comma.
- Empty parentheses indicate that there are no parameters.
- If there is only one parameter whose type is not explicitly specified, then the use of parentheses is not mandatory.

```java
() -> System.out.println("Processing");

(str) -> System.out.println(str);
str -> System.out.println(str); // OR
(String str) -> System.out.println(str); // OR

(a, b) -> (long) a + b;
```

#### Body
Body is defined on the right part of the `->` operator. It can be:
- A unique expression.
- A code block composed of multiple instructions.

It must follow some rules:
- It may have no instructions, a single instruction, or multiple instructions.
- When it contains only a single statement, curly braces are not required, and the return value is that of the statement, if it has one.
- When there are multiple statements, they must be enclosed in curly braces.
- The return value is that of the last expression, or void if nothing is returned.

#### Variable scope
- A lambda behaves syntactically like a nested code block.
- A lambda expression can access certain variables defined in the enclosing context.

So in the body of a lambda expression, it's possible to use:
- variables passed as parameters
- variables defined within the body
- `final` variable defined in the enclosing context
- effectively `final` variables defined in the enclosing context
  - these variables are not declared `final`, but they are assigned a value that is never modified. It would therefore be possible to declare them `final` without causing compilation errors.

```java
public static void display(String message, int iteration) {
// here, message and iteration are effectively final
Runnable r = () -> {
    for (int i = 0; i < iteration; i++) {
    System.out.println(message);
    }
};
new Thread(r).start();
}
```

#### Using lambda expression
A lambda expression can only be used in a context where the compiler can identify the use of its target type, which must be a functional interface:
- variable declaration
```java
Runnable r = () -> System.out.println("run");
```
- variable assignment
```java
Runnable r;
r = () -> System.out.println("assigned");
```
- return value with the return statement
```java
Supplier<String> make() {
    return () -> "hello";
}
```
- array initialization
```java
Runnable[] tasks = {
    () -> System.out.println("a"),
    () -> System.out.println("b") 
};
```
- method or constructor parameter
```java
// method parameter
new Thread(() -> System.out.println("thread")).start();

// constructor parameter
class Wrapper {
    Wrapper(Runnable r) { r.run(); }
}
new Wrapper(() -> System.out.println("ctor"));
```
- lambda expression body
```java
Supplier<Runnable> s = () -> () -> System.out.println("nested");
```
- ternary operator ?:
```java
boolean quiet = false;
Runnable r = quiet ? 
    () -> {} : 
    () -> System.out.println("loud");
```
- cast
```java
Object o = (Runnable) () -> System.out.println("cast");
```

Note, however, that a lambda expression does not necessarily have a unique identity; the semantics of the `equals()` method are therefore not guaranteed.
The type of the functional interface is determined by the compiler based on the context of its usage. Consequently, two syntactically identical lambda expressions can be compatible with multiple functional interfaces and thus be compiled into two objects of different types.
```java
    LongFunction<Long> longFunction = x -> x * 2;
    IntFunction<Integer>  intFunction  = x -> x * 2;
```

#### Method reference
Method references provide a simplified syntax for invoking a method as a lambda expression; they offer a syntactic shortcut for creating a lambda expression intended to invoke a method or a constructor. Method or constructor references use the new :: operator.

| Type | Signature | Example |
|---|---|---|
| Reference to a static method | className::staticMethodName | `String::valueOf` |
| Reference to a method on an instance | object::methodName | `person::toString` |
| Reference to a method of an arbitrary object of a given type | className::methodName | `Object::toString` |
| Reference to a constructor | className::new | `Person::new` |

### Functional interface
A **functional interface** is an interface and have to respect some constraints:
- Only have one abstract method.
- The methods defined in the `Object` class are not considered abstract methods.
- All methods must be `public` (java 8, since Java 9 can have private methods).
- Can have `default` or `static` methods.

Some interfaces define only one method, they are called **Single Abstract Method** (SAM): `Comparator<T>`, `Callable<V>`, `Runnable`, ....


The built-in ones live in `java.util.function` and cover nearly every shape:

| Interface | Signature | Reads as |
|---|---|---|
| `Supplier<T>` | `() -> T` | produce a value |
| `Consumer<T>` | `T -> void` | do something with a value |
| `Function<T, R>` | `T -> R` | transform a value |
| `Predicate<T>` | `T -> boolean` | test a value |
| `UnaryOperator<T>` | `T -> T` | a `Function` whose input and output match |
| `BinaryOperator<T>` | `(T, T) -> T` | a `BiFunction` combining two of a kind |

`BiConsumer`, `BiFunction` and `BiPredicate` take two arguments, and each interface has primitive specializations (`IntPredicate`, `ToIntFunction`, `IntFunction`, `ObjIntConsumer`, …) that exist purely to avoid boxing.

## Run it
Requires Java 8+.

```bash
java Main.java
```

On a Java 8 JDK, `java Main.java` does not exist yet, compile first with `javac Main.java && java Main`.

## What it demonstrates
- **The `java.util.function` shapes over the same basket**: a `Function`, `BiFunction`, `Consumer`, `Supplier` and `Predicate`, so each signature maps to an intent.
- **The same five interfaces written twice**: first as anonymous classes, then as lambdas, so the ceremony around each single expression disappears.
- **Anonymous class versus lambda on a `Comparator`**: five lines of `new Comparator<Product>() { @Override public int compare(...) }` against one `(left, right) -> ...`, both sorting the same list.
- **`this` differs**: printing `this` from inside a lambda gives the enclosing `ThisDemo`, because a lambda introduces no new scope. From inside an anonymous class it gives that anonymous instance, and reaching the enclosing one needs `ThisDemo.this`.
- **Target typing**: one body, `n -> n * 2`, assigned to a `Function<Integer, Integer>` and to an `IntUnaryOperator`. The lambda has no type of its own, the context gives it one, and the primitive specialization avoids the boxing.
- **Composition**: `isMeat.and(isScarce).negate()` and `function.andThen(String::toUpperCase)`, built from small named pieces that stay readable and reusable on their own, rather than one dense lambda.
- **Comparator chaining**: `comparing(...).thenComparingInt(...).reversed()` against the hand-written `compare` it replaces.
- **Method references in several forms**: bound to an instance (`System.out::println`), on an arbitrary object of a type (`Product::getName`), and on a constructor (`Product::new`).
- **Effectively final capture**: a captured `threshold` that compiles, and the commented-out reassignment that would break it.
- **Bound receiver evaluation**: `missing::getName` on a null receiver throws at *creation* of the reference, not at `get()`.
- **Custom functional interfaces**: `BasketCalculationOperation`, passed to `BasketCalculator.calculate` so the *behaviour* is the parameter, and `TriFunction` for a three-argument shape the built-ins do not cover.
- **Default methods carrying lambdas into the library**: `Collection.forEach` exists on `List` without every implementor changing, which is the other half of [JEP 126](https://openjdk.org/jeps/126).

## Best practice / when to use / when to avoid
### Best practice:
1. **Prefer standard functional interfaces**, it satisfies most needs in providing target types for lambda expressions and method references. Each of these interfaces is general and abstract, making them easy to adapt to almost any lambda expression.
2. **Use `@FunctionalInterface` annotation**, so the compiler will trigger an error in response to any attempt to break the predefined structure of a functional interface (one abstract method).
3. **Don't overuse default methods in functional interfaces**. We can add default methods, it's acceptable to the functional interface contract as long as there is only one abstract method declaration. But adding too many default methods to the interface is not a very good architectural decision (conflict can happen if an interface extends multiple functional interfaces with similar default method).
4. **Instantiate functional interfaces with lambda expressions**.
```java
@FunctionalInterface
public interface Foo {
    String method();
}
//
Foo foo = parameter -> parameter + " from Foo";
```
5. **Avoid overloading methods with functional interfaces as parameters**. We should use different names to avoid collisions:
```java
String processWithCallable(Callable<String> c) throws Exception;
String processWithSupplier(Supplier<String> s);
```
6. **Don't treat lambda expressions as inner classes**. Contrary to inner classes (which create a new scope) where we can use `this` keyword inside the inner class as a reference to its instance, lambda expressions work with enclosing scope. We can't hide variables from the enclosing scope inside the lambda's body.
7. **Keep lambda expressions short and self-explanatory**. **Lambdas should be an expression, not a narrative**. We should use one line constructions if possible and they should specifically express the functionality they provide.

*Avoid blocks of code in lambda's body*, for example:
```java
Foo foo = parameter -> buildString(parameter);
private String buildString(String parameter) {
    String result = "Something " + parameter;
    //many lines of code
    return result;
}
```

*Avoid specifying parameter types*: A compiler, in most cases, is able to resolve the type of lambda parameters with the help of type inference.
```java
(a, b) -> a.toLowerCase() + b.toLowerCase();
// instead of:
(String a, String b) -> a.toLowerCase() + b.toLowerCase();
```

*Avoid parentheses around a single parameter*: parentheses are required only when there is more than one parameter or no parameter.
```java
a -> a.toLowerCase();
// instead of:
(a) -> a.toLowerCase();
```

*Avoid return statement and braces*: Braces and return statements are optional in one-line lambda bodies.
```java
a -> a.toLowerCase();
// instead of:
a -> { return a.toLowerCase(); };
```

*Use method references*:
```java
String::toLowerCase;
// instead of:
a -> a.toLowerCase();
```

8. **Use "effectively final" variables**. Accessing a non-final variable inside a lambda expressions will cause a compile-time error, but that doesn't mean that we should mark every target variable as `final`. According to the “effectively final” concept, a compiler treats every variable as final as long as it is assigned only once. **This approach should simplify the process of making lambda execution thread-safe**.
9.  **Protect object variables from mutation**. One of the main purposes of lambdas is use in parallel computing, which means that they’re really helpful when it comes to thread-safety. The “effectively final” paradigm helps a lot here, but not in every case. Lambdas can’t change a value of an object from enclosing scope. But in the case of mutable object variables, a state could be changed inside lambda expressions.
```java
int[] total = new int[1];
Runnable r = () -> total[0]++;
r.run();
```


### When to use:
- **Anywhere an anonymous class implemented a single method**: comparators, listeners, `Runnable` and `Callable`, callbacks.
```java
Runnable r = () -> System.out.println("run");
Comparator<String> byLen = (a, b) -> a.length() - b.length();
button.addActionListener(e -> save());
```
- **Feeding the Stream API**: every `filter`, `map`, `reduce` and `collect` argument is a lambda or a method reference. This is what the feature was built for.
```java
names.stream()
    .filter(s -> s.startsWith("A"))
    .map(String::toUpperCase)
    .reduce("", String::concat);
```
- **Deferred execution**: `Supplier` for a value that may not be needed, `Optional.orElseGet` and the `Logger.atDebug().log(() -> expensive())` form, where the argument is only built if it is used.
```java
String name = Optional.ofNullable(input).orElseGet(() -> loadDefault());
logger.atDebug().log(() -> "cost=" + expensiveReport());  // built only if debug is on
```
- **Strategy passed as a parameter**: one method that takes the varying step as a `Function`, instead of a small class hierarchy or a `switch` over a mode flag.
```java
int applyOp(int x, IntUnaryOperator op) { return op.applyAsInt(x); }
applyOp(5, n -> n * 2);   // double
applyOp(5, n -> n + 1);   // increment
```
- **Map bulk operations**: `computeIfAbsent`, `merge` and `forEach` replace the get-check-put dance.
```java
map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);   // no get-check-put
map.merge(word, 1, Integer::sum);                              // count occurrences
map.forEach((k, v) -> System.out.println(k + "=" + v));
```

### When to avoid:
- **Anything that needs state or multiple methods**: a lambda implements exactly one abstract method and has no fields. Use a class.
- **Long bodies**: a ten-line lambda buried in a call argument is harder to read than the method it should have been, and worse to debug, since a breakpoint inside it and a stack frame named `lambda$process$3` are both awkward.
- **Code full of checked exceptions**: wrapping each one in an unchecked exception to get it through a functional interface loses the original contract.
- **Overload sets that take different functional interfaces**: the call becomes ambiguous and needs a cast, which is worse than the explicit type it was avoiding.
- **Hot loops over primitives**: a lambda on a boxed `Function<Integer, Integer>` allocates where a plain loop, or the `IntUnaryOperator` specialization, does not.
Generics can't hold primitives, they work with reference types (objects).
Example:
```java
Function<Integer, Integer> f = x -> x + 1;
int sum = 0;
for (int i = 0; i < 1_000_000; i++) {
    sum += f.apply(i);  // i is boxed to Integer, result unboxed back to int
}
```
Meaning that `f.apply(i)` does two conversions per iteration. The `IntUnaryOperator` is designed specifically for `int -> int`, so no boxing happens.