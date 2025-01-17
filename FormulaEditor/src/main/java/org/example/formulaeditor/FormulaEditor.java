package org.example.formulaeditor;

import org.example.formulaeditor.model.Formula;
import org.example.formulaeditor.model.VersionVector;
import org.example.formulaeditor.model.Workbook;
import org.example.formulaeditor.parser.Parser;
import org.example.formulaeditor.parser.ast.ASTDifference;
import org.example.formulaeditor.parser.ast.ASTNode;
import org.example.formulaeditor.parser.ast.ASTPrinter;

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
        }});
        Formula formula = new Formula(id, ast, versionVector);
        workbook.addFormula(formula);
        return formula.toString();
    }

    public String updateFormula(String id, String input) throws Exception {
        // Overwrite the existing formula
        ASTNode ast = parser.parse(input);
        Formula existingFormula = workbook.getFormula(id);

        ASTPrinter.printFormulaDetails(existingFormula);

        VersionVector versionVector;
        if (existingFormula == null) {
            versionVector = new VersionVector();
            versionVector.increment(instanceId);

            Formula formula = new Formula(id, ast, versionVector);
            workbook.addFormula(formula);
            return formula.toString();
        } else {
            // Merge node by node on update
            ASTNode oldAST = existingFormula.getAst();
            versionVector = existingFormula.getVersionVector();

            ASTDifference differ = new ASTDifference();
            ASTNode mergedAST = differ.mergeUpdatedAST(oldAST, ast);

            versionVector.increment(instanceId);

            Formula mergedFormula = new Formula(id, mergedAST, versionVector);

            workbook.addFormula(mergedFormula);

            ASTPrinter.printFormulaDetails(mergedFormula);

            return mergedFormula.toString();
        }
    }

    public void deleteFormula(String id) {
        workbook.removeFormula(id);
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    //TODO add more UI methods (for possible future Helix expansion)
}
