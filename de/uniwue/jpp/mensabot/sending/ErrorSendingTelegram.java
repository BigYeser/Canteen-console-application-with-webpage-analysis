package de.uniwue.jpp.mensabot.sending;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorSendingTelegram {
    private boolean ok;
    private int error_code;
    private String description;



    public ErrorSendingTelegram() {

    }

    public boolean isOk() {
        return ok;
    }

    public int getError_code() {
        return error_code;
    }

    public String getDescription() {
        return description;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
