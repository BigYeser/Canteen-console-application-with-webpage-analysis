package de.uniwue.jpp.mensabot.retrieval;

import de.uniwue.jpp.errorhandling.OptionalWithMessage;
import de.uniwue.jpp.errorhandling.OptionalWithMessageMsg;
import de.uniwue.jpp.errorhandling.OptionalWithMessageVal;
import de.uniwue.jpp.mensabot.dataclasses.Meal;
import de.uniwue.jpp.mensabot.dataclasses.Menu;
import de.uniwue.jpp.mensabot.dataclasses.implementation.MealImp;
import de.uniwue.jpp.mensabot.dataclasses.implementation.MenuImp;
import de.uniwue.jpp.mensabot.retrieval.htmlparsing.HtmlNode;
import de.uniwue.jpp.mensabot.retrieval.htmlparsing.HtmlParsingException;
import de.uniwue.jpp.mensabot.retrieval.htmlparsing.nodes.StandardNode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface Parser {

    OptionalWithMessage<Menu> parse(String fetched);

    static Parser createCsvParser() {

        return new Parser() {
            @Override
            public OptionalWithMessage<Menu> parse(String fetched) {
                LocalDate date;
                Set<Meal> meals = new HashSet<Meal>();
                String[] items = fetched.split(";");
                if(items.length <= 1)
                    return new OptionalWithMessageMsg<Menu>("Input does not match! Input was: " + fetched);
                try {
                    date = LocalDate.parse(items[0]);
                } catch (Exception e) {
                    return new OptionalWithMessageMsg<Menu>("Invalid date");
                }

                for(int i = 1 ;  i < items.length ; i++)
                {
                    String[] meal  = items[i].split("_");
                    int price;
                    if(meal.length <= 1)
                        return new OptionalWithMessageMsg<Menu>("Input does not match! Input was: " + fetched);
                    try {
                        price = Integer.parseInt(meal[1]);
                    } catch (NumberFormatException e) {
                        return new OptionalWithMessageMsg<Menu>("Input does not match! Input was: " + fetched);
                    }
                    meals.add(new MealImp(meal[0],price));
                }

                return new OptionalWithMessageVal<Menu>(new MenuImp(date,meals));
            }
        };

    }
    static Parser createMensaHtmlParser(LocalDate date) {
        return new Parser() {
            @Override
            public OptionalWithMessage<Menu> parse( String fetched) {
                Set<Meal> meals = new HashSet<>();
                OptionalWithMessage<Menu> res;
                try {
                    HtmlNode root = HtmlNode.parseString(fetched);
                    String key = "data-day";
                    String value = toGermanDay(date.getDayOfWeek().getValue());
                    value += " " + format(date.getDayOfMonth());
                    value += format(date.getMonthValue());
                    List<HtmlNode> nodeHasDate = root.getNodesByAttribute(key,value);
                    if(nodeHasDate.size() == 0)
                        return OptionalWithMessage.ofMsg("Could not parse html!");

                    List<HtmlNode> nodeHasMenus = new ArrayList<>();
                    for (HtmlNode node : nodeHasDate){
                        if (node instanceof StandardNode) {
                            nodeHasMenus = node.getNodesByAttribute("class", "menu");
                            break;
                        }
                    }

                    if(nodeHasMenus.size() == 0)
                        return OptionalWithMessage.ofMsg("No meals found for today!");

                    for (HtmlNode node : nodeHasMenus){
                        if(node instanceof StandardNode ) {
                            List<HtmlNode> title = node.getNodesByAttribute("class", "title");
                            String name = title.get(0).getChildren().get(0).getText();
                            List<HtmlNode> priceNode = node.getNodesByAttribute("class", "price");
                            String[] priceString = priceNode.get(0).getAttribute("data-default").split(",");
                            int price = 100 * Integer.parseInt(priceString[0]) + Integer.parseInt(priceString[1]);
                            Meal meal = new MealImp(name, price);
                            meals.add(meal);
                        }
                    }
                    return OptionalWithMessage.of(new MenuImp(date,meals));
                } catch (HtmlParsingException e) {
                    return OptionalWithMessage.ofMsg("Could not parse html!");
                }

            }
        };
    }
    public static String toGermanDay(int day){
        switch (day){
            case 1: return "Montag";
            case 2: return "Dienstag";
            case 3: return "Mittwoch";
            case 4: return "Donnerstag";
            case 5: return "Freitag";
            case 6: return "Samstag";
            case 7: return "Sonntag";

        }
        return "Error Date";
    }
    public static String format(int x){
        String res = "";
        res += (x % 100) / 10;
        res += (x % 10);
        res +=".";
        return res;
    }
    public static void main(String[] args) {
        String s = "280";
        String[] priceString = s.split(",");
        int price = Integer.parseInt(priceString[0]) + Integer.parseInt(priceString[1]);
        System.out.println(price);
    }
}
