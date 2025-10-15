package com.petcare;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.java.com.petcare.dao.ClienteDAO;
import main.java.com.petcare.model.Cliente;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ClientesController implements Initializable {
    private Stage stage;
    private Scene scene;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Pane AdicionarCliente;
    @FXML
    private TextField cpfField;
    @FXML
    private TextField nomeField;
    @FXML
    private TextField emailField;
    @FXML
    private TextArea telefonesArea;
    @FXML
    private Label feedback;


    private ClienteDAO clienteDAO;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.clienteDAO = new ClienteDAO();
        } catch (Exception e) {
            feedback.setText("Erro crítico ao carregar o banco de dados.");
            e.printStackTrace();
        }
    }


    public void switchtoGerenciarMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/petcare/view.fxml"));
        stage = (Stage) rootPane.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void MostrarPainelAdicionar() {
        AdicionarCliente.setVisible(!AdicionarCliente.isVisible());
    }


    @FXML
    private void SalvarCliente() {

        if (clienteDAO == null) {
            feedback.setText("Erro: Conexão com banco de dados não foi inicializada.");
            return;
        }

        String cpf = cpfField.getText();
        String nome = nomeField.getText();
        String email = emailField.getText();
        String[] telefones = telefonesArea.getText().split("\\r?\\n");

        Cliente cliente = new Cliente(-1, cpf, nome, email, telefones);

        try {

            if (clienteDAO.incluirCliente(cliente)) {
                feedback.setText("Cliente incluído com sucesso!");

                cpfField.clear();
                nomeField.clear();
                emailField.clear();
                telefonesArea.clear();
            } else {
                feedback.setText("Erro: Não foi possível incluir o cliente.");
            }
        } catch (Exception e) {
            feedback.setText("Ocorreu um erro no banco de dados. Tente novamente.");
            e.printStackTrace();
        }
    }
}