package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import app.BateriaTestes;

public class MainController {

    @FXML
    private Button btnClientes;
    
    @FXML
    private Button btnPets;
    
    @FXML
    private Button btnServicos;
    
    @FXML
    private Button btnTestes;
    
    @FXML
    private Button btnSair;

    @FXML
    private void abrirGerenciarClientes() {
        try {
            abrirJanela("/view/ClienteView.fxml", "Gerenciar Clientes");
        } catch (Exception e) {
            mostrarErro("Erro ao abrir tela de clientes", e.getMessage());
        }
    }

    @FXML
    private void abrirGerenciarPets() {
        try {
            abrirJanela("/view/PetView.fxml", "Gerenciar Pets");
        } catch (Exception e) {
            mostrarErro("Erro ao abrir tela de pets", e.getMessage());
        }
    }

    @FXML
    private void abrirGerenciarServicos() {
        try {
            abrirJanela("/view/ServicoView.fxml", "Gerenciar Serviços");
        } catch (Exception e) {
            mostrarErro("Erro ao abrir tela de serviços", e.getMessage());
        }
    }

    @FXML
    private void executarTestes() {
        try {
            // Executa os testes em uma thread separada para não bloquear a UI
            new Thread(() -> {
                try {
                    BateriaTestes bateria = new BateriaTestes();
                    bateria.executar();
                    Platform.runLater(() -> mostrarInfo("Testes executados", "Bateria de testes concluída com sucesso!"));
                } catch (Exception e) {
                    Platform.runLater(() -> mostrarErro("Erro ao executar testes", e.getMessage()));
                }
            }).start();
        } catch (Exception e) {
            mostrarErro("Erro ao executar testes", e.getMessage());
        }
    }

    @FXML
    private void sair() {
        Platform.exit();
    }

    private void abrirJanela(String fxmlPath, String titulo) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        
        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getScene().getStylesheets().add(getClass().getResource("/css/Style.css").toExternalForm());
        stage.showAndWait();
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