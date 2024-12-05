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

import java.lang.reflect.Field;
import java.util.*;

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
        Formula formula1 = createFormula("SUM(5,2)", "Instance1", 3);
        Formula formula2 = createFormula("SUM(1,8)", "Instance2", 1);
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

    @Test
    // VersionVector should only increment once per cycle
    public void versionVectorOnlyIncrementsOnce() {
        VersionVector versionVector = new VersionVector();
        versionVector.increment("Instance1");
        versionVector.increment("Instance1");
        versionVector.increment("Instance1");
        Assertions.assertEquals(1, versionVector.getVersion("Instance1"));
    }

    @Test
    public void testIsNewerVersionWithEqualVectors() {
        Map<String, Integer> versions1 = new HashMap<>();
        versions1.put("InstanceA", 1);
        VersionVector vector1 = new VersionVector(versions1);

        Map<String, Integer> versions2 = new HashMap<>();
        versions2.put("InstanceA", 1);
        VersionVector vector2 = new VersionVector(versions2);

        Assertions.assertFalse(vector1.isNewerVersion(vector2));
    }

    @Test
    public void testIncrementMultipleInstanceIds() {
        VersionVector versionVector1 = new VersionVector();
        VersionVector versionVector2 = new VersionVector();
        versionVector1.increment("InstanceA");
        versionVector2.increment("InstanceB");
        Assertions.assertEquals(1, versionVector1.getVersion("InstanceA"));
        Assertions.assertEquals(1, versionVector2.getVersion("InstanceB"));
    }

    @Test
    public void notIncrementingAVector() {
        VersionVector versionVector1 = new VersionVector();
        Assertions.assertEquals(0, versionVector1.getVersion("Instance1"));
    }

    @Test
    public void testGetVersionWithNonExistentInstanceId() {
        VersionVector versionVector = new VersionVector();
        Assertions.assertEquals(0, versionVector.getVersion("UnknownInstance"));
    }

    @Test
    public void testIsNewerVersionWithGreaterVector() {
        Map<String, Integer> versions1 = new HashMap<>();
        versions1.put("InstanceA", 2);
        VersionVector vector1 = new VersionVector(versions1);

        Map<String, Integer> versions2 = new HashMap<>();
        versions2.put("InstanceB", 1);
        VersionVector vector2 = new VersionVector(versions2);

        Assertions.assertTrue(vector1.isNewerVersion(vector2)); // TODO why is this false?
    }

    @Test
    public void testIsNewerVersionWithDifferentInstanceIds() {
        Map<String, Integer> versions1 = new HashMap<>();
        versions1.put("InstanceA", 1);
        VersionVector vector1 = new VersionVector(versions1);

        Map<String, Integer> versions2 = new HashMap<>();
        versions2.put("InstanceB", 1);
        VersionVector vector2 = new VersionVector(versions2);

        // They are the same "age" because the other instance in each case is 0
        Assertions.assertFalse(vector1.isNewerVersion(vector2));
    }

    @Test
    public void mergeWithEmptyVector() {
        Map<String, Integer> versions = new HashMap<>();
        versions.put("InstanceA", 1);
        VersionVector vector1 = new VersionVector(versions);
        VersionVector vector2 = new VersionVector();

        vector1.merge(vector2);
        Assertions.assertEquals(1, vector1.getVersion("InstanceA")); // Merging with an empty vector does not increment versions
        Assertions.assertEquals(0, vector2.getVersion("InstanceA"));
    }

    @Test
    public void testMergeWithDifferentInstanceIds() {
        Map<String, Integer> versions1 = new HashMap<>();
        versions1.put("InstanceA", 1);
        VersionVector vector1 = new VersionVector(versions1);

        Map<String, Integer> versions2 = new HashMap<>();
        versions2.put("InstanceB", 2);
        VersionVector vector2 = new VersionVector(versions2);

        vector1.merge(vector2);

        Assertions.assertEquals(1, vector1.getVersion("InstanceA"));
        Assertions.assertEquals(2, vector1.getVersion("InstanceB"));
        Assertions.assertEquals(0, vector2.getVersion("InstanceA"));
        Assertions.assertEquals(2, vector2.getVersion("InstanceB"));
    }

    @Test
    public void testMergeWithOverlappingInstanceIds() {
        Map<String, Integer> versions1 = new HashMap<>();
        versions1.put("InstanceA", 2);
        versions1.put("InstanceB", 1);
        VersionVector vector1 = new VersionVector(versions1);

        Map<String, Integer> versions2 = new HashMap<>();
        versions2.put("InstanceA", 1);
        versions2.put("InstanceB", 3);
        VersionVector vector2 = new VersionVector(versions2);

        vector1.merge(vector2);

        // Also test the increment feature here
        vector1.increment("InstanceB");
        vector1.increment("InstanceB"); // Check that double incrementing doesn't work
        vector2.increment("InstanceA");

        Assertions.assertEquals(2, vector1.getVersion("InstanceA"));
        Assertions.assertEquals(4, vector1.getVersion("InstanceB"));
        Assertions.assertEquals(2, vector2.getVersion("InstanceA"));
        Assertions.assertEquals(3, vector2.getVersion("InstanceB"));
    }

    @Test
    public void testMergeResetsFlag() throws NoSuchFieldException, IllegalAccessException {
        VersionVector vector1 = new VersionVector();
        vector1.increment("InstanceA");

        VersionVector vector2 = new VersionVector();
        vector2.increment("InstanceA");

        vector1.merge(vector2);

        Field editedField = VersionVector.class.getDeclaredField("edited");
        editedField.setAccessible(true);
        boolean edited = (boolean) editedField.get(vector1);

        Assertions.assertFalse(edited);
    }

    @Test
    public void testGetInstances() {
        Map<String, Integer> versions = new HashMap<>();
        versions.put("InstanceA", 1);
        versions.put("InstanceB", 2);
        VersionVector versionVector = new VersionVector(versions);

        Set<String> instances = versionVector.getInstances();
        Set<String> expectedInstances = new HashSet<>(Arrays.asList("InstanceA", "InstanceB"));

        Assertions.assertEquals(expectedInstances, instances);
    }

    @Test
    public void testToString() {
        Map<String, Integer> versions = new HashMap<>();
        versions.put("InstanceA", 1);
        versions.put("InstanceB", 2);
        VersionVector versionVector = new VersionVector(versions);

        String expectedString = "{InstanceA=1, InstanceB=2}";
        Assertions.assertEquals(expectedString, versionVector.toString());
    }

    @Test
    public void testConstructorWithEmptyMap() {
        Map<String, Integer> emptyMap = new HashMap<>();
        VersionVector versionVector = new VersionVector(emptyMap);
        Assertions.assertTrue(versionVector.getInstances().isEmpty());
    }

    @Test
    public void testIncrementAfterMerge() {
        VersionVector vector1 = new VersionVector();
        vector1.increment("InstanceA");

        VersionVector vector2 = new VersionVector();
        vector2.increment("InstanceB");

        vector1.merge(vector2);

        vector1.increment("InstanceA");

        Assertions.assertEquals(2, vector1.getVersion("InstanceA"));
        Assertions.assertEquals(1, vector1.getVersion("InstanceB"));
        Assertions.assertEquals(0, vector2.getVersion("InstanceA"));
        Assertions.assertEquals(1, vector2.getVersion("InstanceB"));
    }

    @Test
    public void testIncrementAfterMerge2() {
        VersionVector vector1 = new VersionVector();
        vector1.increment("InstanceA");

        VersionVector vector2 = new VersionVector();
        vector2.increment("InstanceB");

        vector2.merge(vector1);

        vector1.increment("InstanceA"); // Increments again here

        Assertions.assertEquals(2, vector1.getVersion("InstanceA")); // TODO It says this should be 1
        Assertions.assertEquals(0, vector1.getVersion("InstanceB"));
        Assertions.assertEquals(1, vector2.getVersion("InstanceA"));
        Assertions.assertEquals(1, vector2.getVersion("InstanceB"));
    }

    @Test
    public void testIncrementMultipleInstanceIds2() {
        VersionVector versionVector = new VersionVector();
        versionVector.increment("InstanceA");
        versionVector.increment("InstanceB");
        System.out.println(versionVector);
        Assertions.assertEquals(1, versionVector.getVersion("InstanceA"));
        Assertions.assertEquals(0, versionVector.getVersion("InstanceB")); // TODO why is this not also 1?
    }

    @Test
    public void testMergeWithOverlappingInstanceIds2() {
        Map<String, Integer> versions1 = new HashMap<>();
        versions1.put("InstanceA", 0);
        versions1.put("InstanceB", 0);
        VersionVector versionVector = new VersionVector(versions1);
        versionVector.increment("InstanceA");
        versionVector.increment("InstanceB");

        Assertions.assertEquals(1, versionVector.getVersion("InstanceA"));
        Assertions.assertEquals(0, versionVector.getVersion("InstanceB")); // TODO why is this not also 1?
    }

}
