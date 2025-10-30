package controller;

import dao.ClienteDAO;
import dao.PetDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Cliente;
import model.Pet;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PetController implements Initializable {

    @FXML private TextField txtNomeIncluir;
    @FXML private TextField txtEspecieIncluir;
    @FXML private TextField txtRacaIncluir;
    @FXML private TextField txtPesoIncluir;
    @FXML private TextField txtCpfDonoIncluir;
    @FXML private Button btnIncluir;

    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private GridPane gridAlteracao;
    @FXML private TextField txtIdAlterar;
    @FXML private TextField txtNomeAlterar;
    @FXML private TextField txtEspecieAlterar;
    @FXML private TextField txtRacaAlterar;
    @FXML private TextField txtPesoAlterar;
    @FXML private TextField txtCpfDonoAlterar;
    @FXML private HBox botoesAcao;
    @FXML private Button btnAlterar;
    @FXML private Button btnExcluir;

    @FXML private TextField txtCpfBusca;
    @FXML private Button btnBuscarPorDono;
    @FXML private ListView<String> listViewPets;

    @FXML private Button btnFechar;

    private PetDAO petDAO;
    private ClienteDAO clienteDAO;
    private Pet petAtual;
    private ObservableList<String> listaPets;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            petDAO = new PetDAO();
            clienteDAO = new ClienteDAO();
            listaPets = FXCollections.observableArrayList();
            listViewPets.setItems(listaPets);
        } catch (Exception e) {
            mostrarErro("Erro de Inicialização", "Erro ao inicializar DAO: " + e.getMessage());
        }
    }

    @FXML
    private void incluirPet() {
        try {
            String nome = txtNomeIncluir.getText().trim();
            String especie = txtEspecieIncluir.getText().trim();
            String raca = txtRacaIncluir.getText().trim();
            String pesoStr = txtPesoIncluir.getText().trim();
            String cpfDono = txtCpfDonoIncluir.getText().trim();

            if (nome.isEmpty() || especie.isEmpty() || raca.isEmpty() || pesoStr.isEmpty() || cpfDono.isEmpty()) {
                mostrarAviso("Campos Obrigatórios", "Todos os campos são obrigatórios!");
                return;
            }

            float peso;
            try {
                peso = Float.parseFloat(pesoStr);
            } catch (NumberFormatException e) {
                mostrarAviso("Peso Inválido", "Digite um peso válido (ex: 5.5)!");
                return;
            }

            // Buscar o cliente dono
            Cliente dono = clienteDAO.buscarClientePorCPF(cpfDono);
            if (dono == null) {
                mostrarAviso("Dono não encontrado", "Cliente com CPF " + cpfDono + " não encontrado!");
                return;
            }

            Pet pet = new Pet(-1, nome, especie, raca, peso, dono);
            boolean sucesso = petDAO.incluirPet(pet);
            
            if (sucesso) {
                mostrarSucesso("Pet Incluído", "Pet incluído com sucesso!");
                limparCamposInclusao();
            } else {
                mostrarErro("Erro", "Erro ao incluir pet.");
            }
        } catch (Exception e) {
            mostrarErro("Erro ao incluir pet", e.getMessage());
        }
    }

    @FXML
    private void buscarPet() {
        try {
            String termo = txtBuscar.getText().trim();
            if (termo.isEmpty()) {
                mostrarAviso("Campo Obrigatório", "Digite o ID do pet!");
                return;
            }

            int id;
            try {
                id = Integer.parseInt(termo);
            } catch (NumberFormatException e) {
                mostrarAviso("ID Inválido", "Digite um ID válido!");
                return;
            }

            Pet pet = petDAO.buscarPet(id);

            if (pet != null) {
                petAtual = pet;
                preencherCamposAlteracao(pet);
                gridAlteracao.setVisible(true);
                botoesAcao.setVisible(true);
            } else {
                mostrarInfo("Pet não encontrado", "Nenhum pet encontrado com ID: " + id);
                gridAlteracao.setVisible(false);
                botoesAcao.setVisible(false);
            }
        } catch (Exception e) {
            mostrarErro("Erro ao buscar pet", e.getMessage());
        }
    }

    @FXML
    private void alterarPet() {
        try {
            if (petAtual == null) {
                mostrarAviso("Nenhum pet selecionado", "Busque um pet primeiro!");
                return;
            }

            String nome = txtNomeAlterar.getText().trim();
            String especie = txtEspecieAlterar.getText().trim();
            String raca = txtRacaAlterar.getText().trim();
            String pesoStr = txtPesoAlterar.getText().trim();
            String cpfDono = txtCpfDonoAlterar.getText().trim();

            if (nome.isEmpty() || especie.isEmpty() || raca.isEmpty() || pesoStr.isEmpty() || cpfDono.isEmpty()) {
                mostrarAviso("Campos Obrigatórios", "Todos os campos são obrigatórios!");
                return;
            }

            float peso;
            try {
                peso = Float.parseFloat(pesoStr);
            } catch (NumberFormatException e) {
                mostrarAviso("Peso Inválido", "Digite um peso válido!");
                return;
            }

            // Buscar o cliente dono
            Cliente dono = clienteDAO.buscarClientePorCPF(cpfDono);
            if (dono == null) {
                mostrarAviso("Dono não encontrado", "Cliente com CPF " + cpfDono + " não encontrado!");
                return;
            }

            petAtual.setNome(nome);
            petAtual.setEspecie(especie);
            petAtual.setRaca(raca);
            petAtual.setPeso(peso);
            petAtual.setDono(dono);

            if (petDAO.alterarPet(petAtual)) {
                mostrarSucesso("Pet Alterado", "Pet alterado com sucesso!");
                limparBusca();
            } else {
                mostrarErro("Erro", "Erro ao alterar pet.");
            }
        } catch (Exception e) {
            mostrarErro("Erro ao alterar pet", e.getMessage());
        }
    }

    @FXML
    private void excluirPet() {
        try {
            if (petAtual == null) {
                mostrarAviso("Nenhum pet selecionado", "Busque um pet primeiro!");
                return;
            }

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Exclusão");
            confirmacao.setHeaderText("Deseja realmente excluir este pet?");
            confirmacao.setContentText("Pet: " + petAtual.getNome() + " (ID: " + petAtual.getId() + ")");

            if (confirmacao.showAndWait().get() == ButtonType.OK) {
                if (petDAO.excluirPet(petAtual.getId())) {
                    mostrarSucesso("Pet Excluído", "Pet excluído com sucesso!");
                    limparBusca();
                } else {
                    mostrarErro("Erro", "Erro ao excluir pet.");
                }
            }
        } catch (Exception e) {
            mostrarErro("Erro ao excluir pet", e.getMessage());
        }
    }

    @FXML
    private void buscarPetsPorDono() {
        try {
            String cpf = txtCpfBusca.getText().trim();
            if (cpf.isEmpty()) {
                mostrarAviso("Campo Obrigatório", "Digite o CPF do dono!");
                return;
            }

            List<Pet> pets = petDAO.buscarPetsPorCpfDono(cpf);
            listaPets.clear();

            if (pets.isEmpty()) {
                mostrarInfo("Nenhum pet encontrado", "Nenhum pet encontrado para o CPF: " + cpf);
            } else {
                for (Pet pet : pets) {
                    String info = String.format("ID: %d | %s (%s) - %s - %.2f kg", 
                        pet.getId(), pet.getNome(), pet.getEspecie(), pet.getRaca(), pet.getPeso());
                    listaPets.add(info);
                }
            }
        } catch (Exception e) {
            mostrarErro("Erro ao buscar pets", e.getMessage());
        }
    }

    @FXML
    private void fecharJanela() {
        Stage stage = (Stage) btnFechar.getScene().getWindow();
        stage.close();
    }

    private void preencherCamposAlteracao(Pet pet) {
        txtIdAlterar.setText(String.valueOf(pet.getId()));
        txtNomeAlterar.setText(pet.getNome());
        txtEspecieAlterar.setText(pet.getEspecie());
        txtRacaAlterar.setText(pet.getRaca());
        txtPesoAlterar.setText(String.valueOf(pet.getPeso()));
        txtCpfDonoAlterar.setText(pet.getDono() != null ? pet.getDono().getCpf() : "");
    }

    private void limparCamposInclusao() {
        txtNomeIncluir.clear();
        txtEspecieIncluir.clear();
        txtRacaIncluir.clear();
        txtPesoIncluir.clear();
        txtCpfDonoIncluir.clear();
    }

    private void limparBusca() {
        txtBuscar.clear();
        txtIdAlterar.clear();
        txtNomeAlterar.clear();
        txtEspecieAlterar.clear();
        txtRacaAlterar.clear();
        txtPesoAlterar.clear();
        txtCpfDonoAlterar.clear();
        gridAlteracao.setVisible(false);
        botoesAcao.setVisible(false);
        petAtual = null;
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarAviso(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarSucesso(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}