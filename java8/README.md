# Java 8

Runnable examples of the notable language and JDK features in Java 8 (LTS, released March 2014).

The focus is on **finalized** features.

## Contents

### New in Java 8

| Folder | Feature | What it shows |
|--------|---------|---------------|
| `stream/` | Stream API (JEP 107) | Declarative pipelines over collections: filter, map, reduce, collect, instead of hand-written loops |
| `lambda/` | Lambda Expressions & Method References (JEP 126) | Passing behaviour as a value, the syntax the whole Stream API is built on |
| `default-methods/` | Default & Static Interface Methods (JEP 126) | Adding methods to an interface without breaking every implementor |
| `optional/` | `Optional` | Making "no value" explicit in a return type rather than returning `null` |
| `date-time/` | Date and Time API (JEP 150) | `LocalDate`, `Instant`, `Duration`, the immutable replacement for `Date` and `Calendar` |

## Running the examples

Most examples are single files:

```bash
java Example.java
```

Single-file source execution arrived in Java 11 ([JEP 330](https://openjdk.org/jeps/330)). On a Java 8 JDK, compile first:

```bash
javac Main.java && java Main
```

## References

- [Java 8 JEP index](https://openjdk.org/projects/jdk8/)
- [Java 8 API docs](https://docs.oracle.com/javase/8/docs/api/)
- [`java.util.stream` package summary](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html): the reference description of how pipelines work
- [Java Almanac — Java 8 features](https://javaalmanac.io/jdk/8/)
