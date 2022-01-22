package de.uniwue.jpp.mensabot.retrieval.htmlparsing;

import de.uniwue.jpp.mensabot.util.html.CharacterReference;

import java.util.*;

public class HtmlParsingUtil {

    public static Optional<String> isStartTag(String tag) throws HtmlParsingException{
        if(tag.trim().equals("<!--"))
            return Optional.of("comment");
        if (tag.length() <= 2)
            throw new HtmlParsingException();
        if(!tag.contains("<") && !tag.contains(">"))
            return Optional.empty();
        if(tag.charAt(0) != '<' || tag.charAt(tag.length() - 1) != '>' || tag.substring(1,tag.length()-1).isBlank())
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
        startTag = startTag.trim();
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
        StringBuilder standardAttFormat = new StringBuilder();
        Stack<String> openedQuotes = new Stack<>();
        List<String> tempAttValue = new ArrayList<>();
        String temp = "";
        for(int i = 0 ; i < attributeString.length() ; i++) {
            temp += attributeString.charAt(i);
            if(attributeString.charAt(i) == '\"' || attributeString.charAt(i) == '\'') {
                if (!openedQuotes.empty()) {
                    if (openedQuotes.peek().equals(attributeString.charAt(i) + "")) {
                        openedQuotes.pop();
                        if(openedQuotes.empty()) {
                            tempAttValue.add(temp);
                            temp = "";
                        }
                    }
                }
                else {
                    openedQuotes.push(attributeString.charAt(i) + "");
                }

            }
            else if(attributeString.charAt(i) == '=' && openedQuotes.empty()){
                if(temp.length() > 1)
                    tempAttValue.add(temp.substring(0,temp.length() - 1));
                tempAttValue.add("=");
                    temp = "";
                }
            else if(attributeString.charAt(i) == ' ' && openedQuotes.empty()){
                if(temp.length() > 1)
                    tempAttValue.add(temp.substring(0,temp.length() - 1));
                temp ="";
            }

        }
        if (!temp.isEmpty())
            tempAttValue.add(temp);


       // String[] attValue = standardAttFormat.toString().split("\\s+");
        List<String> attValue = new ArrayList<>();
        for (int i = 0 ;  i < tempAttValue.size() ; i++){
            attValue.add(tempAttValue.get(i).trim());
        }
        if (attValue.size() == 1 && attValue.get(0).length() == 0)
            return res;
        List<Integer> idx = new ArrayList<>();

        for(int i = 0 ; i < attValue.size() ; i++){
            if(attValue.get(i).equals("="))
                idx.add(i);
        }
        int cnt = 0;
        for(int i = 0 ; i < attValue.size(); i++) {
            try {
                if (cnt >= idx.size()) {
                    String key = attValue.get(i);
                    String value = null;
                    if (!isValidAttName(key))
                        throw new HtmlParsingException();
                    res.put(key, value);
                } else if (i == idx.get(cnt)) {
                    cnt++;
                    String key = attValue.get(i - 1);
                    if (!isValidAttName(key))
                        throw new HtmlParsingException();
                    StringBuilder value = new StringBuilder(attValue.get(i + 1));
                    if (value.charAt(0) != '"' && value.charAt(0) != '\'') {
                        if (!isValidValue(value.toString()))
                            throw new HtmlParsingException();
                        res.put(key, value.toString());

                    } else {
                        if (value.charAt(0) == '"') {
                            i++;
                            while (value.charAt(value.length() - 1) != '"') {
                                value.append(" ").append(attValue.get(++i));
                            }
                            if (!isValidValueNormalQuotes(value.toString()))
                                throw new HtmlParsingException();
                            res.put(key, value.substring(1,value.length() - 1));
                            i--;

                        } else {
                            i++;
                            while (value.charAt(value.length() - 1) != '\'') {
                                value.append(" ").append(attValue.get(++i));
                            }
                            if (!isValidValueSingleQuotes(value.toString()))
                                throw new HtmlParsingException();
                            res.put(key, value.substring(1,value.length() - 1));
                            i--;
                        }
                    }
                    i++;
                } else {
                    if (i != idx.get(cnt) - 1) {
                        if (!isValidAttName(attValue.get(i)))
                            throw new HtmlParsingException();
                        String key = attValue.get(i);
                        String value = null;
                        res.put(key, value);
                    }

                }
            } catch (Exception e) {
                throw new HtmlParsingException();
            }
        }
        res.remove("");
        return res;
    }

