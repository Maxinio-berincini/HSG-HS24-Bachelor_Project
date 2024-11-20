package org.example.formulaeditor.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.DefaultStringConverter;
import org.example.formulaeditor.FormulaEditor;
import org.example.formulaeditor.model.Formula;
import org.example.formulaeditor.model.Workbook;

public class WorkbookUI extends BorderPane {
    private final FormulaEditor formulaEditor;
    private final TableView<ObservableList<StringProperty>> tableView;

    private final int numRows = 6;
    private final int numColumns = 6;

    public WorkbookUI(FormulaEditor formulaEditor) {
        this.formulaEditor = formulaEditor;
        this.tableView = new TableView<>();
        initializeUI();
    }

    private void initializeUI() {
        tableView.setEditable(true);

        // Create first column with row numbers
        TableColumn<ObservableList<StringProperty>, String> rowNumberCol = getRowNumberColumn();

        // Add the row number column at the beginning
        tableView.getColumns().add(rowNumberCol);

        // Create columns for each letter (A-Z)
        for (int colIndex = 0; colIndex < numColumns; colIndex++) {
            TableColumn<ObservableList<StringProperty>, String> column = getColumn(colIndex);
            tableView.getColumns().add(column);
        }

        // Adjust the row height
        tableView.setRowFactory(tv -> {
            TableRow<ObservableList<StringProperty>> row = new TableRow<>();
            row.setPrefHeight(30);
            return row;
        });

        // Populate rows
        ObservableList<ObservableList<StringProperty>> data = FXCollections.observableArrayList();

        for (int rowIndex = 1; rowIndex <= numRows; rowIndex++) {
            ObservableList<StringProperty> row = FXCollections.observableArrayList();
            for (int colIndex = 0; colIndex < numColumns; colIndex++) {
                String cellId = getCellId(colIndex, rowIndex);
                String cellValue = getCellDisplayValue(cellId);
                row.add(new SimpleStringProperty(cellValue));
            }
            data.add(row);
        }

        tableView.setItems(data);
        this.setCenter(tableView);
    }
    private TableColumn<ObservableList<StringProperty>, String> getColumn(int colIndex) {
        final int colIdx = colIndex;
        char colChar = (char) ('A' + colIndex);
        TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>(String.valueOf(colChar));

        column.setCellValueFactory(param -> {
            ObservableList<StringProperty> row = param.getValue();
            if (colIdx < row.size()) {
                return row.get(colIdx);
            } else {
                return new SimpleStringProperty("");
            }
        });

        column.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));

        column.setOnEditCommit(event -> {
            int rowIndex = event.getTablePosition().getRow() + 1;
            char columnChar = (char) ('A' + event.getTablePosition().getColumn() - 1);
            String cellId = "" + columnChar + rowIndex;

            String newValue = event.getNewValue();
            handleCellEdit(cellId, newValue);

            event.getRowValue().get(colIdx).set(newValue);
        });

        column.setPrefWidth(100);
        column.setSortable(false);
        column.setReorderable(false);
        return column;
    }

    private TableColumn<ObservableList<StringProperty>, String> getRowNumberColumn() {
        TableColumn<ObservableList<StringProperty>, String> rowNumberCol = new TableColumn<>(" ");
        rowNumberCol.setPrefWidth(50);

        rowNumberCol.setCellFactory(column -> new TableCell<ObservableList<StringProperty>, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getIndex() < 0) {
                    setText(null);
                } else {
                    int rowIndex = getTableRow().getIndex();
                    setText(Integer.toString(rowIndex + 1));
                }
            }
        });
        rowNumberCol.setEditable(false);
        rowNumberCol.setSortable(false);
        rowNumberCol.setReorderable(false);
        return rowNumberCol;
    }

    private String getCellId(int colIndex, int rowIndex) {
        char colChar = (char) ('A' + colIndex);
        return "" + colChar + rowIndex;
    }

    private void handleCellEdit(String cellId, String input) {

    }

    private String getCellDisplayValue(String cellId) {
        Workbook workbook = formulaEditor.getWorkbook();
        Formula formula = workbook.getFormula(cellId);
        if (formula != null) {
            // Return the formula
            // TODO: Display the evaluated result instead of the formula string
            return formula.toString();
        } else {
            return "";
        }
    }


}
