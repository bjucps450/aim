package org.bju;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
        public ArgDecl(String name, SemanticChecker.Type type, Integer level, Integer position) {
            super(name, type, level);
            this.position = position;
        }
        private Integer position;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class MethDecl extends Decl {
        private List<VarDecl> variables = new ArrayList<>();
        private List<ArgDecl> arguments = new ArrayList<>();
    }
}
