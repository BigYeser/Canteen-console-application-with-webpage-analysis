package de.uniwue.jpp.mensabot.dataclasses.implementation;

import de.uniwue.jpp.mensabot.dataclasses.Meal;
import de.uniwue.jpp.mensabot.dataclasses.Menu;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

public class MenuImp implements Menu {
    private LocalDate date;
    private Set<Meal> meals;

    public MenuImp(LocalDate date, Set<Meal> meals) {
        this.date = date;
        this.meals = meals;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public Set<Meal> getMeals() {
        return meals;
    }

    @Override
    public String toCsvLine() {
        String res = date.toString();
        for (Meal meal: meals) {
            res += ";" +  meal.getName() + "_" + meal.getPrice();
        }
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuImp menuImp = (MenuImp) o;
        return date.equals(menuImp.date) && meals.equals(menuImp.meals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, meals);
    }

    @Override
    public String toString() {
        return toCsvLine();
    }
}
