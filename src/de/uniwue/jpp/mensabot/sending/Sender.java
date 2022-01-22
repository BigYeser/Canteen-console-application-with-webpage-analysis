package de.uniwue.jpp.mensabot.sending;

import java.net.http.HttpClient;
import java.util.List;
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
        return new Sender() {
            @Override
            public Optional<String> send(String msg) {

                return null;
            }
        };
    }
}
