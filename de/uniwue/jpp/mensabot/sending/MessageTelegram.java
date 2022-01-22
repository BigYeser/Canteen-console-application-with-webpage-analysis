package de.uniwue.jpp.mensabot.sending;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageTelegram {
    private String chat_id;
    private String text;



    public MessageTelegram(String chat_id, String text) {
        this.chat_id = chat_id;
        this.text = text;
    }

    public MessageTelegram() {
    }

    public String getChat_id() {
        return chat_id;
    }

    public String getText() {
        return text;
    }
    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public void setText(String text) {
        this.text = text;
    }
}
