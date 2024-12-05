package org.example.formulaeditor;


import org.example.formulaeditor.crdt.CRDTMerge;
import org.example.formulaeditor.crdt.CRDTRules;
import org.example.formulaeditor.model.Formula;
import org.example.formulaeditor.model.VersionVector;
import org.example.formulaeditor.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class VersionVectorMergeTest {
    private static Parser parser;
    private static CRDTMerge crdtMerge;
    private Formula mergeResult;

    @BeforeAll
    public static void setUp() {
        System.out.println("Setting up tests");
        parser = new Parser();
        CRDTRules crdtRules = new CRDTRules();
        crdtMerge = new CRDTMerge(crdtRules);
    }

    private Formula createFormula(String expression, String instanceId, Integer version) {
        VersionVector versionVector = new VersionVector(new HashMap<String, Integer>() {{
            put(instanceId, version);
        }});
        return new Formula("A1", parser.parse(expression), versionVector);
    }

    @Test
    public void singleSum() {
        Formula formula1 = createFormula("SUM(5,2)", "Instance1", 1);
        Formula formula2 = createFormula("SUM(1,8)", "Instance2", 3);
        mergeResult = crdtMerge.merge(formula1, formula2);


        System.out.println(formula1.getVersionVector());
        System.out.println(formula2.getVersionVector());
        System.out.println(formula1.getVersionVector().isNewerVersion(formula2.getVersionVector()));
        System.out.println(mergeResult.getVersionVector());

        Assertions.assertEquals("SUM(5, 8)", mergeResult.toString());
    }

    @Test
    public void testMergeWithHigherVersion() throws Exception {
        String instanceA = "InstanceA";
        String instanceB = "InstanceB";

        FormulaEditor editorA = new FormulaEditor(instanceA);
        FormulaEditor editorB = new FormulaEditor(instanceB);

        //create formula and change it to increment version vector
        editorA.addFormula("A1", "A2 + A3");
        editorA.updateFormula("A1", "A2 + A3 + A4");
        editorA.updateFormula("A1", "A2 + A3 + A5");

        Formula formulaA = editorA.getWorkbook().getFormula("A1");

        //create second formula
        editorB.addFormula("A1", "A2 + A3");

        Formula formulaB = editorB.getWorkbook().getFormula("A1");

        // merge the formulas
        CRDTRules crdtRules = new CRDTRules();
        Formula mergedFormula = crdtRules.applyRules(formulaA, formulaB);

        //get formula version vectors
        VersionVector versionVectorA = formulaA.getVersionVector();
        VersionVector versionVectorB = formulaB.getVersionVector();

        // get merged version vector
        VersionVector mergedVersionVector = mergedFormula.getVersionVector();

        //check if the merged formula is the one from Instance A
        Assertions.assertEquals(formulaA.toString(), mergedFormula.toString(), "Expected the formula from InstanceA to be chosen");

        //check if the merged version vector is newer than the one from Instance B
        Assertions.assertTrue(mergedVersionVector.isNewerVersion(versionVectorB));

        //check merged version vector
        Assertions.assertEquals("{InstanceA=1, InstanceB=0}", mergedVersionVector.toString());
    }
}
