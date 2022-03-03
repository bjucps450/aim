package org.bju;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
public class Decl {
    private String name;
    private SemanticChecker.Type type;
    private Integer level;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class VarDecl extends Decl {

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ArgDecl extends Decl {
        private Integer position;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class MethDecl extends Decl {
        private List<VarDecl> variables = new ArrayList<>();
        private List<ArgDecl> arguments = new ArrayList<>();
    }
}
