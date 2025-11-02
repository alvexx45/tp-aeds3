package controller;

import dao.AgendarDAO;
import dao.PetDAO;
import dao.ServicoDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Agendar;
import model.Pet;
import model.Servico;

import java.time.LocalDate;
import java.util.List;

public class AlterarAgendamentoDialogController {
    @FXML private TextField txtCpfCliente;
    @FXML private ComboBox<Pet> cmbPet;
    @FXML private ComboBox<Servico> cmbServico;
    @FXML private DatePicker dtpData;
    @FXML private Button btnAlterar;
    @FXML private Button btnExcluir;

    private AgendarDAO agendarDAO;
    private PetDAO petDAO;
    private ServicoDAO servicoDAO;
    private Agendar agendamentoSelecionado;
    private Runnable onCloseCallback;

    @FXML
    public void initialize() {
        try {
            agendarDAO = new AgendarDAO();
            petDAO = new PetDAO();
            servicoDAO = new ServicoDAO();
            
            // Carregar todos os serviços
            List<Servico> servicos = servicoDAO.buscarServicosPorFaixaPreco(0, Integer.MAX_VALUE);
            cmbServico.setItems(FXCollections.observableArrayList(servicos));
            
            // Listener para CPF - carregar pets do cliente
            txtCpfCliente.textProperty().addListener((observable, oldValue, newValue) -> {
                atualizarPetsPorCpf();
            });
            
        } catch (Exception e) {
            mostrarErro("Erro ao inicializar", e.getMessage());
        }
    }

    public void setAgendamento(Agendar agendamento) {
        this.agendamentoSelecionado = agendamento;
        
        if (agendamento != null) {
            try {
                // Buscar o pet e o cliente
                Pet pet = petDAO.buscarPet(agendamento.getIdPet());
                if (pet != null && pet.getDono() != null) {
                    String cpfCliente = pet.getDono().getCpf();
                    txtCpfCliente.setText(cpfCliente);
                    
                    // Selecionar o pet no combobox
                    for (Pet p : cmbPet.getItems()) {
                        if (p.getId() == agendamento.getIdPet()) {
                            cmbPet.setValue(p);
                            break;
                        }
                    }
                }
                
                // Selecionar o serviço
                for (Servico s : cmbServico.getItems()) {
                    if (s.getId() == agendamento.getIdServico()) {
                        cmbServico.setValue(s);
                        break;
                    }
                }
                
                // Definir a data
                dtpData.setValue(agendamento.getData());
                
            } catch (Exception e) {
                mostrarErro("Erro ao carregar agendamento", e.getMessage());
            }
        }
    }

    public void setOnCloseCallback(Runnable callback) {
        this.onCloseCallback = callback;
    }

    private void atualizarPetsPorCpf() {
        String cpf = txtCpfCliente.getText().replaceAll("[^0-9]", "");
        
        if (cpf.length() == 11) {
            try {
                cmbPet.getItems().clear();
                cmbPet.getItems().setAll(petDAO.buscarPetsPorCpfDono(cpf));
                
                // Se há um agendamento selecionado, reselecionar o pet
                if (agendamentoSelecionado != null) {
                    for (Pet p : cmbPet.getItems()) {
                        if (p.getId() == agendamentoSelecionado.getIdPet()) {
                            cmbPet.setValue(p);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                mostrarErro("Erro ao buscar pets", e.getMessage());
            }
        }
    }

    @FXML
    private void alterarAgendamento() {
        if (!validarCampos()) {
            return;
        }

        try {
            Pet petSelecionado = cmbPet.getValue();
            Servico servicoSelecionado = cmbServico.getValue();
            LocalDate data = dtpData.getValue();

            // Validar data
            if (data.isBefore(LocalDate.now())) {
                mostrarErro("Data inválida", "Não é possível agendar em datas passadas.");
                return;
            }

            // Atualizar o agendamento
            agendamentoSelecionado.setIdPet(petSelecionado.getId());
            agendamentoSelecionado.setIdServico(servicoSelecionado.getId());
            agendamentoSelecionado.setData(data);

            agendarDAO.alterarAgendamento(agendamentoSelecionado);
            
            mostrarSucesso("Agendamento alterado com sucesso!");
            fechar();
            
        } catch (Exception e) {
            mostrarErro("Erro ao alterar agendamento", e.getMessage());
        }
    }

    @FXML
    private void excluirAgendamento() {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Deseja realmente excluir este agendamento?");
        confirmacao.setContentText("Esta ação não pode ser desfeita.");

        if (confirmacao.showAndWait().get() == ButtonType.OK) {
            try {
                agendarDAO.excluirAgendamento(agendamentoSelecionado.getIdPet(), agendamentoSelecionado.getIdServico());
                mostrarSucesso("Agendamento excluído com sucesso!");
                fechar();
            } catch (Exception e) {
                mostrarErro("Erro ao excluir agendamento", e.getMessage());
            }
        }
    }

    @FXML
    private void fechar() {
        // Executar callback para atualizar a lista na tela principal
        if (onCloseCallback != null) {
            onCloseCallback.run();
        }
        
        // Fechar a janela do dialog
        Stage stage = (Stage) txtCpfCliente.getScene().getWindow();
        stage.close();
    }

    private boolean validarCampos() {
        String cpf = txtCpfCliente.getText().replaceAll("[^0-9]", "");
        
        if (cpf.isEmpty() || cpf.length() != 11) {
            mostrarErro("Erro de validação", "CPF inválido.");
            return false;
        }
        
        if (cmbPet.getValue() == null) {
            mostrarErro("Erro de validação", "Selecione um pet.");
            return false;
        }
        
        if (cmbServico.getValue() == null) {
            mostrarErro("Erro de validação", "Selecione um serviço.");
            return false;
        }
        
        if (dtpData.getValue() == null) {
            mostrarErro("Erro de validação", "Selecione uma data.");
            return false;
        }
        
        return true;
    }

    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
