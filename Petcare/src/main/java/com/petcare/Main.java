package com.petcare;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {


        Parent root = FXMLLoader.load(getClass().getResource("view.fxml"));
        Scene scene = new Scene(root,550,600);
        //scene.getStylesheets().add(getClass().getResource("Style.css").toExternalForm());  <- quando usando so uma scene
        String css = this.getClass().getResource("Style.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle("Petcare Manager");
        //scene.setFill(Color.TRANSPARENT); <- cor do scenario


        //Criação do icone

        Image Icon = new Image(getClass().getResourceAsStream("/com/petcare/Petcare-Logo.png"));
        stage.getIcons().add(Icon);

        // *;




        stage.setScene(scene);
        stage.show();
    }
}
