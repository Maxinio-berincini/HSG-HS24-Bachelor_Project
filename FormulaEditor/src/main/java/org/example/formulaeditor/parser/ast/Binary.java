package org.example.formulaeditor.parser.ast;

import org.example.formulaeditor.parser.ASTNodeVisitor;

public class Binary extends AbstractASTNode {
    public final ASTNode left;
    public final ASTNode right;
    public final BinaryOp op;

    public Binary(ASTNode left, BinaryOp op, ASTNode right) {
        super(6);
        this.left = left;
        this.right = right;
        this.op = op;
    }

    public <T> T accept(ASTNodeVisitor<T> visitor) {
        return visitor.visitBinary(this);
    }

    public String toString() {
        return "(" + this.left.toString() + this.op.toString() + this.right.toString() + ")";
    }
}
