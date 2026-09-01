# Java 25

Runnable examples of the notable language and JDK features in Java 25 (LTS, released 16 September 2025, supported until at least 2030).

The focus is on **finalized** features.

## Contents

### New in Java 25

| Folder | Feature | What it shows |
|--------|---------|---------------|
| `compact-source-files/` | Compact Source Files & Instance Main Methods (JEP 512) | Minimal `void main()` programs with no class boilerplate, great for scripts and learning |
| `module-import-declarations/` | Module Import Declarations (JEP 511) | Importing a whole module's exported packages with `import module java.base;` |
| `flexible-constructor-bodies/` | Flexible Constructor Bodies (JEP 513) | Running validation/setup statements *before* `super(...)` or `this(...)` |
| `scoped-values/` | Scoped Values (JEP 506, now final) | A safer, immutable alternative to `ThreadLocal`, ideal with virtual threads |

### Still essential (finalized in earlier releases, valid in Java 25)

| Folder | Feature | What it shows |
|--------|---------|---------------|
| `virtual-threads/` | Virtual Threads | Lightweight concurrency for I/O-bound work, classic thread pool vs. `newVirtualThreadPerTaskExecutor` |
| `record-patterns/` | Record Patterns | Deconstructing records directly in `if` / `switch`, including nested records |
| `pattern-matching-switch/` | Pattern Matching for switch | Type-based `switch` over a sealed interface, with compiler-checked exhaustiveness |
| `sequenced-collections/` | Sequenced Collections | `getFirst()`, `getLast()`, `reversed()`, before/after the old verbose code |


## Running the examples

Most examples are single files:

```bash
java Example.java
```

## Notes

- **Scoped Values are now final in Java 25** (they were preview in Java 21): safe to treat as a real best practice, unlike Structured Concurrency, which is still in preview.
- **String Templates are intentionally not included.** They were a preview in Java 21 and have since been withdrawn.
- Good starting points: `compact-source-files/` (smallest, most fun) and `virtual-threads/` (highest payoff).

## References

- [Java 25 JEP index](https://openjdk.org/projects/jdk/25/)
- [Java 25 API docs](https://docs.oracle.com/en/java/javase/25/)
- [Java Almanac — Java 25 features](https://javaalmanac.io/jdk/25/)