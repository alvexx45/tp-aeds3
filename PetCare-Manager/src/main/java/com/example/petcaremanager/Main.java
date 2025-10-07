package com.example.petcaremanager;

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



    }

    @Override
    public void start(Stage stage) throws IOException {


        Parent root = FXMLLoader.load(getClass().getResource("example.fxml"));
        Scene scene = new Scene(root,400,400,Color.DARKRED);
        stage.setTitle("Petcare Manager");
        //scene.setFill(Color.TRANSPARENT); <- cor do scenario

        //Textos


        Text texto = new  Text();
        texto.setText("Bem vindo ao sistema de gerenciamento de Pets!");
        texto.setX(200);
        texto.setY(200);
        texto.setFill(Color.WHITE);
        texto.setFont(Font.font("Arial",20));
        texto.setStrokeWidth(0.3);
        texto.setStroke(Color.BLACK);

        // root.getChildren().add(texto);  <- chama pro root principal, tem que ficar depois de tudo, so usar getchildren quando nao usando scenebuilder ou fxml file

        // *;



        //Criação do icone

        Image Icon = new Image("Petcare-Logo.png");
        stage.getIcons().add(Icon);

        // *;


        //Adiciona imagems na cena
        Image PetCachorro = new Image("Cachorro.png");
        ImageView CachorroView = new ImageView(PetCachorro);
        CachorroView.setFitWidth(200);


        // root.getChildren().add(CachorroView);

        // *;



        //Controles de Resolução

        stage.setWidth(400);
        stage.setHeight(400);
        //stage.setFullScreen(true);


        // *;

        stage.setScene(scene);
        stage.show();
    }
}