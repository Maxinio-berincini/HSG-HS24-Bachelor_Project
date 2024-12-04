package org.example.formulaeditor.model;

import org.example.formulaeditor.parser.Parser;
import org.example.formulaeditor.parser.ast.ASTNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class FormulaTest {
    private static VersionVector versionVector;
    private static Parser parser;
    @BeforeAll
    public static void setup() {
        System.out.println("Starting Formula tests");
        parser = new Parser();
        versionVector = new VersionVector(new HashMap<String, Integer>() {{
            put("Instance1", 0);
        }});
    }
    @Test
    public void testFormulaCreation() {
        ASTNode ast = parser.parse("SUM(A1:A10)");
        String id = "A1";
        VersionVector versionVector = new VersionVector();
        Formula formula = new Formula(id, ast, versionVector);
        Assertions.assertEquals("SUM(A1:A10)", formula.toString());
        Assertions.assertEquals("A1", formula.getId());
    }

    @Test
    public void testStringCreation() {
        ASTNode ast = parser.parse("apple");
        String id = "B3";
        Formula formula = new Formula(id, ast, versionVector);
        Assertions.assertEquals("apple", formula.toString());
        Assertions.assertEquals("B3", formula.getId());
    }

    @Test
    public void testNumberCreation() {
        ASTNode ast = parser.parse("15");
        String id = "C2";
        Formula formula = new Formula(id, ast, versionVector);
        Assertions.assertEquals("15", formula.toString());
        Assertions.assertEquals("C2", formula.getId());
    }

    @Test
    public void testBooleanCreation() {
        ASTNode ast = parser.parse("true");
        String id = "C1";
        Formula formula = new Formula(id, ast, versionVector);
        Assertions.assertEquals("true", formula.toString());
        Assertions.assertEquals("C1", formula.getId());
    }

    @Test
    public void testCellCreation() {
        ASTNode ast = parser.parse("A3");
        String id = "A4";
        Formula formula = new Formula(id, ast, versionVector);
        Assertions.assertEquals("A3", formula.toString());
        Assertions.assertEquals("A4", formula.getId());
    }

}
