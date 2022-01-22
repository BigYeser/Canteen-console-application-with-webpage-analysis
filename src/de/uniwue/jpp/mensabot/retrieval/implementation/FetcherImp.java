package de.uniwue.jpp.mensabot.retrieval.implementation;

import de.uniwue.jpp.errorhandling.OptionalWithMessage;
import de.uniwue.jpp.mensabot.retrieval.Fetcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FetcherImp implements Fetcher {
    private String data;

    public FetcherImp(String data) {
        this.data = data;
    }

    public FetcherImp() {
        try {
            String path = "D:/mahmodGermany2/Auf2/Mensabot/resources/mensalog.csv";
            BufferedReader csvReader = new BufferedReader(new FileReader(path));
            String row;
            while ((row = csvReader.readLine()) != null) {
                data += row + System.lineSeparator();
            }
            csvReader.close();
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    @Override
    public OptionalWithMessage<String> fetchCurrentData() {
        try {
            String[] rows = data.split(System.lineSeparator());
            for(String row : rows) {
               String[] items = row.split(";");
               if(items[0].equals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {
                   return OptionalWithMessage.of(row);
               }
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return OptionalWithMessage.of("Menu is empty");
    }
}
