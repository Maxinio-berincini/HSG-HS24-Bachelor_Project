package org.example.formulaeditor.visitor_example;

import org.example.formulaeditor.parser.ast.Number;
import org.example.formulaeditor.parser.ast.*;

import java.util.ArrayList;

public class VisitorExampleRunner {

    public static void main(String[] args) {

        ArrayList<ASTNode> funArgs = new ArrayList<>();

        funArgs.add(new org.example.formulaeditor.parser.ast.Boolean(true));
        funArgs.add(new org.example.formulaeditor.parser.ast.Binary(new org.example.formulaeditor.parser.ast.Boolean(false), BinaryOp.PLUS, new org.example.formulaeditor.parser.ast.Number<Double>(1.0)));
        funArgs.add(new CellRange(new Cell("A", 1), new Cell("B", 10)));

        ASTNode test = new FunctionCall(BasicFunction.IF, funArgs);

        ASTNode teoExpr = new Binary(new Number<>(5), BinaryOp.MULT, new Cell("A", 5));

        System.out.println(test.toString());

        System.out.println(teoExpr.accept(new TypeCheckingVisitor(null)));
    }

}
