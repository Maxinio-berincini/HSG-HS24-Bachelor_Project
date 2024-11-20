package org.example.formulaeditor;

import org.example.formulaeditor.model.Formula;
import org.example.formulaeditor.model.Workbook;
import org.example.formulaeditor.parser.Parser;
import org.example.formulaeditor.parser.ast.ASTNode;

public class FormulaEditor {
    private final Parser parser;
    private final Workbook workbook;

    public FormulaEditor() {
        this.parser = new Parser();
        this.workbook = new Workbook();
    }

    public String addFormula(String id, String input) throws Exception {
        ASTNode ast = parser.parse(input);
        Formula formula = new Formula(id, ast);
        workbook.addFormula(formula);
        return formula.toString();
    }

    public String updateFormula(String id, String input) throws Exception {
        // remove existing formula
        workbook.removeFormula(id);
        // Overwrite the existing formula
        ASTNode ast = parser.parse(input);
        Formula formula = new Formula(id, ast);
        workbook.addFormula(formula);
        return formula.toString();
    }

    public void deleteFormula(String id) {
        workbook.removeFormula(id);
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    //TODO add more UI methods
}
