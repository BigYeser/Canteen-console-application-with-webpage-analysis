package de.uniwue.jpp.mensabot.sending.formatting;

import com.sun.jdi.event.StepEvent;
import de.uniwue.jpp.errorhandling.OptionalWithMessageMsg;
import de.uniwue.jpp.mensabot.dataclasses.Meal;
import de.uniwue.jpp.mensabot.dataclasses.Menu;
import de.uniwue.jpp.errorhandling.OptionalWithMessage;
import de.uniwue.jpp.mensabot.sending.formatting.analyse.Analyzer;
import org.xml.sax.helpers.ParserAdapter;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;

public interface Formatter {

    OptionalWithMessage<String> format(Menu latestMenu, Supplier<OptionalWithMessage<List<Menu>>> allMenus);

    static Formatter createSimpleFormatter() {
        return new Formatter() {
            @Override
            public OptionalWithMessage<String> format(Menu latestMenu, Supplier<OptionalWithMessage<List<Menu>>> allMenus) {
                String firstLine = "Essen am "+ latestMenu.getDate() + ":" ;
                String end = firstLine+"";
                for(Meal meal : latestMenu.getMeals())
                {
                    end +=  "\n" +meal.toString() ;
                }

                return OptionalWithMessage.of(end);
            }

            @Override
            public String toString() {
                return "SimpleFormatter";
            }
        };
    }

    static Formatter createSimpleMealFormatter() {
        return new Formatter() {
            @Override
            public OptionalWithMessage<String> format(Menu latestMenu, Supplier<OptionalWithMessage<List<Menu>>> allMenus) {
                String end = "";
                for(Meal meal : latestMenu.getMeals())
                {
                    end +=meal.toString() +"\n" ;
                }
                return OptionalWithMessage.of(end.substring(0, end.length() - 1));
            }

            @Override
            public String toString() {
                return "SimpleMealFormatter";
            }
        };
    }

    static Formatter createFormatterFromAnalyzer(String headline, Analyzer<?> analyzer) {
        if(headline == null || headline.isEmpty() ||analyzer == null)
            throw new IllegalArgumentException("Illegal argument!");
        return new Formatter() {
            @Override
            public OptionalWithMessage<String> format(Menu latestMenu, Supplier<OptionalWithMessage<List<Menu>>> allMenus) {
                if (allMenus == null)
                    return OptionalWithMessage.ofMsg(headline + ":"+"Analyzing is not possible" +"\n");

                String end = "";
                if (allMenus.get() instanceof OptionalWithMessageMsg || analyzer.analyze(allMenus.get().get()) instanceof OptionalWithMessageMsg) {
                    return OptionalWithMessage.of(headline + ":"+"Analyzing is not possible" +"\n");
                }

                if( analyzer.toString().equals("MinPrizeMealAnalyzer") || analyzer.toString().equals("MaxPrizeMealAnalyzer") )
                {

                    String sample = analyzer.analyze(allMenus.get().get()).toString();
                    char[] chars = sample.toCharArray();
                    StringBuilder sb = new StringBuilder();
                    for(char c : chars){
                        if(Character.isDigit(c)){
                            sb.append(c);
                        }
                    }

                    end = headline + ":" + sb+ "\n";
                }
                else
                {
                    end += headline + ":" + analyzer.analyze(allMenus.get().get()).toString()+ "\n";
                }
                return OptionalWithMessage.of(end);
            }
            @Override
            public String toString() {
                return "FormatterFromAnalyzer";
            }
        };
    }

    static Formatter createFormatterFromAnalyzer(String headline, Analyzer<?> analyzer, String name) {
        if(headline == null ||name == null||  headline.isEmpty() ||analyzer == null ||name.isEmpty() )
            throw new IllegalArgumentException("Illegal argument!");
        return new Formatter() {
            @Override
            public OptionalWithMessage<String> format(Menu latestMenu, Supplier<OptionalWithMessage<List<Menu>>> allMenus) {
                if (allMenus == null)
                    return OptionalWithMessage.ofMsg(headline + ":"+"Analyzing is not possible" +"\n");
                String end = "";
                if (allMenus.get() instanceof OptionalWithMessageMsg || analyzer.analyze(allMenus.get().get()) instanceof OptionalWithMessageMsg) {
                    return OptionalWithMessage.of(headline + ":"+"Analyzing is not possible" +"\n");
                }

                if( analyzer.toString().equals("MinPrizeMealAnalyzer") || analyzer.toString().equals("MaxPrizeMealAnalyzer") )
                {

                    String sample = analyzer.analyze(allMenus.get().get()).toString();
                    char[] chars = sample.toCharArray();
                    StringBuilder sb = new StringBuilder();
                    for(char c : chars){
                        if(Character.isDigit(c)){
                            sb.append(c);
                        }
                    }

                    end = headline + ":" + sb+ "\n";
                }
                else
                {
                    end = headline + ":" + analyzer.analyze(allMenus.get().get()).toString()+ "\n";

                }
                return OptionalWithMessage.of(end);
            }
            @Override
            public String toString() {
                return name;
            }
        };
    }


