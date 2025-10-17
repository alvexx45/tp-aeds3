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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;


public class ClientesController implements Initializable {
    private Stage stage;
    private Scene scene;

    @FXML
    private AnchorPane rootPane;


    // --- Paineis ---
    @FXML
    private Pane AdicionarCliente;
    @FXML
    private Pane BuscarPorIDPane;
    @FXML
    private Pane AlterarPorIDPane;
    @FXML
    private Pane AlterandoClientePorIDPane;
    @FXML
    private Pane ExcluirPorIDPane;
    @FXML
    private Pane BuscarPorCPFPane;
    @FXML
    private Pane AlterarPorCPFPane;
    @FXML
    private Pane AlterandoClientePorCPFPane;
    @FXML
    private Pane ExcluirPorCPFPane;
    @FXML
    private Pane BuscarPorEmailPane;
    @FXML
    private Pane BuscarPorNomePane;

    // *

    // --- Variaveis ---
    //Adicionar cliente
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
    private TextField BuscarPorID_ID;
    @FXML
    private Label BuscarPorID_Label;

    // *

    // Alterar por ID
    @FXML
    private TextField AlterarPorID_ID;
    @FXML
    private Label FeedbackAlterarPorID;
    private int idClienteparaAlterarID;

    // *

    //Alterando por ID
    @FXML
    private TextField AlterandoCPFPorID;
    @FXML
    private TextField AlterandoNomePorID;
    @FXML
    private TextField AlterandoEmailPorID;
    @FXML
    private TextArea AlterandoTelefonesPorID;
    @FXML
    private Label feedbackAlterandoPorID;

    // *

    // Excluindo Por ID
    @FXML
    private TextField ExcluirPorID_ID;
    @FXML
    private Label FeedbackExcluirPorID;

    // *

    // Buscar Por CPF
    @FXML
    private TextField BuscarPorCPF_CPF;
    @FXML
    private Label BuscarPorCPF_Label;

    // *

    // Alterar Por CPF
    @FXML
    private TextField AlterarPorCPF_CPF;
    @FXML
    private Label FeedbackAlterarPorCPF;
    private int idClienteparaAlterarCPF;

    // *

    // AlterandoClientePorCPF
    @FXML
    private TextField AlterandoCPFPorCPF;
    @FXML
    private TextField AlterandoNomePorCPF;
    @FXML
    private TextField AlterandoEmailPorCPF;
    @FXML
    private TextArea AlterandoTelefonesPorCPF;
    @FXML
    private Label feedbackAlterandoPorCPF;

    // *

    // Excluindo Por CPF
    @FXML
    private TextField ExcluindoPorCPF_CPF;
    @FXML
    private Label FeedbackExcluirPorCPF;

    // *

    // Buscar Por Email
    @FXML
    private TextField BuscarPorEmail_Email;
    @FXML
    private Label BuscarPorEmail_Label;

    // *

    // Buscar por nome
    @FXML
    private TextField BuscarPorNome_Nome;
    @FXML
    private Label BuscarPorNome_Label;

    // *

    private ClienteDAO clienteDAO;

    private List<Pane> managedPanes;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        managedPanes = new ArrayList<>(Arrays.asList(
                AdicionarCliente, BuscarPorIDPane, AlterarPorIDPane,
                AlterandoClientePorIDPane, ExcluirPorIDPane, BuscarPorCPFPane,
                AlterarPorCPFPane, AlterandoClientePorCPFPane, ExcluirPorCPFPane,
                BuscarPorEmailPane, BuscarPorNomePane
        ));


        managedPanes.forEach(pane -> pane.setVisible(false));

