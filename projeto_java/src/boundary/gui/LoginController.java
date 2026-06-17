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
import domain.Utilizador;
import domain.TipoUtilizador;

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
        passField.setPromptText("Palavra-passe (opcional)");
        passField.getStyleClass().add("password-field");

        Button loginBtn = new Button("Entrar");
        loginBtn.getStyleClass().add("btn-primary");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        
        Button guestBtn = new Button("Entrar como Público");
        guestBtn.getStyleClass().add("btn-secondary");
        guestBtn.setMaxWidth(Double.MAX_VALUE);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #EF4444;"); // Red color for errors
        errorLabel.setVisible(false);

        loginBtn.setOnAction(e -> {
            String email = userField.getText().trim();
            if (email.isEmpty()) {
                errorLabel.setText("Por favor, introduza o email!");
                errorLabel.setVisible(true);
                return;
            }
            boolean ok = AutenticacaoManager.getInstance().autenticar(email);
            if (ok) {
                MainGUI.showDashboard();
            } else {
                errorLabel.setText("Email não encontrado!");
                errorLabel.setVisible(true);
            }
        });

        guestBtn.setOnAction(e -> {
            Utilizador publico = new Utilizador("publico@wc2026.com", "Público Geral", TipoUtilizador.PUBLICO, null);
            AutenticacaoManager.getInstance().registarUtilizador(publico);
            AutenticacaoManager.getInstance().autenticar("publico@wc2026.com");
            MainGUI.showDashboard();
        });

        card.getChildren().addAll(title, subtitle, userField, passField, loginBtn, guestBtn, errorLabel);
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
