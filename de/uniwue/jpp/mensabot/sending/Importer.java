package de.uniwue.jpp.mensabot.sending;

import de.uniwue.jpp.errorhandling.OptionalWithMessage;
import de.uniwue.jpp.mensabot.dataclasses.Menu;
import de.uniwue.jpp.mensabot.retrieval.Parser;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public interface Importer {

    OptionalWithMessage<Menu> getLatest(BufferedReader fileReader);
    OptionalWithMessage<List<Menu>> getAll(BufferedReader fileReader);

    static Importer createCsvImporter() {
        return new Importer() {
            @Override
            public OptionalWithMessage<Menu> getLatest(BufferedReader fileReader) {
                OptionalWithMessage<Menu> res = null;
                try {
                    res = Parser.createCsvParser().parse(fileReader.readLine());
                }catch (Exception e) {
                    System.out.println(e);
                }
                return res;
            }

            @Override
            public OptionalWithMessage<List<Menu>> getAll(BufferedReader fileReader) {
                OptionalWithMessage<List<Menu>> res = OptionalWithMessage.of(new ArrayList<>());
                try {
                    String line;
                    while ((line = fileReader.readLine()) != null){
                        res.get().add(Parser.createCsvParser().parse(fileReader.readLine()).get());
                    }
                }catch (Exception e) {
                    return OptionalWithMessage.ofMsg(e.getMessage());
                }
                return res;
            }
        };
    }
}
