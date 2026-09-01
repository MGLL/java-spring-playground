// Java 25 — Compact Source Files & Instance Main Methods (JEP 512, finalized)
//
// No class declaration, no `public static void main(String[] args)`.
// Everything in java.base is auto-imported (note: List needs no import).
// The new IO methods must be qualified, e.g. IO.println(...).

void main() {
    var foodList = List.of("Spinach", "Chicken", "Lettuce", "Banana", "Apple", "Beef");
    
    IO.println("Available food:");
    for (var food : foodList) {
        IO.println("- " + food + typeOfFood(food));
    }

    IO.println("\nTotal: " + foodList.size() + " food");
}

String typeOfFood(String food) {
    return switch (food) {
        case "Spinach", "Lettuce" -> " (Vegetable)";
        case "Chicken", "Beef"    -> " (Meat)";
        case "Banana", "Apple"    -> " (Fruit)";
        default                   -> "";
    };
}