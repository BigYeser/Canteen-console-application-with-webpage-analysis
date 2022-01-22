package de.uniwue.jpp.mensabot.sending.formatting;

import de.uniwue.jpp.errorhandling.OptionalWithMessageMsg;
import de.uniwue.jpp.mensabot.dataclasses.Meal;
import de.uniwue.jpp.mensabot.dataclasses.Menu;
import de.uniwue.jpp.errorhandling.OptionalWithMessage;
import de.uniwue.jpp.mensabot.sending.formatting.analyse.Analyzer;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public interface Formatter {

    OptionalWithMessage<String> format(Menu latestMenu, Supplier<OptionalWithMessage<List<Menu>>> allMenus);

    static Formatter createSimpleFormatter() {
        return new Formatter() {
            @Override
            public OptionalWithMessage<String> format(Menu latestMenu, Supplier<OptionalWithMessage<List<Menu>>> allMenus) {
                String res = "Essen am "+latestMenu.getDate()+":"+System.lineSeparator();
                for(Meal meal : latestMenu.getMeals())
                    res += meal + System.lineSeparator();

                return OptionalWithMessage.of(res + System.lineSeparator());
            }

            public String toString(){
                return "SimpleFormatter";
            }
        };
    }

    static Formatter createSimpleMealFormatter() {
        return new Formatter() {
            @Override
            public OptionalWithMessage<String> format(Menu latestMenu, Supplier<OptionalWithMessage<List<Menu>>> allMenus) {
               String res ="";
                for(Meal meal : latestMenu.getMeals())
                    res += meal + System.lineSeparator();

                return OptionalWithMessage.of(res + System.lineSeparator());
            }

            public String toString(){
                return "SimpleMealFormatter";
            }
        };
    }

    static Formatter createFormatterFromAnalyzer(String headline, Analyzer<?> analyzer) {
        return createFormatterFromAnalyzer(headline,analyzer,"FormatterFromAnalyzer");
    }

    static Formatter createFormatterFromAnalyzer(String headline, Analyzer<?> analyzer, String name) {
        if (name == null || name.length() == 0 || headline == null || headline.length() == 0 || analyzer == null)
            throw new IllegalArgumentException("Illegal argument!");
        return new Formatter() {
            @Override
            public OptionalWithMessage<String> format(Menu latestMenu, Supplier<OptionalWithMessage<List<Menu>>> allMenus) {
                if (allMenus == null || allMenus.get() instanceof OptionalWithMessageMsg || allMenus.get().isEmpty() || analyzer.analyze(allMenus.get().get()) instanceof OptionalWithMessageMsg)
                    return OptionalWithMessage.of(headline +":" + "Analyzing is not possible");
                return OptionalWithMessage.of(headline +":" + analyzer.analyze(allMenus.get().get()) + System.lineSeparator());
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }

    static Formatter createComplexFormatter(List<String> headlines, List<Analyzer<?>> analyzers) {
        if (headlines == null || analyzers == null || analyzers.isEmpty() || headlines.isEmpty())
            throw new IllegalArgumentException("Illegal argument!");
        if (headlines.size() != analyzers.size())
            throw  new IllegalArgumentException("There must be a headline for each analyzer!");
        return new Formatter() {
            @Override
            public OptionalWithMessage<String> format(Menu latestMenu, Supplier<OptionalWithMessage<List<Menu>>> allMenus) {
               Iterator<String> it1 = headlines.iterator();
               Iterator<Analyzer<?>> it2 = analyzers.iterator();
               String res = "";
               while (it1.hasNext() && it2.hasNext()){
                    String headline = it1.next();
                    Analyzer<?> analyzer = it2.next();
                   if (allMenus == null || allMenus.get() instanceof OptionalWithMessageMsg || allMenus.get().isEmpty() || analyzer.analyze(allMenus.get().get()) instanceof OptionalWithMessageMsg)
                       res += (headline +":" + "Analyzing is not possible");
                   else
                       res += (headline +":" + analyzer.analyze(allMenus.get().get()) + System.lineSeparator());
                }
               return  OptionalWithMessage.of(res);
            }

            @Override
            public String toString() {
                return "ComplexFormatter";
            }
        };
    }

    static Formatter createFormatterFromFormat(String format, List<Analyzer<?>> analyzers, String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Illegal name argument!");
        if (format == null || format.isEmpty())
            throw new IllegalArgumentException("Illegal format argument!");
        String[] seg = format.split("\\$");
        if (seg.length == 1)
            throw new IllegalArgumentException("Format must contain $ signs!");
        if(analyzers == null || analyzers.isEmpty())
            throw new IllegalArgumentException("Illegal analyzer argument!");

        if(seg.length - 1 != analyzers.size())
            throw new IllegalArgumentException("There must be a $ for each analyzer");

        return new Formatter() {
            @Override
            public OptionalWithMessage<String> format(Menu latestMenu, Supplier<OptionalWithMessage<List<Menu>>> allMenus) {
                String[] seg = format.split("\\$");
                String res = seg[0];
                int idx = 1;
                for (Analyzer<?> analyzer : analyzers){
                    if (allMenus == null || allMenus.get() instanceof OptionalWithMessageMsg || allMenus.get().isEmpty() || analyzer.analyze(allMenus.get().get()) instanceof OptionalWithMessageMsg)
                        res += ("Analyzing is not possible");
                    else
                        res += analyzer.analyze(allMenus.get().get());
                    res += seg[idx++];
                }

                return OptionalWithMessage.of(res);
            }

            public String toString() {
                return name;
            }
        };
    }


    /* Die folgenden Formatter werden nicht getestet und muessen auch nicht implementiert werden.
     * Es sind lediglich Vorschlaege für Aufgabenteil 3. */
    static Formatter createHiddenFormatter() {
        throw new UnsupportedOperationException();
    }

    static Formatter createFirstWordFormatter() {
        throw new UnsupportedOperationException();
    }

    static Formatter createShortFormatter() {
        throw new UnsupportedOperationException();
    }

    static Formatter createPricelessFormatter() {
        throw new UnsupportedOperationException();
    }

    static Formatter createSimpleTotalFormatter() {
        throw new UnsupportedOperationException();
    }

    static Formatter createAlphabeticalOrderFormatter() {
        throw new UnsupportedOperationException();
    }

}
