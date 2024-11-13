package org.example.formulaeditor;

import org.junit.Assert;
import org.junit.Test;

public class FormulaEditorTest {
    @Test
    public void testAddFormula() throws Exception {
        FormulaEditor editor = new FormulaEditor();
        String result = editor.addFormula("A5","A1 + 5");
        Assert.assertEquals("(A1 + 5)", result);
    }
}
