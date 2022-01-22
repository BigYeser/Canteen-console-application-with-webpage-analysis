package de.uniwue.jpp.mensabot.retrieval.htmlparsing.nodes;

import de.uniwue.jpp.mensabot.retrieval.htmlparsing.HtmlNode;
import de.uniwue.jpp.mensabot.retrieval.htmlparsing.HtmlParsingException;
import de.uniwue.jpp.mensabot.retrieval.htmlparsing.HtmlParsingUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VoidNode implements HtmlNode {
    private String tagName;
    private Map<String ,String> attributes;
    private String text;

    public VoidNode(String element) throws HtmlParsingException {

      if(element.isEmpty())
          throw new HtmlParsingException();
      if(HtmlParsingUtil.isStartTag(element).equals(Optional.empty()))
          throw new HtmlParsingException();
      if(!HtmlParsingUtil.isVoidElement(HtmlParsingUtil.isStartTag(element).get()))
          throw new HtmlParsingException();
        attributes = HtmlParsingUtil.parseAttributeString(HtmlParsingUtil.getAttributeString(element));

        text = element;
        tagName = HtmlParsingUtil.isStartTag(element).get();
    }

    @Override
    public String getTagName() {
        return tagName;
    }

    @Override
    public List<HtmlNode> getChildren() {
        return new ArrayList<HtmlNode>();
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getInnerText() {
        return "";
    }

    @Override
    public List<HtmlNode> getNodesByAttribute(String key, String value) {
        List<HtmlNode> res = new ArrayList<HtmlNode>();
        if(getAttribute(key).equals(value))
            res.add(this);
        return res;
    }

    @Override
    public void getNodesByAttribute(String key, String value, List<HtmlNode> previous) {
        if (getAttribute(key).equals(value))
            previous.add(this);
    }

    @Override
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    @Override
    public String getAttribute(String key) {
        return hasAttribute(key)? attributes.get(key) : null;
    }

    @Override
    public String toString() {
        return text;
    }
}