        try {
            this.clienteDAO = new ClienteDAO();
        } catch (Exception e) {
            feedback.setText("Erro crítico ao carregar o banco de dados.");
            e.printStackTrace();
        }
    }

    private void showExclusivePane(Pane paneToShow) {

        boolean wasVisible = paneToShow.isVisible();

        for (Pane pane : managedPanes) {
            pane.setVisible(false);
        }

        if (!wasVisible) {
            paneToShow.setVisible(true);
        }
    }


    public void switchtoGerenciarMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/petcare/view.fxml"));
        stage = (Stage) rootPane.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }



    // Mostrar Paineis

    @FXML
    private void MostrarPainelAdicionar() {
        showExclusivePane(AdicionarCliente);
    }

    @FXML
    private void MostrarPainelBuscarPorID(){
        showExclusivePane(BuscarPorIDPane);
    }

    @FXML
    private void MostrarPainelAlterarPorID(){
        showExclusivePane(AlterarPorIDPane);
    }

    private void MostrarPainelAlterandoPorID(){
        showExclusivePane(AlterandoClientePorIDPane);
    }

    @FXML
    private void MostrarPainelExcluirPorID() {
        showExclusivePane(ExcluirPorIDPane);
    }

    @FXML
    private void MostrarPainelBuscarPorCPF(){
        showExclusivePane(BuscarPorCPFPane);
    }

    @FXML
    private void MostrarPainelAlterarPorCPF(){
        showExclusivePane(AlterarPorCPFPane);
    }

    @FXML
    private void MostrarPainelAlterandoPorCPF(){
        showExclusivePane(AlterandoClientePorCPFPane);
    }

    @FXML
    private void MostrarPainelExcluirPorCpf(){
        showExclusivePane(ExcluirPorCPFPane);
    }

    @FXML
    private void MostrarPainelBuscarPorNome() {
        showExclusivePane(BuscarPorNomePane);
    }

    @FXML
    private void MostrarPainelBuscarPorEmail() {
        showExclusivePane(BuscarPorEmailPane);
    }

    // *

    // Adicionar Cliente

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

    // *


    // Seção ID

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
    private void AlterarClientePorId() throws Exception {

        int id = Integer.parseInt(AlterarPorID_ID.getText());

        Cliente clienteExistente = clienteDAO.buscarCliente(id);

        if (clienteExistente == null) {
            FeedbackAlterarPorID.setText("Cliente nâo encontrado!");
            return;
        }


        AlterandoCPFPorID.setText(clienteExistente.getCpf());
        AlterandoNomePorID.setText(clienteExistente.getNome());
        AlterandoEmailPorID.setText(clienteExistente.getEmail());
        String[] telefones = clienteExistente.getTelefones();

        if (telefones != null && telefones.length > 0) {
            AlterandoTelefonesPorID.setText(String.join("\n", telefones));
        } else {
            AlterandoTelefonesPorID.clear();
        }

        this.idClienteparaAlterarID = id;

        showExclusivePane(AlterandoClientePorIDPane);

    }

    @FXML
    private void AlterandoClientePorID() throws Exception {

        String cpf = AlterandoCPFPorID.getText();

        String nome = AlterandoNomePorID.getText();

        String email = AlterandoEmailPorID.getText();


        String[] telefones = AlterandoTelefonesPorID.getText().split("\\r?\\n");


        Cliente clienteAlterado = new Cliente(this.idClienteparaAlterarID, cpf, nome, email, telefones);

        if (clienteDAO.alterarCliente(clienteAlterado))  {
            showExclusivePane(AlterarPorIDPane);
            FeedbackAlterarPorID.setText("Cliente alterado com sucesso!");
        } else {
            feedbackAlterandoPorID.setText("Erro ao alterar cliente!");
        }
    }


    @FXML
    private void ExcluirClientePorId() throws Exception {

        int id = Integer.parseInt(ExcluirPorID_ID.getText());

        Cliente cliente = clienteDAO.buscarCliente(id);

        if (cliente == null) {
            FeedbackExcluirPorID.setText("Cliente não encontrado!");
            return;
        }

        StringBuilder ClienteExcluido = new StringBuilder();

       ClienteExcluido.append(exibirCliente(cliente));

        if (clienteDAO.excluirCliente(id)) {
            FeedbackExcluirPorID.setText("Cliente:\n\n" + ClienteExcluido + "\nexcluído com sucesso!");
        } else {
            FeedbackExcluirPorID.setText("Erro ao excluir cliente!");
        }

    }

    // *

    // Seção CPF

    @FXML
    private void BuscarClientePorCPF() throws Exception {


        String cpf = BuscarPorCPF_CPF.getText();

        Cliente cliente = clienteDAO.buscarClientePorCPF(cpf);

        if (cliente != null) {
            BuscarPorCPF_Label.setText(exibirCliente(cliente));
        } else {
            BuscarPorCPF_Label.setText("Cliente com CPF " + cpf + " não encontrado!");
        }
    }

    @FXML
    private void AlterarClientePorCPF() throws Exception {

        String cpf = AlterarPorCPF_CPF.getText();

        Cliente clienteExistente = clienteDAO.buscarClientePorCPF(cpf);

        if (clienteExistente == null) {
            FeedbackAlterarPorCPF.setText("Cliente com CPF " + cpf + " não encontrado!");
            return;
        }

        AlterandoCPFPorCPF.setText(clienteExistente.getCpf());
        AlterandoNomePorCPF.setText(clienteExistente.getNome());
        AlterandoEmailPorCPF.setText(clienteExistente.getEmail());
        String[] telefones = clienteExistente.getTelefones();

        if (telefones != null && telefones.length > 0) {
            AlterandoTelefonesPorCPF.setText(String.join("\n", telefones));
        } else {
            AlterandoTelefonesPorCPF.clear();
        }

        this.idClienteparaAlterarCPF = clienteExistente.getId();

        showExclusivePane(AlterandoClientePorCPFPane);


    }

    @FXML
    private void AlterandoClientePorCPF() throws Exception {

        String cpf = AlterandoCPFPorCPF.getText();

        String nome = AlterandoNomePorCPF.getText();

        String email = AlterandoEmailPorCPF.getText();


        String[] telefones = AlterandoTelefonesPorCPF.getText().split("\\r?\\n");


        Cliente clienteAlterado = new Cliente(this.idClienteparaAlterarCPF, cpf, nome, email, telefones);

        if (clienteDAO.alterarCliente(clienteAlterado))  {
            showExclusivePane(AlterarPorCPFPane);
            FeedbackAlterarPorCPF.setText("Cliente alterado com sucesso!");
        } else {
            feedbackAlterandoPorCPF.setText("Erro ao alterar cliente!");
        }
    }

    @FXML
    private void ExcluirClientePorCPF() throws Exception {



        String cpf = ExcluindoPorCPF_CPF.getText();

        Cliente cliente = clienteDAO.buscarClientePorCPF(cpf);

        if (cliente == null) {
            FeedbackExcluirPorCPF.setText("Cliente com CPF " + cpf + " não encontrado!");
            return;
        }

        StringBuilder ClienteExcluido = new StringBuilder();

        ClienteExcluido.append(exibirCliente(cliente));

        int id = cliente.getId();

        if (clienteDAO.excluirCliente(id)) {
            FeedbackExcluirPorID.setText("Cliente:\n\n" + ClienteExcluido + "\nexcluído com sucesso!");
        } else {
            FeedbackExcluirPorID.setText("Erro ao excluir cliente!");
        }
    }

    // *

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
