package org.example.formulaeditor.network;

import org.example.formulaeditor.model.Formula;
import org.example.formulaeditor.model.VersionVector;
import org.example.formulaeditor.model.Workbook;
import org.example.formulaeditor.parser.ast.ASTNode;
import org.example.formulaeditor.parser.ast.ASTSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NetworkSerializer {

    public static WorkbookSyncDTO toSyncDTO(Workbook workbook) {
        List<FormulaSyncDTO> formulaSyncList = new ArrayList<>();

        for (Formula formula : workbook.getFormulas()) {
            // Get version vector map
            Map<String, Integer> versionMap = formula.getVersionVector().getVersions();
            // Get serialized formula ast
            String serializedAstJson = ASTSerializer.astToJsonString(formula.getAst());

            FormulaSyncDTO dto = new FormulaSyncDTO(
                    formula.getId(),
                    serializedAstJson,
                    versionMap
            );
            formulaSyncList.add(dto);
        }

        return new WorkbookSyncDTO(formulaSyncList);
    }

    // Convert DTO back to Object
    public static Workbook fromSyncDTO(WorkbookSyncDTO dto) throws Exception {
        Workbook wb = new Workbook();

        if (dto.getFormulas() == null) {
            return wb;
        }

        for (FormulaSyncDTO fDTO : dto.getFormulas()) {
            String cellId = fDTO.getCellId();
            String astJsonString = fDTO.getFormulaString();
            Map<String, Integer> versionMap = fDTO.getVersionVector();

            // Parse json back to ast
            ASTNode ast = ASTSerializer.astFromJsonString(astJsonString);

            // Recreate version vector object
            VersionVector vv = new VersionVector(versionMap);

            // Recreate formula object
            Formula formula = new Formula(cellId, ast, vv);

            wb.addFormula(formula);
        }
        return wb;
    }
}
