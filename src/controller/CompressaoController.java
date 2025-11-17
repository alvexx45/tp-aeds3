package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import dao.CompressaoManager;

public class CompressaoController {

    @FXML
    private Button btnComprimir;
    
    @FXML
    private Button btnDescomprimir;
    
    @FXML
    private Label lblStatus;
    
    @FXML
    private Button btnFechar;
    
    private CompressaoManager compressaoManager;
    
    @FXML
    public void initialize() {
        compressaoManager = new CompressaoManager();
        verificarEstadoCompressao();
    }
    
    @FXML
    private void comprimirDados() {
        try {
            lblStatus.setText("Comprimindo dados...");
            btnComprimir.setDisable(true);
            btnDescomprimir.setDisable(true);
            
            // Executa a compressão em uma thread separada para não bloquear a UI
            new Thread(() -> {
                try {
                    String algoritmo = "LZW";
                    compressaoManager.comprimirDados(algoritmo);
                    
                    Platform.runLater(() -> {
                        lblStatus.setText("Dados comprimidos com sucesso usando " + algoritmo + "!");
                        mostrarInfo("Sucesso", "Dados comprimidos com sucesso!");
                        verificarEstadoCompressao();
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        lblStatus.setText("Erro ao comprimir dados: " + e.getMessage());
                        mostrarErro("Erro", "Erro ao comprimir dados: " + e.getMessage());
                        verificarEstadoCompressao();
                    });
                }
            }).start();
            
        } catch (Exception e) {
            lblStatus.setText("Erro: " + e.getMessage());
            mostrarErro("Erro", "Erro ao iniciar compressão: " + e.getMessage());
            verificarEstadoCompressao();
        }
    }
    
    @FXML
    private void descomprimirDados() {
        try {
            lblStatus.setText("Descomprimindo dados...");
            btnComprimir.setDisable(true);
            btnDescomprimir.setDisable(true);
            
            // Executa a descompressão em uma thread separada para não bloquear a UI
            new Thread(() -> {
                try {
                    compressaoManager.descomprimirDados();
                    
                    Platform.runLater(() -> {
                        lblStatus.setText("Dados descomprimidos com sucesso!");
                        mostrarInfo("Sucesso", "Dados descomprimidos com sucesso!");
                        verificarEstadoCompressao();
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        lblStatus.setText("Erro ao descomprimir dados: " + e.getMessage());
                        mostrarErro("Erro", "Erro ao descomprimir dados: " + e.getMessage());
                        verificarEstadoCompressao();
                    });
                }
            }).start();
            
        } catch (Exception e) {
            lblStatus.setText("Erro: " + e.getMessage());
            mostrarErro("Erro", "Erro ao iniciar descompressão: " + e.getMessage());
            verificarEstadoCompressao();
        }
    }
    
    @FXML
    private void fechar() {
        Stage stage = (Stage) btnFechar.getScene().getWindow();
        stage.close();
    }
    
    private void verificarEstadoCompressao() {
        boolean dadosComprimidos = compressaoManager.verificarDadosComprimidos();
        
        btnComprimir.setDisable(dadosComprimidos);
        btnDescomprimir.setDisable(!dadosComprimidos);
        
        if (dadosComprimidos) {
            String algoritmo = compressaoManager.obterAlgoritmoUtilizado();
            lblStatus.setText("Dados atualmente comprimidos com " + algoritmo);
        } else {
            lblStatus.setText("Dados não comprimidos");
        }
    }
    
    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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
