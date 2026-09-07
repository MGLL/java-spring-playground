# Compact Source Files & Instance Main Methods ([JEP 512](https://openjdk.org/jeps/512))
 
Finalized in Java 25. Lets you write a small Java program without a class declaration or the `public static void main(String[] args)` ceremony.
 
## Run it
Requires Java 25+.

```bash
java Main.java
```
 
## What it demonstrates 
- **No class declaration**: top-level methods and fields belong to an implicit class.
- **Instance `main`**: `void main()` instead of `public static void main(String[] args)`.
- **Automatic `java.base` import**: `List` is used without an `import` statement.
- **The `IO` class**: beginner-friendly console output; methods must be qualified (`IO.println`).

## Best practice / when to use
Great for scripts, quick demos, learning, and small single-file utilities. As a program grows, it upgrades cleanly into an ordinary class, this is a gentle on-ramp, not a separate dialect. For production application code, a normal named class is still the right choice.