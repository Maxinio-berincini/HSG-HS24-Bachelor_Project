package org.example.formulaeditor.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.formulaeditor.FormulaEditor;

public class MainUI extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        final String instanceId = "Instance1";
        final String instanceId2 = "Instance2";
        final String serverUrl = "wss://helix.berinet.ch";

        FormulaEditor formulaEditor = new FormulaEditor(instanceId);
        formulaEditor.addFormula("A1", "11 + 6");
        WorkbookUI workbookUI = new WorkbookUI(formulaEditor, instanceId, serverUrl, primaryStage);

        Scene scene = new Scene(workbookUI, 800, 600);
        primaryStage.setTitle("Formula Editor");
        primaryStage.setScene(scene);
        primaryStage.show();

        Stage secondStage = new Stage();
        FormulaEditor formulaEditor2 = new FormulaEditor(instanceId2);
        formulaEditor2.addFormula("A1", "10 + 15");
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

