package org.bju;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ErrorReporter {
    public enum ErrorType {
        LEXER,
        PARSER,
        SEMANTIC
    }

    private int lexerErrors = 0;
    private int parseErrors = 0;
    private int semanticErrors = 0;
    final private List<Pair<String, Integer>> files = new ArrayList<>();

    private static ErrorReporter errorReporter = null;
    public static ErrorReporter get() {
        if (errorReporter == null) {
            errorReporter = new ErrorReporter();
        }
        return errorReporter;
    }

    public void addLexerError() {
        this.lexerErrors++;
    }

    public void addParseError() {
        this.parseErrors++;
    }

    public void addSemanticErrors() {
        this.semanticErrors++;
    }

    public boolean hasErrors() {
        return lexerErrors > 0 || parseErrors > 0 || semanticErrors > 0;
    }

    public void addFile(String filename, Integer lineCount) {
        this.files.add(new Pair<>(filename, lineCount));
    }

    public void reportError(Integer lineNumber, String message, ErrorType type) {
        switch (type) {
            case LEXER -> addLexerError();
            case PARSER -> addParseError();
            case SEMANTIC -> addSemanticErrors();
        }
        print(lineNumber, message);
    }

    public void print(Integer lineNumber, String message) {
        if(lineNumber != null) {
            for (Pair<String, Integer> pair : files) {
                if (lineNumber <= pair.getValue()) {
                    System.out.println(pair.getKey() + ":" + lineNumber + ":" + message);
                } else {
                    lineNumber -= pair.getValue();
                }
            }
        } else {
            System.out.println("unknown:unknown:" + message);
        }
    }
}
