package de.uniwue.jpp.mensabot.dataclasses;

import de.uniwue.jpp.mensabot.dataclasses.implementation.MenuImp;

import java.time.LocalDate;
import java.util.Set;

public interface Menu {

    LocalDate getDate();
    Set<Meal> getMeals();
    String toCsvLine();

    static Menu createMenu(LocalDate date, Set<Meal> meals) {
        if(date == null || meals.isEmpty() || meals == null)
            throw new IllegalArgumentException("date is null or meals is null or empty");
        return new MenuImp(date,meals);

    }
}
