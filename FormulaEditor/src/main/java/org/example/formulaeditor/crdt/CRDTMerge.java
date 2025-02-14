package org.example.formulaeditor.crdt;

import org.example.formulaeditor.model.Formula;
import org.example.formulaeditor.model.Workbook;

public class CRDTMerge {
    private final CRDTRules rules;

    public CRDTMerge(CRDTRules rules) {
        this.rules = rules;
    }

    public Workbook merge(Workbook local, Workbook remote) {
        return rules.applyRules(local, remote);
    }

    /// Merge two Formulas separately
    public Formula merge(Formula local, Formula remote) {
        return rules.applyRules(local, remote);
    }
}
