package org.example.formulaeditor;

import org.example.formulaeditor.crdt.CRDTMerge;
import org.example.formulaeditor.crdt.CRDTRules;
import org.example.formulaeditor.model.Formula;
import org.example.formulaeditor.model.Workbook;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        String instanceId = "instance1";
        String instanceId2 = "instance2";

        // Create two editors
        FormulaEditor editor1 = new FormulaEditor(instanceId);
        FormulaEditor editor2 = new FormulaEditor(instanceId2);


        // Add formulas to editor1's workbook
        //editor1.addFormula("A1", "5 + 6");
        //editor1.addFormula("A2", "SUM(C5:C10)");
        //editor1.addFormula("A3", "5");
        editor1.addFormula("B5", "SUM(Min(2,7),Product(5+8,4))");
        /*
        System.out.println("Enter formulas for Editor 1. Type 'done' when finished.");
        addFormulasFromInput(scanner, editor1);
         */


        // Add formulas to editor2's workbook
        //editor2.addFormula("A1", "SUM(A2:A10)");
        //editor2.addFormula("A1", "4 + 8");
        //editor2.addFormula("A2", "SUM(A1:B17)");
        editor2.addFormula("B5", "SUM(Max(8,7),Sum(2-10,7))");
        //editor2.addFormula("A3", "10");
        /*
        System.out.println("Enter formulas for Editor 2. Type 'done' when finished.");
        addFormulasFromInput(scanner, editor2);
        */


        // Merge the two workbooks
        CRDTRules rules = new CRDTRules();
        CRDTMerge merger = new CRDTMerge(rules);

        Workbook mergedWorkbook = merger.merge(editor1.getWorkbook(), editor2.getWorkbook());

        // Display result
        System.out.println("Merged Workbook Formulas:");
        for (Formula formula : mergedWorkbook.getFormulas()) {
            System.out.println("Cell " + formula.getId() + ": " + formula);
        }
    }

    private static void addFormulasFromInput(Scanner scanner, FormulaEditor editor) throws Exception {
        while (true) {
            System.out.print("Enter cell ID (e.g., A1): ");
            String cellId = scanner.nextLine();

            if (cellId.equalsIgnoreCase("done")) {
                break;
            }

            System.out.print("Enter formula (e.g., SUM(A2 + B2) ): ");
            String formula = scanner.nextLine();

            editor.addFormula(cellId, formula);
            System.out.println("Formula added: " + cellId + " = " + formula);
        }
    }
}
