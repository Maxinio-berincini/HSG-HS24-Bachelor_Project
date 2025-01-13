package org.example.formulaeditor.parser.ast;

public class ASTDifference {
    public ASTNode mergeUpdatedAST(ASTNode oldNode, ASTNode newNode) {
        if (oldNode == null) {
            //new formula ast
            return newNode;
        }

        //no changes
        if (oldNode == newNode) {
            return oldNode;
        }

        //check AST structure
        if (structurallyEquals(oldNode, newNode)) {
            return oldNode;
        }

        //check one by one
        if (oldNode instanceof Binary && newNode instanceof Binary) {
            return mergeBinary((Binary) oldNode, (Binary) newNode);
        } else if (oldNode instanceof Number && newNode instanceof Number) {
            return mergeNumber((Number<?>) oldNode, (Number<?>) newNode);
        } else if (oldNode instanceof ExcelString && newNode instanceof ExcelString) {
            return mergeExcelString((ExcelString) oldNode, (ExcelString) newNode);
        } else if (oldNode instanceof Boolean && newNode instanceof Boolean) {
            return mergeBoolean((Boolean) oldNode, (Boolean) newNode);
        } else if (oldNode instanceof Cell && newNode instanceof Cell) {
            return mergeCell((Cell) oldNode, (Cell) newNode);
        } else if (oldNode instanceof CellRange && newNode instanceof CellRange) {
            return mergeCellRange((CellRange) oldNode, (CellRange) newNode);
        } else if (oldNode instanceof Negate && newNode instanceof Negate) {
            return mergeNegate((Negate) oldNode, (Negate) newNode);
        } else if (oldNode instanceof FunctionCall && newNode instanceof FunctionCall) {
            return mergeFunctionCall((FunctionCall) oldNode, (FunctionCall) newNode);
        }


        //completely edited
        return createReplacement(oldNode, newNode);
    }

    private ASTNode mergeBinary(Binary oldBin, Binary newBin) {
        return oldBin;
    }

    private ASTNode mergeFunctionCall(FunctionCall oldFunC, FunctionCall newFunC) {
        return oldFunC;
    }

    private ASTNode mergeNegate(Negate oldNeg, Negate newNeg) {
        return oldNeg;
    }

    private ASTNode mergeCell(Cell oldCell, Cell newCell) {
        return oldCell;
    }

    private ASTNode mergeCellRange(CellRange oldRange, CellRange newRange) {
        return oldRange;
    }

    private ASTNode mergeNumber(Number<?> oldNum, Number<?> newNum) {
        return oldNum;
    }

    private ASTNode mergeExcelString(ExcelString oldStr, ExcelString newStr) {
        return oldStr;
    }

    private ASTNode mergeBoolean(Boolean oldBool, Boolean newBool) {
        return oldBool;
    }


    //create new node on complete change
    private ASTNode createReplacement(ASTNode oldNode, ASTNode newNode) {
        return oldNode;
    }

    //check AST structure for relevant changes
    public boolean structurallyEquals(ASTNode oldNode, ASTNode newNode) {
        return false;
    }
}
