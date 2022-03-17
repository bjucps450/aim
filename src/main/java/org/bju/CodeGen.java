package org.bju;

import org.antlr.AIMBaseVisitor;
import org.antlr.AIMParser;

public class CodeGen extends AIMBaseVisitor<String> {
    private SymbolTable symbolTable;

    public CodeGen(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public String visitStart(AIMParser.StartContext ctx) {
        StringBuilder s = new StringBuilder("""
.data
""");
        symbolTable.getGlobalVariables().forEach(var -> {
            s.append("\t.comm _" + var.getName() + ", 8, 4\n");
        });
        s.append("""
.text

.global _main
_main:
    push    %rbp
    movq    %rsp, %rbp
""");
        for(var primary : ctx.primary()) {
            if(primary instanceof AIMParser.UnContext) {
                if(((AIMParser.UnContext)primary).unconditional_primary() instanceof AIMParser.AssignContext) {
                    s.append(visit(((AIMParser.UnContext)primary).unconditional_primary()));
                }
            }
        }
        s.append("""
    leave
    ret
""");
        return s.toString();
    }

    @Override
    public String visitAssignment(AIMParser.AssignmentContext ctx) {
        StringBuilder s = new StringBuilder();
        s.append(visit(ctx.value));
        s.append("\tpopq\t_" + ctx.IDENTIFIER().getText() + "(%rip)\n");
        return s.toString();
    }

    @Override
    public String visitPs(AIMParser.PsContext ctx) {
        StringBuilder s = new StringBuilder();
        s.append(visit(ctx.first));
        s.append(visit(ctx.second));
        s.append("""
    popq    %r11
    popq    %r10
""");
        if(ctx.op.getText().equals("+")) {
            s.append("\taddq\t%r11, %r10\n");
        } else {
            s.append("\tsubq\t%r11, %r10\n");
        }
        s.append("\tpushq\t%r10\n");
        return s.toString();
    }

    @Override
    public String visitInt(AIMParser.IntContext ctx) {
        return "\tpushq\t$" + ctx.INTEGER_LITERAL().getText() + "\n";
    }
}
