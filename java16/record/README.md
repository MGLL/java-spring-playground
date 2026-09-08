# Records ([JEP 395](https://openjdk.org/jeps/395))
*Introduced in Project Amber.*

Finalized in Java 16, after two preview rounds in Java 14 ([JEP 359](https://openjdk.org/jeps/359)) and Java 15 ([JEP 384](https://openjdk.org/jeps/384)). A concise way to declare a class whose purpose is to carry an immutable group of values.

## Note

A record declares its state up front, as a list of *components*, and the compiler derives the rest. `record Person(String firstName, String lastName) {}` is a complete class. The equivalent hand-written class is `PersonClass` in the example: two fields, a constructor, two getters, `toString`, `equals`, and `hashCode`, around 40 lines for the same information.

The point isn't only brevity. Hand-written `equals` and `hashCode` drift: a field gets added and one of the two methods is forgotten, and the object silently misbehaves in a `HashMap`. A record derives both from its component list, so they cannot fall out of sync.

A record is a **nominal tuple**, not a general-purpose class. The components *are* the state, publicly and permanently. If you want to hide or later change how state is represented, use a class.

## Composition

The compiler generates, from the component list:
- A **canonical constructor** taking every component in declaration order.
- A `private final` **field** per component.
- A public **accessor** per component, named after it: `firstName()`, *not* `getFirstName()`.
- `equals` and `hashCode`, derived from all components.
- `toString`, of the form `Person[firstName=FIRSTNAME, lastName=LASTNAME]`.

What a record fixes in exchange:
- It is implicitly `final` and implicitly extends `java.lang.Record`, so it **cannot extend** anything else. It can implement interfaces.
- It **cannot declare additional instance fields**. Its state is exactly its components.
- Static fields, static methods, instance methods, and nested types are all allowed.

You can still override any generated member, and you can add constructors. The example shows the two useful forms:
- A **compact constructor** (`NonNullPerson`) has no parameter list. You write only validation or normalization, and the assignment to the fields is added for you at the end. This is the place to reject bad input.
- An **alternative constructor** (`MissingLastNamePerson`) takes a different signature and must delegate to the canonical one through `this(...)`.

## Run it
Requires Java 16+.

```bash
java Main.java
```

## What it demonstrates
- **`Person` versus `PersonClass`**: one line against roughly forty, for the same behaviour.
- **Equality comes for free**: two `Person` instances with equal components are equal. `PersonClass` reaches the same result only because that logic was written out by hand.
- **Components accept `null` by default**: `new Person("Harry", null)` is valid. A record constrains *shape*, not *content*.
- **`NonNullPerson` adds the constraint**: the compact constructor throws `NullPointerException` before any field is assigned, so a malformed instance never exists.
- **`MissingLastNamePerson` supplies a default**: a second constructor delegates to the canonical one with a static constant.
- **Accessors are named after components**: `firstName()` and `lastName()` rather than getters.
- **`Order` versus `SafeOrder` shows shallow immutability**: both are built from the same `ArrayList`, then that list is mutated. `Order` prints the added element, because it shares the caller's list. `SafeOrder` does not, because its compact constructor copied it, and its accessor hands back an immutable list.

## Best practice / when to use / when to avoid
Best practice:
- **Validate in the compact constructor.** It is the single gate every instance passes through, including deserialization: record deserialization runs the canonical constructor, unlike ordinary Java serialization, which bypasses constructors entirely.
- **Beware shallow immutability.** The *fields* are final, the objects they point at are not. A record holding a `List` shares the caller's list, and that list can still be mutated afterwards, as `Order` shows in the example. Copy on the way in with `List.copyOf(items)` in the compact constructor. That covers the way out too, since `List.copyOf` returns an immutable list. For a component type with no such copy method, an array for instance, clone it in the constructor *and* override the accessor.
- **Expect the accessor naming to matter.** Frameworks built on the JavaBeans convention look for `getFirstName()`. Jackson supports records natively since 2.12, most modern tooling does too, but older or reflective libraries may not.
- **Know how floating-point components compare.** Generated `equals` compares `double` and `float` by their bit patterns, so `NaN` equals `NaN` and `0.0` does *not* equal `-0.0`. Both are the opposite of what `==` does.
- **Keep behaviour light.** Derived values and small helpers are fine as instance methods. Business logic that reaches beyond the components belongs elsewhere.

When to use:
- **Data Transfer Objects**: API request and response bodies, query results, message payloads.
- **Composite keys and value objects**: correct `equals` and `hashCode` make them safe as `Map` keys or `Set` members straight away.
- **Multiple return values**: a small local or nested record instead of an array, a `Map.Entry`, or an out-parameter.
- **Sealed hierarchies**: records are the natural implementations of a sealed interface, and pattern matching destructures them by component.

When to avoid:
- **Mutable state**: a record has no setters and no way to add one. Use a class.
- **Encapsulated or derived representation**: the components are public API. If you need to store state differently from how you expose it, or reserve the right to change it later, use a class.
- **Types that must extend a class**: records are final and already extend `java.lang.Record`.
- **JPA entities**: these require a no-argument constructor and mutable fields, which a record cannot provide. Records work well for the projections you read *out* of a query.
