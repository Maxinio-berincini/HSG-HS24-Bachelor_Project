package org.example.formulaeditor.ui;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

public class WorkbookUI extends BorderPane {
    private final TableView<ObservableList<StringProperty>> tableView;

    public WorkbookUI() {
        this.tableView = new TableView<>();
        initializeUI();
    }

    private void initializeUI() {

        this.setCenter(tableView);
    }

}
