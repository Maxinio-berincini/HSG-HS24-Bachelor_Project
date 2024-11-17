package org.example.formulaeditor;

import org.example.formulaeditor.crdt.CRDTMerge;
import org.example.formulaeditor.crdt.CRDTRules;
import org.example.formulaeditor.model.Formula;
import org.example.formulaeditor.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FormulaMergeTest {

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

    private Formula createFormula(String expression) {
        return new Formula("A1", parser.parse(expression));
    }

    @Test
    public void singleSum() {
        Formula formula1 = createFormula("SUM(5,2)");
        Formula formula2 = createFormula("SUM(1,8)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("SUM(5, 8)", mergeResult.toString());
    }

    @Test
    public void nestedSum() {
        Formula formula1 = createFormula("SUM(SUM(3,2),2)");
        Formula formula2 = createFormula("SUM(SUM(5,1),10)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("SUM(SUM(5, 2), 10)", mergeResult.toString());
    }
}
