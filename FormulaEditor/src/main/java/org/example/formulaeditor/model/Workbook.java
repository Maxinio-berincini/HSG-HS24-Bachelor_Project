package org.example.formulaeditor.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Workbook {
    private Map<String, Formula> formulas;

    public Workbook() {
        this.formulas = new HashMap<>();
    }

    public void addFormula(Formula formula) {
        formulas.put(formula.getId(), formula);
    }

    public Formula getFormula(String id) {
        return formulas.get(id);
    }

    public Collection<Formula> getFormulas() {
        return formulas.values();
    }

    public Map<String, Formula> getFormulasMap() {
        return formulas;
    }

    //check if formula exists in workbook
    public boolean containsFormula(String id) {
        return formulas.containsKey(id);
    }

    //TODO Cell handling
}
