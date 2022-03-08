package org.bju;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class SymbolTable {
    private List<List<Decl>> symbolTable = new ArrayList<>();

    public SymbolTable() {
        this.symbolTable.add(new ArrayList<>());
    }

    public void add(Decl decl) {
        symbolTable.get(symbolTable.size() - 1).add(decl);
    }

    public Integer getCurrentLevel() {
        return symbolTable.size();
    }

    public void push() {
        this.symbolTable.add(new ArrayList<>());
    }

    public void pop() {
        // do the collapse
    }

    public <T extends Decl> Optional<T> lookup(String name, Class<T> thingToLookup) {
        for(int i = symbolTable.size() - 1; i >= 0; --i) {
            var stuff = symbolTable.get(i);
            for(var thing : stuff) {
                if(thing.getName().equals(name) && thing.getClass().equals(thingToLookup)) {
                    return Optional.of((T) thing);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Decl.MethDecl> getCurrentMethod() {
        for(int i = symbolTable.size() - 1; i >= 0; --i) {
            var stuff = symbolTable.get(i);
            for(var thing : stuff) {
                if(thing instanceof Decl.MethDecl) {
                    return Optional.of((Decl.MethDecl) thing);
                }
            }
        }
        return Optional.empty();
    }
}
