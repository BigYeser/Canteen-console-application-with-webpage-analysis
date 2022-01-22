package de.uniwue.jpp.mensabot.retrieval.htmlparsing;

import de.uniwue.jpp.mensabot.retrieval.htmlparsing.nodes.StandardNode;
import de.uniwue.jpp.mensabot.retrieval.htmlparsing.nodes.TextNode;
import de.uniwue.jpp.mensabot.retrieval.htmlparsing.nodes.VoidNode;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface HtmlNode {

    String getTagName();

    List<HtmlNode> getChildren();

    Map<String, String> getAttributes();

    String getText();

    String getInnerText();

    List<HtmlNode> getNodesByAttribute(String key, String value);

    void getNodesByAttribute(String key, String value, List<HtmlNode> previous);

    boolean hasAttribute(String key);

    String getAttribute(String key);

    public static HtmlNode createStandardHtmlNode(List<String> elements) throws HtmlParsingException{
        return new StandardNode(elements);
    }

    public static HtmlNode createVoidHtmlNode(String element) throws HtmlParsingException{
        return new VoidNode(element);
    }

    public static HtmlNode createTextHtmlNode(String text) {

        return new TextNode(text);
    }

    public static HtmlNode parseString(String string) throws HtmlParsingException{
        List<String> elements = HtmlParsingUtil.splitInElements(string);
        string = string.trim();
        if(!elements.get(0).toLowerCase().equals("<!doctype html>"))
            throw new HtmlParsingException();

        elements.remove(0);
        HtmlParsingUtil.cleanElements("<!--","-->",elements);
        HtmlParsingUtil.cleanElements("<script>","</script>",elements);
        HtmlParsingUtil.trimElements(elements);
        return new StandardNode(elements);

    }
}
