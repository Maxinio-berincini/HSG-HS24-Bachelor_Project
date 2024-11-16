package org.example.formulaeditor;

import org.example.formulaeditor.crdt.CRDTMerge;
import org.example.formulaeditor.crdt.CRDTRules;
import org.example.formulaeditor.model.Formula;
import org.example.formulaeditor.model.Workbook;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        // Create two editors
        FormulaEditor editor1 = new FormulaEditor();
        FormulaEditor editor2 = new FormulaEditor();


        // Add formulas to editor1's workbook
        editor1.addFormula("A1", "5 + 6");
        editor1.addFormula("A2", "SUM(2,2)");
        editor1.addFormula("A3", "5");
        /*
        System.out.println("Enter formulas for Editor 1. Type 'done' when finished.");
        addFormulasFromInput(scanner, editor1);
         */


        // Add formulas to editor2's workbook
        //editor2.addFormula("A1", "SUM(A2:A10)");
        editor2.addFormula("A1", "4 + 8");
        editor2.addFormula("A2", "SUM(1,4)");
        editor2.addFormula("A3", "10");
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
