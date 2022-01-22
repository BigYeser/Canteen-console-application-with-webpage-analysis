package de.uniwue.jpp.mensabot.sending.formatting.analyse;

import de.uniwue.jpp.errorhandling.OptionalWithMessage;
import de.uniwue.jpp.errorhandling.OptionalWithMessageMsg;
import de.uniwue.jpp.errorhandling.OptionalWithMessageVal;
import de.uniwue.jpp.mensabot.dataclasses.Meal;
import de.uniwue.jpp.mensabot.dataclasses.Menu;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

public interface Analyzer<T> {

    OptionalWithMessage<T> analyze_unsafe(List<Menu> data);

    default OptionalWithMessage<T> analyze(List<Menu> data) {
        if(data == null || data.isEmpty())
            return new OptionalWithMessageMsg<>("Invalid data argument!");
        return new OptionalWithMessageVal<T>(this.analyze_unsafe(data).get());
    }

    default OptionalWithMessage<String> analyse(List<Menu> data, Function<T, String> convert) {
        if(convert == null)
            return new OptionalWithMessageMsg<>("No convert-function given!");
        return new OptionalWithMessageVal<String>(convert.apply(analyze(data).get()));
    }


    /*

        "AveragePrizeAnalyzer",
        "MedianPrizeAnalyzer",
        "MinPrizeMealAnalyzer",
        "MaxPrizeMealAnalyzer"
        "TotalPrizeAnalyzer"
     */
    static Analyzer<Integer> createAveragePrizeAnalyzer() {
        return new Analyzer<Integer>() {
            @Override
            public OptionalWithMessage<Integer> analyze_unsafe(List<Menu> data) {
                int total = 0;
                int cnt = 0;
                for (Menu menu : data)
                {
                    for(Meal meal : menu.getMeals())
                    {
                        total += meal.getPrice();
                        cnt++;
                    }
                }

                return OptionalWithMessage.of(total / cnt);
            }

            @Override
            public String toString() {
                return "AveragePrizeAnalyzer";
            }
        };
    }

    static Analyzer<Integer> createMedianPrizeAnalyzer() {
        return new Analyzer<Integer>() {
            @Override
            public OptionalWithMessage<Integer> analyze_unsafe(List<Menu> data) {
                int cnt = 0;
                for (Menu menu : data)
                {
                    for(Meal meal : menu.getMeals())
                    {
                        cnt++;
                    }
                }
                int[] arr = new int[cnt];
                int idx = 0;
                for (Menu menu : data)
                {
                    for(Meal meal : menu.getMeals())
                    {
                        arr[idx++] = meal.getPrice();
                    }
                }

                Arrays.sort(arr);

                return OptionalWithMessage.of(arr[(arr.length - 1) / 2]);
            }

            @Override
            public String toString() {
                return "MedianPrizeAnalyzer";
            }

        };

    }

    static Analyzer<Meal> createMinPrizeMealAnalyzer() {

        return new Analyzer<Meal>() {
            @Override
            public OptionalWithMessage<Meal> analyze_unsafe(List<Menu> data) {
                int mn = -1;
                Meal res = null;
                for (Menu menu : data)
                {
                    for(Meal meal : menu.getMeals())
                    {
                        if(mn == -1) {
                            mn = meal.getPrice();
                            res = meal;
                        }
                        if(meal.getPrice() < mn)
                        {
                            mn = meal.getPrice();
                            res = meal;
                        }
                    }
                }
                return OptionalWithMessage.of(res);
            }

            @Override
            public String toString() {
                return "MinPrizeMealAnalyzer";
            }
        };


    }

    static Analyzer<Meal> createMaxPrizeMealAnalyzer() {
        return new Analyzer<Meal>() {
            @Override
            public OptionalWithMessage<Meal> analyze_unsafe(List<Menu> data) {
                int mx = -1;
                Meal res = null;
                for (Menu menu : data)
                {
                    for(Meal meal : menu.getMeals())
                    {

                        if(meal.getPrice() > mx)
                        {
                            mx = meal.getPrice();
                            res = meal;
                        }
                    }
                }
                return OptionalWithMessage.of(res);
            }

            @Override
            public String toString() {
                return "MaxPrizeMealAnalyzer";
            }
        };
    }

