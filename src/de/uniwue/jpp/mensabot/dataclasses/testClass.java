package de.uniwue.jpp.mensabot.dataclasses;

import de.uniwue.jpp.mensabot.retrieval.htmlparsing.HtmlNode;
import de.uniwue.jpp.mensabot.retrieval.htmlparsing.HtmlParsingException;
import de.uniwue.jpp.mensabot.retrieval.htmlparsing.nodes.StandardNode;
import de.uniwue.jpp.mensabot.retrieval.htmlparsing.nodes.TextNode;

import java.util.ArrayList;
import java.util.List;

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
        List<String> elements = new ArrayList<>();
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
        elements.add("<general jpp=\"viel spass\">");
        elements.add("<br class=\"<!DOCTYPE html><html><ul key1='nicerValue' ><li>Item1</li><li >Item2</li><li>Item3</li></ul></html>\">");
        elements.add("</general>");
        HtmlNode root = HtmlNode.createStandardHtmlNode(elements);

        System.out.println(root.getText());
        System.out.println(root.getInnerText());
       // dfs(root,"");
        //dfs(root, "");*/


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