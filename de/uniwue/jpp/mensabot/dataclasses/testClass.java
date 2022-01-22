package de.uniwue.jpp.mensabot.dataclasses;

import de.uniwue.jpp.errorhandling.OptionalWithMessage;
import de.uniwue.jpp.mensabot.retrieval.Parser;
import de.uniwue.jpp.mensabot.retrieval.htmlparsing.HtmlNode;
import de.uniwue.jpp.mensabot.retrieval.htmlparsing.HtmlParsingException;
import de.uniwue.jpp.mensabot.retrieval.htmlparsing.nodes.StandardNode;
import de.uniwue.jpp.mensabot.retrieval.htmlparsing.nodes.TextNode;
import de.uniwue.jpp.mensabot.sending.MessageTelegram;
import de.uniwue.jpp.mensabot.util.JsonUtil;

import java.time.LocalDate;
import java.util.*;

public class testClass {
    public static void main(String[] args) throws HtmlParsingException {
       // System.out.println(HtmlParsingUtil.isStartTag("<  />"));
        //System.out.println(HtmlParsingUtil.isEndTag("'</anotherEndTag >"));
        //System.out.println(HtmlParsingUtil.isVoidElement("area"));
         //System.out.println(HtmlParsingUtil.getAttributeString("<start key/>"));
        //System.out.println(HtmlParsingUtil.getAttributeString("<test key1='value1' key     />").length());
        //HtmlParsingUtil.parseAttributeString("data-did =\"567214\" title=\"Aktions Essen\" class=\"icon\"").forEach((x, y) -> System.out.println(x+"="+y));
       // HtmlParsingUtil.parseAttributeString("key1=value1 key2=value2").forEach((x,y) -> System.out.println(x+"="+y));
       // HtmlParsingUtil.parseAttributeString("key1 = \"value1\" key2= value2").forEach((x,y) -> System.out.println(x+"="+y));
       // HtmlParsingUtil.parseAttributeString("key1='value1' key2").forEach((x,y) -> System.out.println(x+"="+y));
       // HtmlParsingUtil.parseAttributeString("key1=val ue1 key2='val ue2'").forEach((x,y) -> System.out.println(x+"="+y));
        //HtmlParsingUtil.parseAttributeString("key1=val'ue1").forEach((x,y) -> System.out.println(x+"="+y));
       // System.out.println(HtmlParsingUtil.getAttributeString("< start  key=value     key='value' />"));
      /*  List<String> clean = new ArrayList<>();
        clean.add("<!DOCTYPE html>");
        clean.add("<html>");
        clean.add("<!--Just a comment-->");
        clean.add("</html>");

        String startTag = "<!--";
        String endTag = "-->";
        HtmlParsingUtil.cleanElements(startTag,endTag,clean);
        clean.forEach(x -> System.out.println(x));
        */
       // HtmlNode voidNode = HtmlNode.createVoidHtmlNode("<embed wow=wow key3=owo head=vicerNalue>");
        //voidNode.getNodesByAttribute("wow","wow").forEach((x) -> System.out.println(x));
     //  System.out.println(HtmlParsingUtil.isStartTag("</a>"));
       /* List<String> elements = new ArrayList<>();
        elements.add("<!DOCTYPE html>");
        elements.add("<html>");
        elements.add("<p>");
        elements.add("P");
        elements.add("");
        elements.add("<a>");
        elements.add("<A>");
        elements.add("");
        elements.add("<pp>");
        elements.add("PP");
        elements.add("</pp>");
        elements.add("<p>");
        elements.add("P");
        elements.add("</p>");
        elements.add("</html>");
     //   elements.forEach(x -> System.out.println(x));
      //  System.out.println();
        HtmlParsingUtil.cleanElements("<p>","</p>",elements);

        elements.forEach(x -> System.out.println(x));
        */
     /*   String text = "";
        */
      //  List<String> elements = new ArrayList<>();
       /* elements.add("<there there>");
        elements.add("");
        elements.add("<div class=\"nice\">");
        elements.add("some \"text\"");
        elements.add("</div>");
        elements.add("");
        elements.add("<br>");
        elements.add("</there>");*/
     /*   elements.add("<head head=schmead key2='value1' there=\"._.\" wow=owo>");
        elements.add("<meta charset=\"utf-8\">");
        elements.add("<base href=\"https://www.studentenwerk-wuerzburg.de/\">");
        elements.add("<title>");
        elements.add("<p>");
        elements.add("Nice Site");
        elements.add("</p>");
        elements.add("</title>");
        elements.add("<br>");
        elements.add("text");
        elements.add("</head>");*/
        /*System.out.println("<!DOCTYPE html>".toLowerCase());
        Map<String, CharacterReference> characterReferenceMap =  CharacterReference.loadCharacterReferences();
        String text = "&&&&dollar;&dollar;&#36;&#x24;&#X24;&&";
        System.out.println(HtmlParsingUtil.replaceCharacterReferencesInText(text));
        System.out.println(HtmlNode.createTextHtmlNode("&Subset;").getText());
       */
     /*   OptionalWithMessage<String> dataAsCsv = Fetcher.createDummyCsvFetcher().fetchCurrentData();
        if (dataAsCsv instanceof OptionalWithMessageVal) {
            OptionalWithMessage<Menu> dataAsMenu = Parser.createCsvParser().parse(dataAsCsv.get());
            if (dataAsMenu instanceof OptionalWithMessageVal) {
                Optional<String> saver = Saver.createCsvSaver().log(Path.of(nicerValue), dataAsMenu.get());
                if (saver.equals(Optional.empty())) {
                    System.out.println("done");
                }
                else
                    System.out.println("error during save data: " + saver.get());
            }else {
                System.out.println("error during parse data : " + dataAsMenu.getMessage());
            }
        }else {
            System.out.println("error during fetch data: " + dataAsCsv.getMessage());
        }*/

      /*  String feched = "<!DOCTYPE html> <html> <head> <title> hello </title> </head> <body> <div class = \"day\" data-day = \"Sonntag 12.09.\" > <div class = \"menuwrap\" > <article class = \"menu\" > <div class = \"left\" > <div class = \"title\" > Mensa-Vital: Gnocchis mit buntem Gemüse und frischem Bärlauch </div > </div> <div class = \"price\" data-default= \"2,55\" data-bed= \"3,80\" data-guest= \"4,75\" > <span> 2 , 55 </span> &euro; </div > </article> <article class = \"menu\" > <div class = \"left\" > <div class = \"title\" > AAAAAAAAAAAAAAAAAAA </div > </div> <div class = \"price\" data-default= \"3,55\" data-bed= \"3,80\" data-guest= \"4,75\" > <span> 3 , 55 </span> &euro; </div > </article> </div> </div> </body> </html>";
        HtmlNode root = HtmlNode.parseString(feched);
        dfs(root,"");
        OptionalWithMessage<Menu> menu =  Parser.createMensaHtmlParser(LocalDate.now()).parse(feched);
        if (!(menu instanceof OptionalWithMessageMsg)) {
            System.out.println(menu.get().getDate());
            menu.get().getMeals().forEach(x -> System.out.println(x.toString()));
        }
        else
            System.out.println(menu.getMessage());
        */
   /*     List<String> elements = new ArrayList<>();

        elements.add("<general jpp=\"viel spass\">");
        elements.add("<br class=\"<!DOCTYPE html><html><ul key1='nicerValue' ><li>Item1</li><li >Item2</li><li>Item3</li></ul></html>\">");
        elements.add("</general>");*/
       // HtmlNode root = HtmlNode.createStandardHtmlNode(elements);
        //System.out.println(root.getText());
       // System.out.println(root.getInnerText());
     //   String text = "<general jpp=\"viel spass\"><br class=\"<!DOCTYPE html><html><ul key1='nicerValue' ><li>Item1</li><li >Item2</li><li>Item3</li></ul></html>\"></general>";
       // text = "<!DOCTYPE html><html><br class=\"<!DOCTYPE html><html><ul key1='nicerValue' ><li>Item1</li><li>Ite m2</li><li>Item3</li></ul></html>\"</html>";
      //  System.out.println(HtmlNode.parseString(text).getInnerText());
/*
      HtmlNode node = HtmlNode.createStandardHtmlNode(elements);
     node.getAttributes().forEach((x,y) -> System.out.println(x+"="+y));
     HtmlNode voidNode = HtmlNode.createVoidHtmlNode("<br class=\"<!DOCTYPE html><html><ul key1='nicerValue' ><li>Item1</li><li >Item2</li><li>Item3</li></ul></html>\">");
     voidNode.getAttributes().forEach((x,y) -> System.out.println(x+"="+y));
     //   HtmlParsingUtil.parseAttributeString("class=\"price\" data-default=\"3,45\" data-bed=\"4,75\" data-guest=\"5,70\"").forEach((x,y) -> System.out.println(x+"=" + y));
        HtmlParsingUtil.parseAttributesFromStartTag(" <div class=\"price\" data-default=\"3,45\" data-bed=\"4,75\" data-guest=\"5,70\">").forEach((x,y) -> System.out.println(x+ "=" + y));
        Map<String,String> mp = new HashMap<>();
        mp.put("",null);
        mp.remove("");
        mp.forEach((x,y) -> System.out.println(x+"="+y));
        // dfs(root,"");
        //dfs(root, "");*/
        //HtmlNode.parseString("<!DOCTYPE html><html><br><p> <!-- some Stuff --> <script info=java>System.out.println(\"Hello World!\")<<<<<>>>>></script></p></html>");
      /*  String chatId = "1234";
        String msg = "X08C4";
        //OptionalWithMessage<MessageTelgram> test = JsonUtil.stringToObject("{ \"chat_id\" : \"" + chatId+ "\", \"msg\" : \""+ msg +"\" }" , MessageTelgram.class);
       // System.out.println(test.get().getChat_id() +" " + test.get().getMsg());
        MessageTelegram messageTelegram = new MessageTelegram();
        messageTelegram.setChat_id(chatId);
        messageTelegram.setMsg(msg);
        String json = JsonUtil.objectToString(messageTelegram).get();
        System.out.println(json);*/
       /* String s = "11,05";
        String[] priceString = s.split(",");
        System.out.println(priceString[0]);
        System.out.println(priceString[1]);
        int price = 100 * Integer.parseInt(priceString[0]) + Integer.parseInt(priceString[1]);
        System.out.println(price);
        System.out.println(Parser.createMensaHtmlParser(LocalDate.now()).parse("test"));

        */
        System.out.println("azdabcd".compareTo("bcd"));
        //String json = JsonUtil.objectToString(new MessageTelegram("ABX12","Hello")).get();
        //System.out.println(json);
        // HtmlParsingUtil.trimElements(elements);
       // HtmlParsingUtil.cleanElements("<!--","-->",elements);
       // elements.forEach(x -> System.out.println(x));
        //HtmlNode htmlNode = HtmlNode.createStandardHtmlNode(elements);
        //dfs(htmlNode,"");
       // CharacterReference.loadCharacterReferences();
     /*   System.out.println(LocalDate.parse("2021-09-12").getDayOfWeek().getValue());

        System.out.println(LocalDate.now().getDayOfWeek().getValue());
        String data = "<!DOCTYPE html><html><head><title>hello</title></head><body><div data-date=\"Freitag 10.01.\"><div class=\"menu\"><div class=\"title\">first meal</div><div class=\"price\" data-default=\"2.55\">2.55</div></div><div class=\"menu\"><div class=\"title\">second meal</div><div class=\"price\" data-default=\"3.55\">2.55</div></div></div></body></html>";
        OptionalWithMessage<Menu> menu = Parser.createMensaHtmlParser(LocalDate.now()).parse(data);
        Menu menu1 = menu.get();
        System.out.println(menu1.getDate().toString());
        menu1.getMeals().forEach(x -> System.out.println(x.toString()));*/
    }

    public static void dfs(HtmlNode node,String spaces)
    {
        if(node instanceof TextNode)
            System.out.println(spaces + node.getText());
        else
            System.out.println(spaces + node.getTagName());
        if(!(node instanceof StandardNode))
            return;
        for (HtmlNode child : node.getChildren()){
            dfs(child,spaces +"\t");
        }
        System.out.println(spaces + node.getTagName());
    }
}