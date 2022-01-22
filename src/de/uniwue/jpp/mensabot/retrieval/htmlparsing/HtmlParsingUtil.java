package de.uniwue.jpp.mensabot.retrieval.htmlparsing;

import java.util.*;

public class HtmlParsingUtil {

    public static Optional<String> isStartTag(String tag) throws HtmlParsingException{
        if(tag.trim().equals("<!--"))
            return Optional.of("comment");
        if (tag.length() <= 2)
            throw new HtmlParsingException();
        if(!tag.contains("<") && !tag.contains(">"))
            return Optional.empty();
        if(tag.charAt(0) != '<' || tag.charAt(tag.length() - 1) != '>' ||tag.substring(1,tag.length()-1).isBlank())
            throw new HtmlParsingException();
        String tagName = tag.substring(1,tag.length() - 1).trim().split("\\s+")[0];

        if (tagName.endsWith("/"))
            tagName = tagName.substring(0, tagName.length() - 1);
        else if (tagName.charAt(0) == '/')
            return Optional.empty();

        if (tagName.contains("/") || Optional.of(tagName.split("/")[0]).equals(Optional.of("")))
            throw new HtmlParsingException();

        return Optional.of(tagName.split("/")[0]);
    }

    public static Optional<String> isEndTag(String tag) throws HtmlParsingException{
        if (tag.trim().equals("-->"))
            return Optional.of("comment");
        if(!tag.contains("<") && !tag.contains(">"))
            return Optional.empty();
        if(tag.charAt(0) != '<' || tag.charAt(tag.length() - 1) != '>')
            throw new HtmlParsingException();
        String tagName = tag.substring(1,tag.length() - 1).trim().split("\\s+")[0];
        if(tagName.charAt(0) == '/' && tagName.length() == 1)
            throw new HtmlParsingException();
        if (tagName.charAt(0) != '/')
            return Optional.empty();

        return Optional.of(tagName.substring(1,tagName.length()));

    }

    public static boolean isVoidElement(String tagName){
        String[] names = {"area",
                "base",
                "br" ,
                "col" ,
                "command" ,
                "embed",
                "hr",
                "img",
                "input",
                "keygen",
                "link",
                "meta",
                "param",
                "source",
                "track",
                "wbr"
        };
        for (int i = 0 ; i < names.length ; i++)
            if (tagName.equals(names[i]))
                return true;
        return false;
    }

    public static String getAttributeString(String startTag){
       String innerTag =  startTag.substring(1, startTag.length() - 1);
        try {
            innerTag = innerTag.replaceFirst(isStartTag(startTag).get(),"");
        } catch (HtmlParsingException e) {
            e.printStackTrace();
        }
        int i;
       for( i = 0 ; i < innerTag.length() ; i++) {
            if(innerTag.charAt(i) != ' ')
                break;
       }
       innerTag = innerTag.substring(i, innerTag.length());
        for( i = innerTag.length() - 1 ; i >= 0 ; i--) {
            if(innerTag.charAt(i) != ' ' && innerTag.charAt(i) !='/')
                break;
        }
        innerTag = innerTag.substring(0,i + 1);
       return innerTag;
    }

    public static Map<String, String> parseAttributeString(String attributeString) throws HtmlParsingException{
        Map<String ,String> res = new HashMap<String,String>();

        String standardAttFormat = "";
        for(int i = 0 ; i < attributeString.length() ; i++)
            if(attributeString.charAt(i) == '=')
                standardAttFormat += " = ";
            else
                standardAttFormat += attributeString.charAt(i);

        String[] attValue = standardAttFormat.split("\\s+");
        if (attValue.length == 1 && attValue[0].length() == 0)
            return res;
        List<Integer> idx = new ArrayList<>();
        for(int i = 0 ; i < attValue.length ; i++){
            if(attValue[i].equals("="))
                idx.add(i);
        }
        int cnt = 0;
        for(int i = 0 ; i < attValue.length; i++) {
            try {
                if (cnt >= idx.size()) {
                    String key = attValue[i];
                    String value = null;
                    if (!isValidAttName(key))
                        throw new HtmlParsingException();
                    res.put(key, value);
                } else if (i == idx.get(cnt)) {
                    cnt++;
                    String key = attValue[i - 1];
                    if (!isValidAttName(key))
                        throw new HtmlParsingException();
                    String value = attValue[i + 1];
                    if (value.charAt(0) != '"' && value.charAt(0) != '\'') {
                        if (!isValidValue(value))
                            throw new HtmlParsingException();
                        res.put(key, value);

                    } else {
                        if (value.charAt(0) == '"') {
                            i++;
                            while (value.charAt(value.length() - 1) != '"') {
                                value += " " + attValue[++i];
                            }
                            if (!isValidValueNormalQuotes(value))
                                throw new HtmlParsingException();
                            res.put(key, value.substring(1,value.length() - 1));

                        } else {
                            i++;
                            while (value.charAt(value.length() - 1) != '\'') {
                                value += " " + attValue[++i];
                            }
                            if (!isValidValueSingleQuotes(value))
                                throw new HtmlParsingException();
                            res.put(key, value.substring(1,value.length() - 1));
                        }
                    }
                    i++;
                } else {
                    if (i != idx.get(cnt) - 1) {
                        if (!isValidAttName(attValue[i]))
                            throw new HtmlParsingException();
                        String key = attValue[i];
                        String value = null;
                        res.put(key, value);
                    }

                }
            } catch (Exception e) {
                throw new HtmlParsingException();
            }
        }
        return res;
    }

