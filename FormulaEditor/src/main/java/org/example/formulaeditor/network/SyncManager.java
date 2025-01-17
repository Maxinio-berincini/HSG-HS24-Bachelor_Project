package org.example.formulaeditor.network;

import org.example.formulaeditor.crdt.CRDTMerge;
import org.example.formulaeditor.crdt.CRDTRules;
import org.example.formulaeditor.model.Workbook;

public class SyncManager {
    private static SyncManager instance = null;

    private SyncManager() {
    }

    public static synchronized SyncManager getInstance() {
        if (instance == null) {
            instance = new SyncManager();
        }
        return instance;
    }

    public synchronized void merge(Workbook localWorkbook, Workbook remoteWorkbook) {
        CRDTRules rules = new CRDTRules();
        CRDTMerge merger = new CRDTMerge(rules);

        Workbook mergedWorkbook = merger.merge(localWorkbook, remoteWorkbook);

        localWorkbook.updateFrom(mergedWorkbook);
    }
}
