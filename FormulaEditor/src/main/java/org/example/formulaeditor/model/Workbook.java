package org.example.formulaeditor.model;

import java.util.ArrayList;
import java.util.List;

public class Workbook {
    private List<Formula> formulas;

    public Workbook() {
        this.formulas = new ArrayList<>();
    }

    public void addFormula(Formula formula) {
        formulas.add(formula);
    }

    public List<Formula> getFormulas() {
        return formulas;
    }

    //TODO Cell handling
}
