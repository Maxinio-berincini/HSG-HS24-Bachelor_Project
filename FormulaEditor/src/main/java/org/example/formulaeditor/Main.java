package org.example.formulaeditor;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        FormulaEditor editor = new FormulaEditor();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter formulas (type 'exit' to finish):");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) break;
            try {
                String result = editor.addFormula(input);
                System.out.println("Parsed formula: " + result);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
