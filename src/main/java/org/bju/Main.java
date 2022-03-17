package org.bju;

import org.antlr.AIMLexer;
import org.antlr.AIMParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ParseException, IOException, URISyntaxException {
        List<String> files = parseCommandLineArgs(args);

        String contents = "";
        for(String file : files) {
            String fileContents = Files.readString(Paths.get(file));
            if(!fileContents.endsWith("\n")) {
                fileContents += "\n";
            }
            ErrorReporter.get().addFile(file, fileContents.split("\n").length);
            contents += fileContents;
        }

        CharStream input = CharStreams.fromStream(IOUtils.toInputStream(contents, Charset.defaultCharset()));
        AIMLexer lexer = new AIMLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        AIMParser parser = new AIMParser(tokens);
        AIMParser.StartContext tree = parser.start();
        if(ErrorReporter.get().getLexerErrors() == 0 && ErrorReporter.get().getParseErrors() == 0) {
            var symTab = (SymbolTable) (new SemanticChecker()).visit(tree);
            if (ErrorReporter.get().getSemanticErrors() == 0) {
                var assembly = (new CodeGen(symTab)).visit(tree);
                Files.writeString(Path.of("temp.s"), assembly);
                Runtime.getRuntime().exec("gcc temp.s -o " + files.get(0).replace(".aim", ".fire"));
            }
        }
    }

    private static List<String> parseCommandLineArgs(String[] args) throws ParseException {
        Options options = new Options();

        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine commandLine = commandLineParser.parse(options, args);

        // all non matched things get returned
        return commandLine.getArgList();
    }
}