    ///////////////
    static Formatter createComplexFormatter(List<String> headlines, List<Analyzer<?>> analyzers) {
        if (headlines == null || analyzers == null || analyzers.isEmpty()|| headlines.isEmpty())
            throw new IllegalArgumentException("Illegal argument!");
        if (headlines.size() != analyzers.size())
            throw new IllegalArgumentException("There must be a headline for each analyzer!");
        return new Formatter() {
            @Override
            public OptionalWithMessage<String> format(Menu latestMenu, Supplier<OptionalWithMessage<List<Menu>>> allMenus) {
                String end = "";
                int i = 0;
                for (Analyzer<?> analyzer : analyzers) {
                    i++;

                    if (analyzer.toString().equals("MinPrizeMealAnalyzer") || analyzer.toString().equals("MaxPrizeMealAnalyzer")) {

                        assert allMenus != null;
                        String sample = analyzer.analyze(allMenus.get().get()).toString();
                        char[] chars = sample.toCharArray();
                        StringBuilder sb = new StringBuilder();
                        for (char c : chars) {
                            if (Character.isDigit(c)) {
                                sb.append(c);
                            }
                        }

                        end += headlines.get(i-1) + ":" + sb + "\n";
                    } else {

                        if (allMenus == null || allMenus.get() instanceof OptionalWithMessageMsg ||
                                analyzer.analyze(allMenus.get().get()) instanceof OptionalWithMessageMsg) {
                            end += OptionalWithMessage.of(headlines.get(i-1)+ ":" + "Analyzing is not possible" + "\n").toString();
                        }else
                            end += headlines.get(i-1) + ":" + analyzer.analyze(allMenus.get().get()).toString() + "\n";
                    }
                }
                if (allMenus == null)
                    return OptionalWithMessage.ofMsg(end);
                return OptionalWithMessage.of(end);
            }
            @Override
            public String toString() {
                return "ComplexFormatter";
            }
        };
    }


    ///////////////
    static Formatter createFormatterFromFormat(String format, List<Analyzer<?>> analyzers, String name) {

        int j = 0;
        if(format == null ||format.isEmpty())
            throw new IllegalArgumentException("Illegal format argument!");
        if(name == null ||name.isEmpty())
            throw new IllegalArgumentException("Illegal name argument!");
        if(!format.contains("$"))
            throw new IllegalArgumentException("Format must contain $ signs!");
        if(analyzers == null || analyzers.isEmpty())
            throw new IllegalArgumentException("Illegal analyzer argument!");

        for(int i = 0 ; i<format.length();i++) {
            if (format.charAt(i) == '$') {
                j++;
            }
        }
        if(analyzers.size() != j)
            throw new IllegalArgumentException("There must be a $ for each analyzer");



        return new Formatter() {
            @Override
            public OptionalWithMessage<String> format(Menu latestMenu, Supplier<OptionalWithMessage<List<Menu>>> allMenus) {


                String[] strArr = format.split("\\$");
                System.out.println(Arrays.toString(strArr).toString());
                System.out.println(strArr.length);
                StringBuilder Builder = new StringBuilder();
                List<Analyzer<?>> analyzerList = new ArrayList<>(analyzers);
                for(int i = 0; i < strArr.length; i++)
                {
                    Builder.append(strArr[i]);


                    if (i != analyzerList.size())
                    {
                        if (analyzerList.get(i).toString().equals("MinPrizeMealAnalyzer") || analyzerList.get(i).toString().equals("MaxPrizeMealAnalyzer"))
                        {
                            String meal  = analyzerList.get(i).analyze(allMenus.get().get()).toString();
                            int iend = meal.indexOf("(");
                            String subString ="";
                            if (iend != -1)
                            {
                                subString= meal.substring(0 , iend); //this will give
                            }
                            Builder.append(subString);

                        }
                        else if (allMenus == null || allMenus.get() instanceof OptionalWithMessageMsg ||
                                analyzerList.get(i).analyze(allMenus.get().get()) instanceof OptionalWithMessageMsg) {
                            Builder.append("Analyzing is not possible");
                        }

                        else
                            Builder.append(analyzerList.get(i).analyze(allMenus.get().get()).toString());
                    }
                }
                for(Analyzer ana : analyzers) {

                }
                if (allMenus == null)
                    return OptionalWithMessage.ofMsg(Builder.toString());
                return OptionalWithMessage.of(Builder.toString());
            }
            @Override
            public String toString() {
                return name;
            }
        };
    }

