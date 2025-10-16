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


    //Adicionar cliente

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

    // *

    // Buscar Por ID

    @FXML
    private Pane BuscarPorIDPane;

    @FXML
    private TextField BuscarPorID_ID;

    @FXML
    private Label BuscarPorID_Label;

    //*

    // Buscar Por Email

    @FXML
    private Pane BuscarPorEmailPane;

    @FXML
    private TextField BuscarPorEmail_Email;

    @FXML
    private Label BuscarPorEmail_Label;


    // Buscar por nome

    @FXML
    private Pane BuscarPorNomePane;

    @FXML
    private TextField BuscarPorNome_Nome;

    @FXML
    private Label BuscarPorNome_Label;


    // *

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
    private void MostrarPainelBuscarPorID(){
        BuscarPorIDPane.setVisible(!BuscarPorIDPane.isVisible());
    }



    @FXML
    private void MostrarPainelBuscarPorNome() {
        BuscarPorNomePane.setVisible(!BuscarPorNomePane.isVisible());
    }

    @FXML
    private void MostrarPainelBuscarPorEmail() {
        BuscarPorEmailPane.setVisible(!BuscarPorEmailPane.isVisible());
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

    @FXML
    private void BuscarClientePorId() throws Exception {

        int id = Integer.parseInt(BuscarPorID_ID.getText());

        Cliente cliente = clienteDAO.buscarCliente(id);

        StringBuilder textoCompleto = new StringBuilder();

        if (cliente != null) {
            textoCompleto.append(" cliente encontrado:\n\n");

            textoCompleto.append(exibirCliente(cliente));


        } else {
            textoCompleto.append("Cliente com Id: " +id + " não encontrado!");
        }

        BuscarPorID_Label.setText(textoCompleto.toString());
    }






    @FXML
    private void BuscarClientePorEmail() throws Exception {

        String email = BuscarPorEmail_Email.getText();

        Cliente cliente = clienteDAO.buscarClientePorEmail(email);

        StringBuilder textoCompleto = new StringBuilder();

        if (cliente != null) {

            textoCompleto.append(" cliente(s) encontrado(s):\n\n");

            textoCompleto.append(exibirCliente(cliente));
       
        } else {
          textoCompleto.append("Cliente com email " + email + " não encontrado!");
        }

        BuscarPorEmail_Label.setText(textoCompleto.toString());
    }


    @FXML
    private void BuscarClientePorNome() throws Exception {
        String nome = BuscarPorNome_Nome.getText();

        java.util.List<Cliente> clientes = clienteDAO.buscarClientesPorNome(nome);
        StringBuilder textoCompleto = new StringBuilder();
        textoCompleto.append(clientes.size()).append(" cliente(s) encontrado(s):\n\n");


        for (Cliente cliente : clientes) {

            textoCompleto.append(exibirCliente(cliente));
            textoCompleto.append("--------------------\n");
        }

        BuscarPorNome_Label.setText(textoCompleto.toString());
    }




    private String exibirCliente(Cliente cliente) {

        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(cliente.getId()).append("\n");
        sb.append("CPF: ").append(cliente.getCpf()).append("\n");
        sb.append("Nome: ").append(cliente.getNome()).append("\n");
        sb.append("Email: ").append(cliente.getEmail()).append("\n");


        String[] telefones = cliente.getTelefones();
        if (telefones != null && telefones.length > 0) {

            String telefonesFormatados = String.join(", ", telefones);
            sb.append("Telefones: ").append(telefonesFormatados).append("\n");
        } else {
            sb.append("Telefones: (Nenhum telefone cadastrado)\n");
        }

        return sb.toString();

    }
}
