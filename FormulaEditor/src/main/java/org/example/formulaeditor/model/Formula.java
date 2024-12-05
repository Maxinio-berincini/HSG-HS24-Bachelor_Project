package org.example.formulaeditor.model;

import org.example.formulaeditor.parser.ast.ASTNode;

public class Formula {
    String id;
    private final ASTNode ast;
    private VersionVector versionVector;

    public Formula(String id, ASTNode ast, VersionVector versionVector) {
        this.id = id;
        this.ast = ast;
        this.versionVector = versionVector;
    }

    public String getId() {
        return id;
    }

    public ASTNode getAst() {
        return ast;
    }

    public VersionVector getVersionVector() {
        return versionVector;
    }

    public void setVersionVector(VersionVector versionVector) {
        this.versionVector = versionVector;
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
