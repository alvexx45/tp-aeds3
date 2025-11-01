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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AgendarController implements Initializable {

    // Campos para inclusão de agendamento
    @FXML private TextField txtCpfClienteIncluir;
    @FXML private ComboBox<Pet> cmbPetIncluir;
    @FXML private ComboBox<Servico> cmbServicoIncluir;
    @FXML private DatePicker dtpDataIncluir;
    @FXML private Button btnIncluir;

    // Campos para busca
    @FXML private TextField txtCpfClienteBuscar;
    @FXML private Button btnBuscar;
    @FXML private GridPane gridAlteracao;
    @FXML private TextField txtIdAlterar;
    @FXML private TextField txtCpfClienteAlterar;
    @FXML private ComboBox<Pet> cmbPetAlterar;
    @FXML private ComboBox<Servico> cmbServicoAlterar;
    @FXML private DatePicker dtpDataAlterar;
    @FXML private HBox botoesAcao;
    @FXML private Button btnAlterar;
    @FXML private Button btnExcluir;

    // Lista de agendamentos
    @FXML private Button btnListarTodos;
    @FXML private ListView<String> listViewAgendamentosFuturos;
    @FXML private ListView<String> listViewAgendamentosPassados;

    @FXML private Button btnFechar;

    private AgendarDAO agendarDAO;
    private ClienteDAO clienteDAO;
    private PetDAO petDAO;
    private ServicoDAO servicoDAO;
    
    private Agendar agendamentoAtual;
    private ObservableList<String> listaAgendamentosFuturos;
    private ObservableList<String> listaAgendamentosPassados;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            agendarDAO = new AgendarDAO();
            clienteDAO = new ClienteDAO();
            petDAO = new PetDAO();
            servicoDAO = new ServicoDAO();
            
            listaAgendamentosFuturos = FXCollections.observableArrayList();
            listaAgendamentosPassados = FXCollections.observableArrayList();
            listViewAgendamentosFuturos.setItems(listaAgendamentosFuturos);
            listViewAgendamentosPassados.setItems(listaAgendamentosPassados);
            
            inicializarComboBoxes();
            limparCampos();
            
        } catch (Exception e) {
            mostrarAlerta("Erro ao inicializar", "Erro ao inicializar o controlador: " + e.getMessage());
        }
    }

    private void inicializarComboBoxes() throws Exception {
        // Carregar serviços - buscar todos usando faixa de preço ampla
        List<Servico> servicos = servicoDAO.buscarServicosPorFaixaPreco(0, Integer.MAX_VALUE);
        cmbServicoIncluir.setItems(FXCollections.observableArrayList(servicos));
        cmbServicoAlterar.setItems(FXCollections.observableArrayList(servicos));
        
        // Configurar listeners para atualizar pets quando CPF for digitado
        txtCpfClienteIncluir.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                atualizarPetsPorCpf(newVal, cmbPetIncluir);
            } else {
                cmbPetIncluir.setItems(FXCollections.observableArrayList());
            }
        });
        
        txtCpfClienteAlterar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                atualizarPetsPorCpf(newVal, cmbPetAlterar);
            } else {
                cmbPetAlterar.setItems(FXCollections.observableArrayList());
            }
        });
    }

    private void atualizarPetsPorCpf(String cpf, ComboBox<Pet> cmbPet) {
        try {
            // Remover caracteres não numéricos do CPF
            String cpfLimpo = cpf.replaceAll("[^0-9]", "");
            
            if (cpfLimpo.length() >= 11) {
                List<Pet> pets = petDAO.buscarPetsPorCpfDono(cpfLimpo);
                cmbPet.setItems(FXCollections.observableArrayList(pets));
                if (!pets.isEmpty()) {
                    cmbPet.getSelectionModel().selectFirst();
                }
            } else {
                cmbPet.setItems(FXCollections.observableArrayList());
            }
        } catch (Exception e) {
            cmbPet.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    public void incluirAgendamento() {
        try {
            String cpf = txtCpfClienteIncluir.getText();
            Pet pet = cmbPetIncluir.getSelectionModel().getSelectedItem();
            Servico servico = cmbServicoIncluir.getSelectionModel().getSelectedItem();
            LocalDate data = dtpDataIncluir.getValue();

            if (pet == null || servico == null || data == null) {
                mostrarAlerta("Campos obrigatórios", "Todos os campos devem ser preenchidos");
                return;
            }
            
            // Validar se a data não é passada
            if (data.isBefore(LocalDate.now())) {
                mostrarAlerta("Data inválida", "Não é permitido agendar em datas passadas");
                return;
            }

            Agendar agendamento = new Agendar(0, data, pet.getId(), servico.getId());
            
            boolean sucesso = agendarDAO.incluirAgendamento(agendamento);
            if (sucesso) {
                mostrarSucesso("Agendamento incluído com sucesso!");
                
                // Buscar e exibir agendamentos do cliente que acabou de agendar
                String cpfLimpo = cpf.replaceAll("[^0-9]", "");
                if (cpfLimpo.length() == 11) {
                    List<Pet> pets = petDAO.buscarPetsPorCpfDono(cpfLimpo);
                    List<Agendar> todosAgendamentos = new ArrayList<>();
                    
                    for (Pet p : pets) {
                        List<Agendar> agendamentosDoPet = agendarDAO.buscarAgendamentosPorPet(p.getId());
                        todosAgendamentos.addAll(agendamentosDoPet);
                    }
                    
                    atualizarListaAgendamentos(todosAgendamentos);
                }
                
                limparCamposInclusao();
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
            String cpf = txtCpfClienteBuscar.getText();
            if (cpf == null || cpf.trim().isEmpty()) {
                mostrarAlerta("Seleção necessária", "Digite o CPF do cliente para buscar seus agendamentos");
                return;
            }
            
            // Remover caracteres não numéricos
            String cpfLimpo = cpf.replaceAll("[^0-9]", "");
            if (cpfLimpo.length() != 11) {
                mostrarAlerta("CPF inválido", "Digite um CPF válido com 11 dígitos");
                return;
            }

            // Buscar os pets do cliente e depois os agendamentos de cada pet
            List<Pet> pets = petDAO.buscarPetsPorCpfDono(cpfLimpo);
            
            if (pets.isEmpty()) {
                mostrarAlerta("Nenhum pet encontrado", "Não há pets cadastrados para este CPF");
                return;
            }
            
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
            
            // Validar se a data não é passada
            if (data.isBefore(LocalDate.now())) {
                mostrarAlerta("Data inválida", "Não é permitido agendar em datas passadas");
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
            String itemSelecionado = listViewAgendamentosFuturos.getSelectionModel().getSelectedItem();
            if (itemSelecionado != null && !itemSelecionado.isEmpty()) {
                processarSelecaoAgendamento(itemSelecionado);
            }
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao selecionar agendamento: " + e.getMessage());
        }
    }
    
    private void processarSelecaoAgendamento(String itemSelecionado) throws Exception {
        // Extrair ID do agendamento do texto
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

    private void preencherCamposAlteracao() {
        if (agendamentoAtual != null) {
            try {
                txtIdAlterar.setText(String.valueOf(agendamentoAtual.getId()));
                
                // Buscar o pet pelo ID para obter o dono
                Pet pet = petDAO.buscarPet(agendamentoAtual.getIdPet());
                if (pet != null && pet.getDono() != null) {
                    // Preencher CPF do cliente
                    txtCpfClienteAlterar.setText(pet.getDono().getCpf());
                    
                    // Atualizar pets e selecionar pet
                    atualizarPetsPorCpf(pet.getDono().getCpf(), cmbPetAlterar);
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
        listaAgendamentosFuturos.clear();
        listaAgendamentosPassados.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate hoje = LocalDate.now();
        
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
                
                String dataFormatada = agendamento.getData() != null ? 
                    agendamento.getData().format(formatter) : "N/A";
                
                String item = String.format("ID: %d, Data: %s, Cliente: %s, Pet: %s, Serviço: %s",
                    agendamento.getId(),
                    dataFormatada,
                    cliente != null ? cliente.getNome() : "N/A",
                    pet != null ? pet.getNome() : "N/A",
                    servico != null ? servico.getNome() : "N/A");
                
                // Separar entre futuros e passados
                if (agendamento.getData() != null && agendamento.getData().isBefore(hoje)) {
                    listaAgendamentosPassados.add(item);
                } else {
                    listaAgendamentosFuturos.add(item);
                }
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
        txtCpfClienteIncluir.clear();
        cmbPetIncluir.getItems().clear();
        cmbServicoIncluir.getSelectionModel().clearSelection();
        dtpDataIncluir.setValue(null);
    }

    private void limparCamposAlteracao() {
        agendamentoAtual = null;
        txtIdAlterar.clear();
        txtCpfClienteAlterar.clear();
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