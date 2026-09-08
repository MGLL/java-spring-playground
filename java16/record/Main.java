import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class Main {
    public static void main(String[] args) {
        // Default approach with class
        var personClass = new PersonClass("John", "Doe");
        System.out.println(personClass.toString());

        // Record generate a constructor, only constrains the shape
        var person = new Person("Harry", null);
        System.out.println(person.toString());

        // Record generate an equals method
        System.out.println("record equals, generated: " +
            new Person("Tom", "Jedusor")
            .equals(new Person("Tom", "Jedusor")));
        System.out.println("class equals, hand-written: " +
            new PersonClass("Tom", "Jedusor")
            .equals(new PersonClass("Tom", "Jedusor")));

        // Can add constraints
        try {
            new NonNullPerson("Harry", null);
        } catch (NullPointerException e) {
            System.out.println(e);
        }
        var nonNullPerson = new NonNullPerson("Harry", "Potter");
        // Example with record generated accessors
        System.out.printf("Person: %s %s %n", nonNullPerson.firstName(), nonNullPerson.lastName());

        // Can supplies default value
        var missingLastNamePerson = new MissingLastNamePerson("Tom");
        System.out.println(missingLastNamePerson.toString());

        // Records are only shallowly immutable: the field is final, the List it points at is not
        var items = new ArrayList<String>(List.of("Wand"));
        var order = new Order(items);
        var safeOrder = new SafeOrder(items);

        items.add("Broom"); // mutating the caller's list, after both records were built
        System.out.println(order);     // leaked, the record shares the caller's list
        System.out.println(safeOrder); // copied on the way in, so unaffected

        try {
            safeOrder.items().add("Cloak");
        } catch (UnsupportedOperationException e) {
            System.out.println("safeOrder.items() is immutable: " + e);
        }
    }
}

record Person(String firstName, String lastName) {}

record NonNullPerson(String firstName, String lastName) {
    // Can customize for validation
    public NonNullPerson {
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);
    }
}

record MissingLastNamePerson(String firstName, String lastName) {
    // Can use static variables
    public static final String UNKNOWN_LASTNAME = "Unknown";

    public MissingLastNamePerson(String firstName) {
        this(firstName, UNKNOWN_LASTNAME);
    }

    /*
    // Or static methods
    public static MissingLastNamePerson unnamed(String firstName) {
        return new MissingLastNamePerson(firstName, "Unnamed");
    }
    */
}

record Order(List<String> items) {}

record SafeOrder(List<String> items) {
    // Defensive copy. List.copyOf is immutable, so the generated accessor is safe too
    public SafeOrder {
        items = List.copyOf(items);
    }
}

class PersonClass {
    private final String firstName;
    private final String lastName;

    public PersonClass(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return "Person=[firstName=" + firstName + ", lastName=" + lastName + "]";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof PersonClass)) {
            return false;
        } else {
            PersonClass other = (PersonClass) object;
            return Objects.equals(firstName, other.firstName)
                && Objects.equals(lastName, other.lastName);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }
}
