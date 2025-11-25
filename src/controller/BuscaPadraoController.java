package controller;

import algorithms.BoyerM;
import algorithms.Kmp;
import dao.ClienteDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Cliente;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller para busca de clientes por padrão usando KMP ou Boyer-Moore
 */
public class BuscaPadraoController {
    
    @FXML private RadioButton radioKMP;
    @FXML private RadioButton radioBoyerMoore;
    @FXML private TextField txtPadrao;
    @FXML private Button btnBuscar;
    @FXML private Button btnLimpar;
    @FXML private Button btnFechar;
    @FXML private Label lblInfo;
    @FXML private Label lblTotal;
    @FXML private Label lblTempo;
    
    @FXML private TableView<Cliente> tableResultados;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNome;
    
    private ClienteDAO clienteDAO;
    private ObservableList<Cliente> resultados;
    
    @FXML
    public void initialize() {
        try {
            clienteDAO = new ClienteDAO();
            resultados = FXCollections.observableArrayList();
            
            // Configurar colunas da tabela (apenas ID e Nome)
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
            
            tableResultados.setItems(resultados);
            
            // Habilitar busca ao pressionar Enter
            txtPadrao.setOnAction(event -> buscarPorPadrao());
            
        } catch (Exception e) {
            mostrarErro("Erro ao inicializar", "Erro ao inicializar o sistema de busca: " + e.getMessage());
        }
    }
    
    @FXML
    private void buscarPorPadrao() {
        String padrao = txtPadrao.getText();
        
        // Validar entrada
        if (padrao == null || padrao.trim().isEmpty()) {
            lblInfo.setText("⚠ Por favor, informe um padrão para buscar.");
            lblInfo.setStyle("-fx-text-fill: orange;");
            return;
        }
        
        try {
            // Determinar algoritmo escolhido
            String algoritmo = radioKMP.isSelected() ? "KMP" : "Boyer-Moore";
            
            lblInfo.setText("Buscando usando " + algoritmo + "...");
            lblInfo.setStyle("-fx-text-fill: blue;");
            
            // Medir tempo de execução
            long inicio = System.nanoTime();
            
            // Executar busca
            List<Cliente> clientes;
            if (radioKMP.isSelected()) {
                clientes = buscarComKMP(padrao);
            } else {
                clientes = buscarComBoyerMoore(padrao);
            }
            
            long fim = System.nanoTime();
            double tempoMs = (fim - inicio) / 1_000_000.0;
            
            // Atualizar resultados
            resultados.clear();
            resultados.addAll(clientes);
            
            // Atualizar labels
            lblTotal.setText("Total de registros: " + clientes.size());
            lblTempo.setText(String.format("Tempo de execução: %.3f ms", tempoMs));
            
            if (clientes.isEmpty()) {
                lblInfo.setText("ℹ Nenhum cliente encontrado com o padrão \"" + padrao + "\".");
                lblInfo.setStyle("-fx-text-fill: gray;");
            } else {
                lblInfo.setText("✓ Busca concluída usando " + algoritmo + ". Encontrados " + clientes.size() + " resultado(s).");
                lblInfo.setStyle("-fx-text-fill: green;");
            }
            
        } catch (Exception e) {
            mostrarErro("Erro na busca", "Erro ao buscar padrão: " + e.getMessage());
            lblInfo.setText("✗ Erro ao realizar busca.");
            lblInfo.setStyle("-fx-text-fill: red;");
        }
    }
    
    /**
     * Busca usando algoritmo KMP (case-insensitive)
     */
    private List<Cliente> buscarComKMP(String padrao) throws Exception {
        List<Cliente> todosClientes = clienteDAO.listarTodos();
        List<Cliente> encontrados = new ArrayList<>();
        
        // Converter padrão para lowercase para busca case-insensitive
        String padraoLower = padrao.toLowerCase();
        
        // Buscar em cada cliente usando o método contains do KMP
        for (Cliente cliente : todosClientes) {
            String nomeLower = cliente.getNome().toLowerCase();
            
            // Usar Kmp.contains para verificar se o padrão existe no nome
            if (Kmp.contains(nomeLower, padraoLower)) {
                encontrados.add(cliente);
            }
        }
        
        return encontrados;
    }
    
    /**
     * Busca usando algoritmo Boyer-Moore (Bad Character) (case-insensitive)
     */
    private List<Cliente> buscarComBoyerMoore(String padrao) throws Exception {
        List<Cliente> todosClientes = clienteDAO.listarTodos();
        List<Cliente> encontrados = new ArrayList<>();
        
        // Converter padrão para lowercase para busca case-insensitive
        String padraoLower = padrao.toLowerCase();
        char[] patArray = padraoLower.toCharArray();
        
        // Buscar em cada cliente usando o método contains do BoyerM
        for (Cliente cliente : todosClientes) {
            String nomeLower = cliente.getNome().toLowerCase();
            char[] txtArray = nomeLower.toCharArray();
            
            // Usar BoyerM.contains para verificar se o padrão existe no nome
            if (BoyerM.contains(txtArray, patArray)) {
                encontrados.add(cliente);
            }
        }
        
        return encontrados;
    }
    
    @FXML
    private void limparResultados() {
        txtPadrao.clear();
        resultados.clear();
        lblInfo.setText("");
        lblTotal.setText("Total de registros: 0");
        lblTempo.setText("");
        txtPadrao.requestFocus();
    }
    
    @FXML
    private void fechar() {
        Stage stage = (Stage) btnFechar.getScene().getWindow();
        stage.close();
    }
    
    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
