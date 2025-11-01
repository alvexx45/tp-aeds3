package controller;

import dao.ServicoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Servico;

import java.net.URL;
import java.util.ResourceBundle;

public class ServicoController implements Initializable {

    @FXML private TextField txtNomeIncluir;
    @FXML private TextField txtValorIncluir;
    @FXML private Button btnIncluir;

    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private GridPane gridAlteracao;
    @FXML private TextField txtIdAlterar;
    @FXML private TextField txtNomeAlterar;
    @FXML private TextField txtValorAlterar;
    @FXML private HBox botoesAcao;
    @FXML private Button btnAlterar;
    @FXML private Button btnExcluir;

    @FXML private TextField txtValorMin;
    @FXML private TextField txtValorMax;
    @FXML private Button btnBuscarPorFaixa;
    @FXML private Button btnListarTodos;
    @FXML private ListView<String> listViewServicos;

    @FXML private Button btnFechar;

    private ServicoDAO servicoDAO;
    private Servico servicoAtual;
    private ObservableList<String> listaServicos;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            servicoDAO = new ServicoDAO();
            listaServicos = FXCollections.observableArrayList();
            listViewServicos.setItems(listaServicos);
            
            // Listener para buscar automaticamente ao clicar em um serviço
            listViewServicos.setOnMouseClicked(event -> {
                String servicoSelecionado = listViewServicos.getSelectionModel().getSelectedItem();
                if (servicoSelecionado != null && !servicoSelecionado.isEmpty()) {
                    // Extrair o ID do serviço (formato: "ID: 1 | ...")
                    String[] partes = servicoSelecionado.split("\\|");
                    if (partes.length > 0) {
                        String id = partes[0].replace("ID:", "").trim();
                        txtBuscar.setText(id);
                        buscarServico(); // Aciona a busca automaticamente
                    }
                }
            });
        } catch (Exception e) {
            mostrarErro("Erro de Inicialização", "Erro ao inicializar DAO: " + e.getMessage());
        }
    }

    @FXML
    private void incluirServico() {
        try {
            String nome = txtNomeIncluir.getText().trim();
            String valorStr = txtValorIncluir.getText().trim();

            if (nome.isEmpty() || valorStr.isEmpty()) {
                mostrarAviso("Campos Obrigatórios", "Nome e valor são obrigatórios!");
                return;
            }

            int valor;
            try {
                valor = Integer.parseInt(valorStr);
                if (valor < 0) {
                    mostrarAviso("Valor Inválido", "O valor deve ser um número positivo!");
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarAviso("Valor Inválido", "Digite um valor válido (apenas números)!");
                return;
            }

            Servico servico = new Servico(-1, nome, valor);
            boolean sucesso = servicoDAO.incluirServico(servico);
            
            if (sucesso) {
                mostrarSucesso("Serviço Incluído", "Serviço incluído com sucesso!");
                limparCamposInclusao();
            } else {
                mostrarErro("Erro", "Erro ao incluir serviço.");
            }
        } catch (Exception e) {
            mostrarErro("Erro ao incluir serviço", e.getMessage());
        }
    }

    @FXML
    private void buscarServico() {
        try {
            String termo = txtBuscar.getText().trim();
            if (termo.isEmpty()) {
                mostrarAviso("Campo Obrigatório", "Digite o ID do serviço!");
                return;
            }

            Servico servico = null;

            // Tenta buscar por ID primeiro
            try {
                int id = Integer.parseInt(termo);
                servico = servicoDAO.buscarServico(id);
            } catch (NumberFormatException e) {
                // Se não for número, tenta buscar por nome
                servico = servicoDAO.buscarServicoPorNome(termo);
            }

            if (servico != null) {
                servicoAtual = servico;
                preencherCamposAlteracao(servico);
                gridAlteracao.setVisible(true);
                botoesAcao.setVisible(true);
            } else {
                mostrarInfo("Serviço não encontrado", "Nenhum serviço encontrado com o termo: " + termo);
                gridAlteracao.setVisible(false);
                botoesAcao.setVisible(false);
            }
        } catch (Exception e) {
            mostrarErro("Erro ao buscar serviço", e.getMessage());
        }
    }

    @FXML
    private void alterarServico() {
        try {
            if (servicoAtual == null) {
                mostrarAviso("Nenhum serviço selecionado", "Busque um serviço primeiro!");
                return;
            }

            String nome = txtNomeAlterar.getText().trim();
            String valorStr = txtValorAlterar.getText().trim();

            if (nome.isEmpty() || valorStr.isEmpty()) {
                mostrarAviso("Campos Obrigatórios", "Nome e valor são obrigatórios!");
                return;
            }

            int valor;
            try {
                valor = Integer.parseInt(valorStr);
                if (valor < 0) {
                    mostrarAviso("Valor Inválido", "O valor deve ser um número positivo!");
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarAviso("Valor Inválido", "Digite um valor válido!");
                return;
            }

            servicoAtual.setNome(nome);
            servicoAtual.setValor(valor);

            if (servicoDAO.alterarServico(servicoAtual)) {
                mostrarSucesso("Serviço Alterado", "Serviço alterado com sucesso!");
                limparBusca();
                listarTodosServicos(false); // Atualiza a lista dinamicamente sem exibir mensagem
            } else {
                mostrarErro("Erro", "Erro ao alterar serviço.");
            }
        } catch (Exception e) {
            mostrarErro("Erro ao alterar serviço", e.getMessage());
        }
    }

    @FXML
    private void excluirServico() {
        try {
            if (servicoAtual == null) {
                mostrarAviso("Nenhum serviço selecionado", "Busque um serviço primeiro!");
                return;
            }

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Exclusão");
            confirmacao.setHeaderText("Deseja realmente excluir este serviço?");
            confirmacao.setContentText("Serviço: " + servicoAtual.getNome() + " (ID: " + servicoAtual.getId() + ")");

            if (confirmacao.showAndWait().get() == ButtonType.OK) {
                if (servicoDAO.excluirServico(servicoAtual.getId())) {
                    mostrarSucesso("Serviço Excluído", "Serviço excluído com sucesso!");
                    limparBusca();
                    listarTodosServicos(false); // Atualiza a lista dinamicamente sem exibir mensagem
                } else {
                    mostrarErro("Erro", "Erro ao excluir serviço.");
                }
            }
        } catch (Exception e) {
            mostrarErro("Erro ao excluir serviço", e.getMessage());
        }
    }

    @FXML
    private void listarTodosServicos() {
        listarTodosServicos(true);
    }
    
    private void listarTodosServicos(boolean exibirMensagem) {
        try {
            listaServicos.clear();
            // Buscar todos os serviços usando uma faixa muito ampla de preços
            java.util.List<Servico> servicos = servicoDAO.buscarServicosPorFaixaPreco(0, 999999);
            
            if (servicos.isEmpty()) {
                if (exibirMensagem) {
                    mostrarInfo("Nenhum serviço encontrado", "Não há serviços cadastrados no sistema.");
                }
            } else {
                for (Servico servico : servicos) {
                    String info = String.format("ID: %d | %s - R$ %d", 
                        servico.getId(), 
                        servico.getNome(),
                        servico.getValor());
                    listaServicos.add(info);
                }
                if (exibirMensagem) {
                    mostrarSucesso("Serviços Listados", servicos.size() + " serviço(s) encontrado(s).");
                }
            }
        } catch (Exception e) {
            mostrarErro("Erro ao listar serviços", e.getMessage());
        }
    }

    @FXML
    private void buscarServicosPorFaixa() {
        try {
            String valorMinStr = txtValorMin.getText().trim();
            String valorMaxStr = txtValorMax.getText().trim();
            
            if (valorMinStr.isEmpty() || valorMaxStr.isEmpty()) {
                mostrarAviso("Campos Obrigatórios", "Digite os valores mínimo e máximo para buscar!");
                return;
            }
            
            int valorMin, valorMax;
            try {
                valorMin = Integer.parseInt(valorMinStr);
                valorMax = Integer.parseInt(valorMaxStr);
                
                if (valorMin < 0 || valorMax < 0) {
                    mostrarAviso("Valores Inválidos", "Os valores devem ser números positivos!");
                    return;
                }
                
                if (valorMin > valorMax) {
                    mostrarAviso("Faixa Inválida", "O valor mínimo não pode ser maior que o máximo!");
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarAviso("Valores Inválidos", "Digite valores válidos (apenas números)!");
                return;
            }
            
            listaServicos.clear();
            java.util.List<Servico> servicos = servicoDAO.buscarServicosPorFaixaPreco(valorMin, valorMax);
            
            if (servicos.isEmpty()) {
                mostrarInfo("Nenhum serviço encontrado", 
                    String.format("Nenhum serviço encontrado na faixa de R$ %d - R$ %d", valorMin, valorMax));
            } else {
                for (Servico servico : servicos) {
                    String info = String.format("ID: %d | %s - R$ %d", 
                        servico.getId(), 
                        servico.getNome(),
                        servico.getValor());
                    listaServicos.add(info);
                }
                mostrarSucesso("Busca Realizada", 
                    String.format("%d serviço(s) encontrado(s) na faixa de R$ %d - R$ %d", 
                        servicos.size(), valorMin, valorMax));
            }
        } catch (Exception e) {
            mostrarErro("Erro ao buscar serviços por faixa", e.getMessage());
        }
    }

    @FXML
    private void fecharJanela() {
        Stage stage = (Stage) btnFechar.getScene().getWindow();
        stage.close();
    }

    private void preencherCamposAlteracao(Servico servico) {
        txtIdAlterar.setText(String.valueOf(servico.getId()));
        txtNomeAlterar.setText(servico.getNome());
        txtValorAlterar.setText(String.valueOf(servico.getValor()));
    }

    private void limparCamposInclusao() {
        txtNomeIncluir.clear();
        txtValorIncluir.clear();
    }

    private void limparBusca() {
        txtBuscar.clear();
        txtIdAlterar.clear();
        txtNomeAlterar.clear();
        txtValorAlterar.clear();
        gridAlteracao.setVisible(false);
        botoesAcao.setVisible(false);
        servicoAtual = null;
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