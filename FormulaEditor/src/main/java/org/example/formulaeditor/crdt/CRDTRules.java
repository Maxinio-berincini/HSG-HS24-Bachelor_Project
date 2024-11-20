package org.example.formulaeditor.crdt;

import org.example.formulaeditor.model.Formula;
import org.example.formulaeditor.model.Workbook;
import org.example.formulaeditor.parser.ast.Boolean;
import org.example.formulaeditor.parser.ast.Number;
import org.example.formulaeditor.parser.ast.*;

import java.util.ArrayList;
import java.util.List;


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
            System.out.println("Instance of: " + local + "is: " + local.getClass() + " and " + remote + "is: " + remote.getClass());
            // Handle type conflicts
            return resolveTypeConflict(local, remote);
        }

        System.out.println("Instance of: " + local + " is: " + local.getClass() + " and " + remote + " is: " + remote.getClass());
        if (local instanceof Binary && remote instanceof Binary) {
            return mergeBinary((Binary) local, (Binary) remote);
        } else if (local instanceof Number && remote instanceof Number) {
            return mergeNumbers((Number<?>) local, (Number<?>) remote);
        } else if (local instanceof ExcelString && remote instanceof ExcelString) {
            return mergeExcelStrings((ExcelString) local, (ExcelString) remote);
        } else if (local instanceof Boolean && remote instanceof Boolean) {
            return mergeBooleans((Boolean) local, (Boolean) remote);
        } else if (local instanceof Cell && remote instanceof Cell) {
            return mergeCells((Cell) local, (Cell) remote);
        } else if (local instanceof CellRange && remote instanceof CellRange) {
            return mergeCellRanges((CellRange) local, (CellRange) remote);
        } else if (local instanceof Negate && remote instanceof Negate) {
            return mergeNegates((Negate) local, (Negate) remote);
        } else if (local instanceof FunctionCall && remote instanceof FunctionCall) {
            return mergeFunctionCall((FunctionCall) local, (FunctionCall) remote);
        } else {
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
        // Merge arguments
        // traverse the arguments and merge them
        if (local.functionName.equals(remote.functionName)) {
            List<ASTNode> mergedArguments = new ArrayList<>();
            int localSize = local.args.size();
            int remoteSize = remote.args.size();
            int maxSize = Math.max(localSize, remoteSize);
            for (int i = 0; i < maxSize; i++) {
                ASTNode localArg = i < localSize ? local.args.get(i) : null;
                ASTNode remoteArg = i < remoteSize ? remote.args.get(i) : null;
                if (localArg != null && remoteArg != null) {
                    ASTNode mergedArg = mergeASTNodes(localArg, remoteArg);
                    mergedArguments.add(mergedArg);
                } else if (localArg != null) {
                    mergedArguments.add(localArg);
                } else if (remoteArg != null) {
                    mergedArguments.add(remoteArg);
                }
            }

            return new FunctionCall(local.functionName, mergedArguments);
        } else {
            // hierarchy of functions
            return mergeBasicFunction(local, remote);
        }
    }

    private FunctionCall mergeBasicFunction(FunctionCall local, FunctionCall remote) {
        BasicFunction localFunction = local.functionName;
        BasicFunction remoteFunction = remote.functionName;
        // Choose the function with the higher priority
        if (localFunction.getPriority() > remoteFunction.getPriority()) {
            return mergeFunctionCall(local, new FunctionCall(localFunction, remote.args));
        } else if (localFunction.getPriority() < remoteFunction.getPriority()) {
            return mergeFunctionCall(new FunctionCall(remoteFunction, local.args), remote);
        } else {
            // Same priority Exception
            // Max over Min, Sum over Product, And over Or
            if (localFunction == BasicFunction.MAX || remoteFunction == BasicFunction.MAX) {
                return mergeFunctionCall(new FunctionCall(BasicFunction.MAX, local.args), new FunctionCall(BasicFunction.MAX, remote.args));
            } else if (localFunction == BasicFunction.SUM || remoteFunction == BasicFunction.SUM) {
                return mergeFunctionCall(new FunctionCall(BasicFunction.SUM, local.args), new FunctionCall(BasicFunction.SUM, remote.args));
            } else if (localFunction == BasicFunction.AND || remoteFunction == BasicFunction.AND) {
                return mergeFunctionCall(new FunctionCall(BasicFunction.AND, local.args), new FunctionCall(BasicFunction.AND, remote.args));
            } else {
                // Default to local function
                return mergeFunctionCall(new FunctionCall(localFunction, local.args), new FunctionCall(localFunction, remote.args));
            }


        }
    }

    // Rule for merging Numbers: choose the larger Number
    private Number<?> mergeNumbers(Number<?> local, Number<?> remote) {
        double localValue = local.value.doubleValue();
        double remoteValue = remote.value.doubleValue();
        return localValue >= remoteValue ? local : remote;
    }

    // Rule for merging Binary Operators
    private BinaryOp mergeBinaryOp(BinaryOp localOp, BinaryOp remoteOp) {
        // Choose the operator with the lower precedence
        // precedences: 0--> modulo, 1 --> pow, 2 --> multiply/divide, 3 --> add/subtract, 4 --> comparison operators
        if (localOp.precedence < remoteOp.precedence) {
            return localOp;
        } else if (localOp.precedence > remoteOp.precedence) {
            return remoteOp;
        } else {
            return switch (localOp.precedence) {
                case 2 ->
                    // Precedence Level 2: Multiplication vs. Division
                        mergeMultiplicationDivision(localOp, remoteOp);
                case 3 ->
                    // Precedence Level 3: Plus vs. Minus
                        mergeAdditionSubtraction(localOp, remoteOp);
                case 4 ->
                    // Precedence Level 4: Comparison Operators
                        mergeComparisonOperators(localOp, remoteOp);
                default ->
                    // Default to local
                        localOp;
            };
        }
    }

    private BinaryOp mergeAdditionSubtraction(BinaryOp localOp, BinaryOp remoteOp) {
        if (localOp == BinaryOp.PLUS || remoteOp == BinaryOp.PLUS) {
            return BinaryOp.PLUS;
        } else {
            // Both are MINUS or unhandled cases
            return localOp;
        }
    }

    private BinaryOp mergeMultiplicationDivision(BinaryOp localOp, BinaryOp remoteOp) {
        if (localOp == BinaryOp.MULT || remoteOp == BinaryOp.MULT) {
            return BinaryOp.MULT;
        } else {
            // Both are DIV or unhandled cases
            return localOp;
        }
    }

    private BinaryOp mergeComparisonOperators(BinaryOp localOp, BinaryOp remoteOp) {
        //general not equal always loses
        if (localOp == BinaryOp.NOT_EQUAL || remoteOp == BinaryOp.NOT_EQUAL) {
            if (localOp == BinaryOp.NOT_EQUAL) {
                return remoteOp;
            } else {
                return localOp;
            }
        } else {
            // If both are the same, return the same
            if (localOp == remoteOp) {
                return localOp;
            }
            // If one is '<' and the other is '=', combine to '<='
            else if ((localOp == BinaryOp.LESS_THAN && remoteOp == BinaryOp.EQUAL) ||
                    (localOp == BinaryOp.EQUAL && remoteOp == BinaryOp.LESS_THAN)) {
                return BinaryOp.LESS_THAN_OR_EQ;
            }
            // If one is '>' and the other is '=', combine to '>='
            else if ((localOp == BinaryOp.BIGGER_THAN && remoteOp == BinaryOp.EQUAL) ||
                    (localOp == BinaryOp.EQUAL && remoteOp == BinaryOp.BIGGER_THAN)) {
                return BinaryOp.BIGGER_THAN_OR_EQ;
            }
            // General case: '>' and '>=' win over other operators
            else if (localOp == BinaryOp.BIGGER_THAN_OR_EQ || remoteOp == BinaryOp.BIGGER_THAN_OR_EQ) {
                return BinaryOp.BIGGER_THAN_OR_EQ;
            } else {
                // Default to local operator
                return localOp;
            }
        }
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

    // Rule for merging cell references: first compare column values and if they are the same compare row values
    private Cell mergeCells(Cell local, Cell remote) {
        String localColumn = local.column.toUpperCase();
        String remoteColumn = remote.column.toUpperCase();

        // First compare column values lexicographically
        int comparisonColumn = localColumn.compareTo(remoteColumn);
        if (comparisonColumn != 0) {
            // For different columns, return higher column
            return comparisonColumn > 0 ? local : remote;
        } else {
            int localRow = local.row;
            int remoteRow = remote.row;
            // For same columns, return higher row number
            return localRow >= remoteRow ? local : remote;
        }
    }

    // Rule for merging cell ranges (THIS DOES NOT MEAN THAT THE LARGER RANGE WILL BE PICKED)
    private CellRange mergeCellRanges(CellRange local, CellRange remote) {
        // Use mergeCells method to compare start and end cells to create a new range
        Cell mergedStart = mergeCells(local.start, remote.start);
        Cell mergedEnd = mergeCells(local.end, remote.end);

        return new CellRange(mergedStart, mergedEnd);
    }

    // Rule for merging negates
    private Negate mergeNegates(Negate local, Negate remote) {
        // Merge the inner nodes recursively
        ASTNode mergedInnerNode = mergeASTNodes(local.node, remote.node);
        // Return a new Negate node with the merged inner node
        return new Negate(mergedInnerNode);
    }




    private int getTypePriority(ASTNode node) {
        if (node instanceof FunctionCall) return 7;
        if (node instanceof Binary) return 6;
        if (node instanceof CellRange) return 5;
        if (node instanceof Cell) return 4;
        if (node instanceof Number<?>) return 3;
        if (node instanceof Boolean) return 2;
        if (node instanceof ExcelString) return 1;
        return 0; // Default priority for unknown types
    }





    // Resolve type conflicts between different node types
    private ASTNode resolveTypeConflict(ASTNode local, ASTNode remote) {
        // Resolve conflicts for negate
        if (local instanceof Negate && !(remote instanceof Negate)) {
            return remote;
        } else if (!(local instanceof Negate) && remote instanceof Negate) {
            return local;


//        } else if (local instanceof ExcelString || remote instanceof ExcelString)
//            if (local instanceof ExcelString) {
//                return remote;
//            } else {
//                return local;
//            }


            // try merging binary node with other types, if compatible
        }else if (isCompatible(local, remote)) {
            // Attempt to merge nodes
            return attemptMergeDifferentTypes(local, remote);
        } else {
            // Nodes are not compatible --> choose preferred
            return choosePreferredNode(local, remote);
        }

    }

    private boolean isCompatible(ASTNode local, ASTNode remote) {
        // Nodes are compatible if they are of the same type
        if (local.getClass().equals(remote.getClass())) {
            return true;
        }

        // merge binary with function call
        if (local instanceof Binary localBinary && remote instanceof FunctionCall) {
            return localBinary.left.getClass().equals(remote.getClass());
        }
        if (local instanceof FunctionCall && remote instanceof Binary remoteBinary) {
            return remoteBinary.left.getClass().equals(local.getClass());
        }
        // TODO define other compatibility conditions
        return false;
    }

    private ASTNode attemptMergeDifferentTypes(ASTNode local, ASTNode remote) {
        // Try to merge binary with function call
        if (local instanceof Binary localBinary && remote instanceof FunctionCall) {
            ASTNode mergedLeft = mergeASTNodes(localBinary.left, remote);
            // reconstruct Binary with  merged left side
            return new Binary(mergedLeft, localBinary.op, localBinary.right);
        } else if (local instanceof FunctionCall && remote instanceof Binary remoteBinary) {
            ASTNode mergedLeft = mergeASTNodes(local, remoteBinary.left);
            // reconstruct Binary with merged left side
            return new Binary(mergedLeft, remoteBinary.op, remoteBinary.right);
        } else {
            // If we cannot merge, decide which node to prefer
            return choosePreferredNode(local, remote);
        }
        // TODO define other merge strategies
    }

    private ASTNode choosePreferredNode(ASTNode local, ASTNode remote) {
        int localPriority = getTypePriority(local);
        int remotePriority = getTypePriority(remote);

        if (localPriority > remotePriority) {
            return local;
        } else if (remotePriority > localPriority) {
            return remote;
        } else {
            return local;
        }
    }

}
