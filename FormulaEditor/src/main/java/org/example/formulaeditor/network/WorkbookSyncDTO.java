package org.example.formulaeditor.network;

import java.util.ArrayList;
import java.util.List;

public class WorkbookSyncDTO {
    private List<FormulaSyncDTO> formulas;

    public WorkbookSyncDTO() {
        this.formulas = new ArrayList<>();
    }

    public WorkbookSyncDTO(List<FormulaSyncDTO> formulas) {
        this.formulas = formulas;
    }

    public List<FormulaSyncDTO> getFormulas() {
        return formulas;
    }

    public void setFormulas(List<FormulaSyncDTO> formulas) {
        this.formulas = formulas;
    }
}
