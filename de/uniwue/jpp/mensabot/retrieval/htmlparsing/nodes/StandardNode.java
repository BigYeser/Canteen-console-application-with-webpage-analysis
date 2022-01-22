package de.uniwue.jpp.mensabot.retrieval.htmlparsing.nodes;

import de.uniwue.jpp.mensabot.retrieval.htmlparsing.HtmlNode;
import de.uniwue.jpp.mensabot.retrieval.htmlparsing.HtmlParsingException;
import de.uniwue.jpp.mensabot.retrieval.htmlparsing.HtmlParsingUtil;

import java.util.*;

public class StandardNode implements HtmlNode {
    private List<String> elements = new ArrayList<>();
    private String tagName = "";
    private Map<String ,String> attributes = new HashMap<>();
    private List<HtmlNode> children = new ArrayList<>();
    private String text = "";

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
        StringBuilder res = new StringBuilder();
        for (HtmlNode child : children){
            res.append(child.getText());
        }
        return res.toString();
    }

    @Override
    public List<HtmlNode> getNodesByAttribute(String key, String value) {
        key = key.trim();
        value = value.trim();
        List<HtmlNode> res = new ArrayList<HtmlNode>();
        try {
            String v = getAttribute(key);
            if (v != null && v.equals(value))
                res.add(this);
        }catch (NullPointerException e)
        {
            e.getMessage();
        }
        for (HtmlNode node : children){
            if(node.getNodesByAttribute(key, value).size() != 0) {
                List<HtmlNode> temp = node.getNodesByAttribute(key, value);
                res.addAll(temp);
            }
        }
        return res;
    }

    @Override
    public void getNodesByAttribute(String key, String value, List<HtmlNode> previous) {
        key = key.trim();
        value = value.trim();
        try {
            if (getAttribute(key).equals(value))
                previous.add(this);
        }catch (NullPointerException e)
        {
            e.getMessage();
        }


        for (HtmlNode node : children){
            node.getNodesByAttribute(key, value,previous);
        }
    }

    @Override
    public boolean hasAttribute(String key) {
        key = key.trim();
        return attributes.containsKey(key);
    }

    @Override
    public String getAttribute(String key) {
        key = key.trim();
        return hasAttribute(key)? attributes.get(key) : null;
    }

    @Override
    public String toString() {
        return getText();
    }

    public static void main(String[] args) throws HtmlParsingException {
        String res ="";
        List<String> list = new ArrayList<>();
        list.add("<general jpp='viel spass'  >");
        list.add("<br class=\"<!DOCTYPE html><html><ul key1='nicerValue'  ><li>Item1</li><li>Item2</li><li>Item3</li></ul></html>\">");
        list.add("</general>");
       /* for (int i = 0 ; i < list.size() ; i++)
            res +=l*/
        // System.out.println(res);

        for(String elem : list)
            res +=elem;
        System.out.println(new StandardNode(list).toString());

    }
}