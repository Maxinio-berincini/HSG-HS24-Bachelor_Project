package org.example.formulaeditor.parser.ast;

public abstract class AbstractASTNode implements ASTNode {
    private int revisionCount;
    private int priority;

    public AbstractASTNode(int priority) {
        this.revisionCount = 1;
        this.priority = priority;
    }

    public int getRevisionCount() {
        return revisionCount;
    }
    public int getPriority(){return priority;}

    public void setRevisionCount(int revisionCount) {
        this.revisionCount = revisionCount;
    }

    public void incrementRevisionCount() {
        this.revisionCount++;
    }
}
