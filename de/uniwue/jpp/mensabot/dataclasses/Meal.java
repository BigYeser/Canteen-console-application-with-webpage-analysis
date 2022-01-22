package de.uniwue.jpp.mensabot.dataclasses;

import de.uniwue.jpp.mensabot.dataclasses.implementation.MealImp;

public interface Meal {
    String getName();
    int getPrice(); // in cents

    static Meal createMeal(String name, int price) {
        if(name == null || name.isEmpty() || price <= 0)
            throw new IllegalArgumentException("price <= 0 Or Empty name");
        return new MealImp(name, price);
    }
}
