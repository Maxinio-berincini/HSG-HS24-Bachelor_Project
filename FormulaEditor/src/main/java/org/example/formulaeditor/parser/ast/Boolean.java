package org.example.formulaeditor.parser.ast;

import org.example.formulaeditor.parser.ASTNodeVisitor;

public class Boolean extends AbstractASTNode {
    public final boolean value;

    public Boolean(boolean value) {
        super(2);
        this.value = value;
    }

    public <T> T accept(ASTNodeVisitor<T> visitor) {
        return visitor.visitBoolean(this);
    }

    public String toString() {
        return "" + this.value;
    }
}
