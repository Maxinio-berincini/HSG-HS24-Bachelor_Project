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

    @Test
    public void formulaAndNumber() {
        Formula formula1 = createFormula("80");
        Formula formula2 = createFormula("MIN(A1:A3)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("MIN(A1:A3)", mergeResult.toString());
    }

    @Test
    public void numberAndString() {
        Formula formula1 = createFormula("80");
        Formula formula2 = createFormula("apple");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("80", mergeResult.toString());
    }

    @Test
    public void numberAndNumber() {
        Formula formula1 = createFormula("80");
        Formula formula2 = createFormula("90");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("90", mergeResult.toString());
    }

    @Test
    // One string longer than the other
    public void stringAndString() {
        Formula formula1 = createFormula("banana");
        Formula formula2 = createFormula("apple");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("banana", mergeResult.toString());
    }

    @Test
    public void formulaAndString() {
        Formula formula1 = createFormula("MIN(A1:A3)");
        Formula formula2 = createFormula("apple");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("MIN(A1:A3)", mergeResult.toString());
    }

    @Test
    // Two strings of equal length that should sort alphabetically
    public void equalLengthStrings() {
        Formula formula1 = createFormula("four");
        Formula formula2 = createFormula("tree");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("four", mergeResult.toString());
    }

    @Test
    public void differentCellRanges() {
        Formula formula1 = createFormula("SUM(A1:A3)");
        Formula formula2 = createFormula("SUM(D5:D10)"); // TODO ASK ABOUT WHY IF THIS CELL SAYS SUM(D5:D10 without the last bracket a strange error occurs
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("SUM(D5:D10)", mergeResult.toString());
    }

    @Test
    public void standAloneCellRanges() {
        Formula formula1 = createFormula("C5:C10");
        Formula formula2 = createFormula("A1:B17");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("C5:C10", mergeResult.toString());
    }

    @Test
    // Here Binary Operations and Numbers are tested
    public void differentAppends() {
        Formula formula1 = createFormula("MIN(A1:A3)/2");
        Formula formula2 = createFormula("MIN(A1:A3)/4");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("MIN(A1:A3)/4", mergeResult.toString()); // TODO check whats going on here. It seems right but strange bracket placement
    }

    @Test
    // Here Binary Operations are tested: addition vs division
    public void differentAppendsAndNumbers() {
        Formula formula1 = createFormula("MIN(A1:A3)/2");
        Formula formula2 = createFormula("MIN(A1:A3)+4");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("MIN(A1:A3)/4", mergeResult.toString()); // TODO same problem as above
    }

    @Test
    // Here appending and cell ranges are tested
    public void Appends() {
        Formula formula1 = createFormula("MIN(A1:A3)/2");
        Formula formula2 = createFormula("MIN(A1:A5)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("MIN(A1:A5)/2", mergeResult.toString()); // TODO appending is messing
    }

    @Test
    public void callRangesAndAppendsAndNumbers() {
        Formula formula1 = createFormula("MIN(A1:A2)/2");
        Formula formula2 = createFormula("MIN(A1:A3)+4");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("MIN(A1:A3)/4", mergeResult.toString()); // TODO bracket issue here again
    }

    @Test
    public void longerAppendsAndNumbers() {
        Formula formula1 = createFormula("MIN(A1:A3)/2-1");
        Formula formula2 = createFormula("MIN(A1:A3)+4");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("MIN(A1:A3)/4-1", mergeResult.toString()); //TODO idk what is going on here
    }

    @Test
    public void testCellReferences() {
        Formula formula1 = createFormula("15*A8");
        Formula formula2 = createFormula("15*A7");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("15*A8", mergeResult.toString()); //TODO Bracket issue
    }

    @Test
    public void numbersAndCellReferences() {
        Formula formula1 = createFormula("18*A6");
        Formula formula2 = createFormula("20+A9");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("20*A9", mergeResult.toString()); //TODO Bracket issue
    }

    @Test
    public void numbersWithSameCellReferences() {
        Formula formula1 = createFormula("10*A5");
        Formula formula2 = createFormula("15*A5");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("15*A5", mergeResult.toString()); //TODO Bracket issue
    }

    @Test
    public void operationsAndNumbers() {
        Formula formula1 = createFormula("18+A5");
        Formula formula2 = createFormula("11*A5");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("18*A5", mergeResult.toString()); //TODO Bracket issue
    }

    @Test
    public void functionCallAndBinaryOperation() {
        Formula formula1 = createFormula("MIN(A1:A5)");
        Formula formula2 = createFormula("17*A5");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("MIN(A1:A5)", mergeResult.toString());
    }

    @Test
    public void longerBinaryOperations() {
        Formula formula1 = createFormula("18*A6/A3");
        Formula formula2 = createFormula("20+A9/7");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("20*A9/A3", mergeResult.toString()); //TODO Check this!!!!!!!!
    }

    @Test
    // Slight variation of formula above
    public void longerBinaryOperations2() {
        Formula formula1 = createFormula("18*A6+A3");
        Formula formula2 = createFormula("20+A9/7");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("20*A9/A3", mergeResult.toString()); //TODO Check this!!!!!!!!
    }

    @Test
    public void cellReferenceAndBinaryOperation() {
        Formula formula1 = createFormula("A6");
        Formula formula2 = createFormula("17*3+4");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("17*3+4", mergeResult.toString()); //TODO BRACKET ISSUE
    }

    @Test
    public void differentColumnCellReferences() {
        Formula formula1 = createFormula("15*D7");
        Formula formula2 = createFormula("15*C14");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("17*D7", mergeResult.toString()); //TODO BRACKET ISSUE
    }

    @Test
    public void complexFormulaAndNumber() {
        Formula formula1 = createFormula("teo+max"); //TODO I think this is yielding an error
        Formula formula2 = createFormula("5");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("5", mergeResult.toString());
    }

    @Test
    public void numberBinaryOpAndCellReferencesBinaryOp() {
        Formula formula1 = createFormula("A5*A7");
        Formula formula2 = createFormula("17*10");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("A5*A7", mergeResult.toString()); //TODO BRACKET ISSUE
    }

    @Test
    public void numberAndCellReference() {
        Formula formula1 = createFormula("A5");
        Formula formula2 = createFormula("3");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("A5", mergeResult.toString());
    }

    @Test
    public void stringAndCellReference() {
        Formula formula1 = createFormula("cat");
        Formula formula2 = createFormula("A5");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("A5", mergeResult.toString());
    }

    @Test
    public void twoTrueBooleans() {
        Formula formula1 = createFormula("true");
        Formula formula2 = createFormula("true");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("true", mergeResult.toString());
    }

    @Test
    // Also check case sensitivity here
    public void twoFalseBooleans() {
        Formula formula1 = createFormula("False");
        Formula formula2 = createFormula("faLSe");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("false", mergeResult.toString());
    }

    @Test
    public void falseAndTrueBooleans() {
        Formula formula1 = createFormula("true");
        Formula formula2 = createFormula("false");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("true", mergeResult.toString());
    }

    @Test
    public void stringAndBoolean() {
        Formula formula1 = createFormula("true");
        Formula formula2 = createFormula("cat");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("true", mergeResult.toString());
    }

    @Test
    public void numberAndBoolean() {
        Formula formula1 = createFormula("15");
        Formula formula2 = createFormula("false");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("15", mergeResult.toString());
    }

    @Test
    public void cellReferenceAndBoolean() {
        Formula formula1 = createFormula("A7");
        Formula formula2 = createFormula("true");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("A7", mergeResult.toString());
    }

    @Test
    public void cellRangeAndBoolean() {
        Formula formula1 = createFormula("A1:A3");
        Formula formula2 = createFormula("false");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("A1:A3", mergeResult.toString());
    }

    @Test
    public void functionCallAndBoolean() {
        Formula formula1 = createFormula("MIN(A1:A3)");
        Formula formula2 = createFormula("true");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("MIN(A1:A3)", mergeResult.toString());
    }

    @Test
    public void binaryOperationAndBoolean() {
        Formula formula1 = createFormula("10-3");
        Formula formula2 = createFormula("true");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("10-3", mergeResult.toString()); //TODO Bracket Issue
    }

}
