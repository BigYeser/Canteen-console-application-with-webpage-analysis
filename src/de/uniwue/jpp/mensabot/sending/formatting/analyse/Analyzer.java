package de.uniwue.jpp.mensabot.sending.formatting.analyse;

import de.uniwue.jpp.errorhandling.OptionalWithMessage;
import de.uniwue.jpp.errorhandling.OptionalWithMessageMsg;
import de.uniwue.jpp.errorhandling.OptionalWithMessageVal;
import de.uniwue.jpp.mensabot.dataclasses.Meal;
import de.uniwue.jpp.mensabot.dataclasses.Menu;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
        throw new UnsupportedOperationException();
    }

    static Analyzer<Map<LocalDate, Double>> createTotalPrizePerDayAnalyzer() {
        throw new UnsupportedOperationException();
    }

    static Analyzer<Meal> createPopularityAnalyzer(int rank) {
        throw new UnsupportedOperationException();
    }

    static Analyzer<Meal> createPopularMealOnWeekdayAnalyzer(DayOfWeek dayOfWeek) {
        throw new UnsupportedOperationException();
    }

    static Analyzer<Map<Integer, Long>> createPrizeRangeAnalyzer() {
        throw new UnsupportedOperationException();
    }

    static Analyzer<Map<LocalDate, Integer>> createAmountOfDishesPerDayAnalyzer() {
        throw new UnsupportedOperationException();
    }

    static Analyzer<Map<Meal, Long>> createRepetitionAnalyzer() {
        throw new UnsupportedOperationException();
    }
}
