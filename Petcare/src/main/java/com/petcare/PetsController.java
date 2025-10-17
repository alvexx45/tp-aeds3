package com.petcare;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.java.com.petcare.dao.ClienteDAO;
import main.java.com.petcare.dao.PetDAO;
import main.java.com.petcare.model.Cliente;
import main.java.com.petcare.model.Pet;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class PetsController implements Initializable {

    private Stage stage;
    private Scene scene;

    private ClienteDAO clienteDAO;
    private PetDAO petDAO;

    private List<Pane> managedPanes;


    @FXML
    private AnchorPane rootPane;

    // Adicionar Pet

    @FXML
    private Pane AdicionarPetPane;

    @FXML
    private TextField AdicionarPet_Nome;

    @FXML
    private TextField AdicionarPet_Especie;

    @FXML
    private TextField AdicionarPet_Raca;

    @FXML
    private TextField AdicionarPet_Peso;

    @FXML
    private TextField AdicionarPet_CPF;

    @FXML
    private Label FeedbackAdicionarPet;

    // *

    // Buscar Pet

    @FXML
    private Pane BuscarPetPane;

    @FXML
    private TextField BuscarPet_ID;

    @FXML
    private Label BuscarPet_Label;

    // *

    // Alterar Pet

    @FXML
    private Pane AlterarPetPane;

    @FXML
    private TextField AlterarPet_ID;

    @FXML
    private Label FeedbackAlterarPet;
    private int idPetParaAlterar;
    private Cliente donoAtual;

    // *

    // Alterando Pet

    @FXML
    private Pane AlterandoPetPane;

    @FXML
    private TextField AlterandoPet_Nome;

    @FXML
    private TextField AlterandoPet_Especie;

    @FXML
    private TextField AlterandoPet_Raca;

    @FXML
    private TextField AlterandoPet_Peso;

    @FXML
    private TextField AlterandoPet_CPF;

    @FXML
    private Label FeedbackAlterandoPet;

    @FXML
    private Label AlterandoPet_Label;


    // *



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        managedPanes = new ArrayList<>(Arrays.asList(
                AdicionarPetPane, BuscarPetPane , AlterarPetPane, AlterandoPetPane
        ));

        managedPanes.forEach(pane -> pane.setVisible(false));

        try {
            this.clienteDAO = new ClienteDAO();
            this.petDAO = new PetDAO();
        } catch (Exception e) {
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


    @FXML
    private void MostrarPainelAdicionarPet(){
        showExclusivePane(AdicionarPetPane);
    }

    @FXML
    private void MostrarPainelBuscarPet(){
        showExclusivePane(BuscarPetPane);
    }

    @FXML
    private void MostrarPainelAlterarPet(){
        showExclusivePane(AlterarPetPane);
    }

    @FXML
    private void MostrarPainelAlterandoPet(){
        showExclusivePane(AlterandoPetPane);
    }




    @FXML
    private void AdicionarPet() throws Exception {

        String nome = AdicionarPet_Nome.getText();

        String especie = AdicionarPet_Especie.getText();


        String raca = AdicionarPet_Raca.getText();


        float peso = Float.parseFloat(AdicionarPet_Peso.getText());


        String cpfDono = AdicionarPet_CPF.getText();

        // Verificar se o cliente existe
        Cliente dono = clienteDAO.buscarClientePorCPF(cpfDono);
        if (dono == null) {
            FeedbackAdicionarPet.setText("Cliente com CPF " + cpfDono + " não encontrado!");
        }

        Pet pet = new Pet(-1, nome, especie, raca, peso, dono);

        if (petDAO.incluirPet(pet)) {
            FeedbackAdicionarPet.setText("Pet incluído com sucesso! ID: " + pet.getId());
        } else {
            FeedbackAdicionarPet.setText("Erro ao incluir pet!");
        }
    }

    @FXML
    private void BuscarPet() throws Exception {

        int id = Integer.parseInt(BuscarPet_ID.getText());

        Pet pet = petDAO.buscarPet(id);

        if (pet != null) {
            // Carregar dados completos do dono
            Cliente dono = clienteDAO.buscarClientePorCPF(pet.getDono().getCpf());
            pet.setDono(dono);

            BuscarPet_Label.setText(exibirPet(pet));
        } else {
            BuscarPet_Label.setText("Pet não encontrado!");
        }
    }

    @FXML
    private void AlterarPet() throws Exception {

        int id = Integer.parseInt(AlterarPet_ID.getText());
        Pet petExistente = petDAO.buscarPet(id);

        if (petExistente == null) {
            FeedbackAlterarPet.setText("Pet não encontrado!");
            return;
        }

        if (petExistente.getDono() != null && petExistente.getDono().getCpf() != null) {
            donoAtual = clienteDAO.buscarClientePorCPF(petExistente.getDono().getCpf());
            petExistente.setDono(donoAtual);
        }


        if (donoAtual == null) {
            FeedbackAlterarPet.setText("Dono do pet não encontrado! CPF: " + petExistente.getDono().getCpf());
            return;
        }

        // Now we can populate the fields with the correct data
        AlterandoPet_Nome.setText(petExistente.getNome());
        AlterandoPet_Especie.setText(petExistente.getEspecie());
        AlterandoPet_Raca.setText(petExistente.getRaca());
        AlterandoPet_Peso.setText(String.valueOf(petExistente.getPeso()));
        AlterandoPet_CPF.setText(petExistente.getDono().getCpf());


        AlterandoPet_Label.setText(exibirCliente(donoAtual));

        this.idPetParaAlterar = petExistente.getId();


        FeedbackAlterandoPet.setText("");
        FeedbackAlterarPet.setText("");

        showExclusivePane(AlterandoPetPane);
    }

    @FXML
    private void AlterandoPet() throws Exception{
        String nome = AlterandoPet_Nome.getText();
        String especie = AlterandoPet_Especie.getText();
        String raca = AlterandoPet_Raca.getText();
        float peso = Float.parseFloat(AlterandoPet_Peso.getText());
        String cpf = AlterandoPet_CPF.getText();

        Cliente novoDono = donoAtual;
        if (!cpf.equals(this.donoAtual.getCpf())) {
            novoDono = clienteDAO.buscarClientePorCPF(cpf);
            if (novoDono == null) {
                FeedbackAlterandoPet.setText("Cliente com CPF " + cpf + " não encontrado!");
                return;
            }
        }

        Pet petAlterado = new Pet(this.idPetParaAlterar, nome, especie, raca, peso, novoDono);


        if(petDAO.alterarPet(petAlterado)) {

            showExclusivePane(AlterarPetPane);
            FeedbackAlterarPet.setText("Pet alterado com sucesso!");
        } else {

            FeedbackAlterandoPet.setText("Erro ao alterar pet!");
        }

    }


    private String exibirPet(Pet pet) {

        StringBuilder sb = new StringBuilder();

        sb.append("\n\n------ DADOS DO PET ------\n\n");
        sb.append("ID: " + pet.getId() + "\n");
        sb.append("Nome: " + pet.getNome() + "\n");
        sb.append("Raca: " + pet.getRaca() + "\n");
        sb.append("Peso: " + pet.getPeso() + " kg\n");


        if (pet.getDono() != null) {
            sb.append("Dono: " + pet.getDono().getNome() + " (CPF: " + pet.getDono().getCpf() + ")\n\n");
        }
        sb.append("---------------------------------");
        return sb.toString();

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