    public static Map<String, String> parseAttributesFromStartTag(String startTag) throws HtmlParsingException{
        startTag = startTag.trim();
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
        System.out.println("Start: ");
        for (String element : elements)
            System.out.println(element);
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
        System.out.println("Middle: ");
        for (String element : elements)
            System.out.println(element);
        Stack<String> stack = new Stack<>();
        for(int i = 0 ; i < elements.size() ; i++){
            String element = elements.get(i);

            if(stack.empty() && (element.trim().equals(startTag.trim()) || (isvalidTag(element) && !isStartTag(element).equals(Optional.empty()) && isStartTag(element).get().equals(isStartTag(startTag).get())))){
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


        System.out.println("ُEnd: ");
        for (String element : elements)
            System.out.println(element);

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

    public static String replaceCharacterReferencesInText(String text) {
        Map<String, CharacterReference> characterReferenceMap = CharacterReference.loadCharacterReferences();
        for (Map.Entry<String, CharacterReference> characterReferenceEntry : characterReferenceMap.entrySet()) {
            String value = characterReferenceEntry.getValue().getCharacters();
            String keyAsCharacters = characterReferenceEntry.getKey();
            try {
                if (text.contains(keyAsCharacters)) {
                    String check = text.replace(keyAsCharacters, value);
                    if(check.endsWith(";"))
                        check = text.replace(keyAsCharacters +";", value);
                    else
                        check = text.replace(keyAsCharacters , value);
                    if (check.contains(";"))
                        text = check.replace(";","");
                    else text= check;
                }

            }catch (NullPointerException e)
            {
                return text;
            }


            List<Integer> codepoints = characterReferenceEntry.getValue().getCodepoints();
            for (Integer codepoint : codepoints) {
                String keyAsInteger = "&#" + codepoint + ";";
                if (text.contains(keyAsInteger)) {
                    text = text.replace(keyAsInteger, value);
                }
                String keyAsHex = "&#x" + Integer.toHexString(codepoint) + ";";
                if (text.contains(keyAsHex)) {
                    text = text.replace(keyAsHex, value);
                }
                keyAsHex = "&#X" + Integer.toHexString(codepoint) + ";";

                if (text.contains(keyAsHex)) {
                    text = text.replace(keyAsHex, value);
                }
            }
        }

        return text;

        //return null;
    }
    private static boolean isValidAttName(String key){
        key = key.trim();
        String[] symbols = {"\"", "'",">", "/", "=", " "};
        for(int i = 0 ; i < symbols.length ; i++)
            if(key.contains(symbols[i]))
                return false;
        return true;
    }

    private static boolean isValidValue(String value){
        value = value.trim();
        String[] symbols = {"\"", "'",">", "<", "`", " "};
        for(int i = 0 ; i < symbols.length ; i++)
            if(value.contains(symbols[i]))
                return false;
        return true;
    }

    private static boolean isValidValueSingleQuotes(String value){
        value = value.trim();

        return !value.substring(1,value.length() - 1).contains("'");
    }

    private static boolean isValidValueNormalQuotes(String value){
        value = value.trim();

        return !value.substring(1,value.length() - 1).contains("\"");
    }
    public static boolean isvalidTag(String tag){
        tag = tag.trim();
        if (tag.equals("<>"))
            return false;
        boolean opend = false;
        try {
            isStartTag(tag);
        } catch (HtmlParsingException e) {
            return false;
        }
        for(int i = 0 ; i < tag.length(); i++){
            if(tag.charAt(i) == '<')
                opend = true;
            if(opend && tag.charAt(i) == '>')
                return true;
        }
        return false;
    }

    public static void main(String[] args) throws HtmlParsingException {

        System.out.println(HtmlParsingUtil.splitInElements("<ul key1='nicerValue'  >"));
        List<String> s = new ArrayList<>();
        s.add("<ul key1='nicerValue'  >");
        HtmlParsingUtil.trimElements(s);
        // System.out.println(s.toString());
        System.out.println(HtmlParsingUtil.parseAttributeString("key1=nicerValue"));

    }
}
