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
import model.Cliente;
import model.Pet;

import java.net.URL;
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
    @FXML private Button btnListarTodos;
    @FXML private ListView<String> listViewPets;

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
            
            // Listener para buscar automaticamente ao clicar em um pet
            listViewPets.setOnMouseClicked(event -> {
                String petSelecionado = listViewPets.getSelectionModel().getSelectedItem();
                if (petSelecionado != null && !petSelecionado.isEmpty()) {
                    // Extrair o ID do pet (formato: "ID: 1 | ...")
                    String[] partes = petSelecionado.split("\\|");
                    if (partes.length > 0) {
                        String id = partes[0].replace("ID:", "").trim();
                        txtBuscar.setText(id);
                        buscarPet(); // Aciona a busca automaticamente
                    }
                }
            });
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
            String cpfDono = limparCpf(txtCpfDonoIncluir.getText());

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
            String cpfDono = limparCpf(txtCpfDonoAlterar.getText());

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
                // Atualiza a lista dinamicamente buscando pets do mesmo dono
                txtCpfBusca.setText(cpfDono);
                buscarPetsPorDono();
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
            confirmacao.setContentText("Pet: " + petAtual.getNome() + " (ID: " + petAtual.getId() + ")\n\n" +
                                      "ATENÇÃO: Todos os agendamentos deste pet também serão excluídos!");

            if (confirmacao.showAndWait().get() == ButtonType.OK) {
                String cpfDono = petAtual.getDono().getCpf(); // Salva o CPF antes de excluir
                if (petDAO.excluirPet(petAtual.getId())) {
                    mostrarSucesso("Pet Excluído", "Pet e seus agendamentos foram excluídos com sucesso!");
                    limparBusca();
                    // Atualiza a lista dinamicamente buscando pets do mesmo dono
                    txtCpfBusca.setText(cpfDono);
                    buscarPetsPorDono();
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
    private void listarTodosPets() {
        listarTodosPets(true);
    }

    private void listarTodosPets(boolean exibirMensagem) {
        try {
            List<Pet> pets = petDAO.listarTodosPets();
            listaPets.clear();

            if (pets.isEmpty()) {
                if (exibirMensagem) {
                    mostrarInfo("Nenhum pet encontrado", "Não há pets cadastrados no sistema.");
                }
            } else {
                for (Pet pet : pets) {
                    String dono = "N/A";
                    if (pet.getDono() != null) {
                        dono = pet.getDono().getNome() + " (CPF: " + pet.getDono().getCpf() + ")";
                    }
                    String info = String.format("ID: %d | %s (%s) - %s - %.2f kg | Dono: %s", 
                        pet.getId(), 
                        pet.getNome(), 
                        pet.getEspecie(), 
                        pet.getRaca(), 
                        pet.getPeso(),
                        dono);
                    listaPets.add(info);
                }
                if (exibirMensagem) {
                    mostrarSucesso("Pets Listados", pets.size() + " pet(s) encontrado(s).");
                }
            }
        } catch (Exception e) {
            mostrarErro("Erro ao listar pets", e.getMessage());
        }
    }

    @FXML
    private void preencherCamposAlteracao(Pet pet) {
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
        txtNomeAlterar.clear();
        txtEspecieAlterar.clear();
        txtRacaAlterar.clear();
        txtPesoAlterar.clear();
        txtCpfDonoAlterar.clear();
        gridAlteracao.setVisible(false);
        botoesAcao.setVisible(false);
        petAtual = null;
    }

    /**
     * Remove caracteres não numéricos do CPF, mantendo apenas os dígitos
     */
    private String limparCpf(String cpf) {
        if (cpf == null) {
            return "";
        }
        return cpf.replaceAll("[^0-9]", "");
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