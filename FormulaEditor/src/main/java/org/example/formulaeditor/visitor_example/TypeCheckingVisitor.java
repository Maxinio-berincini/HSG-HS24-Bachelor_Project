package org.example.formulaeditor.visitor_example;

import org.example.formulaeditor.parser.ASTNodeVisitor;
import org.example.formulaeditor.parser.ast.Boolean;
import org.example.formulaeditor.parser.ast.Number;
import org.example.formulaeditor.parser.ast.*;

public class TypeCheckingVisitor implements ASTNodeVisitor<VisitorExample.ExcelType> {

    private final VisitorExample.ExcelType hint;

    TypeCheckingVisitor(VisitorExample.ExcelType hint) {
        this.hint = hint;
    }

    @Override
    public <N extends java.lang.Number> VisitorExample.ExcelType visitNumber(Number<N> n) {
        return VisitorExample.ExcelType.NUMBER;
    }

    @Override
    public VisitorExample.ExcelType visitCell(Cell n) {
        return hint;
    }

    @Override
    public VisitorExample.ExcelType visitCellRange(CellRange n) {
        return null;
    }

    @Override
    public VisitorExample.ExcelType visitString(ExcelString n) {
        return null;
    }

    @Override
    public VisitorExample.ExcelType visitFunctionCall(FunctionCall n) {
        return null;
    }

    @Override
    public VisitorExample.ExcelType visitBoolean(Boolean n) {
        return null;
    }

    @Override
    public VisitorExample.ExcelType visitBinary(Binary n) {
        switch (n.op) {
            case PLUS:
            case DIV:
            case MULT:
                VisitorExample.ExcelType leftType = n.left.accept(new TypeCheckingVisitor(VisitorExample.ExcelType.NUMBER));
                VisitorExample.ExcelType rightType = n.right.accept(new TypeCheckingVisitor(VisitorExample.ExcelType.NUMBER));
                if (leftType != VisitorExample.ExcelType.NUMBER || rightType != VisitorExample.ExcelType.NUMBER) {
                    throw new RuntimeException("Both sides of the binary operation are expected to be numbers");
                } else {
                    return VisitorExample.ExcelType.NUMBER;
                }
            default:
                new RuntimeException("Unknown operation");
        }
        return null;
    }

    @Override
    public VisitorExample.ExcelType visitNegate(Negate n) {
        return null;
    }
}
