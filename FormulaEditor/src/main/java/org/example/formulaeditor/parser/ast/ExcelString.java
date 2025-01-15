package org.example.formulaeditor.parser.ast;

import org.example.formulaeditor.parser.ASTNodeVisitor;

public class ExcelString extends AbstractASTNode {
    public final String value;

    public ExcelString(String value) {
        super(1);
        this.value = value;
    }

    public <T> T accept(ASTNodeVisitor<T> visitor) {
        return visitor.visitString(this);
    }

    public String toString() {
        return this.value;
    }
}
