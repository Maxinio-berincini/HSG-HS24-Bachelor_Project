package org.example.formulaeditor.parser.ast;

import org.example.formulaeditor.parser.ASTNodeVisitor;

public class Cell extends AbstractASTNode {
    public final String column;
    public final int row;

    public Cell(String column, int row) {
        this.column = column;
        this.row = row;
    }

    public <T> T accept(ASTNodeVisitor<T> visitor) {
        return visitor.visitCell(this);
    }

    public String toString() {
        return this.column + this.row;
    }
}
