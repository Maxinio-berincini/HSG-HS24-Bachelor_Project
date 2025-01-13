package org.example.formulaeditor.parser.ast;

import org.example.formulaeditor.parser.ASTNodeVisitor;

public class CellRange extends AbstractASTNode {
    public final Cell start;
    public final Cell end;

    public CellRange(Cell start, Cell end) {
        this.start = start;
        this.end = end;
    }

    public <T> T accept(ASTNodeVisitor<T> visitor) {
        return visitor.visitCellRange(this);
    }

    public String toString() {
        return this.start + ":" + this.end;
    }
}