    ////////////////    /////////////////
    /* Die folgenden Formatter werden nicht getestet und muessen auch nicht implementiert werden.
     * Es sind lediglich Vorschlaege für Aufgabenteil 3. */
    static Formatter createHiddenFormatter() {
        return new Formatter() {
            @Override
            public OptionalWithMessage<String> format(Menu latestMenu, Supplier<OptionalWithMessage<List<Menu>>> allMenus) {
                String res ="";
                Set<Meal> meals = latestMenu.getMeals();
                for (Meal meal : meals)
                    res += "(" + meal.getPrice() +"\u20ac)" + System.lineSeparator();
                return  OptionalWithMessage.of(res);
            }
        };
    }
    static Formatter createShortFormatter() {
        return new Formatter() {
            @Override
            public OptionalWithMessage<String> format(Menu latestMenu, Supplier<OptionalWithMessage<List<Menu>>> allMenus) {
                String res = "";
                Set<Meal> meals = latestMenu.getMeals();
                for(Meal meal : meals){
                    String[] wordsOfName = meal.getName().split(" ");
                    for (String word : wordsOfName){
                        res += word.charAt(0);
                    }
                    res += " (" + meal.getPrice() +"\u20ac)" + System.lineSeparator();
                }
                return OptionalWithMessage.of(res);
            }
        };
    }

    static Formatter createFirstWordFormatter() {
        return new Formatter() {
            @Override
            public OptionalWithMessage<String> format(Menu latestMenu, Supplier<OptionalWithMessage<List<Menu>>> allMenus) {
                String res = "";
                Set<Meal> meals = latestMenu.getMeals();
                for(Meal meal : meals){
                    res += meal.getName().split(" ")[0] + " (" + meal.getPrice() +"\u20ac)" + System.lineSeparator();
                }
                return OptionalWithMessage.of(res);
            }
        };
    }

    static Formatter createPricelessFormatter() {
        return new Formatter() {
            @Override
            public OptionalWithMessage<String> format(Menu latestMenu, Supplier<OptionalWithMessage<List<Menu>>> allMenus) {
                String res = "";
                Set<Meal> meals = latestMenu.getMeals();
                for(Meal meal : meals){
                    res += meal.getName() + System.lineSeparator();
                }
                return OptionalWithMessage.of(res);
            }
        };
    }

    static Formatter createSimpleTotalFormatter() {
        throw new UnsupportedOperationException();
    }

    static Formatter createAlphabeticalOrderFormatter() {
        return new Formatter() {
            @Override
            public OptionalWithMessage<String> format(Menu latestMenu, Supplier<OptionalWithMessage<List<Menu>>> allMenus) {
                String res = "";
                Set<Meal> meals = latestMenu.getMeals();
                List<String> listOfMeals = new ArrayList<>();
                for (Meal meal : meals){
                    listOfMeals.add(meal.toString());
                }
                Collections.sort(listOfMeals);
                for (String meal : listOfMeals){
                    res += meal + System.lineSeparator();
                }
                return OptionalWithMessage.of(res);
            }
        };
    }

    public static void main(String[] args) {
        Set<Meal> s = new HashSet<>();
        Set<Meal> s1 = new HashSet<>();
        s.add(Meal.createMeal("Hummos1  ",50));
        s.add(Meal.createMeal("Falafel1  ", 60));
        s.add(Meal.createMeal("makluba1  ", 40));

        s1.add(Meal.createMeal("Hummos2  ",100));
        s1.add(Meal.createMeal("Falafel2  ", 200));
        s1.add(Meal.createMeal("makluba2  ", 300));

        Menu m = Menu.createMenu(LocalDate.now(),s);
        Menu m1 = Menu.createMenu(LocalDate.of(2020,2,2),s1);
        List<Menu> menList = new ArrayList<>();
        menList.add(m1);
        menList.add(m);

        Supplier<OptionalWithMessage<List<Menu>>> allMenus = ()-> OptionalWithMessage.of(menList);


        Analyzer an = new Analyzer() {
            @Override
            public OptionalWithMessage analyze_unsafe(List data) {
                return OptionalWithMessage.ofMsg("");
            }
        };
        List<String> strList = new ArrayList<>();
        strList.add("1");
        strList.add("2");
        strList.add("3");




        List<Analyzer<?>> analyzList = new ArrayList<>();
        Analyzer<?> ss = Analyzer.createMinPrizeMealAnalyzer();
        Analyzer<?> s2 = Analyzer.createMaxPrizeMealAnalyzer();
        Analyzer<?> s3 = Analyzer.createTotalPrizeAnalyzer();

        analyzList.add(ss);
        analyzList.add(s2);
        analyzList.add(an);



        String formatt = "Am heutigen Tag ist $ das billigste Essen und $ das teuerste Essen. " +
                "Will man alle Gerichte konsumieren, so müssen $ct bezahlt werden" ;


        System.out.println(Formatter.createFormatterFromFormat(formatt,analyzList, "Hallo").format(m1,allMenus));

 /*
        StringBuilder formatt = new StringBuilder("");



        String[] strArr = str.split("\\$");

        formatt.append("Hallo");
        formatt.append("Hallo2");
        System.out.println(formatt);
*/

    }


}
