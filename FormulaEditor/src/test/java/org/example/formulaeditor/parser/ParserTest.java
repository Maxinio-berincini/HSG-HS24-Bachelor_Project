package org.example.formulaeditor.parser;

import org.example.formulaeditor.parser.Parser;
import org.example.formulaeditor.parser.ast.ASTNode;
import org.junit.Assert;
import org.junit.Test;

public class ParserTest {
    @Test
    public void testParseSimpleFormula() {
        Parser parser = new Parser();
        ASTNode ast = parser.parse("A1 + B2");
        Assert.assertEquals("(A1 + B2)", ast.toString());
    }

}
