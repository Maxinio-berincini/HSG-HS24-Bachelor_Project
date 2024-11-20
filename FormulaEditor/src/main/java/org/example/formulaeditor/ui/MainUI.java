package org.example.formulaeditor.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainUI extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        WorkbookUI workbookUI = new WorkbookUI();

        Scene scene = new Scene(workbookUI, 800, 600);
        primaryStage.setTitle("Formula Editor");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}

