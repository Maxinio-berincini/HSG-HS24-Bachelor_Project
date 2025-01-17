package org.example.formulaeditor.parser.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Compare old and new AST
// If nodes differ, take content from new node but revision count from old node
public class ASTDifference {
    public ASTNode mergeUpdatedAST(ASTNode oldNode, ASTNode newNode) {
        if (oldNode == null) {
            // New formula ast
            return newNode;
        }

        // No changes
        if (oldNode == newNode) {
            return oldNode;
        }

        // Check AST structure
        if (structurallyEquals(oldNode, newNode)) {
            return oldNode;
        }

        // Check one by one
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


        // Completely edited
        return createReplacement(oldNode, newNode);
    }

    private ASTNode mergeBinary(Binary oldBin, Binary newBin) {
        // Merge left and right branch
        ASTNode mergedLeft = mergeUpdatedAST(oldBin.left, newBin.left);
        ASTNode mergedRight = mergeUpdatedAST(oldBin.right, newBin.right);

        // Update revision count if there are differences
        if (!Objects.equals(oldBin.op, newBin.op)
                || mergedLeft != oldBin.left
                || mergedRight != oldBin.right) {
            //new binary with merged children
            Binary updated = new Binary(mergedLeft, newBin.op, mergedRight);
            //keep revision count from old bin
            updated.setRevisionCount(oldBin.getRevisionCount() + 1);
            return updated;
        } else {
            return oldBin;
        }
    }

    private ASTNode mergeFunctionCall(FunctionCall oldFunC, FunctionCall newFunC) {
        // Check for change in function name
        boolean nameChanged = (oldFunC.functionName != newFunC.functionName);

        List<ASTNode> oldArgs = oldFunC.args;
        List<ASTNode> newArgs = newFunC.args;

        // Changes flag
        boolean anyArgChanged = (oldArgs.size() != newArgs.size()) || nameChanged;

        int minSize = Math.min(oldArgs.size(), newArgs.size());
        ArrayList<ASTNode> mergedArgs = new ArrayList<>(newArgs.size());

        for (int i = 0; i < minSize; i++) {
            ASTNode merged = mergeUpdatedAST(oldArgs.get(i), newArgs.get(i));
            mergedArgs.add(merged);
            if (merged != oldArgs.get(i)) {
                anyArgChanged = true;
            }
        }

        if (oldArgs.size() != newArgs.size()) {
            for (int i = minSize; i < newArgs.size(); i++) {
                mergedArgs.add(newArgs.get(i));
            }
            anyArgChanged = true;
        }

        if (anyArgChanged) {
            // Create a new FunctionCall with old revision count +1
            FunctionCall updated = new FunctionCall(newFunC.functionName, mergedArgs);
            updated.setRevisionCount(oldFunC.getRevisionCount() + 1);
            return updated;
        } else {
            return oldFunC;
        }
    }

    private ASTNode mergeNegate(Negate oldNeg, Negate newNeg) {
        ASTNode mergedInner = mergeUpdatedAST(oldNeg.node, newNeg.node);

        if (mergedInner != oldNeg.node) {
            Negate updated = new Negate(mergedInner);
            updated.setRevisionCount(oldNeg.getRevisionCount() + 1);
            return updated;
        } else {
            return oldNeg;
        }
    }

    private ASTNode mergeCell(Cell oldCell, Cell newCell) {
        // Column or row changed
        boolean changed = !oldCell.column.equals(newCell.column) || (oldCell.row != newCell.row);

        if (changed) {
            Cell updated = new Cell(newCell.column, newCell.row);
            updated.setRevisionCount(oldCell.getRevisionCount() + 1);
            return updated;
        } else {
            return oldCell;
        }
    }

