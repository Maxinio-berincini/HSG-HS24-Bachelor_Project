package org.example.formulaeditor.parser.ast;

import org.example.formulaeditor.model.Formula;

import java.util.ArrayList;
import java.util.List;

public class ASTPrinter {

    //print formula node by node with revision count
    public static void printFormulaDetails(Formula formula) {
        if (formula == null) {
            System.out.println("No formula provided.");
            return;
        }
        System.out.println("Formula (" + formula.getId() + "): " + formula);

        //get list of all nodes
        ASTNode root = formula.getAst();
        List<ASTNode> allNodes = new ArrayList<>();
        collectAllNodes(root, allNodes);

        //print each node
        System.out.println("Node details (class name 'content' -> revisionCount):");
        for (ASTNode node : allNodes) {
            int revCount = ((AbstractASTNode) node).getRevisionCount();
            String nodeType = node.getClass().getSimpleName();
            System.out.println("  " + nodeType + " '" + node + "' -> " + revCount);
        }
    }

    //get a list of all nodes
    private static void collectAllNodes(ASTNode node, List<ASTNode> result) {
        if (node == null) {
            return;
        }
        result.add(node);

        if (node instanceof Binary bin) {
            collectAllNodes(bin.left, result);
            collectAllNodes(bin.right, result);
        } else if (node instanceof Negate neg) {
            collectAllNodes(neg.node, result);
        } else if (node instanceof FunctionCall funCall) {
            for (ASTNode arg : funCall.args) {
                collectAllNodes(arg, result);
            }
        } else if (node instanceof CellRange range) {
            collectAllNodes(range.start, result);
            collectAllNodes(range.end, result);
        }
    }

}
