package org.example.formulaeditor.crdt;

import org.example.formulaeditor.model.Formula;
import org.example.formulaeditor.model.Workbook;

import java.util.Random;

public class CRDTRules {

    private Random random = new Random();

    public Formula applyRules(Formula local, Formula remote) {
        //TODO merge logic

        // Randomly choose between local and remote
        if (random.nextBoolean()) {
            return local;
        } else {
            return remote;
        }
    }

    public Workbook applyRules(Workbook local, Workbook remote) {
        Workbook mergedWorkbook = new Workbook();

        // Merge formulas
        for (String id : local.getFormulasMap().keySet()) {
            Formula localFormula = local.getFormula(id);
            if (remote.containsFormula(id)) {
                // Formula exists in both, merge them
                Formula remoteFormula = remote.getFormula(id);
                Formula mergedFormula = applyRules(localFormula, remoteFormula);
                mergedWorkbook.addFormula(mergedFormula);
            } else {
                // Only in local
                mergedWorkbook.addFormula(localFormula);
            }
        }

        // Add formulas that are only in remote
        for (String id : remote.getFormulasMap().keySet()) {
            if (!local.containsFormula(id)) {
                mergedWorkbook.addFormula(remote.getFormula(id));
            }
        }

        return mergedWorkbook;
    }
}