    private ASTNode mergeCellRange(CellRange oldRange, CellRange newRange) {
        // Merge both cell references
        ASTNode mergedStart = mergeUpdatedAST(oldRange.start, newRange.start);
        ASTNode mergedEnd = mergeUpdatedAST(oldRange.end, newRange.end);

        if (mergedStart != oldRange.start || mergedEnd != oldRange.end) {
            CellRange updated = new CellRange((Cell) mergedStart, (Cell) mergedEnd);
            updated.setRevisionCount(oldRange.getRevisionCount() + 1);
            return updated;
        } else {
            return oldRange;
        }
    }

    private ASTNode mergeNumber(Number<?> oldNum, Number<?> newNum) {
        if (!oldNum.value.equals(newNum.value)) {
            Number<?> updated = new Number<>(newNum.value);
            updated.setRevisionCount(oldNum.getRevisionCount() + 1);
            return updated;
        } else {
            return oldNum;
        }
    }

    private ASTNode mergeExcelString(ExcelString oldStr, ExcelString newStr) {
        if (!oldStr.value.equals(newStr.value)) {
            ExcelString updated = new ExcelString(newStr.value);
            updated.setRevisionCount(oldStr.getRevisionCount() + 1);
            return updated;
        } else {
            return oldStr;
        }
    }

    private ASTNode mergeBoolean(Boolean oldBool, Boolean newBool) {
        if (oldBool.value != newBool.value) {
            Boolean updated = new Boolean(newBool.value);
            updated.setRevisionCount(oldBool.getRevisionCount() + 1);
            return updated;
        } else {
            return oldBool;
        }
    }

    // Create new node on complete change
    private ASTNode createReplacement(ASTNode oldNode, ASTNode newNode) {
        AbstractASTNode oldAbs = (AbstractASTNode) oldNode;
        AbstractASTNode newAbs = (AbstractASTNode) newNode;

        int combined = oldAbs.getRevisionCount() + 1;
        newAbs.setRevisionCount(combined);

        return newAbs;
    }

    // Check AST structure for relevant changes
    public boolean structurallyEquals(ASTNode oldNode, ASTNode newNode) {
        if (oldNode == newNode) {
            return true;
        }
        if (oldNode == null || newNode == null) {
            return false;
        }
        if (!oldNode.getClass().equals(newNode.getClass())) {
            return false;
        }

        // Types
        if (oldNode instanceof Binary oBin && newNode instanceof Binary nBin) {
            return oBin.op == nBin.op
                    && structurallyEquals(oBin.left, nBin.left)
                    && structurallyEquals(oBin.right, nBin.right);

        } else if (oldNode instanceof FunctionCall oFun && newNode instanceof FunctionCall nFun) {
            if (oFun.functionName != nFun.functionName
                    || oFun.args.size() != nFun.args.size()) {
                return false;
            }
            // Compare each arg
            for (int i = 0; i < oFun.args.size(); i++) {
                if (!structurallyEquals(oFun.args.get(i), nFun.args.get(i))) {
                    return false;
                }
            }
            return true;

        } else if (oldNode instanceof Negate oNeg && newNode instanceof Negate nNeg) {
            return structurallyEquals(oNeg.node, nNeg.node);

        } else if (oldNode instanceof Cell oCell && newNode instanceof Cell nCell) {
            return oCell.column.equals(nCell.column) && oCell.row == nCell.row;

        } else if (oldNode instanceof CellRange oRange && newNode instanceof CellRange nRange) {
            return structurallyEquals(oRange.start, nRange.start)
                    && structurallyEquals(oRange.end, nRange.end);

        } else if (oldNode instanceof Number<?> oNum && newNode instanceof Number<?> nNum) {
            return Objects.equals(oNum.value, nNum.value);

        } else if (oldNode instanceof ExcelString oStr && newNode instanceof ExcelString nStr) {
            return Objects.equals(oStr.value, nStr.value);

        } else if (oldNode instanceof Boolean oBool
                && newNode instanceof Boolean nBool) {
            return oBool.value == nBool.value;
        }

        // Return false if none of the above hit
        return false;
    }
}
