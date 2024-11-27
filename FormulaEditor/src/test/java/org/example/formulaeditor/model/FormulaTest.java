package org.example.formulaeditor.model;

import org.example.formulaeditor.parser.Parser;
import org.example.formulaeditor.parser.ast.ASTNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FormulaTest {
    @Test
    public void testFormulaCreation() {
        Parser parser = new Parser();
        ASTNode ast = parser.parse("SUM(A1:A10)");
        String id = "A1";
        Formula formula = new Formula(id, ast);
        Assertions.assertEquals("SUM(A1:A10)", formula.toString());
        Assertions.assertEquals("A1", formula.getId());
    }

    @Test
    public void testStringCreation() {
        Parser parser = new Parser();
        ASTNode ast = parser.parse("apple");
        String id = "B3";
        Formula formula = new Formula(id, ast);
        Assertions.assertEquals("apple", formula.toString());
        Assertions.assertEquals("B3", formula.getId());
    }

    @Test
    public void testNumberCreation() {
        Parser parser = new Parser();
        ASTNode ast = parser.parse("15");
        String id = "C2";
        Formula formula = new Formula(id, ast);
        Assertions.assertEquals("15", formula.toString());
        Assertions.assertEquals("C2", formula.getId());
    }

    @Test
    public void testBooleanCreation() {
        Parser parser = new Parser();
        ASTNode ast = parser.parse("true");
        String id = "C1";
        Formula formula = new Formula(id, ast);
        Assertions.assertEquals("true", formula.toString());
        Assertions.assertEquals("C1", formula.getId());
    }

    @Test
    public void testCellCreation() {
        Parser parser = new Parser();
        ASTNode ast = parser.parse("A4");
        String id = "A4";
        Formula formula = new Formula(id, ast);
        Assertions.assertEquals("A4", formula.toString());
        Assertions.assertEquals("A4", formula.getId());
    }

}
