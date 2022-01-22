package de.uniwue.jpp.mensabot.retrieval;

import de.uniwue.jpp.errorhandling.OptionalWithMessage;
import de.uniwue.jpp.mensabot.retrieval.implementation.FetcherImp;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface Fetcher {

    OptionalWithMessage<String> fetchCurrentData();

    static Fetcher createDummyCsvFetcher() {

        return new FetcherImp("2021-08-06;Mensa-Vital: Ofengemüse mit Basilikum-Dip und Süßkartoffelpüree_430;Schneller Teller: Seelachsfilet \"Florentine\" an Rahmspinat und Salzkartoffeln_290;Mensa-Vital: Asiatische Gemüsepfanne mit Falafelbällchen und Glasnudeln_280;Spaghetti \"Carbonara\"_230;Sommerlicher Salatteller mit einem Süßkartoffel - Amaranth - Bratling und Tomatensalsa_330;Pfannkuchen mit Apfelmus_185\n");
    }

    static Fetcher createHttpFetcher(HttpClient client, String url) {
        return new Fetcher() {
            @Override
            public OptionalWithMessage<String> fetchCurrentData() {
                if (client == null || url == null)
                    throw new IllegalArgumentException("A argument was null!");
                URI uri;
                try {
                    uri = new URL(url).toURI();

                } catch (Exception e){
                    throw new IllegalArgumentException("Invalid url!");
                }
                HttpResponse response = null;
                try {
                     response =  client.send(
                            HttpRequest.newBuilder()
                                    .uri(uri)
                                    .GET()
                                    .build(),
                            HttpResponse.
                                    BodyHandlers
                                    .ofString()
                    );
                } catch (IOException e) {
                    return OptionalWithMessage.of("Error during execution of request!");
                } catch (InterruptedException e) {
                    return OptionalWithMessage.of("Error during execution of request!");
                }
                int status = response.statusCode();
                if(status / 100 == 2)
                     return OptionalWithMessage.of(response.body()+"");
                return OptionalWithMessage.of("Error codereturned: " + status);
            }
        };

    }
}
