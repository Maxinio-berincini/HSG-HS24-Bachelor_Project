package org.example.formulaeditor.network;

import org.example.formulaeditor.model.Formula;
import org.example.formulaeditor.model.VersionVector;
import org.example.formulaeditor.model.Workbook;
import org.example.formulaeditor.parser.Parser;
import org.example.formulaeditor.parser.ast.ASTNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class NetworkSerializer {


    public static WorkbookSyncDTO toSyncDTO(Workbook workbook) {
        List<FormulaSyncDTO> formulaSyncList = new ArrayList<>();

        for (Formula formula : workbook.getFormulas()) {
            //get version vector map
            Map<String, Integer> versionMap = formula.getVersionVector().getVersions();
            //get formula strings
            String formulaString = formula.toString();

            FormulaSyncDTO dto = new FormulaSyncDTO(
                    formula.getId(),
                    formulaString,
                    versionMap
            );
            formulaSyncList.add(dto);
        }

        return new WorkbookSyncDTO(formulaSyncList);
    }


    //convert DTO back to Object
    public static Workbook fromSyncDTO(WorkbookSyncDTO dto, Parser parser) throws Exception {
        Workbook wb = new Workbook();

        if (dto.getFormulas() == null) {
            return wb;
        }

        for (FormulaSyncDTO fDTO : dto.getFormulas()) {
            String cellId = fDTO.getCellId();
            String formulaString = fDTO.getFormulaString();
            Map<String, Integer> versionMap = fDTO.getVersionVector();

            //parse strings to ast
            ASTNode ast = parser.parse(formulaString);

            //recreate version vector object
            VersionVector vv = new VersionVector(versionMap);

            //recreate formula object
            Formula formula = new Formula(cellId, ast, vv);

            wb.addFormula(formula);
        }

        return wb;
    }
}
