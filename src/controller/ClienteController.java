package controller;

import dao.ClienteDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Cliente;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ClienteController implements Initializable {

    @FXML private TextField txtCpfIncluir;
    @FXML private TextField txtNomeIncluir;
    @FXML private TextField txtEmailIncluir;
    @FXML private TextField txtTelefonesIncluir;
    @FXML private Button btnIncluir;

    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private GridPane gridAlteracao;
    @FXML private TextField txtIdAlterar;
    @FXML private TextField txtCpfAlterar;
    @FXML private TextField txtNomeAlterar;
    @FXML private TextField txtEmailAlterar;
    @FXML private TextField txtTelefonesAlterar;
    @FXML private HBox botoesAcao;
    @FXML private Button btnAlterar;
    @FXML private Button btnExcluir;

    @FXML private Button btnListarTodos;
    @FXML private TextField txtBuscarNome;
    @FXML private Button btnBuscarPorNome;
    @FXML private ListView<String> listViewClientes;

    @FXML private Button btnFechar;

    private ClienteDAO clienteDAO;
    private Cliente clienteAtual;
    private ObservableList<String> listaClientes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            clienteDAO = new ClienteDAO();
            listaClientes = FXCollections.observableArrayList();
            listViewClientes.setItems(listaClientes);
        } catch (Exception e) {
            mostrarErro("Erro de Inicialização", "Erro ao inicializar DAO: " + e.getMessage());
        }
    }

    @FXML
    private void incluirCliente() {
        try {
            String cpf = txtCpfIncluir.getText().trim();
            String nome = txtNomeIncluir.getText().trim();
            String email = txtEmailIncluir.getText().trim();
            String telefonesStr = txtTelefonesIncluir.getText().trim();

            if (cpf.isEmpty() || nome.isEmpty() || email.isEmpty()) {
                mostrarAviso("Campos Obrigatórios", "CPF, Nome e Email são obrigatórios!");
                return;
            }

            String[] telefones = telefonesStr.isEmpty() ? new String[0] : telefonesStr.split(",");
            for (int i = 0; i < telefones.length; i++) {
                telefones[i] = telefones[i].trim();
            }

            Cliente cliente = new Cliente(-1, cpf, nome, email, telefones);
            boolean sucesso = clienteDAO.incluirCliente(cliente);
            
            if (sucesso) {
                mostrarSucesso("Cliente Incluído", "Cliente incluído com sucesso!");
                limparCamposInclusao();
            } else {
                mostrarErro("Erro", "Erro ao incluir cliente.");
            }
        } catch (Exception e) {
            mostrarErro("Erro ao incluir cliente", e.getMessage());
        }
    }

    @FXML
    private void buscarCliente() {
        try {
            String termo = txtBuscar.getText().trim();
            if (termo.isEmpty()) {
                mostrarAviso("Campo Obrigatório", "Digite um termo para busca!");
                return;
            }

            Cliente cliente = null;

            // Tenta buscar por ID primeiro
            try {
                int id = Integer.parseInt(termo);
                cliente = clienteDAO.buscarCliente(id);
            } catch (NumberFormatException e) {
                // Não é um número, tenta outras buscas
            }

            // Se não encontrou por ID, tenta por CPF
            if (cliente == null) {
                cliente = clienteDAO.buscarClientePorCPF(termo);
            }

            // Se não encontrou por CPF, tenta por Email
            if (cliente == null) {
                cliente = clienteDAO.buscarClientePorEmail(termo);
            }

            if (cliente != null) {
                clienteAtual = cliente;
                preencherCamposAlteracao(cliente);
                gridAlteracao.setVisible(true);
                botoesAcao.setVisible(true);
            } else {
                mostrarInfo("Cliente não encontrado", "Nenhum cliente encontrado com o termo: " + termo);
                gridAlteracao.setVisible(false);
                botoesAcao.setVisible(false);
            }
        } catch (Exception e) {
            mostrarErro("Erro ao buscar cliente", e.getMessage());
        }
    }

    @FXML
    private void alterarCliente() {
        try {
            if (clienteAtual == null) {
                mostrarAviso("Nenhum cliente selecionado", "Busque um cliente primeiro!");
                return;
            }

            String cpf = txtCpfAlterar.getText().trim();
            String nome = txtNomeAlterar.getText().trim();
            String email = txtEmailAlterar.getText().trim();
            String telefonesStr = txtTelefonesAlterar.getText().trim();

            if (cpf.isEmpty() || nome.isEmpty() || email.isEmpty()) {
                mostrarAviso("Campos Obrigatórios", "CPF, Nome e Email são obrigatórios!");
                return;
            }

            String[] telefones = telefonesStr.isEmpty() ? new String[0] : telefonesStr.split(",");
            for (int i = 0; i < telefones.length; i++) {
                telefones[i] = telefones[i].trim();
            }

            clienteAtual.setCpf(cpf);
            clienteAtual.setNome(nome);
            clienteAtual.setEmail(email);
            clienteAtual.setTelefones(telefones);

            if (clienteDAO.alterarCliente(clienteAtual)) {
                mostrarSucesso("Cliente Alterado", "Cliente alterado com sucesso!");
                limparBusca();
                listarTodosClientes(false); // Atualiza a lista dinamicamente sem exibir mensagem
            } else {
                mostrarErro("Erro", "Erro ao alterar cliente.");
            }
        } catch (Exception e) {
            mostrarErro("Erro ao alterar cliente", e.getMessage());
        }
    }

    @FXML
    private void excluirCliente() {
        try {
            if (clienteAtual == null) {
                mostrarAviso("Nenhum cliente selecionado", "Busque um cliente primeiro!");
                return;
            }

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Exclusão");
            confirmacao.setHeaderText("Deseja realmente excluir este cliente?");
            confirmacao.setContentText("Cliente: " + clienteAtual.getNome() + " (ID: " + clienteAtual.getId() + ")");

            if (confirmacao.showAndWait().get() == ButtonType.OK) {
                if (clienteDAO.excluirCliente(clienteAtual.getId())) {
                    mostrarSucesso("Cliente Excluído", "Cliente excluído com sucesso!");
                    limparBusca();
                    listarTodosClientes(false); // Atualiza a lista dinamicamente sem exibir mensagem
                } else {
                    mostrarErro("Erro", "Erro ao excluir cliente.");
                }
            }
        } catch (Exception e) {
            mostrarErro("Erro ao excluir cliente", e.getMessage());
        }
    }

    @FXML
    private void listarTodosClientes() {
        listarTodosClientes(true);
    }
    
    private void listarTodosClientes(boolean exibirMensagem) {
        try {
            listaClientes.clear();
            
            // Buscar clientes por nome usando uma string vazia para pegar todos
            List<Cliente> clientes = clienteDAO.buscarClientesPorNome("");
            
            if (clientes.isEmpty()) {
                if (exibirMensagem) {
                    mostrarInfo("Nenhum cliente encontrado", "Não há clientes cadastrados no sistema.");
                }
            } else {
                for (Cliente cliente : clientes) {
                    String info = String.format("ID: %d | %s - %s | Email: %s | Tel: %s", 
                        cliente.getId(), 
                        cliente.getNome(), 
                        cliente.getCpf(),
                        cliente.getEmail(),
                        String.join(", ", cliente.getTelefones()));
                    listaClientes.add(info);
                }
                if (exibirMensagem) {
                    mostrarSucesso("Clientes Listados", clientes.size() + " cliente(s) encontrado(s).");
                }
            }
        } catch (Exception e) {
            mostrarErro("Erro ao listar clientes", e.getMessage());
        }
    }

    @FXML
    private void buscarClientesPorNome() {
        try {
            String nome = txtBuscarNome.getText().trim();
            if (nome.isEmpty()) {
                mostrarAviso("Campo Obrigatório", "Digite pelo menos parte do nome para buscar!");
                return;
            }
            
            listaClientes.clear();
            List<Cliente> clientes = clienteDAO.buscarClientesPorNome(nome);
            
            if (clientes.isEmpty()) {
                mostrarInfo("Nenhum cliente encontrado", "Nenhum cliente encontrado com o nome: " + nome);
            } else {
                for (Cliente cliente : clientes) {
                    String info = String.format("ID: %d | %s - %s | Email: %s | Tel: %s", 
                        cliente.getId(), 
                        cliente.getNome(), 
                        cliente.getCpf(),
                        cliente.getEmail(),
                        String.join(", ", cliente.getTelefones()));
                    listaClientes.add(info);
                }
                mostrarSucesso("Busca Realizada", clientes.size() + " cliente(s) encontrado(s) com o nome: " + nome);
            }
        } catch (Exception e) {
            mostrarErro("Erro ao buscar clientes por nome", e.getMessage());
        }
    }

    @FXML
    private void fecharJanela() {
        Stage stage = (Stage) btnFechar.getScene().getWindow();
        stage.close();
    }

    private void preencherCamposAlteracao(Cliente cliente) {
        txtIdAlterar.setText(String.valueOf(cliente.getId()));
        txtCpfAlterar.setText(cliente.getCpf());
        txtNomeAlterar.setText(cliente.getNome());
        txtEmailAlterar.setText(cliente.getEmail());
        txtTelefonesAlterar.setText(String.join(", ", cliente.getTelefones()));
    }

    private void limparCamposInclusao() {
        txtCpfIncluir.clear();
        txtNomeIncluir.clear();
        txtEmailIncluir.clear();
        txtTelefonesIncluir.clear();
    }

    private void limparBusca() {
        txtBuscar.clear();
        txtIdAlterar.clear();
        txtCpfAlterar.clear();
        txtNomeAlterar.clear();
        txtEmailAlterar.clear();
        txtTelefonesAlterar.clear();
        gridAlteracao.setVisible(false);
        botoesAcao.setVisible(false);
        clienteAtual = null;
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