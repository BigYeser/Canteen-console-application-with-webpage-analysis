package de.uniwue.jpp.mensabot.gui;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class MensabotGui extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("start...");
        Parent root = null ;
        try {
            System.out.println(getClass().getResource("main.fxml").getFile());
             root = FXMLLoader.load(getClass().getResource("main.fxml"));
        }catch (Exception e){
            System.out.println("Hello:  "+ e);
            return;
        }
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        System.out.println("End...");

    }
}
