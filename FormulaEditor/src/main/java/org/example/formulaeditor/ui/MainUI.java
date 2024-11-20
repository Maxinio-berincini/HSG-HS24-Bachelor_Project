package org.example.formulaeditor.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.formulaeditor.FormulaEditor;

public class MainUI extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FormulaEditor formulaEditor = new FormulaEditor();
        WorkbookUI workbookUI = new WorkbookUI(formulaEditor);

        Scene scene = new Scene(workbookUI, 800, 600);
        primaryStage.setTitle("Formula Editor");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}

