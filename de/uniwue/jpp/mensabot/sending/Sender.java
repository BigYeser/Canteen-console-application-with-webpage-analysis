package de.uniwue.jpp.mensabot.sending;

import de.uniwue.jpp.errorhandling.OptionalWithMessage;
import de.uniwue.jpp.errorhandling.OptionalWithMessageVal;
import de.uniwue.jpp.mensabot.util.JsonUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface Sender {

    Optional<String> send(String msg);

    static Sender createDummySender() {
        return new Sender() {
            @Override
            public Optional<String> send(String msg) {
                return Optional.empty();
            }
        };
    }

    static Sender createTelegramSender(HttpClient client, String apiToken, List<String> chatIds) {
        if (client == null || apiToken == null || chatIds == null)
            throw new IllegalArgumentException("A argument was null!");
        URI uri;
        try {
            String url = "https://api.telegram.org/bot"+apiToken+"/sendMessage";
            uri = new URL(url).toURI();

        } catch (Exception e){
            throw new IllegalArgumentException("Invalid url!");
        }
        return new Sender() {
            @Override
            public Optional<String> send(String msg) {
                boolean ok = true;
                List<String> errs = new ArrayList<>();
                for (String chatId : chatIds) {
                    try {
                        HttpResponse response = null;
                        MessageTelegram messageTelegram = new MessageTelegram(chatId,msg);
                        String json = JsonUtil.objectToString(messageTelegram).get();
                        response = client.send(
                                HttpRequest.newBuilder()
                                        .uri(uri)
                                        .header("Content-Type", "application/json")
                                        .POST(HttpRequest.BodyPublishers.ofString(json))
                                        .build(),
                                HttpResponse.
                                        BodyHandlers
                                        .ofString()
                        );

                        OptionalWithMessage<ErrorSendingTelegram> errorSendingTelegram = JsonUtil.stringToObject(response.body()+"",ErrorSendingTelegram.class);
                        if(errorSendingTelegram instanceof OptionalWithMessageVal &&  !errorSendingTelegram.get().isOk()) {
                            ok = false;
                            errs.add("ID: '" + chatId + "' Response from API: '" + errorSendingTelegram.get().getDescription() + "'");
                        }
                    } catch (IOException e) {
                        return Optional.of("Error during execution of request!");
                    }catch (InterruptedException e){
                        return Optional.of("Error during execution of request!");
                    }

                }
                if(ok)
                    return Optional.empty();
                String res = "Error code received when sending messages to these chats: ";
                for (String err : errs){
                    res += System.lineSeparator() + err ;
                }
                return Optional.of(res);
            }
        };
    }
}
