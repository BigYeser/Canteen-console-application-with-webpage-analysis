package de.uniwue.jpp.mensabot.retrieval;

import de.uniwue.jpp.mensabot.dataclasses.Menu;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface Saver {

    Optional<String> log(Path path, Menu newMenu);

    static Saver createCsvSaver() {
        return new Saver() {
            @Override
            public Optional<String> log(Path path, Menu newMenu) {
                List<String> data = new ArrayList<String>();
                String firstLine = newMenu.toString();
                data.add(firstLine);
                try {

                    BufferedReader csvReader = new BufferedReader(new FileReader(path.toString()));
                    String row;
                    while ((row = csvReader.readLine()) != null) {
                        data.add(row);
                    }
                    csvReader.close();

                    BufferedWriter writer = new BufferedWriter(new FileWriter(path.toString()));
                    for (String line : data){
                        writer.write(line +"\n");
                    }
                    writer.close();

                } catch (Exception e) {
                   return Optional.of(e.getMessage());
                }
                return Optional.empty();
            }
        };
    }
}
