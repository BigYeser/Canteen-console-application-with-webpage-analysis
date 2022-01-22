package de.uniwue.jpp.mensabot.retrieval.htmlparsing.nodes;

import de.uniwue.jpp.mensabot.retrieval.htmlparsing.HtmlNode;
import de.uniwue.jpp.mensabot.retrieval.htmlparsing.HtmlParsingException;
import de.uniwue.jpp.mensabot.retrieval.htmlparsing.HtmlParsingUtil;

import java.util.*;

public class StandardNode implements HtmlNode {
    private List<String> elements;
    private String tagName;
    private Map<String ,String> attributes;
    private List<HtmlNode> children;
    private String text;

    public StandardNode(List<String> elements) throws HtmlParsingException {
        children = new ArrayList<HtmlNode>();
        if (elements.size() < 2)
            throw new HtmlParsingException();
        if(HtmlParsingUtil.isStartTag(elements.get(0)).equals(Optional.empty()))
            throw new HtmlParsingException();
        if(HtmlParsingUtil.isEndTag(elements.get(elements.size() - 1)).equals(Optional.empty()))
            throw new HtmlParsingException();
        if(!(HtmlParsingUtil.isStartTag(elements.get(0)).equals(HtmlParsingUtil.isEndTag(elements.get(elements.size() - 1)))))
            throw new HtmlParsingException();
        attributes = HtmlParsingUtil.parseAttributeString(HtmlParsingUtil.getAttributeString(elements.get(0)));
        tagName = HtmlParsingUtil.isStartTag(elements.get(0)).get();
        text ="";
        for (int i = 0 ; i < elements.size() ; i++)
            text += elements.get(i);
        elements.remove(0);
        elements.remove(elements.size() - 1);
        Stack<String> stack = new Stack<>();
        List<String> temp = new ArrayList<>();

        for (int i = 0 ; i < elements.size() ; i++)
        {
            String element = elements.get(i);
            try {
                HtmlNode child = HtmlNode.createVoidHtmlNode(element);
                if (stack.empty())
                    children.add(HtmlNode.createVoidHtmlNode(element));
                else
                    temp.add(element);
            }catch (HtmlParsingException e){
                try {
                    HtmlParsingUtil.isStartTag(element);
                }
                catch (HtmlParsingException e2) {
                    try {
                        HtmlParsingUtil.isEndTag(element);
                    }catch (HtmlParsingException ex3){
                        if (stack.empty())
                            children.add(HtmlNode.createTextHtmlNode(element));
                        else
                            temp.add(element);
                        continue;
                    }
                    if (stack.empty())
                        children.add(HtmlNode.createTextHtmlNode(element));
                    else
                        temp.add(element);
                    continue;
                }

                    if (!HtmlParsingUtil.isStartTag(element).equals(Optional.empty())) {
                        stack.push(HtmlParsingUtil.isStartTag(element).get());
                        temp.add(element);
                    } else if (!HtmlParsingUtil.isEndTag(element).equals(Optional.empty())) {
                        if (stack.empty() || !stack.peek().equals(HtmlParsingUtil.isEndTag(element).get()))
                            throw new HtmlParsingException();
                        stack.pop();
                        temp.add(element);
                        if (stack.empty()) {
                            children.add(HtmlNode.createStandardHtmlNode(temp));
                            temp = new ArrayList<>();
                        }
                    } else if (stack.empty())
                        children.add(HtmlNode.createTextHtmlNode(element));
                    else {
                        temp.add(element);
                    }
                }

        }
        if (stack.empty() && temp.isEmpty())
            return;
        if (stack.empty() && temp.size() == 1)
            children.add(HtmlNode.createVoidHtmlNode(temp.get(0)));
        else
            throw new HtmlParsingException();

    }

    @Override
    public String getTagName() {
        return tagName;
    }

    @Override
    public List<HtmlNode> getChildren() {
        return children;
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
        String res ="";
        for (HtmlNode child : children){
            res += child.getText();
        }
        return res;
    }

    @Override
    public List<HtmlNode> getNodesByAttribute(String key, String value) {
        List<HtmlNode> res = new ArrayList<HtmlNode>();
        String v = getAttribute(key);
        if (v != null && v.equals(value))
            res.add(this);

        for (HtmlNode node : children){
            List<HtmlNode> temp = node.getNodesByAttribute(key, value);
            res.addAll(temp);
        }
        return res;
    }

    @Override
    public void getNodesByAttribute(String key, String value, List<HtmlNode> previous) {
        if (getAttribute(key) == value)
            previous.add(this);

        for (HtmlNode node : children){
            node.getNodesByAttribute(key, value,previous);
        }
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
        return getText();
    }
}
