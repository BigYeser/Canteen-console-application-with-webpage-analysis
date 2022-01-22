package de.uniwue.jpp.mensabot.retrieval.implementation;

import de.uniwue.jpp.errorhandling.OptionalWithMessage;
import de.uniwue.jpp.mensabot.retrieval.Fetcher;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FetcherImp implements Fetcher {
    private String data;

    public FetcherImp() {
        try {
            data = "";
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("mensalog.csv");
            InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader);
            for (String line; (line = reader.readLine()) != null; ) {
                data += line + System.lineSeparator();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public OptionalWithMessage<String> fetchCurrentData() {
        try {

            Reader path = new StringReader(data);
            String Line = "";
            BufferedReader br = new BufferedReader(path);

            while ((Line = br.readLine()) != null) {
                String[] values = Line.split(";");
                if (values[0].equals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {
                    return OptionalWithMessage.of(Line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return OptionalWithMessage.ofMsg("Data ist leer");

    }


}
