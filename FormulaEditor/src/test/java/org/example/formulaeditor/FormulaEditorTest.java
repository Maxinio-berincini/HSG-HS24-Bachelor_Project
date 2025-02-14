package org.example.formulaeditor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FormulaEditorTest {
    @Test
    public void testAddFormula() throws Exception {
        String instanceId = "Instance1";
        FormulaEditor editor = new FormulaEditor(instanceId);
        String result = editor.addFormula("A5", "A1 + 5");
        Assertions.assertEquals("(A1+5)", result);
    }
}