    public static Map<String, String> parseAttributesFromStartTag(String startTag) throws HtmlParsingException{
        String attributeString = getAttributeString(startTag);
        return parseAttributeString(attributeString);
    }

    public static List<String> splitInElements(String text){
        List<String> res = new ArrayList<>();
        boolean openQuotes = false;
        String temp = "";
        for(int i = 0 ; i < text.length() ; i++) {
            if(openQuotes && text.charAt(i) != '\"') {
                temp += text.charAt(i);
                continue;
            }
            if(openQuotes && text.charAt(i) == '\"')
            {
                openQuotes = false;
                temp += text.charAt(i);
                continue;
            }
            if(!openQuotes && text.charAt(i) == '\"')
            {
                openQuotes = true;
                temp += text.charAt(i);
                continue;
            }
            if(text.charAt(i) == '<')
            {
                if(temp.length() > 0)
                  res.add(temp);
                temp = "";
                temp += text.charAt(i);
                continue;
            }
            if(text.charAt(i) == '>')
            {
                temp += text.charAt(i);
                res.add(temp);
                temp = "";
                continue;
            }
            temp += text.charAt(i);
        }
        if(temp.length() > 0)
            res.add(temp);
        return res;
    }

    public static void cleanElements(String startTag, String endTag, List<String> elements) throws HtmlParsingException{

        if (startTag.equals("<!--") && endTag.equals("-->"))
        {
          for (int i = 0 ; i < elements.size() ; i++)
          {
              String element = elements.get(i);
              if (element.contains(startTag) && element.contains(endTag))
              {
                  elements.remove(i--);
              }
          }
        }
        Stack<String> stack = new Stack<>();
        for(int i = 0 ; i < elements.size() ; i++){
            String element = elements.get(i);
            if(stack.empty() && element.trim().equals(startTag.trim())){
                stack.push(element);
                elements.remove(i--);
            }
            else if(!stack.empty() && element.trim().equals(endTag.trim())){
                elements.remove(i--);
                stack.pop();
            }else if (stack.empty() && element.trim().equals(endTag.trim()))
                throw new HtmlParsingException();
            else if (!stack.empty())
                elements.remove(i--);
        }
        if (!stack.empty())
            throw new HtmlParsingException();

    }

    public static void trimElements(List<String> elements) throws HtmlParsingException{
        for(int i = 0 ; i < elements.size(); i++){
            String element = elements.get(i);
            if (element.equals("<!DOCTYPE html>"))
                break;
            try {
                if (isStartTag(element).equals(Optional.empty()) || (!isStartTag(element).equals(Optional.empty()) && isVoidElement(element.substring(1,element.length() - 1))) || (!isStartTag(element).equals(Optional.empty()) && isVoidElement(element.substring(1,element.length() - 2)))) {
                    elements.remove(i--);
                }
                else
                    break;
            }catch (Exception e){
                elements.remove(i--);
            }

        }
        for(int i = elements.size() - 1; i >= 0; i--){
            String element = elements.get(i);
            try {
                if (isEndTag(element).equals(Optional.empty())) {
                    elements.remove(i);
                }
                else
                    break;
            }catch (Exception e){
                elements.remove(i);
            }
        }
    }

    public static String replaceCharacterReferencesInText(String text){
        throw new UnsupportedOperationException();
    }

    private static boolean isValidAttName(String key){
        String[] symbols = {"\"", "'",">", "/", "=", " "};
        for(int i = 0 ; i < symbols.length ; i++)
            if(key.contains(symbols[i]))
                return false;
        return true;
    }

    private static boolean isValidValue(String value){
        String[] symbols = {"\"", "'",">", "<", "`", " "};
        for(int i = 0 ; i < symbols.length ; i++)
            if(value.contains(symbols[i]))
                return false;
        return true;
    }

    private static boolean isValidValueSingleQuotes(String value){
        return !value.substring(1,value.length() - 1).contains("'");
    }

    private static boolean isValidValueNormalQuotes(String value){
        return !value.substring(1,value.length() - 1).contains("\"");
    }
    public static boolean isvalidTag(String tag){
        boolean opend = false;
        for(int i = 0 ; i < tag.length(); i++){
            if(tag.charAt(i) == '<')
                opend = true;
            if(opend && tag.charAt(i) == '>')
                return true;
        }
        return false;
    }
}
