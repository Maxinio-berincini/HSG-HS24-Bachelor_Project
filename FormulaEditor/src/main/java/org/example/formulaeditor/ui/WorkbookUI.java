package org.example.formulaeditor.ui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.WindowEvent;
import javafx.util.converter.DefaultStringConverter;
import org.example.formulaeditor.FormulaEditor;
import org.example.formulaeditor.model.Formula;
import org.example.formulaeditor.model.Workbook;
import org.example.formulaeditor.network.SyncManager;

public class WorkbookUI extends BorderPane {
    private final FormulaEditor formulaEditor;
    private final TableView<ObservableList<StringProperty>> tableView;
    private TextField formulaInputField;
    private Label headerLabel;
    private Button syncButton;
    private String selectedCellId;

    private final int numRows = 6;
    private final int numColumns = 6;

    public WorkbookUI(FormulaEditor formulaEditor) {
        this.formulaEditor = formulaEditor;
        this.tableView = new TableView<>();
        initializeUI();

        // Register the workbook with SyncManager
        SyncManager.getInstance().registerWorkbook(formulaEditor.getWorkbook());

        // Add listener to observe changes in the formulas map
        formulaEditor.getWorkbook().getFormulasMap().addListener((MapChangeListener<String, Formula>) change -> {
            if (change.wasAdded()) {
                System.out.println("Formula added at cell: " + change.getKey());
            } else if (change.wasRemoved()) {
                System.out.println("Formula removed from cell: " + change.getKey());
            }
            refreshTableView();
        });

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

        // Header section and input field
        headerLabel = new Label("Collaborative Formula Editor");
        headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        syncButton = new Button("Sync");
        syncButton.getStyleClass().add("sync-button");
        syncButton.setOnAction(event -> handleSyncButton());

        HBox titleBar = new HBox(10);
        titleBar.getChildren().addAll(headerLabel, syncButton);

        titleBar.setAlignment(Pos.CENTER);
        HBox.setHgrow(headerLabel, Priority.ALWAYS);

        formulaInputField = new TextField();
        formulaInputField.setPromptText("Select a cell");

        // Handle input field edits
        formulaInputField.setOnAction(event -> handleFormulaInputFieldEdit());

        VBox topSection = new VBox();
        topSection.getChildren().addAll(titleBar, formulaInputField);

        this.setTop(topSection);
        this.setCenter(tableView);
        // Use tableview.css for CSS styles
        this.getStylesheets().add(getClass().getResource("/tableview.css").toExternalForm());


        tableView.getSelectionModel().setCellSelectionEnabled(true);
        ObservableList<TablePosition> selectedCells = tableView.getSelectionModel().getSelectedCells();
        selectedCells.addListener((ListChangeListener<TablePosition>) change -> updateFormulaInputField());

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
            // Update formula input field if on cell select
            TablePosition selectedPos = tableView.getSelectionModel().getSelectedCells().isEmpty() ? null
                    : tableView.getSelectionModel().getSelectedCells().get(0);
            if (selectedPos != null && selectedPos.getRow() == event.getTablePosition().getRow()
                    && selectedPos.getColumn() == event.getTablePosition().getColumn()) {
                updateFormulaInputField();
            }
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
        try {
            Workbook workbook = formulaEditor.getWorkbook();
            boolean formulaExists = workbook.containsFormula(cellId);

            if (input == null || input.trim().isEmpty()) {
                if (formulaExists) {
                    // If input is empty and formula exists, delete formula in workbook
                    formulaEditor.deleteFormula(cellId);
                }
                // If input is empty and no formula exists, do nothing
            } else {
                if (formulaExists) {
                    // If formula exists, update it
                    formulaEditor.updateFormula(cellId, input);
                    refreshTableView();
                } else {
                    // If no formula exists, add it
                    formulaEditor.addFormula(cellId, input);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle parsing errors (e.g., show an error message)
            showErrorDialog("Error", "Failed to parse the formula: " + e.getMessage());
        }
    }

    private void updateFormulaInputField() {
        Platform.runLater(() -> {
            TablePosition pos = tableView.getSelectionModel().getSelectedCells().isEmpty() ? null
                    : tableView.getSelectionModel().getSelectedCells().get(0);
            if (pos != null) {
                int rowIndex = pos.getRow() + 1;
                int colIndex = pos.getColumn() - 1;
                if (colIndex >= 0) {
                    String cellId = getCellId(colIndex, rowIndex);
                    selectedCellId = cellId;
                    String cellFormula = getCellFormula(cellId);
                    formulaInputField.setText(cellFormula);
                } else {
                    formulaInputField.setText("");
                    selectedCellId = null;
                }
            } else {
                formulaInputField.setText("");
                selectedCellId = null;
            }
        });
    }

    private String getCellFormula(String cellId) {
        Workbook workbook = formulaEditor.getWorkbook();
        Formula formula = workbook.getFormula(cellId);
        if (formula != null) {
            // Return the formula as a string
            return formula.toString();
        } else {
            return "";
        }
    }

    private void handleFormulaInputFieldEdit() {
        if (selectedCellId != null) {
            String input = formulaInputField.getText();
            handleCellEdit(selectedCellId, input);
            // Update the cell value in the table
            refreshTableView();
        }
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

    private void refreshTableView() {
        Platform.runLater(() -> {
            for (int rowIndex = 1; rowIndex <= numRows; rowIndex++) {
                ObservableList<StringProperty> row = tableView.getItems().get(rowIndex - 1);
                for (int colIndex = 0; colIndex < numColumns; colIndex++) {
                    String cellId = getCellId(colIndex, rowIndex);
                    String cellValue = getCellDisplayValue(cellId);
                    row.get(colIndex).set(cellValue);
                }
            }
        });
    }

    private void handleSyncButton() {
        System.out.println("Sync button clicked");
        SyncManager.getInstance().synchronize(formulaEditor.getWorkbook());
        // TODO implement synchronization
    }

    public void setOnCloseRequest(EventHandler<WindowEvent> eventHandler) {
        // Unregister the workbook from SyncManager
        SyncManager.getInstance().unregisterWorkbook(formulaEditor.getWorkbook());
    }


    private void showErrorDialog(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

}
