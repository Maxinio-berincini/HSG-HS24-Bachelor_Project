package org.example.formulaeditor.crdt;

import org.example.formulaeditor.model.Formula;
import org.example.formulaeditor.model.VersionVector;
import org.example.formulaeditor.model.Workbook;
import org.example.formulaeditor.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CRDTMergeTest {
    @Test
    public void testMergeWorkbooks() throws Exception {
        Parser parser = new Parser();

        VersionVector versionVector = new VersionVector();

        // Local workbook
        Workbook local = new Workbook();
        local.addFormula(new Formula("A1", parser.parse("A2 + B2"), versionVector));
        local.addFormula(new Formula("A2", parser.parse("5"), versionVector));

        // Remote workbook
        Workbook remote = new Workbook();
        remote.addFormula(new Formula("A1", parser.parse("SUM(A2:A10)"), versionVector));
        remote.addFormula(new Formula("B1", parser.parse("10"), versionVector));

        CRDTRules rules = new CRDTRules();
        CRDTMerge merger = new CRDTMerge(rules);

        Workbook merged = merger.merge(local, remote);

        // Verify that all formulas are present
        Assertions.assertEquals(3, merged.getFormulas().size());
        Assertions.assertTrue(merged.containsFormula("A1"));
        Assertions.assertTrue(merged.containsFormula("A2"));
        Assertions.assertTrue(merged.containsFormula("B1"));


        // merge is random --> check formula is  local or remote
        Formula mergedA1 = merged.getFormula("A1");
        String mergedA1String = mergedA1.toString();
        boolean isLocalA1 = mergedA1String.equals("(A2+B2)");
        boolean isRemoteA1 = mergedA1String.equals("SUM(A2:A10)");
        Assertions.assertTrue(isLocalA1 || isRemoteA1);
    }
}
