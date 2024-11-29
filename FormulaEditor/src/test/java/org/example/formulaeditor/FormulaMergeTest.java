package org.example.formulaeditor;

import org.example.formulaeditor.crdt.CRDTMerge;
import org.example.formulaeditor.crdt.CRDTRules;
import org.example.formulaeditor.model.Formula;
import org.example.formulaeditor.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
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

    @Disabled
    @Test
    // TODO implement vectors for this
    // Checks that if only one user edited a cell before a sync, that their changes are chosen
    public void onlyOneUserEditsCell() {
        Formula formula1 = createFormula("17");
        Formula formula2 = createFormula("");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("17", mergeResult.toString());
    }

    @Test
    // TODO take a look at and discuss this
    public void errorInEntry() {
        Formula formula1 = createFormula("17");
        Formula formula2 = createFormula("17=15");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(17=15)", mergeResult.toString());
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
    public void numberAndCellrange() {
        Formula formula1 = createFormula("80");
        Formula formula2 = createFormula("A1:C3");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("A1:C3", mergeResult.toString());
    }

    @Test
    public void stringAndCellrange() {
        Formula formula1 = createFormula("apple");
        Formula formula2 = createFormula("A1:C3");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("A1:C3", mergeResult.toString());
    }

    @Test
    public void functionCallAndCellrange() {
        Formula formula1 = createFormula("MIN(D1:D16)");
        Formula formula2 = createFormula("A1:C3");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("MIN(D1:D16)", mergeResult.toString());
    }

    @Test
    public void oddString() {
        Formula formula1 = createFormula("80");
        Formula formula2 = createFormula("30apple30");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("80", mergeResult.toString());
    }

    @Test
    public void oddString2() {
        Formula formula1 = createFormula("apple");
        Formula formula2 = createFormula("apple30");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("apple30", mergeResult.toString());
    }

    @Test
    public void oddString3() {
        Formula formula1 = createFormula("apple");
        Formula formula2 = createFormula("30apple");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("30apple", mergeResult.toString());
    }

    @Test
    public void oddString4() {
        Formula formula1 = createFormula("1");
        Formula formula2 = createFormula("30apple");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("1", mergeResult.toString());
    }

    @Disabled
    // TODO See if spaces can be allowed in strings
    @Test
    public void stringWithSpaces() {
        Formula formula1 = createFormula("1");
        Formula formula2 = createFormula("30 apples");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("1", mergeResult.toString());
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
        Formula formula2 = createFormula("SUM(D5:D10)");
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
    public void cellReferenceAndCellRange() {
        Formula formula1 = createFormula("C10");
        Formula formula2 = createFormula("A1:B17");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("A1:B17", mergeResult.toString());
    }

    @Test
    // Here Binary Operations and Numbers are tested
    public void differentAppends() {
        Formula formula1 = createFormula("MIN(A1:A3)/2");
        Formula formula2 = createFormula("MIN(A1:A3)/4");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(MIN(A1:A3)/4)", mergeResult.toString());
    }

    @Test
    // Here Binary Operations are tested: addition vs division
    public void differentAppendsAndNumbers() {
        Formula formula1 = createFormula("MIN(A1:A3)/2");
        Formula formula2 = createFormula("MIN(A1:A3)+4");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(MIN(A1:A3)/4)", mergeResult.toString());
    }

    @Test
    // Here appending and cell ranges are tested
    public void Appends() {
        Formula formula1 = createFormula("MIN(A1:A3)/2");
        Formula formula2 = createFormula("MIN(A1:A5)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(MIN(A1:A5)/2)", mergeResult.toString());
    }

    @Test
    public void callRangesAndAppendsAndNumbers() {
        Formula formula1 = createFormula("MIN(A1:A2)/2");
        Formula formula2 = createFormula("MIN(A1:A3)+4");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(MIN(A1:A3)/4)", mergeResult.toString());
    }

    //TODO Append in the middle keep in mind for later tests
    @Test
    public void longerAppendsAndNumbers() {
        Formula formula1 = createFormula("MIN(A1:A3)/2-1");
        Formula formula2 = createFormula("MIN(A1:A3)+4");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("((MIN(A1:A3)/2)+4)", mergeResult.toString());
    }

    @Test
    public void testCellReferences() {
        Formula formula1 = createFormula("15*A8");
        Formula formula2 = createFormula("15*A7");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(15*A8)", mergeResult.toString());
    }

    @Test
    public void numbersAndCellReferences() {
        Formula formula1 = createFormula("18*A6");
        Formula formula2 = createFormula("20+A9");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(20*A9)", mergeResult.toString());
    }

    @Test
    public void numbersWithSameCellReferences() {
        Formula formula1 = createFormula("10*A5");
        Formula formula2 = createFormula("15*A5");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(15*A5)", mergeResult.toString());
    }

    @Test
    public void operationsAndNumbers() {
        Formula formula1 = createFormula("18+A5");
        Formula formula2 = createFormula("11*A5");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(18*A5)", mergeResult.toString());
    }

    @Test
    public void functionCallAndBinaryOperation() {
        Formula formula1 = createFormula("MIN(A1:A5)");
        Formula formula2 = createFormula("17+A5");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("MIN(A1:A5)", mergeResult.toString());
    }

    @Test
    public void longerBinaryOperations() {
        Formula formula1 = createFormula("18*A6/A3");
        Formula formula2 = createFormula("20+A9/7");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("((18*A6)/(A9/7))", mergeResult.toString());
    }

    @Test
    // Slight variation of formula above
    public void longerBinaryOperations2() {
        Formula formula1 = createFormula("18*A6+A3");
        Formula formula2 = createFormula("20+A9/7");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("((18*A6)+(A9/7))", mergeResult.toString());
    }

    @Test
    public void cellReferenceAndBinaryOperation() {
        Formula formula1 = createFormula("A6");
        Formula formula2 = createFormula("A5*3+4");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("((A5*3)+4)", mergeResult.toString());
    }

    @Test
    public void differentColumnCellReferences() {
        Formula formula1 = createFormula("15*D7");
        Formula formula2 = createFormula("15*C14");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(15*D7)", mergeResult.toString());
    }

    //TODO Invalid Input --> add Input Validation
    @Disabled
    @Test
    public void complexFormulaAndNumber() {
        Formula formula1 = createFormula("teo+max");
        Formula formula2 = createFormula("5");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("5", mergeResult.toString());
    }

    @Test
    public void numberBinaryOpAndCellReferencesBinaryOp() {
        Formula formula1 = createFormula("A5*A7");
        Formula formula2 = createFormula("17*10");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(A5*A7)", mergeResult.toString());
    }

    @Test
    public void BinaryOpAndNumber() {
        Formula formula1 = createFormula("20");
        Formula formula2 = createFormula("17*10");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(17*10)", mergeResult.toString());
    }

    @Test
    public void BinaryOpAndString() {
        Formula formula1 = createFormula("apple");
        Formula formula2 = createFormula("17*10");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(17*10)", mergeResult.toString());
    }

    @Test
    public void CellReferences1() {
        Formula formula1 = createFormula("A5");
        Formula formula2 = createFormula("A7");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("A7", mergeResult.toString());
    }

    @Test
    public void CellReferences2() {
        Formula formula1 = createFormula("A5");
        Formula formula2 = createFormula("B1");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("B1", mergeResult.toString());
    }

    @Test
    // Just to check that identical objects merge correctly
    public void sameCellReferences() {
        Formula formula1 = createFormula("A1");
        Formula formula2 = createFormula("A1");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("A1", mergeResult.toString());
    }

    @Test
    public void numberAndCellReference() {
        Formula formula1 = createFormula("A5");
        Formula formula2 = createFormula("3");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("A5", mergeResult.toString());
    }

    @Test
    public void functionAndCellReference() {
        Formula formula1 = createFormula("A7");
        Formula formula2 = createFormula("MIN(A1:A5)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("MIN(A1:A5)", mergeResult.toString());
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
    public void cellRangeAndBinaryOp() {
        Formula formula1 = createFormula("A1:A3");
        Formula formula2 = createFormula("A4-17");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(A4-17)", mergeResult.toString());
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
        Assertions.assertEquals("(10-3)", mergeResult.toString());
    }

    @Disabled
    @Test
    // TODO WE NEED TO COME UP WITH THE ARBITRARY RULING HERE
    public void differentFunctions() {
        Formula formula1 = createFormula("MIN(A1:A3)");
        Formula formula2 = createFormula("MAX(A1:A3)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("XXXXXXX", mergeResult.toString());
    }

    @Disabled
    @Test
    // TODO WE NEED TO COME UP WITH THE ARBITRARY RULING HERE
    public void differentFunctions2() {
        Formula formula1 = createFormula("SUM(A1:A3)");
        Formula formula2 = createFormula("MAX(A1:A3)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("XXXXXXX", mergeResult.toString());
    }

    @Disabled
    @Test
    // TODO WE NEED TO COME UP WITH THE ARBITRARY RULING HERE
    public void differentFunctions3() {
        Formula formula1 = createFormula("PRODUCT(A1:A3)");
        Formula formula2 = createFormula("MIN(A1:A3)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("XXXXXXX", mergeResult.toString());
    }

    @Test
    public void functionsWithRangeVersusCellReference() {
        Formula formula1 = createFormula("MIN(A7)");
        Formula formula2 = createFormula("MIN(A1:A3)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("MIN(A1:A3)", mergeResult.toString());
    }

}
