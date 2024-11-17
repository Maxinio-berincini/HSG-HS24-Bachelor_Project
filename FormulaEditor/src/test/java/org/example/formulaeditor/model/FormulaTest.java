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
}
