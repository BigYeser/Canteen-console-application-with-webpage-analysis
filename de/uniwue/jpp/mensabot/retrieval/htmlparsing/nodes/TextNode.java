package de.uniwue.jpp.mensabot.retrieval.htmlparsing.nodes;

import de.uniwue.jpp.mensabot.retrieval.htmlparsing.HtmlNode;
import de.uniwue.jpp.mensabot.retrieval.htmlparsing.HtmlParsingUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TextNode implements HtmlNode {
    private String text ;

    public TextNode(String text) {
        this.text = text;
    }


    @Override
    public String getTagName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<HtmlNode> getChildren() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> getAttributes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getText() {
        return HtmlParsingUtil.replaceCharacterReferencesInText(text);
    }

    @Override
    public String getInnerText() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<HtmlNode> getNodesByAttribute(String key, String value) {
        return new ArrayList<HtmlNode>();
    }

    @Override
    public void getNodesByAttribute(String key, String value, List<HtmlNode> previous) {
    }

    @Override
    public boolean hasAttribute(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAttribute(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return text;
    }

    public static void main(String[] args) {
        System.out.println (new TextNode ("Ig8yyzExxI140zY6knAK").getText());
    }

}
