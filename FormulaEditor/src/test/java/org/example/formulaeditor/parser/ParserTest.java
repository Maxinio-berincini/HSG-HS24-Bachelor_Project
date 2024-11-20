package org.example.formulaeditor.parser;

import org.example.formulaeditor.parser.ast.ASTNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
public class ParserTest {
    @Test
    public void testParseSimpleFormula() {
        Parser parser = new Parser();
        ASTNode ast = parser.parse("A1 + B2");
        Assertions.assertEquals("(A1+B2)", ast.toString());
    }

}
