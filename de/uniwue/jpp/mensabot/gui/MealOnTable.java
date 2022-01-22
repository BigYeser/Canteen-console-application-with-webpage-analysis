package de.uniwue.jpp.mensabot.gui;

import de.uniwue.jpp.mensabot.dataclasses.Meal;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Set;

public class MealOnTable {
    private SimpleStringProperty date;
    private ObservableList<String> meals;
    private ObservableList<String> prices;


    public MealOnTable(String date,ObservableList<String> meals, ObservableList<String> prices) {
        this.date = new SimpleStringProperty(date);
        this.meals = meals;
        this.prices = prices;
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public ObservableList<String> getMeals() {
        return meals;
    }

    public ObservableList<String> getPrices() {
        return prices;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public void setMeals(ObservableList<String> meals) {
        this.meals = meals;
    }

    public void setPrices(ObservableList<String> prices) {
        this.prices = prices;
    }
}
