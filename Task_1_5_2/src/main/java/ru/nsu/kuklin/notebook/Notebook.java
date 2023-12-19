package ru.nsu.kuklin.notebook;

import java.util.*;
import java.util.stream.Collectors;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;

import java.nio.file.*;
import java.io.IOException;

/*
 * Notebook main class.
 */
@Command(name = "notebook", subcommands = { CommandLine.HelpCommand.class })
public class Notebook implements Runnable {
    @Spec CommandSpec spec;

    private record Note (String title, String content, LocalDateTime createdAt) {}

    @Command(name = "add", description = "Add new entry in the notebook")
    void add(@Parameters(index = "0") String name, @Parameters(index = "1") String content) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        if (name.indexOf('/') != -1) {
            System.out.println("Note titles cannot contain forward slashes");
            return;
        }

        String data = mapper.writeValueAsString(new Note(name, content, LocalDateTime.now()));
        Files.createDirectories(Paths.get("./notes"));

        Path filename = Paths.get("./notes/" + name + ".txt");
        if (Files.exists(filename)) {
            System.out.println("Note with the same title already exists");
            return;
        }
        Files.write(Paths.get("./notes/" + name + ".txt"), data.getBytes());
    }

    @Command(name = "rm", description = "Delete existing notebook entry")
    void rm(@Parameters(index = "0") String title) throws IOException {
        Path filename = Paths.get("./notes/" + title + ".txt");
        if (!Files.exists(filename)) {
            System.out.println("Note with this title does not exist");
            return;
        }
        Files.delete(filename);
    }

    @Command(name = "show", description = "Print existing entries")
    void show() throws IOException {
        Path root = Paths.get("./notes");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Files
            .list(root)
            .filter(path -> Files.isRegularFile(path))
            .map(path -> {
                try {
                    return Files.lines(path).collect(Collectors.joining("\n"));
                } catch (Exception e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .map(data -> {
                try {
                    return mapper.readValue(data, Note.class);
                } catch (Exception e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .forEach(path -> System.out.println(path));
    }

    @Override
    public void run() {
        throw new ParameterException(spec.commandLine(), "Specify a subcommand to run");
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new Notebook()).execute(args);
        System.exit(exitCode);
    }
}
