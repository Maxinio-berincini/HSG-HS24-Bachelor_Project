package org.example.formulaeditor.model;

import org.example.formulaeditor.parser.ast.ASTNode;

public class Formula {
    String id;
    private final ASTNode ast;

    public Formula(String id, ASTNode ast) {
        this.id = id;
        this.ast = ast;
    }

    public String getId() {
        return id;
    }

    public ASTNode getAst() {
        return ast;
    }

    @Override
    public String toString() {
        return ast.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Formula other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
