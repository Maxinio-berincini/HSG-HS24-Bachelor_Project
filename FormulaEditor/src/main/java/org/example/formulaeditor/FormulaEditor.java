package org.example.formulaeditor;

import org.example.formulaeditor.model.Formula;
import org.example.formulaeditor.model.VersionVector;
import org.example.formulaeditor.model.Workbook;
import org.example.formulaeditor.parser.Parser;
import org.example.formulaeditor.parser.ast.ASTNode;

import java.util.HashMap;

public class FormulaEditor {
    private final Parser parser;
    private final Workbook workbook;
    private final String instanceId;

    public FormulaEditor(String instanceId) {
        this.parser = new Parser();
        this.workbook = new Workbook();
        this.instanceId = instanceId;
    }

    public String addFormula(String id, String input) throws Exception {
        ASTNode ast = parser.parse(input);
        VersionVector versionVector = new VersionVector(new HashMap<String, Integer>() {{
            put(instanceId, 1);
        }}  );
        Formula formula = new Formula(id, ast, versionVector);
        workbook.addFormula(formula);
        return formula.toString();
    }

    public String updateFormula(String id, String input) throws Exception {
        // Overwrite the existing formula
        ASTNode ast = parser.parse(input);
        Formula existingFormula = workbook.getFormula(id);

        VersionVector versionVector;
        if (existingFormula != null) {
            versionVector = existingFormula.getVersionVector();
        } else {
            versionVector = new VersionVector();
        }
        versionVector.increment(instanceId);

        Formula formula = new Formula(id, ast, versionVector);
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
