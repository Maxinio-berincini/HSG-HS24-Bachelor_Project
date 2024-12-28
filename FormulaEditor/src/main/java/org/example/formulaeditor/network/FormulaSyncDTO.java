package org.example.formulaeditor.network;

import java.util.Map;

public class FormulaSyncDTO {
    private String cellId;
    private String formulaString;
    private Map<String, Integer> versionVector;

    public FormulaSyncDTO() {
    }

    public FormulaSyncDTO(String cellId, String formulaString, Map<String, Integer> versionVector) {
        this.cellId = cellId;
        this.formulaString = formulaString;
        this.versionVector = versionVector;
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public String getFormulaString() {
        return formulaString;
    }

    public void setFormulaString(String formulaString) {
        this.formulaString = formulaString;
    }

    public Map<String, Integer> getVersionVector() {
        return versionVector;
    }

    public void setVersionVector(Map<String, Integer> versionVector) {
        this.versionVector = versionVector;
    }
}
