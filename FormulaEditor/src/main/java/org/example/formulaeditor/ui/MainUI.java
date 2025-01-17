package org.example.formulaeditor.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.formulaeditor.FormulaEditor;
import org.example.formulaeditor.io.WorkbookFileIO;
import org.example.formulaeditor.model.Workbook;

import java.io.IOException;

public class MainUI extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        final String instanceId = "Instance1";
        final String instanceId2 = "Instance2";
        final String serverUrl = "wss://helix.berinet.ch";
        final String filePath = "src/main/resources/workbook.json";
        final String filePath2 = "src/main/resources/workbook2.json";

        //try to load workbook
        Workbook workbook;
        try {
            workbook = WorkbookFileIO.load(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            workbook = new Workbook();
        }

        FormulaEditor formulaEditor = new FormulaEditor(instanceId);
        formulaEditor.setWorkbook(workbook);
        formulaEditor.setFilePath(filePath);
        WorkbookUI workbookUI = new WorkbookUI(formulaEditor, instanceId, serverUrl, primaryStage);

        Scene scene = new Scene(workbookUI, 800, 600);
        primaryStage.setTitle("Formula Editor");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Second empty instance for testing
        Stage secondStage = new Stage();
        FormulaEditor formulaEditor2 = new FormulaEditor(instanceId2);
        formulaEditor2.setFilePath(filePath2);
        WorkbookUI workbookUI2 = new WorkbookUI(formulaEditor2, instanceId2, serverUrl, secondStage);

        Scene scene2 = new Scene(workbookUI2, 800, 600);
        secondStage.setTitle("Formula Editor - Instance 2");
        secondStage.setScene(scene2);
        secondStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}