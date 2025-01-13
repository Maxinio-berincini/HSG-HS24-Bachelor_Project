package org.example.formulaeditor.parser.ast;

import org.example.formulaeditor.parser.ASTNodeVisitor;

public class Negate extends AbstractASTNode {
    public final ASTNode node;

    public Negate(ASTNode node) {
        this.node = node;
    }

    public <T> T accept(ASTNodeVisitor<T> visitor) {
        return visitor.visitNegate(this);
    }

    public String toString() {
        return "(-" + this.node.toString() + ")";
    }
}
