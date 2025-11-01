package controller;

import dao.AgendarDAO;
import dao.ClienteDAO;
import dao.PetDAO;
import dao.ServicoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import model.Agendar;
import model.Cliente;
import model.Pet;
import model.Servico;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AgendarController implements Initializable {

    // Campos para inclusão de agendamento
    @FXML private ComboBox<Cliente> cmbClienteIncluir;
    @FXML private ComboBox<Pet> cmbPetIncluir;
    @FXML private ComboBox<Servico> cmbServicoIncluir;
    @FXML private DatePicker dtpDataIncluir;
    @FXML private Button btnIncluir;

    // Campos para busca
    @FXML private ComboBox<Cliente> cmbClienteBuscar;
    @FXML private Button btnBuscar;
    @FXML private GridPane gridAlteracao;
    @FXML private TextField txtIdAlterar;
    @FXML private ComboBox<Cliente> cmbClienteAlterar;
    @FXML private ComboBox<Pet> cmbPetAlterar;
    @FXML private ComboBox<Servico> cmbServicoAlterar;
    @FXML private DatePicker dtpDataAlterar;
    @FXML private HBox botoesAcao;
    @FXML private Button btnAlterar;
    @FXML private Button btnExcluir;

    // Lista de agendamentos
    @FXML private Button btnListarTodos;
    @FXML private ListView<String> listViewAgendamentos;

    @FXML private Button btnFechar;

    private AgendarDAO agendarDAO;
    private ClienteDAO clienteDAO;
    private PetDAO petDAO;
    private ServicoDAO servicoDAO;
    
    private Agendar agendamentoAtual;
    private ObservableList<String> listaAgendamentos;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            agendarDAO = new AgendarDAO();
            clienteDAO = new ClienteDAO();
            petDAO = new PetDAO();
            servicoDAO = new ServicoDAO();
            
            listaAgendamentos = FXCollections.observableArrayList();
            listViewAgendamentos.setItems(listaAgendamentos);
            
            inicializarComboBoxes();
            limparCampos();
            
        } catch (Exception e) {
            mostrarAlerta("Erro ao inicializar", "Erro ao inicializar o controlador: " + e.getMessage());
        }
    }

    private void inicializarComboBoxes() throws Exception {
        // Carregar clientes - buscar todos usando uma busca por nome vazio
        List<Cliente> clientes = clienteDAO.buscarClientesPorNome("");
        cmbClienteIncluir.setItems(FXCollections.observableArrayList(clientes));
        cmbClienteBuscar.setItems(FXCollections.observableArrayList(clientes));
        cmbClienteAlterar.setItems(FXCollections.observableArrayList(clientes));
        
        // Carregar serviços - buscar todos usando faixa de preço ampla
        List<Servico> servicos = servicoDAO.buscarServicosPorFaixaPreco(0, Integer.MAX_VALUE);
        cmbServicoIncluir.setItems(FXCollections.observableArrayList(servicos));
        cmbServicoAlterar.setItems(FXCollections.observableArrayList(servicos));
        
        // Configurar listeners para atualizar pets quando cliente for selecionado
        cmbClienteIncluir.setOnAction(e -> atualizarPetsCliente(cmbClienteIncluir, cmbPetIncluir));
        cmbClienteAlterar.setOnAction(e -> atualizarPetsCliente(cmbClienteAlterar, cmbPetAlterar));
    }

    private void atualizarPetsCliente(ComboBox<Cliente> cmbCliente, ComboBox<Pet> cmbPet) {
        try {
            Cliente clienteSelecionado = cmbCliente.getSelectionModel().getSelectedItem();
            if (clienteSelecionado != null) {
                List<Pet> pets = petDAO.buscarPetsPorCpfDono(clienteSelecionado.getCpf());
                cmbPet.setItems(FXCollections.observableArrayList(pets));
                cmbPet.getSelectionModel().clearSelection();
            } else {
                cmbPet.setItems(FXCollections.observableArrayList());
            }
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao carregar pets do cliente: " + e.getMessage());
        }
    }

    @FXML
    public void incluirAgendamento() {
        try {
            Pet pet = cmbPetIncluir.getSelectionModel().getSelectedItem();
            Servico servico = cmbServicoIncluir.getSelectionModel().getSelectedItem();
            LocalDate data = dtpDataIncluir.getValue();

            if (pet == null || servico == null || data == null) {
                mostrarAlerta("Campos obrigatórios", "Todos os campos devem ser preenchidos");
                return;
            }

            Agendar agendamento = new Agendar(0, data, pet.getId(), servico.getId());
            
            boolean sucesso = agendarDAO.incluirAgendamento(agendamento);
            if (sucesso) {
                mostrarSucesso("Agendamento incluído com sucesso!");
                limparCamposInclusao();
                listarTodos();
            } else {
                mostrarAlerta("Erro", "Erro ao incluir agendamento");
            }

        } catch (IllegalArgumentException e) {
            mostrarAlerta("Erro de validação", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao incluir agendamento: " + e.getMessage());
        }
    }

    @FXML
    public void buscarAgendamentos() {
        try {
            Cliente cliente = cmbClienteBuscar.getSelectionModel().getSelectedItem();
            if (cliente == null) {
                mostrarAlerta("Seleção necessária", "Selecione um cliente para buscar seus agendamentos");
                return;
            }

            // Buscar os pets do cliente e depois os agendamentos de cada pet
            List<Pet> pets = petDAO.buscarPetsPorCpfDono(cliente.getCpf());
            List<Agendar> todosAgendamentos = new ArrayList<>();
            
            for (Pet pet : pets) {
                List<Agendar> agendamentosDoPet = agendarDAO.buscarAgendamentosPorPet(pet.getId());
                todosAgendamentos.addAll(agendamentosDoPet);
            }
            
            atualizarListaAgendamentos(todosAgendamentos);

        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao buscar agendamentos: " + e.getMessage());
        }
    }

    @FXML
    public void listarTodos() {
        try {
            List<Agendar> agendamentos = agendarDAO.listarTodosAgendamentos();
            atualizarListaAgendamentos(agendamentos);

        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao listar agendamentos: " + e.getMessage());
        }
    }

    @FXML
    public void alterarAgendamento() {
        try {
            if (agendamentoAtual == null) {
                mostrarAlerta("Seleção necessária", "Selecione um agendamento para alterar");
                return;
            }

            Pet pet = cmbPetAlterar.getSelectionModel().getSelectedItem();
            Servico servico = cmbServicoAlterar.getSelectionModel().getSelectedItem();
            LocalDate data = dtpDataAlterar.getValue();

            if (pet == null || servico == null || data == null) {
                mostrarAlerta("Campos obrigatórios", "Todos os campos devem ser preenchidos");
                return;
            }

            agendamentoAtual.setData(data);
            agendamentoAtual.setIdPet(pet.getId());
            agendamentoAtual.setIdServico(servico.getId());

            boolean sucesso = agendarDAO.alterarAgendamento(agendamentoAtual);
            if (sucesso) {
                mostrarSucesso("Agendamento alterado com sucesso!");
                limparCampos();
                listarTodos();
            } else {
                mostrarAlerta("Erro", "Erro ao alterar agendamento");
            }

        } catch (IllegalArgumentException e) {
            mostrarAlerta("Erro de validação", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao alterar agendamento: " + e.getMessage());
        }
    }

    @FXML
    public void excluirAgendamento() {
        try {
            if (agendamentoAtual == null) {
                mostrarAlerta("Seleção necessária", "Selecione um agendamento para excluir");
                return;
            }

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmação de Exclusão");
            confirmacao.setHeaderText("Deseja realmente excluir este agendamento?");
            confirmacao.setContentText("Esta ação não pode ser desfeita.");

            if (confirmacao.showAndWait().get() == ButtonType.OK) {
                boolean sucesso = agendarDAO.excluirAgendamento(
                    agendamentoAtual.getIdPet(),
                    agendamentoAtual.getIdServico()
                );

                if (sucesso) {
                    mostrarSucesso("Agendamento excluído com sucesso!");
                    limparCampos();
                    listarTodos();
                } else {
                    mostrarAlerta("Erro", "Erro ao excluir agendamento");
                }
            }

        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao excluir agendamento: " + e.getMessage());
        }
    }

    @FXML
    public void selecionarAgendamento() {
        try {
            String itemSelecionado = listViewAgendamentos.getSelectionModel().getSelectedItem();
            if (itemSelecionado != null && !itemSelecionado.isEmpty()) {
                // Extrair ID do agendamento do texto (assumindo formato específico)
                // Esta implementação é simplificada - numa implementação real, 
                // você manteria uma lista paralela de objetos Agendar
                int inicioId = itemSelecionado.indexOf("ID: ") + 4;
                int fimId = itemSelecionado.indexOf(",", inicioId);
                if (inicioId > 3 && fimId > inicioId) {
                    int id = Integer.parseInt(itemSelecionado.substring(inicioId, fimId));
                    
                    // Buscar o agendamento completo
                    List<Agendar> todos = agendarDAO.listarTodosAgendamentos();
                    for (Agendar agendamento : todos) {
                        if (agendamento.getId() == id) {
                            agendamentoAtual = agendamento;
                            preencherCamposAlteracao();
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao selecionar agendamento: " + e.getMessage());
        }
    }

    private void preencherCamposAlteracao() {
        if (agendamentoAtual != null) {
            try {
                txtIdAlterar.setText(String.valueOf(agendamentoAtual.getId()));
                
                // Buscar o pet pelo ID para obter o dono
                Pet pet = petDAO.buscarPet(agendamentoAtual.getIdPet());
                if (pet != null && pet.getDono() != null) {
                    // Selecionar cliente
                    for (Cliente cliente : cmbClienteAlterar.getItems()) {
                        if (cliente.getCpf().equals(pet.getDono().getCpf())) {
                            cmbClienteAlterar.getSelectionModel().select(cliente);
                            break;
                        }
                    }
                    
                    // Atualizar pets e selecionar pet
                    atualizarPetsCliente(cmbClienteAlterar, cmbPetAlterar);
                    for (Pet p : cmbPetAlterar.getItems()) {
                        if (p.getId() == agendamentoAtual.getIdPet()) {
                            cmbPetAlterar.getSelectionModel().select(p);
                            break;
                        }
                    }
                }
                
                // Selecionar serviço
                for (Servico servico : cmbServicoAlterar.getItems()) {
                    if (servico.getId() == agendamentoAtual.getIdServico()) {
                        cmbServicoAlterar.getSelectionModel().select(servico);
                        break;
                    }
                }
                
                dtpDataAlterar.setValue(agendamentoAtual.getData());
                
                gridAlteracao.setVisible(true);
                botoesAcao.setVisible(true);
            } catch (Exception e) {
                mostrarAlerta("Erro", "Erro ao preencher campos: " + e.getMessage());
            }
        }
    }

    private void atualizarListaAgendamentos(List<Agendar> agendamentos) {
        listaAgendamentos.clear();
        try {
            for (Agendar agendamento : agendamentos) {
                // Buscar pet e serviço pelos IDs
                Pet pet = petDAO.buscarPet(agendamento.getIdPet());
                Servico servico = servicoDAO.buscarServico(agendamento.getIdServico());
                
                // Buscar cliente através do pet
                Cliente cliente = null;
                if (pet != null && pet.getDono() != null) {
                    cliente = clienteDAO.buscarClientePorCPF(pet.getDono().getCpf());
                }
                
                String item = String.format("ID: %d, Data: %s, Cliente: %s, Pet: %s, Serviço: %s",
                    agendamento.getId(),
                    agendamento.getData() != null ? agendamento.getData().toString() : "N/A",
                    cliente != null ? cliente.getNome() : "N/A",
                    pet != null ? pet.getNome() : "N/A",
                    servico != null ? servico.getNome() : "N/A");
                listaAgendamentos.add(item);
            }
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao atualizar lista: " + e.getMessage());
        }
    }

    private void limparCampos() {
        limparCamposInclusao();
        limparCamposAlteracao();
    }

    private void limparCamposInclusao() {
        cmbClienteIncluir.getSelectionModel().clearSelection();
        cmbPetIncluir.getItems().clear();
        cmbServicoIncluir.getSelectionModel().clearSelection();
        dtpDataIncluir.setValue(null);
    }

    private void limparCamposAlteracao() {
        agendamentoAtual = null;
        txtIdAlterar.clear();
        cmbClienteAlterar.getSelectionModel().clearSelection();
        cmbPetAlterar.getItems().clear();
        cmbServicoAlterar.getSelectionModel().clearSelection();
        dtpDataAlterar.setValue(null);
        gridAlteracao.setVisible(false);
        botoesAcao.setVisible(false);
    }

    @FXML
    public void fechar() {
        // Implementar fechamento da janela
        btnFechar.getScene().getWindow().hide();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}