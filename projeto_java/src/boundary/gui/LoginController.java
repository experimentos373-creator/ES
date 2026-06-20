package boundary.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import manager.AutenticacaoManager;
import manager.CampeonatoManager;
import domain.*;
import javafx.scene.control.ComboBox;
import java.net.URL;

public class LoginController {

    private final Scene scene;

    public LoginController(Stage stage) {
        this.scene = createScene();
    }

    private Scene createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("login-root");

        // Card Container
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("card");
        card.setMaxWidth(400);

        Label title = new Label("World Cup 2026");
        title.getStyleClass().add("label-title");

        Label subtitle = new Label("Bem-vindo ao Portal de Gestão e Consulta");
        subtitle.getStyleClass().add("label-subtitle");

        TextField userField = new TextField();
        userField.setPromptText("Email (ex: admin@fifa.com)");
        userField.getStyleClass().add("text-field");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Palavra-passe (use 123)");
        passField.getStyleClass().add("password-field");

        Label lblEquipa = new Label("Selecione a sua Equipa:");
        lblEquipa.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #374151;");
        lblEquipa.setVisible(false);
        lblEquipa.setManaged(false);

        ComboBox<String> cmbEquipas = new ComboBox<>();
        for (Equipa eq : CampeonatoManager.getInstance().getEquipas()) {
            cmbEquipas.getItems().add(eq.getNome());
        }
        cmbEquipas.setStyle("-fx-font-size: 12px; -fx-background-color: #FFFFFF; -fx-border-color: #D1D5DB; -fx-border-radius: 6px; -fx-background-radius: 6px;");
        cmbEquipas.setMaxWidth(Double.MAX_VALUE);
        cmbEquipas.setVisible(false);
        cmbEquipas.setManaged(false);

        userField.textProperty().addListener((obs, oldText, newText) -> {
            String email = newText.trim();
            Utilizador u = AutenticacaoManager.getInstance().procurarUtilizadorPorEmail(email);
            if (u != null && u.getCargo() == TipoUtilizador.GESTOR_EQUIPA) {
                lblEquipa.setVisible(true);
                lblEquipa.setManaged(true);
                cmbEquipas.setVisible(true);
                cmbEquipas.setManaged(true);
            } else {
                lblEquipa.setVisible(false);
                lblEquipa.setManaged(false);
                cmbEquipas.setVisible(false);
                cmbEquipas.setManaged(false);
            }
        });

        Button loginBtn = new Button("Login");
        loginBtn.getStyleClass().add("btn-primary");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        
        Button guestBtn = new Button("Portal Público (Adepto)");
        guestBtn.getStyleClass().add("btn-secondary");
        guestBtn.setMaxWidth(Double.MAX_VALUE);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #EF4444;"); // Red color for errors
        errorLabel.setVisible(false);

        Label professorWarning = new Label("⚠️ Nota para o Professor: A palavra-passe é '123' para todos os perfis.");
        professorWarning.setStyle("-fx-text-fill: #92400E; -fx-background-color: #FEF3C7; -fx-padding: 8px 12px; -fx-border-color: #F59E0B; -fx-border-width: 1px; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-font-size: 11px; -fx-font-weight: bold;");
        professorWarning.setMaxWidth(Double.MAX_VALUE);

        loginBtn.setOnAction(e -> {
            String email = userField.getText().trim();
            String pass = passField.getText().trim();
            
            if (email.isEmpty()) {
                errorLabel.setText("Por favor, introduza o email!");
                errorLabel.setVisible(true);
                return;
            }
            
            if (!"123".equals(pass)) {
                errorLabel.setText("Palavra-passe incorreta! (Use '123')");
                errorLabel.setVisible(true);
                return;
            }

            Utilizador u = AutenticacaoManager.getInstance().procurarUtilizadorPorEmail(email);
            if (u != null) {
                if (u.getCargo() == TipoUtilizador.GESTOR_EQUIPA) {
                    String selectedTeam = cmbEquipas.getValue();
                    if (selectedTeam == null) {
                        errorLabel.setText("Por favor, selecione uma equipa!");
                        errorLabel.setVisible(true);
                        return;
                    }
                    u.setEquipaAssociada(selectedTeam);
                }
                
                boolean ok = AutenticacaoManager.getInstance().autenticar(email);
                if (ok) {
                    MainGUI.showDashboard();
                } else {
                    errorLabel.setText("Falha na autenticação!");
                    errorLabel.setVisible(true);
                }
            } else {
                errorLabel.setText("Email não encontrado!");
                errorLabel.setVisible(true);
            }
        });

        guestBtn.setOnAction(e -> {
            AutenticacaoManager.getInstance().autenticar("adepto@wc2026.com");
            MainGUI.showDashboard();
        });

        card.getChildren().addAll(
            title, subtitle, professorWarning,
            userField, passField, lblEquipa, cmbEquipas, loginBtn, guestBtn, 
            errorLabel
        );
        root.getChildren().add(card);

        Scene newScene = new Scene(root, 1000, 700);
        
        // Load CSS from classpath resources
        try {
            URL cssUrl = getClass().getResource("/ui/styles.css");
            if (cssUrl != null) {
                newScene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("CSS file /ui/styles.css not found in classpath!");
            }
        } catch (Exception ex) {
            System.err.println("Failed to load stylesheet: " + ex.getMessage());
        }

        return newScene;
    }

    public Scene getScene() {
        return scene;
    }
}
