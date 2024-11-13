package org.example.formulaeditor;

import org.example.formulaeditor.model.Formula;
import org.example.formulaeditor.model.Workbook;
import org.example.formulaeditor.parser.Parser;
import org.example.formulaeditor.parser.ast.ASTNode;

public class FormulaEditor {
    private Parser parser;
    private Workbook workbook;

    public FormulaEditor() {
        this.parser = new Parser();
        this.workbook = new Workbook();
    }

    public String addFormula(String input) throws Exception {
        ASTNode ast = parser.parse(input);
        Formula formula = new Formula(ast);
        workbook.addFormula(formula);
        return formula.toString();
    }

    //TODO add more UI methods
}
