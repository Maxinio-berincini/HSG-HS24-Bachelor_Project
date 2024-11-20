package org.example.formulaeditor.network;

import org.example.formulaeditor.crdt.CRDTMerge;
import org.example.formulaeditor.crdt.CRDTRules;
import org.example.formulaeditor.model.Workbook;

import java.util.ArrayList;
import java.util.List;

public class SyncManager {
    private static SyncManager instance = null;
    private final List<Workbook> workbooks;

    private SyncManager() {
        workbooks = new ArrayList<>();
    }

    public static synchronized SyncManager getInstance() {
        if (instance == null) {
            instance = new SyncManager();
        }
        return instance;
    }

    public synchronized void registerWorkbook(Workbook workbook) {
        if (!workbooks.contains(workbook)) {
            workbooks.add(workbook);
        }
    }

    public synchronized void unregisterWorkbook(Workbook workbook) {
        workbooks.remove(workbook);
    }

    public synchronized void synchronize(Workbook workbook) {
        CRDTRules rules = new CRDTRules();
        CRDTMerge merger = new CRDTMerge(rules);

        for (Workbook otherWorkbook : workbooks) {
            if (otherWorkbook != workbook) {
                // merge the two workbooks
                Workbook merged = merger.merge(workbook, otherWorkbook);

                // ppdate both workbooks
                workbook.updateFrom(merged);
                otherWorkbook.updateFrom(merged);
            }
        }
    }
}
