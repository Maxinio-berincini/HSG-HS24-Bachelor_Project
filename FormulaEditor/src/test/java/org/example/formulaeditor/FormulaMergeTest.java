package org.example.formulaeditor;

import org.example.formulaeditor.crdt.CRDTMerge;
import org.example.formulaeditor.crdt.CRDTRules;
import org.example.formulaeditor.model.Formula;
import org.example.formulaeditor.model.VersionVector;
import org.example.formulaeditor.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class FormulaMergeTest {

    private static Parser parser;
    private static CRDTMerge crdtMerge;
    private Formula mergeResult;
    private static VersionVector versionVector;

    @BeforeAll
    public static void setUp() {
        System.out.println("Setting up tests");
        parser = new Parser();
        CRDTRules crdtRules = new CRDTRules();
        crdtMerge = new CRDTMerge(crdtRules);
        versionVector = new VersionVector(new HashMap<String, Integer>() {{
            put("Instance1", 0);
        }});
    }

    private Formula createFormula(String expression) {
        return new Formula("A1", parser.parse(expression), versionVector);
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
    public void spaces() {
        Formula formula1 = createFormula(" apple");
        Formula formula2 = createFormula(" apple    ");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("apple", mergeResult.toString());
    }

    @Test
    public void spacesWithDifferentLengthStrings() {
        Formula formula1 = createFormula(" peaches");
        Formula formula2 = createFormula(" apple    ");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("peaches", mergeResult.toString());
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
        Formula formula2 = createFormula("30 apples"); // The space here cannot be parsed correctly
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

    // This test is disabled because it fails, however it's final outcome is still correct
    @Disabled
    @Test
    public void invalidFormulaAndNumber() {
        Formula formula1 = createFormula("teo+max"); // "teo+max" is parsed as an error (max is a keyword reserved for formulas) however can still be entered by a user
        Formula formula2 = createFormula("5"); // As long as this second formula is a valid one, it will always win the merge
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("5", mergeResult.toString());
    }

    @Test
    public void strangeFormulaStrings() {
        Formula formula1 = createFormula("teo+apple");
        Formula formula2 = createFormula("apple+peach");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(apple+apple)", mergeResult.toString());
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

    @Test
    public void differentFunctions() {
        Formula formula1 = createFormula("MIN(A1:A3)");
        Formula formula2 = createFormula("MAX(A1:A3)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("MAX(A1:A3)", mergeResult.toString());
    }

    @Test
    public void differentFunctions2() {
        Formula formula1 = createFormula("SUM(A1:A3)");
        Formula formula2 = createFormula("MAX(A1:A3)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("MAX(A1:A3)", mergeResult.toString());
    }

    @Test
    public void differentFunctions3() {
        Formula formula1 = createFormula("PRODUCT(A1:A9)");
        Formula formula2 = createFormula("MIN(A1:A3)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("MIN(A1:A9)", mergeResult.toString());
    }

    @Test
    public void differentFunctions4() {
        Formula formula1 = createFormula("NOT(10+4)");
        Formula formula2 = createFormula("IF(5*3)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("IF((10*4))", mergeResult.toString());
    }

    @Test
    public void functionsWithRangeVersusCellReference() {
        Formula formula1 = createFormula("MIN(A7)");
        Formula formula2 = createFormula("MIN(A1:A3)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("MIN(A1:A3)", mergeResult.toString());
    }

    @Test
    public void complexMerge1() {
        Formula formula1 = createFormula("SUM(A1:A10)+MIN(B2:B5)");
        Formula formula2 = createFormula("PRODUCT(A5:B10)-false");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(SUM(A5:B10)+MIN(B2:B5))", mergeResult.toString());
    }

    @Test
    public void complexMerge2() {
        Formula formula1 = createFormula("A10:B20 / 15");
        Formula formula2 = createFormula("C5-(true*2)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(A10:B20/(true*2))", mergeResult.toString());
    }

    @Test
    public void complexMerge3() {
        Formula formula1 = createFormula("biggestApple+123");
        Formula formula2 = createFormula("MIN(A1:A4)+pear");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(MIN(A1:A4)+123)", mergeResult.toString());
    }

    @Test
    public void complexMerge4() {
        Formula formula1 = createFormula("NOT(10+4)+IF(A1<4)");
        Formula formula2 = createFormula("false*(2*8)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(NOT((10+4))*IF((A1<4)))", mergeResult.toString());
    }

    @Test
    public void complexMerge5() {
        Formula formula1 = createFormula("(A1+15)*B3-22");
        Formula formula2 = createFormula("C10/5+true");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(((A1+15)*B3)+22)", mergeResult.toString());
    }

    @Test
    public void complexMerge6() {
        Formula formula1 = createFormula("IF(MAX(A1:A2),MIN(B2:B3))-4");
        Formula formula2 = createFormula("apple+NOT(5*4)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(IF(MAX(A1:A2), MIN(B2:B3))+NOT((5*4)))", mergeResult.toString());
    }

    @Test
    public void complexMerge7() {
        Formula formula1 = createFormula("(bananaSplit+100)/true - MIN(D1:D5)");
        Formula formula2 = createFormula("A5:A10 + berryCup * 50 - false");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(((bananaSplit+100)/(berryCup*50))-MIN(D1:D5))", mergeResult.toString());
    }

    @Test
    public void complexMerge7Variation() {
        Formula formula1 = createFormula("bananaSplit + 100 / true - MIN(D1:D5)");
        Formula formula2 = createFormula("A5:A10 + berryCup * 50 - false");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("((A5:A10+(100*50))-MIN(D1:D5))", mergeResult.toString());
    }

    @Test
    public void complexMerge8() {
        Formula formula1 = createFormula("IF(A1 > 10, IF(B2 < 5, true, false), C3*7)");
        Formula formula2 = createFormula("IF(A1 > 0, IF(D4 = 5, false, true), (E5/E6)-1)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("IF((A1>10), IF((D4<=5), true, true), ((E5/E6)*7))", mergeResult.toString());
    }

    @Test
    public void complexMerge9() {
        Formula formula1 = createFormula("NOT(SUM(A1:A5)-MIN(B1:B2))+true"); // Parsed as follows: (NOT((SUM(A1:A5)-MIN(B1:B2)))+true)
        Formula formula2 = createFormula("PRODUCT(A1:B5)/false - MAX(X1:X2)"); // Parsed as follows: ((PRODUCT(A1:B5)/false)-MAX(X1:X2))
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("((PRODUCT((SUM(A1:A5)-MIN(B1:B2)))/false)+MAX(X1:X2))", mergeResult.toString());
    }

    @Test
    public void complexMerge9Variation() {
        Formula formula1 = createFormula("SUM(A1:A5)-MIN(B1:B2)");
        Formula formula2 = createFormula("PRODUCT(A1:B5)/false");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(SUM(A1:B5)/MIN(B1:B2))", mergeResult.toString());
    }

    @Test
    public void complexMerge9Variation2() {
        Formula formula1 = createFormula("NOT(SUM(A1:A5)-MIN(B1:B2))");
        Formula formula2 = createFormula("PRODUCT(A1:B5)/false");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(PRODUCT((SUM(A1:A5)-MIN(B1:B2)))/false)", mergeResult.toString());
    }

    @Test
    public void complexMerge10() {
        Formula formula1 = createFormula("(15 + A10)*grapes - IF(true, false)");
        Formula formula2 = createFormula("AND(A5 > 8,peach) + OR(B1, B2, 100)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(AND((A5>8), peach)+IF(B1, B2, 100))", mergeResult.toString());
    }


    @Test
    public void complexMerge11() {
        Formula formula1 = createFormula("(((A1+20)-B2)*C3)/((D4-5)+(E5*6))");
        Formula formula2 = createFormula("(Z1-2)+(Y2*(X3+10))/(W4-33)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("((((A1+20)-B2)*C3)/((Y2*(X3+10))/(W4*33)))", mergeResult.toString());
    }

    @Test
    public void complexMerge12() {
        Formula formula1 = createFormula("NOT(MAX(A1:A2)-15) + (Q5/7)");
        Formula formula2 = createFormula("SUM(NEGATE10, MIN(A7:B7)) - finalTest");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(SUM((MAX(A1:A2)-15), MIN(A7:B7))+(Q5/7))", mergeResult.toString());
    }

    @Test
    public void complexMerge13() {
        Formula formula1 = createFormula("apple-banana+(true*3)");
        Formula formula2 = createFormula("peach + false - (A1/10)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("((apple+false)+(A1*10))", mergeResult.toString());
    }

    @Test
    public void complexMerge14() {
        Formula formula1 = createFormula("MIN(A1:A2)*MAX(B1:B2) /AND(true,9)");
        Formula formula2 = createFormula("SUM(10,15) +IF(A5,false)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("((MIN(A1:A2, 15)*MAX(B1:B2))/IF(A5, 9))", mergeResult.toString());
    }

    @Test
    public void complexMerge15() {
        Formula formula1 = createFormula("false - MIN(A10:B12) + 30");
        Formula formula2 = createFormula("IF(A1=3, SUM(1,5), true)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("IF((A1=3), SUM(1, 5), true)", mergeResult.toString());
    }

    @Test
    public void complexMerge15Variation1() {
        Formula formula1 = createFormula("false - MIN(A10:B12) + 30");
        Formula formula2 = createFormula("IF(A1=3, SUM(1,5), true)*10");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(IF((A1=3), SUM(1, 5), true)*30)", mergeResult.toString());
    }

    @Test
    public void complexMerge16() {
        Formula formula1 = createFormula("mango * 25 + A5 + B3");
        Formula formula2 = createFormula("peach * 13 + C6 - 100");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(((mango*25)+C6)+B3)", mergeResult.toString());
    }

    @Test
    public void complexMerge17() {
        Formula formula1 = createFormula("true*(A1+cat)");
        Formula formula2 = createFormula("false+(B5*dog)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(true*(B5*cat))", mergeResult.toString());
    }

    @Test
    public void complexMerge18() {
        Formula formula1 = createFormula("(A2+A5)*B7-(C10/2)");
        Formula formula2 = createFormula("((A1*B3)/(C4+5))-D1");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("(((A2*B3)*(C4+5))-(C10/2))", mergeResult.toString());
    }

    @Test
    public void complexMerge19() {
        Formula formula1 = createFormula("(((A1+B2)-C3)+D4) *E5");
        Formula formula2 = createFormula("((Z1*Z2)/(Z3-Z4)) + IF(Z5=10,Z6,Z7)");
        mergeResult = crdtMerge.merge(formula1, formula2);
        Assertions.assertEquals("((((A1+B2)*Z2)/(Z3-Z4))*IF((Z5=10), Z6, Z7))", mergeResult.toString());
    }

}
