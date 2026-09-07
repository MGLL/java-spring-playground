# Virtual Threads ([JEP 444](https://openjdk.org/jeps/444))
*Introduced in Project Loom.*

Finalized in Java 21. Introduces lightweight threads to Java.

## Note

Virtual Threads are managed and scheduled by the JVM. Their allocation doesn't require a system call and they are free of the operating system's context switch. It reduces their cost and allows spawning more virtual threads.

Furthermore, virtual threads don't block the carrier thread (a.k.a. Platform Thread). Blocking a virtual thread is a much cheaper operation as the JVM will schedule another virtual thread, leaving the carrier thread unblocked. *Note that the carrier thread can potentially be blocked when a thread calls a native method and performs blocking operations.*

Also, we won't need to use Async APIs, which should result in more readable code.

## Composition
A virtual thread is made of a **continuation and a scheduler**.
- The scheduler may be any implementation of the *Executor* interface.
- A continuation is an execution unit that can be started, parked (yielded), rescheduled back, and resumes its execution in that same way from where it left off.

When a blocking operation happens, the continuation will yield, leaving the carrier thread unblocked.

## Run it
Requires Java 21+.

```bash
java Main.java
```

## What it demonstrates
- **10,000 tasks, each blocking for 200ms**: ~10s on a 200-thread platform pool vs ~0.25s on virtual threads.
- **The bottleneck is the pool cap, not per-task speed**: 200 platform threads means 50 sequential rounds. Raising the cap to 10,000 isn't an option: each reserves ~1MB of stack.
- **`ExecutorService` is `AutoCloseable`**: try-with-resources waits for every task before closing.

## Best practice / when to use / when to avoid
Best practice:
- Do not pool virtual threads. Create a new virtual thread for every individual task.
- Avoid `synchronized` blocks **on Java 21-23**. They pin the virtual thread to its carrier, use `ReentrantLock` instead. Fixed in Java 24 ([JEP 491](https://openjdk.org/jeps/491)), where `synchronized` no longer pins.
- Minimize `ThreadLocal` usage. `InheritableThreadLocal` makes every child thread *copy* the parent's map, which doesn't scale to millions of threads. Prefer `ScopedValue`: an immutable binding, automatically unbound when its block exits, and *shared* rather than copied by child threads. Preview in **Java 21** ([JEP 446](https://openjdk.org/jeps/446)), final in **Java 25** ([JEP 506](https://openjdk.org/jeps/506)).
- Use standard Executors API: `Executors.newVirtualThreadPerTaskExecutor()`

When to use:
- **I/O Bound workloads**: for applications that wait on network calls, db queries, file operations or remote APIs.
- **High-concurrency Services**: Web servers handling thousands or millions of concurrent client connections.

When to avoid:
- **CPU Bound tasks**: avoid for heavy calculations, complex data processing, or algorithmic work. Use platform threads instead. Virtual threads only pay off when tasks block. CPU-bound work never yields, so it holds its carrier thread throughout and gains nothing.
