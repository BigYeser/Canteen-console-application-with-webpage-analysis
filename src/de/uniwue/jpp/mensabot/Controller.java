package de.uniwue.jpp.mensabot;

import de.uniwue.jpp.errorhandling.OptionalWithMessage;
import de.uniwue.jpp.errorhandling.OptionalWithMessageMsg;
import de.uniwue.jpp.mensabot.dataclasses.Menu;
import de.uniwue.jpp.mensabot.retrieval.Fetcher;
import de.uniwue.jpp.mensabot.retrieval.Parser;
import de.uniwue.jpp.mensabot.retrieval.Saver;
import de.uniwue.jpp.mensabot.sending.Importer;
import de.uniwue.jpp.mensabot.sending.Sender;
import de.uniwue.jpp.mensabot.sending.formatting.Formatter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;


public interface Controller {

    Optional<String> retrieveData();
    Optional<String> send(Formatter formatter);

    static Controller create(Fetcher f, Parser p, Saver sav, Path logfile, Importer i, Sender s) {
        if (f == null)
            throw new NullPointerException("Fetcher can't be null");

        if(p == null)
            throw new NullPointerException("Parser can't be null");

        if(sav == null)
            throw new NullPointerException("Saver can't be null");

        if(logfile == null)
            throw new NullPointerException("Path can't be null");

        if(i == null)
            throw new NullPointerException("Importer can't be null");

        if(s == null)
            throw new NullPointerException("Sender can't be null");

       return  new Controller() {

            @Override
            public Optional<String> retrieveData() {
                OptionalWithMessage<String> fetcher = f.fetchCurrentData();
                if(fetcher instanceof OptionalWithMessageMsg)
                    return Optional.of(fetcher.get());
                OptionalWithMessage<Menu> parser = p.parse(fetcher.get());
                if(parser instanceof  OptionalWithMessageMsg)
                    return Optional.of(parser.getMessage());

                Optional<String> saver = sav.log(logfile,parser.get());
                if(!(saver.equals(Optional.empty())))
                    return Optional.of(saver.get());

                return Optional.empty();
            }

            @Override
            public Optional<String> send(Formatter formatter) {
                try {
                    BufferedReader bufferedReader1 = new BufferedReader(new FileReader(logfile.toString()));
                    BufferedReader bufferedReader2 = new BufferedReader(new FileReader(logfile.toString()));
                    OptionalWithMessage<List<Menu>> importer = i.getAll(bufferedReader1);
                    if(importer instanceof OptionalWithMessageMsg)
                        return Optional.of(importer.getMessage());

                    OptionalWithMessage<String> formated = formatter.format(i.getLatest(bufferedReader2).get(), () -> importer);
                    if (formated instanceof OptionalWithMessageMsg)
                        return Optional.of(formated.getMessage());

                    Optional<String> sender = s.send(formated.get());
                    return sender;

                } catch (FileNotFoundException e) {
                   return Optional.of(e.getMessage());
                }
            }
        };

    }

    static void executeDummyPipeline() {
        create(Fetcher.createDummyCsvFetcher(),Parser.createCsvParser(),Saver.createCsvSaver(),Path.of("dummylog.csv"),Importer.createCsvImporter(),Sender.createDummySender());
    }
}
