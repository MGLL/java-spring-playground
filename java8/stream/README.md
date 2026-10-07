# Stream API ([JEP 107](https://openjdk.org/jeps/107))
*Introduced in Project Lambda.*

Finalized in Java 8, alongside the lambda expressions ([JEP 126](https://openjdk.org/jeps/126)) it depends on. A declarative way to express a computation over a sequence of elements: what to keep, what to turn it into, what to reduce it to. Instead of using a loop, with an index and an accumulator variable.

## Note

A stream is **not a collection**. **It stores no elements and it is not a data structure**. It is a *description of a computation* attached to a source, and nothing happens until you ask for a result. `list.stream().filter(...).map(...)` does no filtering and no mapping, it builds a pipeline. The terminal operation runs it.

This is what separates the API from a `for` loop: you say *what* the result is, and the library decides *how* to traverse. That freedom is what lets the same pipeline run in parallel by changing one call, and it is also why the operations you pass in must not depend on iteration order or mutate anything outside themselves.

The cost of that freedom is that a stream is **single-use**. Once a terminal operation has run, the stream is consumed, and touching it again throws `IllegalStateException`. If you need the result twice, store the result, not the stream.

## Composition

A pipeline is always three parts:

- A **source**: `collection.stream()`, `Arrays.stream(array)`, `Stream.of(...)`, `IntStream.range(0, n)`, `Files.lines(path)`, or an infinite generator with `Stream.iterate` / `Stream.generate`.
- Zero or more **intermediate operations**, each returning a new stream and each **lazy**: `filter`, `map`, `flatMap`, `distinct`, `sorted`, `limit`, `skip`, `peek`.
- Exactly one **terminal operation**, which triggers evaluation and produces a value or a side effect: `collect`, `reduce`, `forEach`, `count`, `min` / `max`, `findFirst` / `findAny`, `anyMatch` / `allMatch` / `noneMatch`, `toArray`.

Two properties follow from laziness:

- **Fusion.** Elements are pulled one at a time through the whole chain. A `filter` then a `map` over a million elements does not build an intermediate million-element list. Each element that survives the filter goes straight into the map.
- **Short-circuiting.** `findFirst`, `anyMatch` and `limit` stop pulling as soon as the answer is known. This is also what makes an infinite source such as `Stream.iterate` usable at all, provided you cap it with `limit`.

Intermediate operations split further into **stateless** ones (`filter`, `map`), which look at one element at a time, and **stateful** ones (`sorted`, `distinct`, `limit`, `skip`), which need to buffer or count. Stateful operations are the expensive ones, and `sorted` on an infinite stream never terminates.

Two supporting pieces complete the API:

- **Primitive streams**: `IntStream`, `LongStream` and `DoubleStream` avoid boxing every element into an `Integer`. Cross over with `mapToInt` / `mapToObj` / `boxed`, and note the extras they add: `sum`, `average`, `summaryStatistics`.
- **Collectors**: the recipes that `collect` uses to build a result. `toList`, `toSet`, `toMap`, `joining`, and the ones that make the API worth learning, `groupingBy` and `partitioningBy`, which take a downstream collector of their own (`counting()`, `summingInt(...)`, `mapping(...)`).

Operations that may find nothing (`findFirst`, `min`, `max`, single-argument `reduce`) return an `Optional`, so the empty case is part of the signature rather than a `null` waiting to be dereferenced.

## Run it
Requires Java 8+.

```bash
java Main.java
```

On a Java 8 JDK, `java Main.java` does not exist yet, compile first with `javac Main.java && java Main`.

## What it demonstrates
- **Loop versus pipeline**: the same filter-and-transform written by hand and as a stream, so the accumulator variable and the mutable result list disappear.
- **Laziness**: an intermediate chain with no terminal operation does nothing at all, and a `peek` shows the order elements actually flow through the stages, one element at a time rather than stage by stage.
- **Short-circuiting**: `findFirst` and `limit` over an infinite `Stream.iterate` source, which terminate because the pipeline stops pulling.
- **`map` versus `flatMap`**: turning each element into one value, against flattening a stream of collections into a single stream of elements.
- **Reduction**: `reduce` with and without an identity value, and why the version without one returns an `Optional` and `max(Comparator.comparingInt(...))` is the idiomatic form of the same reduction.
- **Collectors**: `groupingBy` with a downstream `counting()` and `summingInt(...)`, and the `toMap` merge function that keeps a duplicate key from throwing.
- **`partitioningBy` against `groupingBy` on a boolean**: a predicate split always has both the `false` and the `true` key, with an empty list on a side that matched nothing, where grouping on the same predicate omits the key it never saw.
- **Primitive streams**: `mapToInt(...).sum()` and `summaryStatistics()`, against the `Stream<Integer>` in the reduction above, which boxes every element.
- **Single use**: reusing a consumed stream throws `IllegalStateException`.

## Best practice / when to use / when to avoid
### Best practice:
- **Keep the lambdas pure.** The functions you pass in must not mutate anything outside themselves and must not touch the stream's source. Mutating the source during a pipeline gives you a `ConcurrentModificationException` at best, and a silently wrong result at worst. Build the result with `collect`, never with `forEach` plus `list.add`.
- **Collect, do not accumulate.** `collect(Collectors.toList())` is the correct way to materialize. A shared mutable accumulator works by accident in sequential mode and breaks the moment the stream goes parallel.
- **Give `toMap` a merge function.** The two-argument form throws `IllegalStateException` on a duplicate key. The three-argument form makes you state what should happen instead, which is nearly always what you meant.
- **Know what `Collectors.toList()` promises**, which is nothing about the type or the mutability of the list it returns. Use `toCollection(ArrayList::new)` when you need to modify the result, `Collectors.toUnmodifiableList()` (Java 10+) or `stream.toList()` (Java 16+) when you do not.
- **Use primitive streams for numeric work.** `mapToInt(Order::quantity).sum()` avoids allocating an `Integer` per element, and reads better than a `reduce` over boxed values.
- **Close streams backed by a resource.** `Files.lines` and `Files.walk` hold a file handle. They are `AutoCloseable`, so wrap them in try-with-resources. Streams over collections need no closing.
- **Treat `peek` as a debugging tool only.** It exists to observe elements in flight, and the implementation is allowed to skip it when it can prove the elements are not needed.
- **Be deliberate about `parallel()`.** It pays off only for a large, cheaply-splittable source (an `ArrayList`, an array, an `IntStream.range`) doing CPU-bound work per element. It is a poor fit for a `LinkedList` or a `Stream.iterate` source, which split badly, and for I/O-bound work, which blocks. Every parallel stream in the JVM shares the common `ForkJoinPool`, so one slow pipeline starves the others. The function you give `reduce` must also be genuinely associative, or the result depends on how the work was split.

### When to use:
- **Transforming and filtering collections**: the pipeline states the intent, and the intermediate variables the loop needed are gone.
- **Grouping and summarizing**: `groupingBy` with a downstream collector replaces a `Map`, a `computeIfAbsent`, and a nested loop.
- **Lazily-generated or large sources**: reading a file line by line, or capping an infinite generator, without holding everything in memory.
- **Aggregating numbers**: `summaryStatistics()` gives count, sum, min, average and max in a single pass.

### When to avoid:
- **Plain iteration**: a `for` loop over a handful of elements, calling one method on each, is clearer as a loop. `forEach` on a stream is a loop wearing a costume.
- **Index-dependent logic**: streams have no element index, and no way to look at the neighbour. `IntStream.range` over the indices works, but at that point the loop was the honest version.
- **Mutating elements in place**: streams produce a new result. If the job is to update the objects already in a collection, iterate.
- **Checked exceptions inside the pipeline**: the functional interfaces do not declare them, so every call that throws one needs a try/catch inside the lambda, which undoes the readability the stream bought you.
- **Hot paths over small collections**: setting up a pipeline costs more than a loop that runs three times. Measure before rewriting anything performance-critical into streams.
