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
        List<Decl> groupToPop = symbolTable.get(symbolTable.size() - 1);
        symbolTable.remove(groupToPop);
        Decl.MethDecl declToCompressInto = (Decl.MethDecl) groupToPop.get(0); // class or method is always first
        for(var entry : groupToPop) {
            if(entry instanceof Decl.VarDecl) { // arg decls are already added so we only have to worry about the others
                declToCompressInto.getVariables().add((Decl.VarDecl) entry);
            }
        }
        add(declToCompressInto);
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

    public List<Decl.VarDecl> getGlobalVariables() {
        List<Decl.VarDecl> decls = new ArrayList<>();
        var stuff = symbolTable.get(0);
        for(var thing : stuff) {
            if(thing instanceof Decl.VarDecl) {
                decls.add((Decl.VarDecl) thing);
            }
        }
        return decls;
    }

    public Optional<Decl.MethDecl> getCurrentMethod() {
        var stuff = symbolTable.get(symbolTable.size() - 1);
        for(var thing : stuff) {
            if(thing instanceof Decl.MethDecl) {
                return Optional.of((Decl.MethDecl) thing);
            }
        }
        return Optional.empty();
    }
}
