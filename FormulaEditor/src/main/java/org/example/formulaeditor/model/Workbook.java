package org.example.formulaeditor.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Collection;

public class Workbook {
    private final ObservableMap<String, Formula> formulas;

    public Workbook() {
        this.formulas = FXCollections.observableHashMap();
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

    public void removeFormula(String id) {
        formulas.remove(id);
    }

    public ObservableMap<String, Formula> getFormulasMap() {
        return formulas;
    }

    //check if formula exists in workbook
    public boolean containsFormula(String id) {
        return formulas.containsKey(id);
    }

    //update workbook with merged formulas
    public void updateFrom(Workbook other) {
        this.formulas.clear();
        this.formulas.putAll(other.getFormulasMap());
    }

    //TODO Cell handling
}
