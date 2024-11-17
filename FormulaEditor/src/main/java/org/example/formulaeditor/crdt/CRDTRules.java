package org.example.formulaeditor.crdt;

import org.example.formulaeditor.model.Formula;
import org.example.formulaeditor.model.Workbook;
import org.example.formulaeditor.parser.ast.Number;
import org.example.formulaeditor.parser.ast.*;
import org.example.formulaeditor.parser.ast.Boolean;


public class CRDTRules {


    public Formula applyRules(Formula local, Formula remote) {
        //TODO merge logic

        ASTNode mergedAST = mergeASTNodes(local.getAst(), remote.getAst());
        // Create a new Formula with the merged AST
        return new Formula(local.getId(), mergedAST);

    }

    public Workbook applyRules(Workbook local, Workbook remote) {
        Workbook mergedWorkbook = new Workbook();

        // Merge formulas
        for (String id : local.getFormulasMap().keySet()) {
            Formula localFormula = local.getFormula(id);
            if (remote.containsFormula(id)) {
                // Formula exists in both, merge them
                Formula remoteFormula = remote.getFormula(id);
                Formula mergedFormula = applyRules(localFormula, remoteFormula);
                mergedWorkbook.addFormula(mergedFormula);
            } else {
                // Only in local
                mergedWorkbook.addFormula(localFormula);
            }
        }

        // Add formulas that are only in remote
        for (String id : remote.getFormulasMap().keySet()) {
            if (!local.containsFormula(id)) {
                mergedWorkbook.addFormula(remote.getFormula(id));
            }
        }

        return mergedWorkbook;
    }


    // Merge two ASTNodes according to the CRDT rules
    public ASTNode mergeASTNodes(ASTNode local, ASTNode remote) {
        // check if both nodes are of the same type
        if (local.getClass() != remote.getClass()) {
            // Handle type conflicts
            return resolveTypeConflict(local, remote);
        }

        System.out.println("Instance of: " + local + "is: " + local.getClass() + " and " + remote + "is: " + remote.getClass());
        if (local instanceof Binary && remote instanceof Binary) {
            return mergeBinary((Binary) local, (Binary) remote);
        } else if (local instanceof Number && remote instanceof Number) {
            return mergeNumbers((Number<?>) local, (Number<?>) remote);
        } else if (local instanceof ExcelString && remote instanceof ExcelString) {
            return mergeExcelStrings((ExcelString) local, (ExcelString) remote);
        } else if (local instanceof Boolean && remote instanceof Boolean) {
            return mergeBooleans((Boolean) local, (Boolean) remote);
        } else {
            //TODO Implement merge logic for other node types
            //BinaryOp, Boolean, Cell, CellRange, Negate, Basic Function

            // If nodes are of the same type but not handled, return local by default
            return local;
        }
    }

    // Handle merging of Binary nodes
    private Binary mergeBinary(Binary local, Binary remote) {
        ASTNode mergedLeft = mergeASTNodes(local.left, remote.left);
        ASTNode mergedRight = mergeASTNodes(local.right, remote.right);
        BinaryOp mergedOp = mergeBinaryOp(local.op, remote.op);
        return new Binary(mergedLeft, mergedOp, mergedRight);
    }

    // Handle merging of FunctionCall nodes
    private FunctionCall mergeFunctionCall(FunctionCall local, FunctionCall remote) {
        if(local.functionName.equals(remote.functionName)){
            // Merge arguments
            // traverse the arguments and merge them
            return local;
        } else{
            // hierarchy of functions
            return local;
        }

        //TODO Implement merge logic for FunctionCall nodes
    }

    // Rule for merging Numbers: choose the larger Number
    private Number<?> mergeNumbers(Number<?> local, Number<?> remote) {
        double localValue = local.value.doubleValue();
        double remoteValue = remote.value.doubleValue();
        return localValue >= remoteValue ? local : remote;
    }

    // Rule for merging Binary Operators
    private BinaryOp mergeBinaryOp(BinaryOp localOp, BinaryOp remoteOp) {
        //TODO implement hierarchy of operators
        return localOp;
    }

    // Resolve type conflicts between different node types
    private ASTNode resolveTypeConflict(ASTNode local, ASTNode remote) {
        //TODO Implement conflict resolution
        return local;
    }

    // Rule for merging strings: choose the longer string or the lexicographically lower string
    private ExcelString mergeExcelStrings(ExcelString local, ExcelString remote) {
        String localValue = local.value;
        String remoteValue = remote.value;

        int localLength = localValue.length();
        int remoteLength = remoteValue.length();

        if (localLength != remoteLength) {
            // Return the string with the longer length
            return localLength > remoteLength ? local : remote;
        } else {
            // Lengths are equal, compare lexicographically and return the string that is alphabetically lower
            int comparisonResult = localValue.compareTo(remoteValue);
            return comparisonResult <= 0 ? local : remote;
        }
    }
    // Rule for merging two booleans: pick true unless both are false
    private Boolean mergeBooleans(Boolean local, Boolean remote) {
        if (local.value == remote.value) {
            // Both values are the same, return either one
            return local;
        } else if (local.value) {
            // Local is true, remote is false
            return local;
        } else {
            // Local is false, remote is true
            return remote;
        }
    }

}
