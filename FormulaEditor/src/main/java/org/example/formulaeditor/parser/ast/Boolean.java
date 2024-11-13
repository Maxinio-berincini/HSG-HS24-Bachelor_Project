package org.example.formulaeditor.parser.ast;

import org.example.formulaeditor.parser.ASTNodeVisitor;

public class Boolean implements ASTNode {
    public final boolean value;

    public Boolean(boolean value) {
        this.value = value;
    }

    public <T> T accept(ASTNodeVisitor<T> visitor) {
        return visitor.visitBoolean(this);
    }

    public String toString() {
        return "" + this.value;
    }
}
