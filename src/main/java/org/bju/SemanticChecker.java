package org.bju;

import org.antlr.AIMBaseVisitor;
import org.antlr.AIMParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SemanticChecker extends AIMBaseVisitor<Object> {
    public enum Type {
        INT,
        STR,
        ERR;
    }

    private SymbolTable symbolTable = new SymbolTable();

    @Override
    protected Object aggregateResult(Object aggregate, Object nextResult) {
        if(aggregate == null) {
            return nextResult;
        } else {
            if(aggregate instanceof List) {
                if(nextResult != null) {
                    ((List<Object>) aggregate).add(nextResult);
                }
                return aggregate;
            } else {
                List<Object> objects = new ArrayList<Object>();
                objects.add(aggregate);
                objects.add(nextResult);
                objects = objects.stream().filter(x -> x != null).collect(Collectors.toList());
                if(objects.size() == 1) {
                    return objects.get(0);
                } else {
                    return objects;
                }
            }
        }
    }

    @Override
    public Object visitAssignment(AIMParser.AssignmentContext ctx) {
        Decl.VarDecl decl = new Decl.VarDecl();
        decl.setName(ctx.getText());
        decl.setLevel(symbolTable.getCurrentLevel());
        decl.setType((Type) visit(ctx.value));
        symbolTable.add(decl);
        return decl.getType();
    }

    @Override
    public Object visitCall(AIMParser.CallContext ctx) {
        return super.visitCall(ctx);
    }

    @Override
    public Object visitNeg(AIMParser.NegContext ctx) {
        Type first = (Type)visit(ctx.expression());

        if (first != Type.INT) {
            ErrorReporter.get().reportError(ctx.start.getLine(), "expression of negate must be of type int", ErrorReporter.ErrorType.SEMANTIC);
        }

        return Type.INT;
    }

    @Override
    public Object visitPs(AIMParser.PsContext ctx) {
        Type first = (Type)visit(ctx.first);
        Type second = (Type)visit(ctx.second);

        String operation = ctx.op.getText().equals("+") ? "addition" : "subtraction";
        if (first != Type.INT) {
            ErrorReporter.get().reportError(ctx.start.getLine(), "first part of " + operation + " must be of type int", ErrorReporter.ErrorType.SEMANTIC);
        }
        if (second != Type.INT) {
            ErrorReporter.get().reportError(ctx.start.getLine(), "second part of " + operation + " must be of type int", ErrorReporter.ErrorType.SEMANTIC);
        }

        return Type.INT;
    }

    @Override
    public Object visitMd(AIMParser.MdContext ctx) {
        Type first = (Type)visit(ctx.first);
        Type second = (Type)visit(ctx.second);

        if (first != Type.INT) {
            ErrorReporter.get().reportError(ctx.start.getLine(), "first part of multiplication / division must be of type int", ErrorReporter.ErrorType.SEMANTIC);
        }
        if (second != Type.INT) {
            ErrorReporter.get().reportError(ctx.start.getLine(), "second part of multiplication / division must be of type int", ErrorReporter.ErrorType.SEMANTIC);
        }

        return Type.INT;
    }

    @Override
    public Object visitConcat(AIMParser.ConcatContext ctx) {
        Type first = (Type)visit(ctx.first);
        Type second = (Type)visit(ctx.second);

        // 4 plusplus 3 plusplus 65
        if (!(first == Type.INT || first == Type.STR)) {
            ErrorReporter.get().reportError(ctx.start.getLine(), "first part of plusplus must be of type int or string", ErrorReporter.ErrorType.SEMANTIC);
        }
        if (!(second == Type.INT || second == Type.STR)) {
            ErrorReporter.get().reportError(ctx.start.getLine(), "second part of plusplus must be of type int or string", ErrorReporter.ErrorType.SEMANTIC);
        }

        return Type.STR;
    }

    @Override
    public Object visitInt(AIMParser.IntContext ctx) {
        return Type.INT;
    }

    @Override
    public Object visitId(AIMParser.IdContext ctx) {
        var varLookup = symbolTable.lookup(ctx.getText(), Decl.VarDecl.class);
        var argLookup = symbolTable.lookup(ctx.getText(), Decl.ArgDecl.class);

        if(varLookup.isPresent() && argLookup.isEmpty()) {
            return varLookup.get().getType();
        } else if(argLookup.isPresent() && varLookup.isEmpty()) {
            return argLookup.get().getType();
        } else if(argLookup.isEmpty() && varLookup.isEmpty()) {
            ErrorReporter.get().reportError(ctx.start.getLine(), ctx.getText() + " is not defined", ErrorReporter.ErrorType.SEMANTIC);
            return Type.ERR;
        }
        if(varLookup.get().getLevel() < argLookup.get().getLevel()) {
            return argLookup.get().getType();
        } else {
            return varLookup.get().getType();
        }
    }

    @Override
    public Object visitWith_args(AIMParser.With_argsContext ctx) {
        this.symbolTable.push();
        Decl.MethDecl decl = new Decl.MethDecl();

        this.symbolTable.pop();
        return decl.getType();
    }

    @Override
    public Object visitNo_args(AIMParser.No_argsContext ctx) {
        return super.visitNo_args(ctx);
    }
}
