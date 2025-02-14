package org.example.formulaeditor.parser.ast;

import org.example.formulaeditor.parser.ASTNodeVisitor;

public class Number<N extends java.lang.Number> extends AbstractASTNode {
    public final N value;

    public Number(N value) {
        super(3);
        this.value = value;
    }

    public <T> T accept(ASTNodeVisitor<T> visitor) {
        return visitor.visitNumber(this);
    }

    public String toString() {
        return this.value.toString();
    }
}

