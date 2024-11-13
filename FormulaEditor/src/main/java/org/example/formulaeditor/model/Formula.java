package org.example.formulaeditor.model;

import org.example.formulaeditor.parser.ast.ASTNode;

public class Formula {
    private ASTNode ast;

    public Formula(ASTNode ast) {
        this.ast = ast;
    }

    public ASTNode getAst() {
        return ast;
    }

    @Override
    public String toString() {
        return ast.toString();
    }
}
