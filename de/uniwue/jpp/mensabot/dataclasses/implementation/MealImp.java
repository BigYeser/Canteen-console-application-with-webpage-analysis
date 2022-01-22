package de.uniwue.jpp.mensabot.dataclasses.implementation;

import de.uniwue.jpp.mensabot.dataclasses.Meal;

import java.util.Objects;

public class MealImp implements Meal {
    private String name;
    private int price;
    public MealImp(String name,int price){
        this.name = name;
        this.price = price;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPrice() {
        return price;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealImp mealImp = (MealImp) o;
        return price == mealImp.price && name.equals(mealImp.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }

    @Override
    public String toString() {
        int x = price / 100;
        int y = price % 100;
        return name +" (" +x + ","+ y + "\u20ac)";

    }

}
