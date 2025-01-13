package org.example.formulaeditor.parser.ast;

public abstract class AbstractASTNode implements ASTNode {
    private int revisionCount;

    public int getRevisionCount() {
        return revisionCount;
    }

    public void setRevisionCount(int revisionCount) {
        this.revisionCount = revisionCount;
    }

    public void incrementRevisionCount() {
        this.revisionCount++;
    }
}