    static Analyzer<Integer> createTotalPrizeAnalyzer() {
        return new Analyzer<Integer>() {
            @Override
            public OptionalWithMessage<Integer> analyze_unsafe(List<Menu> data) {
                int total = 0;
                for (Menu menu : data) {
                    for (Meal meal : menu.getMeals()) {
                        total += meal.getPrice();
                    }
                }

                return OptionalWithMessage.of(total);
            }
            @Override
            public String toString() {
                return "TotalPrizeAnalyzer";
            }
        };
    }

    /* Die folgenden Analyzer werden nicht getestet und muessen nicht implementiert werden.
     * Es sind lediglich Vorschlaege fuer weitere Analyzer fuer Aufgabenteil 3. */
    static Analyzer<Map<LocalDate, Double>> createAveragePrizePerDayAnalyzer() {
        return new Analyzer<Map<LocalDate, Double>>() {
            @Override
            public OptionalWithMessage<Map<LocalDate, Double>> analyze_unsafe(List<Menu> data) {
                Map<LocalDate, Double> res = new HashMap<>();
                for (Menu menu : data){
                    LocalDate localDate = menu.getDate();
                    Set<Meal> meals = menu.getMeals();
                    double avg = 0;
                    double cnt = 0;
                    double sum = 0;
                    for (Meal meal : meals){
                        sum += meal.getPrice() / 100;
                        cnt++;
                    }
                    avg = sum / cnt;
                    res.put(localDate, avg);
                }
                return OptionalWithMessage.of(res);
            }
        };
    }

    static Analyzer<Map<LocalDate, Double>> createTotalPrizePerDayAnalyzer() {
        return new Analyzer<Map<LocalDate, Double>>() {
            @Override
            public OptionalWithMessage<Map<LocalDate, Double>> analyze_unsafe(List<Menu> data) {
                Map<LocalDate, Double> res = new HashMap<>();
                for (Menu menu : data){
                    LocalDate localDate = menu.getDate();
                    Set<Meal> meals = menu.getMeals();
                    double avg = 0;
                    double cnt = 0;
                    double sum = 0;
                    for (Meal meal : meals){
                        sum += meal.getPrice() / 100;
                    }
                    res.put(localDate, sum);
                }
                return OptionalWithMessage.of(res);
            }
        };
    }

    static Analyzer<Meal> createPopularityAnalyzer(int rank) {
        throw new UnsupportedOperationException();
    }

    static Analyzer<Meal> createPopularMealOnWeekdayAnalyzer(DayOfWeek dayOfWeek) {
        throw new UnsupportedOperationException();
    }

    static Analyzer<Map<Integer, Long>> createPrizeRangeAnalyzer() {
        return new Analyzer<Map<Integer, Long>>() {
            @Override
            public OptionalWithMessage<Map<Integer, Long>> analyze_unsafe(List<Menu> data) {
                    Map<Integer,Long> res = new HashMap<>();
                    for (Menu menu : data){
                        Set<Meal> meals = menu.getMeals();
                        for(Meal meal: meals){
                            int price = meal.getPrice();
                            int priceFloor = (int)(price / 100.0);
                            Long value;
                            if (res.containsKey(priceFloor))
                                value = res.get(priceFloor) + 1;
                            else
                                value = 1L;
                            res.put(priceFloor,value);
                        }
                    }
                    return OptionalWithMessage.of(res);
            }
        };
    }

    static Analyzer<Map<LocalDate, Integer>> createAmountOfDishesPerDayAnalyzer() {
        return new Analyzer<Map<LocalDate, Integer>>() {
            @Override
            public OptionalWithMessage<Map<LocalDate, Integer>> analyze_unsafe(List<Menu> data) {
                Map<LocalDate, Integer> res = new HashMap<>();
                for (Menu menu: data){
                    LocalDate date = menu.getDate();
                    int amountOfDishes = menu.getMeals().size();
                    res.put(date,amountOfDishes);
                }
                return  OptionalWithMessage.of(res);
            }
        };
    }

    static Analyzer<Map<Meal, Long>> createRepetitionAnalyzer() {
        return new Analyzer<Map<Meal, Long>>() {
            @Override
            public OptionalWithMessage<Map<Meal, Long>> analyze_unsafe(List<Menu> data) {
                 Map<Meal, Long> res = new HashMap<>();
                for (Menu menu : data){
                    Set<Meal> meals = menu.getMeals();
                    for(Meal meal: meals){
                        if (!res.containsKey(meal))
                            res.put(meal,1L);
                        else
                            res.put(meal,res.get(meal) + 1);
                    }
                }
                 return OptionalWithMessage.of(res);
            }
        };
    }
}
