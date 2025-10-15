package com.petcare;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
    private Stage stage;
    private Scene scene;

    @FXML
    private AnchorPane rootPane;



    public void switchtoGerenciarMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/petcare/view.fxml"));
        stage = (Stage) rootPane.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchtoGerenciarClientes(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/com/petcare/GerenciarClientes.fxml"));
        stage = (Stage)rootPane.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchtoGerenciarPets(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/petcare/GerenciarPets.fxml"));
        stage = (Stage)rootPane.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchtoGerenciarServicos(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/com/petcare/GerenciarServicos.fxml"));
        stage = (Stage)rootPane.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}