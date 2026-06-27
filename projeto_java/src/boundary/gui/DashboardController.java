package boundary.gui;

import domain.*;
import manager.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.chart.*;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class DashboardController {

    private final Scene scene;
    private final BorderPane rootPane;
    private final Utilizador utilizadorLogado;

    public Scene getScene() {
        return this.scene;
    }

    private CampeonatoManager campManager;
    private ArbitragemManager arbManager;
    private LogisticaManager logManager;
    private BilheteiraManager bilManager;

    private ObservableList<InventoryItem> inventoryItems = null;
    private ObservableList<FraudLog> fraudLogs = null;

    public DashboardController(Stage stage) {
        this.campManager = CampeonatoManager.getInstance();
        this.arbManager = ArbitragemManager.getInstance();
        this.logManager = LogisticaManager.getInstance();
        this.bilManager = BilheteiraManager.getInstance();
        this.utilizadorLogado = AutenticacaoManager.getInstance().getUtilizadorAtual();
        this.fraudLogs = FXCollections.observableArrayList(
            new FraudLog(1, "2026-06-17 10:14", "Bilhete Duplicado", "Tentativa de entrada dupla no Setor Económico do Estádio da Luz"),
            new FraudLog(2, "2026-06-17 10:15", "IP Suspeito", "Múltiplas compras em menos de 2 segundos a partir do IP 192.168.1.105")
        );
        this.rootPane = new BorderPane();
        this.scene = createScene();
    }

    private Scene createScene() {
        if (utilizadorLogado == null) {
            // Guard clause if not logged in
            System.err.println("User is not logged in!");
            return null;
        }

        rootPane.getStyleClass().add("root");

        TipoUtilizador cargo = utilizadorLogado.getCargo();

        // Topbar
        HBox topbar = new HBox(20);
        topbar.setAlignment(Pos.CENTER_RIGHT);
        topbar.setPadding(new Insets(15, 30, 15, 30));
        topbar.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: transparent transparent #E5E7EB transparent; -fx-border-width: 1px;");
        
        Label lblPerfil = new Label("Mudar Perfil:");
        lblPerfil.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 13px; -fx-font-weight: bold;");
        
        ComboBox<String> cmbPerfil = new ComboBox<>();
        cmbPerfil.setStyle("-fx-font-size: 12px; -fx-background-color: #F3F4F6; -fx-border-color: #D1D5DB; -fx-border-radius: 6px; -fx-background-radius: 6px;");
        cmbPerfil.getItems().addAll(
            "Adepto Geral (Público)",
            "Administrador FIFA",
            "Gestor Arbitragem",
            "Gestor Equipa (Portugal)",
            "Gestor Logística",
            "Gestor Bilheteira"
        );
        
        switch (cargo) {
            case PUBLICO -> cmbPerfil.setValue("Adepto Geral (Público)");
            case ADMIN -> cmbPerfil.setValue("Administrador FIFA");
            case GESTOR_ARBITRAGEM -> cmbPerfil.setValue("Gestor Arbitragem");
            case GESTOR_EQUIPA -> cmbPerfil.setValue("Gestor Equipa (Portugal)");
            case GESTOR_LOGISTICA -> cmbPerfil.setValue("Gestor Logística");
            case GESTOR_BILHETEIRA -> cmbPerfil.setValue("Gestor Bilheteira");
        }
        
        cmbPerfil.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                String targetEmail = switch (newV) {
                    case "Administrador FIFA" -> "admin@fifa.com";
                    case "Gestor Arbitragem" -> "arbitragem@fifa.com";
                    case "Gestor Equipa (Portugal)" -> "equipa@fifa.com";
                    case "Gestor Logística" -> "logistica@fifa.com";
                    case "Gestor Bilheteira" -> "bilheteira@fifa.com";
                    default -> "adepto@wc2026.com";
                };
                if ("Gestor Equipa (Portugal)".equals(newV)) {
                    Utilizador u = AutenticacaoManager.getInstance().procurarUtilizadorPorEmail(targetEmail);
                    if (u != null) {
                        u.setEquipaAssociada("Portugal");
                    }
                }
                if (!targetEmail.equalsIgnoreCase(utilizadorLogado.getEmail())) {
                    boolean ok = AutenticacaoManager.getInstance().autenticar(targetEmail);
                    if (ok) {
                        MainGUI.showDashboard();
                    }
                } else if ("Gestor Equipa (Portugal)".equals(newV)) {
                    MainGUI.showDashboard();
                }
            }
        });

        Label userLabel = new Label("Utilizador: " + utilizadorLogado.getNome() + " (" + utilizadorLogado.getCargo() + ")");
        userLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1A202C; -fx-font-size: 14px;");
        topbar.getChildren().addAll(lblPerfil, cmbPerfil, userLabel);
        rootPane.setTop(topbar);

        // Sidebar Navigation
        VBox sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(260);

        Label logo = new Label("WC 2026");
        logo.getStyleClass().add("label-title");
        logo.setStyle("-fx-font-size: 24px; -fx-text-fill: #00D26A; -fx-alignment: center;");
        logo.setPadding(new Insets(10, 20, 20, 20));
        sidebar.getChildren().add(logo);

        List<Button> navButtons = new ArrayList<>();
        Button btnClassificacao = null;

        // Conditionally show buttons based on Role
        if (cargo != TipoUtilizador.PUBLICO) {
            Button btnVisaoGeral = createNavButton("Visão Geral", navButtons);
            btnVisaoGeral.setOnAction(e -> {
                setActiveButton(btnVisaoGeral, navButtons);
                showOverview();
            });
            sidebar.getChildren().add(btnVisaoGeral);
            
            // Set default view to Visão Geral
            btnVisaoGeral.getStyleClass().add("sidebar-btn-active");
        }

        if (cargo == TipoUtilizador.ADMIN) {
            Button btnGestao = createNavButton("Gestão Geral", navButtons);
            btnGestao.setOnAction(e -> {
                setActiveButton(btnGestao, navButtons);
                showGestaoGeral();
            });
            sidebar.getChildren().add(btnGestao);
        }

        if (cargo == TipoUtilizador.ADMIN || cargo == TipoUtilizador.GESTOR_EQUIPA) {
            String label = (cargo == TipoUtilizador.GESTOR_EQUIPA) ? "A Minha Equipa" : "Equipas e Plantéis";
            Button btnEquipas = createNavButton(label, navButtons);
            btnEquipas.setOnAction(e -> {
                setActiveButton(btnEquipas, navButtons);
                showEquipas();
            });
            sidebar.getChildren().add(btnEquipas);
        }

        if (cargo == TipoUtilizador.ADMIN || cargo == TipoUtilizador.GESTOR_ARBITRAGEM) {
            Button btnArbitros = createNavButton("Arbitragem", navButtons);
            btnArbitros.setOnAction(e -> {
                setActiveButton(btnArbitros, navButtons);
                showArbitragem();
            });
            sidebar.getChildren().add(btnArbitros);
        }

        if (cargo == TipoUtilizador.ADMIN || cargo == TipoUtilizador.GESTOR_LOGISTICA) {
            Button btnLogistica = createNavButton("Logística", navButtons);
            btnLogistica.setOnAction(e -> {
                setActiveButton(btnLogistica, navButtons);
                showLogistica();
            });
            sidebar.getChildren().add(btnLogistica);
        }

        if (cargo == TipoUtilizador.PUBLICO || cargo == TipoUtilizador.ADMIN || cargo == TipoUtilizador.GESTOR_BILHETEIRA) {
            Button btnBilhetes = createNavButton("Bilheteira", navButtons);
            btnBilhetes.setOnAction(e -> {
                setActiveButton(btnBilhetes, navButtons);
                showBilheteira();
            });
            sidebar.getChildren().add(btnBilhetes);
        }

        btnClassificacao = createNavButton("Tabelas e Bracket", navButtons);
        final Button finalBtnClass = btnClassificacao;
        btnClassificacao.setOnAction(e -> {
            setActiveButton(finalBtnClass, navButtons);
            showStandingsAndBracket();
        });
        sidebar.getChildren().add(btnClassificacao);

        // Separator
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);

        // Profile Box
        String nome = utilizadorLogado.getNome();
        String iniciais = "";
        if (nome != null && !nome.isEmpty()) {
            String[] partes = nome.split("\\s+");
            if (partes.length > 0) {
                iniciais += partes[0].substring(0, 1).toUpperCase();
                if (partes.length > 1) {
                    iniciais += partes[partes.length - 1].substring(0, 1).toUpperCase();
                }
            }
        }
        if (iniciais.isEmpty()) {
            iniciais = "U";
        }

        HBox profileBox = new HBox(12);
        profileBox.setAlignment(Pos.CENTER_LEFT);
        profileBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-padding: 12px; -fx-background-radius: 16px;");
        VBox.setMargin(profileBox, new Insets(0, 15, 10, 15));

        Label avatar = new Label(iniciais);
        avatar.setStyle(
            "-fx-background-color: #00D26A; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 50%; " +
            "-fx-min-width: 40px; -fx-min-height: 40px; " +
            "-fx-max-width: 40px; -fx-max-height: 40px; " +
            "-fx-alignment: center; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 14px;"
        );

        VBox details = new VBox(2);
        details.setAlignment(Pos.CENTER_LEFT);

        Label lblName = new Label(utilizadorLogado.getNome());
        lblName.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 13px;");

        Label lblRole = new Label(utilizadorLogado.getCargo().toString());
        lblRole.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 11px;");

        details.getChildren().addAll(lblName, lblRole);
        profileBox.getChildren().addAll(avatar, details);
        sidebar.getChildren().add(profileBox);

        // Logout
        Button btnSair = new Button("Terminar Sessão");
        btnSair.getStyleClass().add("sidebar-btn");
        btnSair.setMaxWidth(Double.MAX_VALUE);
        btnSair.setStyle("-fx-text-fill: #EF4444; -fx-padding: 15px 30px;");
        btnSair.setOnAction(e -> {
            AutenticacaoManager.getInstance().logout();
            MainGUI.showLoginScreen();
        });
        sidebar.getChildren().add(btnSair);

        rootPane.setLeft(sidebar);

        // Initial view
        if (cargo == TipoUtilizador.PUBLICO && btnClassificacao != null) {
            setActiveButton(btnClassificacao, navButtons);
            showStandingsAndBracket();
        } else {
            showOverview();
        }

        Scene newScene = new Scene(rootPane, 1100, 750);

        // Load CSS
        try {
            URL cssUrl = getClass().getResource("/ui/styles.css");
            if (cssUrl != null) {
                newScene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("CSS file /ui/styles.css not found in classpath!");
            }
        } catch (Exception ex) {
            System.err.println("Failed to load stylesheet in Dashboard: " + ex.getMessage());
        }

        return newScene;
    }

    private Button createNavButton(String text, List<Button> list) {
        Button btn = new Button(text);
        btn.getStyleClass().add("sidebar-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        list.add(btn);
        return btn;
    }

    private void setActiveButton(Button active, List<Button> list) {
        for (Button btn : list) {
            btn.getStyleClass().remove("sidebar-btn-active");
        }
        active.getStyleClass().add("sidebar-btn-active");
    }

    private void setContent(VBox panel) {
        ScrollPane scrollPane = new ScrollPane(panel);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        rootPane.setCenter(scrollPane);
    }

    // ==========================================
    // VIEW 1: Visão Geral (Dashboard Overview)
    // ==========================================
    private void showOverview() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        TipoUtilizador cargo = utilizadorLogado.getCargo();

        Label title = new Label();
        title.getStyleClass().add("label-title");

        Label subtitle = new Label();
        subtitle.getStyleClass().add("label-subtitle");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        switch (cargo) {
            case ADMIN -> showAdminOverview(content, title, subtitle, grid);
            case GESTOR_EQUIPA -> showEquipaOverview(content, title, subtitle, grid);
            case GESTOR_ARBITRAGEM -> showArbitragemOverview(content, title, subtitle, grid);
            case GESTOR_LOGISTICA -> showLogisticaOverview(content, title, subtitle, grid);
            case GESTOR_BILHETEIRA -> showBilheteiraOverview(content, title, subtitle, grid);
            default -> showAdminOverview(content, title, subtitle, grid);
        }
    }

    private void showAdminOverview(VBox content, Label title, Label subtitle, GridPane grid) {
        title.setText("Visão Geral do Campeonato (Administrador)");
        subtitle.setText("Acompanhamento global das estatísticas operacionais do torneio.");

        int numTeams = campManager.getEquipas().size();
        int numGames = campManager.getJogos().size();
        int numRefs = arbManager.getArbitros().size();
        int numTickets = bilManager.getBilhetes().size();

        grid.add(createStatCard("Seleções Inscritas", String.valueOf(numTeams)), 0, 0);
        grid.add(createStatCard("Jogos no Calendário", String.valueOf(numGames)), 1, 0);
        grid.add(createStatCard("Árbitros Credenciados", String.valueOf(numRefs)), 0, 1);
        grid.add(createStatCard("Bilhetes Vendidos", String.valueOf(numTickets)), 1, 1);

        VBox recentGamesCard = new VBox(15);
        recentGamesCard.getStyleClass().add("card");
        Label gamesTitle = new Label("Próximos Jogos Agendados");
        gamesTitle.getStyleClass().add("label-subtitle");
        gamesTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1A202C;");

        VBox gamesList = new VBox(10);
        List<Jogo> scheduled = campManager.getJogos();
        int count = 0;
        for (Jogo j : scheduled) {
            if (count >= 5) break;
            HBox gameRow = new HBox(10);
            gameRow.setStyle("-fx-padding: 8px; -fx-background-color: #F3F4F6; -fx-background-radius: 8px;");
            
            Label l1 = new Label(j.getData() + " " + j.getHora() + " | " + j.getPhase());
            l1.setStyle("-fx-text-fill: #6B7280;");
            
            String home = j.getHomeTeam() != null ? j.getHomeTeam().getNome() : "A definir";
            String away = j.getAwayTeam() != null ? j.getAwayTeam().getNome() : "A definir";
            Label l2 = new Label(home + " vs " + away);
            l2.setStyle("-fx-font-weight: bold; -fx-text-fill: #1A202C;");
            
            Region s = new Region();
            HBox.setHgrow(s, Priority.ALWAYS);
            
            Label l3 = new Label(j.getStatus().toString());
            l3.setStyle("-fx-text-fill: #00D26A; -fx-font-weight: bold;");

            gameRow.getChildren().addAll(l1, l2, s, l3);
            gamesList.getChildren().add(gameRow);
            count++;
        }
        if (count == 0) {
            gamesList.getChildren().add(new Label("Nenhum jogo agendado atualmente."));
        }
        recentGamesCard.getChildren().addAll(gamesTitle, gamesList);

        content.getChildren().addAll(title, subtitle, grid, recentGamesCard);
        setContent(content);
    }

    private void showEquipaOverview(VBox content, Label title, Label subtitle, GridPane grid) {
        String teamName = utilizadorLogado.getEquipaAssociada();
        if (teamName == null || teamName.isEmpty()) {
            teamName = "Portugal"; // Fallback to Portugal if none selected
        }
        title.setText("Painel da Seleção - " + teamName);
        subtitle.setText("Estatísticas operacionais e planeamento para a comitiva de " + teamName + ".");

        Equipa equipa = campManager.procurarEquipaPorNome(teamName);
        
        int totalMatches = 0;
        for (Jogo j : campManager.getJogos()) {
            if ((j.getHomeTeam() != null && j.getHomeTeam().getNome().equalsIgnoreCase(teamName)) ||
                (j.getAwayTeam() != null && j.getAwayTeam().getNome().equalsIgnoreCase(teamName))) {
                totalMatches++;
            }
        }
        
        int aptosCount = 0;
        int totalPlayers = 0;
        int teamGoals = 0;
        int yellows = 0;
        int reds = 0;
        if (equipa != null) {
            totalPlayers = equipa.getJogadores().size();
            for (Jogador jog : equipa.getJogadores()) {
                if (EstadoJogador.APTO.equals(jog.getEstado())) {
                    aptosCount++;
                }
                teamGoals += jog.getGoals();
                yellows += jog.getYellowCards();
                reds += jog.getRedCards();
            }
        }

        grid.add(createStatCard("Total de Jogos", String.valueOf(totalMatches)), 0, 0);
        grid.add(createStatCard("Jogadores Aptos", aptosCount + " / " + totalPlayers), 1, 0);
        grid.add(createStatCard("Golos Marcados", String.valueOf(teamGoals)), 0, 1);
        grid.add(createStatCard("Cartões da Equipa", yellows + " 🟨 | " + reds + " 🟥"), 1, 1);

        // Base Camp (Hotel) Card
        VBox baseCampCard = new VBox(10);
        baseCampCard.getStyleClass().add("card");
        baseCampCard.setStyle("-fx-background-color: linear-gradient(to right, #111827, #1F2937); -fx-padding: 20px;");
        
        Hotel allocated = null;
        for (Hotel h : logManager.getHoteis()) {
            if (h.getEquipaHospedada() != null && h.getEquipaHospedada().getNome().equalsIgnoreCase(teamName)) {
                allocated = h;
                break;
            }
        }
        
        Label baseCampTitle = new Label("🏨 Base de Estágio / Alojamento");
        baseCampTitle.setStyle("-fx-text-fill: #9CA3AF; -fx-font-weight: bold; -fx-font-size: 12px;");
        Label baseCampDetail;
        if (allocated != null) {
            baseCampDetail = new Label(allocated.getNome() + " (" + allocated.getLocalizacao() + ") | Check-In: " + allocated.getCheckInDate() + " | Check-Out: " + allocated.getCheckOutDate());
            baseCampDetail.setStyle("-fx-text-fill: #34D399; -fx-font-weight: bold; -fx-font-size: 14px;");
        } else {
            baseCampDetail = new Label("Sem alojamento oficial alocado de momento.");
            baseCampDetail.setStyle("-fx-text-fill: #F87171; -fx-font-weight: bold; -fx-font-size: 14px;");
        }
        baseCampCard.getChildren().addAll(baseCampTitle, baseCampDetail);

        VBox teamGamesCard = new VBox(15);
        teamGamesCard.getStyleClass().add("card");
        Label gamesTitle = new Label("Próximos Jogos de " + teamName);
        gamesTitle.getStyleClass().add("label-subtitle");
        gamesTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1A202C;");

        VBox gamesList = new VBox(10);
        int count = 0;
        for (Jogo j : campManager.getJogos()) {
            boolean isTeamMatch = (j.getHomeTeam() != null && j.getHomeTeam().getNome().equalsIgnoreCase(teamName)) ||
                                  (j.getAwayTeam() != null && j.getAwayTeam().getNome().equalsIgnoreCase(teamName));
            if (isTeamMatch) {
                if (count >= 5) break;
                HBox gameRow = new HBox(10);
                gameRow.setStyle("-fx-padding: 8px; -fx-background-color: #F3F4F6; -fx-background-radius: 8px;");
                
                Label l1 = new Label(j.getData() + " " + j.getHora() + " | " + j.getPhase());
                l1.setStyle("-fx-text-fill: #6B7280;");
                
                String home = j.getHomeTeam() != null ? j.getHomeTeam().getNome() : "A definir";
                String away = j.getAwayTeam() != null ? j.getAwayTeam().getNome() : "A definir";
                Label l2 = new Label(home + " vs " + away);
                l2.setStyle("-fx-font-weight: bold; -fx-text-fill: #1A202C;");
                
                Region s = new Region();
                HBox.setHgrow(s, Priority.ALWAYS);
                
                Label l3 = new Label(j.getStatus().toString());
                l3.setStyle("-fx-text-fill: #00D26A; -fx-font-weight: bold;");

                gameRow.getChildren().addAll(l1, l2, s, l3);
                gamesList.getChildren().add(gameRow);
                count++;
            }
        }
        if (count == 0) {
            gamesList.getChildren().add(new Label("Nenhum jogo agendado para esta equipa."));
        }
        teamGamesCard.getChildren().addAll(gamesTitle, gamesList);

        content.getChildren().addAll(title, subtitle, grid, baseCampCard, teamGamesCard);
        setContent(content);
    }

    private void showArbitragemOverview(VBox content, Label title, Label subtitle, GridPane grid) {
        title.setText("Painel de Gestão de Arbitragem");
        subtitle.setText("Controlo de elegibilidade, repouso regulamentar e escalação de equipas de arbitragem.");

        int totalRefs = arbManager.getArbitros().size();
        int totalMatches = campManager.getJogos().size();
        
        int allocatedMatches = 0;
        int unallocatedMatches = 0;
        for (Jogo j : campManager.getJogos()) {
            if (j.getEscalaArbitros() != null && j.getEscalaArbitros().getPrincipal() != null) {
                allocatedMatches++;
            } else {
                unallocatedMatches++;
            }
        }

        grid.add(createStatCard("Árbitros Credenciados", String.valueOf(totalRefs)), 0, 0);
        grid.add(createStatCard("Jogos Totais", String.valueOf(totalMatches)), 1, 0);
        grid.add(createStatCard("Escalações Efetuadas", String.valueOf(allocatedMatches)), 0, 1);
        grid.add(createStatCard("Jogos sem Escala", String.valueOf(unallocatedMatches)), 1, 1);

        VBox pendingRefCard = new VBox(15);
        pendingRefCard.getStyleClass().add("card");
        Label pendingTitle = new Label("Jogos Pendentes de Escala de Arbitragem");
        pendingTitle.getStyleClass().add("label-subtitle");
        pendingTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1A202C;");

        VBox pendingList = new VBox(10);
        int count = 0;
        for (Jogo j : campManager.getJogos()) {
            if (j.getEscalaArbitros() == null || j.getEscalaArbitros().getPrincipal() == null) {
                if (count >= 5) break;
                HBox gameRow = new HBox(10);
                gameRow.setStyle("-fx-padding: 8px; -fx-background-color: #FEF3C7; -fx-background-radius: 8px; -fx-border-color: #F59E0B; -fx-border-width: 1px; -fx-border-radius: 8px;");
                
                Label l1 = new Label(j.getData() + " " + j.getHora() + " | " + j.getPhase());
                l1.setStyle("-fx-text-fill: #92400E;");
                
                String home = j.getHomeTeam() != null ? j.getHomeTeam().getNome() : "A definir";
                String away = j.getAwayTeam() != null ? j.getAwayTeam().getNome() : "A definir";
                Label l2 = new Label(home + " vs " + away);
                l2.setStyle("-fx-font-weight: bold; -fx-text-fill: #92400E;");
                
                Region s = new Region();
                HBox.setHgrow(s, Priority.ALWAYS);
                
                Label l3 = new Label("Pendente");
                l3.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");

                gameRow.getChildren().addAll(l1, l2, s, l3);
                pendingList.getChildren().add(gameRow);
                count++;
            }
        }
        if (count == 0) {
            pendingList.getChildren().add(new Label("Todos os jogos têm árbitros principais atribuídos."));
        }
        pendingRefCard.getChildren().addAll(pendingTitle, pendingList);

        content.getChildren().addAll(title, subtitle, grid, pendingRefCard);
        setContent(content);
    }

    private void showLogisticaOverview(VBox content, Label title, Label subtitle, GridPane grid) {
        title.setText("Painel de Operações e Logística");
        subtitle.setText("Gestão de alojamentos, frotas, transporte e viagens das comitivas.");

        int totalHotels = logManager.getHoteis().size();
        int occupiedHotels = 0;
        int totalCapacity = 0;
        int occupiedCapacity = 0;
        for (Hotel h : logManager.getHoteis()) {
            totalCapacity += h.getCapacidadeQuartos();
            if (h.getEquipaHospedada() != null) {
                occupiedHotels++;
                occupiedCapacity += h.getCapacidadeQuartos();
            }
        }
        int totalViagens = logManager.getViagens().size();
        
        int busCount = 0;
        int flightCount = 0;
        for (Viagem v : logManager.getViagens()) {
            if (v.getMeioTransporte() != null && (v.getMeioTransporte().toLowerCase().contains("voo") || v.getMeioTransporte().toLowerCase().contains("avi"))) {
                flightCount++;
            } else {
                busCount++;
            }
        }

        grid.add(createStatCard("Hotéis Ocupados", occupiedHotels + " / " + totalHotels), 0, 0);
        grid.add(createStatCard("Capacidade Quartos", occupiedCapacity + " / " + totalCapacity), 1, 0);
        grid.add(createStatCard("Viagens Planeadas", String.valueOf(totalViagens)), 0, 1);
        grid.add(createStatCard("Frota (Autocarro | Voo)", busCount + " 🚌 | " + flightCount + " ✈️"), 1, 1);

        VBox travelsCard = new VBox(15);
        travelsCard.getStyleClass().add("card");
        Label travelsTitle = new Label("Próximas Viagens Agendadas");
        travelsTitle.getStyleClass().add("label-subtitle");
        travelsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1A202C;");

        VBox travelsList = new VBox(10);
        int count = 0;
        for (Viagem v : logManager.getViagens()) {
            if (count >= 5) break;
            HBox travelRow = new HBox(10);
            travelRow.setStyle("-fx-padding: 8px; -fx-background-color: #F3F4F6; -fx-background-radius: 8px;");
            
            Label l1 = new Label(v.getDataPartida() + " -> " + v.getDataChegada());
            l1.setStyle("-fx-text-fill: #6B7280;");
            
            Label l2 = new Label(v.getOrigem() + " para " + v.getDestino());
            l2.setStyle("-fx-font-weight: bold; -fx-text-fill: #1A202C;");
            
            Region s = new Region();
            HBox.setHgrow(s, Priority.ALWAYS);
            
            Label l3 = new Label(v.getMeioTransporte());
            l3.setStyle("-fx-text-fill: #00D26A; -fx-font-weight: bold;");

            travelRow.getChildren().addAll(l1, l2, s, l3);
            travelsList.getChildren().add(travelRow);
            count++;
        }
        if (count == 0) {
            travelsList.getChildren().add(new Label("Nenhuma viagem agendada de momento."));
        }
        travelsCard.getChildren().addAll(travelsTitle, travelsList);

        content.getChildren().addAll(title, subtitle, grid, travelsCard);
        setContent(content);
    }

    private void showBilheteiraOverview(VBox content, Label title, Label subtitle, GridPane grid) {
        title.setText("Painel de Vendas e Bilheteira");
        subtitle.setText("Estatísticas de vendas, receita acumulada e logs de auditoria de fraudes.");

        double totalRev = 0.0;
        for (Bilhete b : bilManager.getBilhetes()) {
            totalRev += b.getPreco();
        }
        
        int soldCount = bilManager.getBilhetes().size();
        int activeStadiums = campManager.getEstadios().size();
        int activeFrauds = fraudLogs.size();

        grid.add(createStatCard("Bilhetes Vendidos", String.valueOf(soldCount)), 0, 0);
        grid.add(createStatCard("Receita Total", String.format("%.2f €", totalRev)), 1, 0);
        grid.add(createStatCard("Estádios Ativos", String.valueOf(activeStadiums)), 0, 1);
        grid.add(createStatCard("Alertas de Fraude", String.valueOf(activeFrauds)), 1, 1);

        VBox fraudCard = new VBox(15);
        fraudCard.getStyleClass().add("card");
        Label fraudTitle = new Label("Logs de Segurança e Auditoria de Fraudes Recentes");
        fraudTitle.getStyleClass().add("label-subtitle");
        fraudTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1A202C;");

        TableView<FraudLog> tblFraud = new TableView<>();
        
        TableColumn<FraudLog, String> colTime = new TableColumn<>("Timestamp");
        colTime.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTimestamp()));
        colTime.setPrefWidth(130);

        TableColumn<FraudLog, String> colType = new TableColumn<>("Tipo");
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
        colType.setPrefWidth(120);

        TableColumn<FraudLog, String> colDesc = new TableColumn<>("Descrição");
        colDesc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));
        colDesc.setPrefWidth(380);

        TableColumn<FraudLog, Void> colBlock = new TableColumn<>("Ação");
        colBlock.setPrefWidth(110);
        colBlock.setCellFactory(column -> new TableCell<FraudLog, Void>() {
            private final Button btn = new Button("Bloquear");
            {
                btn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 6px 12px; -fx-cursor: hand; -fx-font-size: 11px;");
                btn.setOnAction(evt -> {
                    FraudLog log = getTableView().getItems().get(getIndex());
                    fraudLogs.remove(log);
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "Ação tomada: Transação/IP bloqueado preventivamente.");
                    a.showAndWait();
                    showOverview(); // Refresh the overview
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(btn);
            }
        });

        tblFraud.getColumns().addAll(colTime, colType, colDesc, colBlock);
        tblFraud.setItems(this.fraudLogs);
        tblFraud.setPrefHeight(200);

        fraudCard.getChildren().addAll(fraudTitle, tblFraud);

        content.getChildren().addAll(title, subtitle, grid, fraudCard);
        setContent(content);
    }

    private VBox createStatCard(String title, String value) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setStyle("-fx-padding: 25px;");
        card.setMinWidth(220);

        Label lblTitle = new Label(title);
        lblTitle.getStyleClass().add("label-subtitle");

        Label lblValue = new Label(value);
        lblValue.getStyleClass().add("label-title");
        lblValue.setStyle("-fx-font-size: 26px; -fx-text-fill: #00D26A;");

        card.getChildren().addAll(lblTitle, lblValue);
        return card;
    }

    // ==========================================
    // VIEW 2: Gestão Geral (Admin-only Panel)
    // ==========================================
    private void showGestaoGeral() {
        VBox content = new VBox(25);
        content.setPadding(new Insets(30));

        Label title = new Label("Painel de Gestão Campeonato");
        title.getStyleClass().add("label-title");

        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-tab-max-height: 40px;");

        // Tab 1: Seleções e Grupos
        Tab tabTeams = new Tab("Seleções e Jogadores");
        tabTeams.setClosable(false);
        VBox formTeams = new VBox(15);
        formTeams.setPadding(new Insets(20));
        formTeams.getStyleClass().add("card");

        Label formTeamsTitle = new Label("Registar Nova Seleção");
        formTeamsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        TextField txtTeamName = new TextField();
        txtTeamName.setPromptText("Nome da Seleção (ex: Portugal)");
        TextField txtCoachName = new TextField();
        txtCoachName.setPromptText("Selecionador");
        
        ComboBox<String> cmbGroup = new ComboBox<>(FXCollections.observableArrayList(
            "Grupo A", "Grupo B", "Grupo C", "Grupo D", "Grupo E", "Grupo F", "Grupo G", "Grupo H"
        ));
        cmbGroup.setPromptText("Associar ao Grupo (Opcional)");

        Button btnAddTeam = new Button("Registar Seleção");
        btnAddTeam.getStyleClass().add("btn-primary");
        Label lblTeamMsg = new Label();

        btnAddTeam.setOnAction(e -> {
            String team = txtTeamName.getText().trim();
            String coach = txtCoachName.getText().trim();
            if (team.isEmpty() || coach.isEmpty()) {
                lblTeamMsg.setText("Preencha o nome da seleção e o treinador!");
                lblTeamMsg.setStyle("-fx-text-fill: #EF4444;");
                return;
            }
            if (campManager.procurarEquipaPorNome(team) != null) {
                lblTeamMsg.setText("Esta seleção já está registada!");
                lblTeamMsg.setStyle("-fx-text-fill: #EF4444;");
                return;
            }
            Equipa eq = new Equipa(team, coach);
            campManager.registarEquipa(eq);
            
            String selectedGroup = cmbGroup.getValue();
            if (selectedGroup != null) {
                campManager.registarEquipaNoGrupo(selectedGroup, team);
            }
            
            lblTeamMsg.setText("Seleção '" + team + "' registada com sucesso!");
            lblTeamMsg.setStyle("-fx-text-fill: #00D26A;");
            txtTeamName.clear();
            txtCoachName.clear();
            cmbGroup.setValue(null);
        });

        formTeams.getChildren().addAll(formTeamsTitle, txtTeamName, txtCoachName, cmbGroup, btnAddTeam, lblTeamMsg);
        tabTeams.setContent(formTeams);

        // Tab 2: Estádios e Setores
        Tab tabStadiums = new Tab("Estádios");
        tabStadiums.setClosable(false);
        VBox formStadiums = new VBox(15);
        formStadiums.setPadding(new Insets(20));
        formStadiums.getStyleClass().add("card");

        Label stadiumTitle = new Label("Registar Novo Estádio");
        stadiumTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        TextField txtStadiumName = new TextField();
        txtStadiumName.setPromptText("Nome do Estádio (ex: Estádio da Luz)");
        TextField txtLocation = new TextField();
        txtLocation.setPromptText("Localização (Cidade)");

        Button btnAddStadium = new Button("Registar Estádio");
        btnAddStadium.getStyleClass().add("btn-primary");
        Label lblStadiumMsg = new Label();

        // Sector area
        Separator sep = new Separator();
        Label sectorTitle = new Label("Adicionar Setor a Estádio");
        sectorTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        ComboBox<Estadio> cmbStadiums = new ComboBox<>();
        cmbStadiums.setPromptText("Selecionar Estádio");
        cmbStadiums.setMinWidth(250);
        
        ComboBox<String> cmbSectors = new ComboBox<>(FXCollections.observableArrayList(
            "Premium", "Intermedia", "Economica", "Local"
        ));
        cmbSectors.setPromptText("Tipo de Setor");
        
        TextField txtCapacity = new TextField();
        txtCapacity.setPromptText("Capacidade Total");
        TextField txtBasePrice = new TextField();
        txtBasePrice.setPromptText("Preço Base (EUR)");

        Button btnAddSector = new Button("Adicionar Setor");
        btnAddSector.getStyleClass().add("btn-primary");
        Label lblSectorMsg = new Label();

        // Trigger updates on stadium combo box
        Runnable updateStadiumCombo = () -> {
            cmbStadiums.setItems(FXCollections.observableArrayList(campManager.getEstadios()));
        };
        updateStadiumCombo.run();

        btnAddStadium.setOnAction(e -> {
            String name = txtStadiumName.getText().trim();
            String loc = txtLocation.getText().trim();
            if (name.isEmpty() || loc.isEmpty()) {
                lblStadiumMsg.setText("Preencha todos os campos do estádio!");
                lblStadiumMsg.setStyle("-fx-text-fill: #EF4444;");
                return;
            }
            campManager.registarEstadio(new Estadio(name, loc));
            lblStadiumMsg.setText("Estádio '" + name + "' registado com sucesso!");
            lblStadiumMsg.setStyle("-fx-text-fill: #00D26A;");
            txtStadiumName.clear();
            txtLocation.clear();
            updateStadiumCombo.run();
        });

        btnAddSector.setOnAction(e -> {
            Estadio est = cmbStadiums.getValue();
            String sec = cmbSectors.getValue();
            if (est == null || sec == null) {
                lblSectorMsg.setText("Selecione o estádio e o tipo de setor!");
                lblSectorMsg.setStyle("-fx-text-fill: #EF4444;");
                return;
            }
            try {
                int cap = Integer.parseInt(txtCapacity.getText().trim());
                double price = Double.parseDouble(txtBasePrice.getText().trim());
                if (cap <= 0 || price < 0) {
                    throw new NumberFormatException();
                }
                
                SetorEstadio setor = new SetorEstadio(sec, cap, price);
                est.adicionarSetor(setor);
                campManager.registarEstadio(est); // Save
                
                lblSectorMsg.setText("Setor '" + sec + "' adicionado ao " + est.getNome() + "!");
                lblSectorMsg.setStyle("-fx-text-fill: #00D26A;");
                txtCapacity.clear();
                txtBasePrice.clear();
                cmbStadiums.setValue(null);
                cmbSectors.setValue(null);
            } catch (NumberFormatException ex) {
                lblSectorMsg.setText("Capacidade e Preço devem ser números válidos maiores que 0!");
                lblSectorMsg.setStyle("-fx-text-fill: #EF4444;");
            }
        });

        formStadiums.getChildren().addAll(
            stadiumTitle, txtStadiumName, txtLocation, btnAddStadium, lblStadiumMsg,
            sep,
            sectorTitle, cmbStadiums, cmbSectors, txtCapacity, txtBasePrice, btnAddSector, lblSectorMsg
        );
        tabStadiums.setContent(formStadiums);

        // Tab 3: Agendar e Finalizar Jogos
        Tab tabMatches = new Tab("Jogos e Resultados");
        tabMatches.setClosable(false);
        VBox formMatches = new VBox(15);
        formMatches.setPadding(new Insets(20));
        formMatches.getStyleClass().add("card");

        Label matchTitle = new Label("Agendar Novo Jogo");
        matchTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        TextField txtMatchId = new TextField();
        txtMatchId.setPromptText("ID do Jogo (Inteiro)");
        DatePicker dpDate = new DatePicker();
        dpDate.setPromptText("Data do Jogo");
        TextField txtMatchTime = new TextField();
        txtMatchTime.setPromptText("Hora (ex: 18:30)");
        
        ComboBox<Estadio> cmbMatchStadium = new ComboBox<>();
        cmbMatchStadium.setPromptText("Estádio");
        ComboBox<Equipa> cmbHomeTeam = new ComboBox<>();
        cmbHomeTeam.setPromptText("Equipa Casa");
        ComboBox<Equipa> cmbAwayTeam = new ComboBox<>();
        cmbAwayTeam.setPromptText("Equipa Fora");
        
        ComboBox<String> cmbPhase = new ComboBox<>(FXCollections.observableArrayList(
            "Grupos", "Dezasseis-avos", "Oitavos", "Quartos", "Meias-Finais", "Final"
        ));
        cmbPhase.setPromptText("Fase do Campeonato");

        ComboBox<String> cmbMatchGroupFilter = new ComboBox<>(FXCollections.observableArrayList(
            "Todos", "Grupo A", "Grupo B", "Grupo C", "Grupo D", "Grupo E", "Grupo F", "Grupo G", "Grupo H"
        ));
        cmbMatchGroupFilter.setPromptText("Filtrar por Grupo (Opcional)");
        cmbMatchGroupFilter.setValue("Todos");
        
        // Esconder filtro de grupo por defeito, ativado apenas se a fase for "Grupos"
        cmbMatchGroupFilter.setVisible(false);
        cmbMatchGroupFilter.setManaged(false);
        
        cmbPhase.valueProperty().addListener((obs, oldPhase, newPhase) -> {
            if ("Grupos".equalsIgnoreCase(newPhase)) {
                cmbMatchGroupFilter.setVisible(true);
                cmbMatchGroupFilter.setManaged(true);
            } else {
                cmbMatchGroupFilter.setValue("Todos");
                cmbMatchGroupFilter.setVisible(false);
                cmbMatchGroupFilter.setManaged(false);
            }
        });
        
        cmbMatchGroupFilter.valueProperty().addListener((obs, oldGroup, newGroup) -> {
            if (newGroup != null && !"Todos".equalsIgnoreCase(newGroup)) {
                List<String> teamNames = campManager.getGrupos().get(newGroup);
                List<Equipa> filtered = new ArrayList<>();
                if (teamNames != null) {
                    for (Equipa eq : campManager.getEquipas()) {
                        if (teamNames.contains(eq.getNome())) {
                            filtered.add(eq);
                        }
                    }
                }
                cmbHomeTeam.setItems(FXCollections.observableArrayList(filtered));
                cmbAwayTeam.setItems(FXCollections.observableArrayList(filtered));
            } else {
                cmbHomeTeam.setItems(FXCollections.observableArrayList(campManager.getEquipas()));
                cmbAwayTeam.setItems(FXCollections.observableArrayList(campManager.getEquipas()));
            }
            cmbHomeTeam.setValue(null);
            cmbAwayTeam.setValue(null);
        });

        // Load combobox elements
        cmbMatchStadium.setItems(FXCollections.observableArrayList(campManager.getEstadios()));
        cmbHomeTeam.setItems(FXCollections.observableArrayList(campManager.getEquipas()));
        cmbAwayTeam.setItems(FXCollections.observableArrayList(campManager.getEquipas()));

        Button btnSchedule = new Button("Agendar Jogo");
        btnSchedule.getStyleClass().add("btn-primary");
        Label lblMatchMsg = new Label();

        btnSchedule.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtMatchId.getText().trim());
                if (campManager.procurarJogoPorId(id) != null) {
                    lblMatchMsg.setText("ID do jogo já existe!");
                    lblMatchMsg.setStyle("-fx-text-fill: #EF4444;");
                    return;
                }
                if (dpDate.getValue() == null) {
                    lblMatchMsg.setText("Selecione a data!");
                    lblMatchMsg.setStyle("-fx-text-fill: #EF4444;");
                    return;
                }
                String date = dpDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String time = txtMatchTime.getText().trim();
                if (!time.matches("^\\d{2}:\\d{2}$")) {
                    lblMatchMsg.setText("Hora deve estar no formato HH:MM!");
                    lblMatchMsg.setStyle("-fx-text-fill: #EF4444;");
                    return;
                }
                Estadio est = cmbMatchStadium.getValue();
                Equipa home = cmbHomeTeam.getValue();
                Equipa away = cmbAwayTeam.getValue();
                String phase = cmbPhase.getValue();
                
                if (est == null || home == null || away == null || phase == null) {
                    lblMatchMsg.setText("Preencha todos os campos do jogo!");
                    lblMatchMsg.setStyle("-fx-text-fill: #EF4444;");
                    return;
                }
                if (home.equals(away)) {
                    lblMatchMsg.setText("A equipa casa deve ser diferente da equipa fora!");
                    lblMatchMsg.setStyle("-fx-text-fill: #EF4444;");
                    return;
                }

                Jogo jogo = new Jogo(id, date, time, est, home, away, phase, null, null);
                campManager.registarJogo(jogo);
                
                lblMatchMsg.setText("Jogo agendado com sucesso!");
                lblMatchMsg.setStyle("-fx-text-fill: #00D26A;");
                txtMatchId.clear();
                txtMatchTime.clear();
                dpDate.setValue(null);
                cmbMatchStadium.setValue(null);
                cmbHomeTeam.setValue(null);
                cmbAwayTeam.setValue(null);
                cmbPhase.setValue(null);
            } catch (NumberFormatException ex) {
                lblMatchMsg.setText("ID do jogo deve ser um número inteiro válido!");
                lblMatchMsg.setStyle("-fx-text-fill: #EF4444;");
            } catch (IllegalArgumentException ex) {
                lblMatchMsg.setText(ex.getMessage());
                lblMatchMsg.setStyle("-fx-text-fill: #EF4444;");
            }
        });

        // Sub-section: Finalizar Jogo
        Separator sep2 = new Separator();
        Label finalizeTitle = new Label("Finalizar Jogo (Registar Resultado)");
        finalizeTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ComboBox<Jogo> cmbPendingMatches = new ComboBox<>();
        cmbPendingMatches.setPromptText("Selecionar Jogo Pendente");
        cmbPendingMatches.setMinWidth(300);

        // Load pending matches
        Runnable reloadPendingMatches = () -> {
            List<Jogo> active = new ArrayList<>();
            for (Jogo j : campManager.getJogos()) {
                if (!StatusJogo.FINALIZADO.equals(j.getStatus())) {
                    active.add(j);
                }
            }
            cmbPendingMatches.setItems(FXCollections.observableArrayList(active));
        };
        reloadPendingMatches.run();

        HBox scoreBox = new HBox(10);
        scoreBox.setAlignment(Pos.CENTER_LEFT);
        TextField txtGoalsHome = new TextField();
        txtGoalsHome.setPromptText("Golos Casa");
        txtGoalsHome.setPrefWidth(100);
        Label vs = new Label("vs");
        TextField txtGoalsAway = new TextField();
        txtGoalsAway.setPromptText("Golos Fora");
        txtGoalsAway.setPrefWidth(100);
        scoreBox.getChildren().addAll(txtGoalsHome, vs, txtGoalsAway);

        VBox penaltyContainer = new VBox(10);
        penaltyContainer.setVisible(false);
        penaltyContainer.setManaged(false);
        Label penaltyInfo = new Label("Jogo empatado em fase eliminatória! Decisão por Penaltis:");
        penaltyInfo.setStyle("-fx-text-fill: #6B7280;");
        HBox penaltyBox = new HBox(10);
        penaltyBox.setAlignment(Pos.CENTER_LEFT);
        TextField txtPenHome = new TextField();
        txtPenHome.setPromptText("Pen. Casa");
        txtPenHome.setPrefWidth(100);
        Label vsPen = new Label("vs");
        TextField txtPenAway = new TextField();
        txtPenAway.setPromptText("Pen. Fora");
        txtPenAway.setPrefWidth(100);
        penaltyBox.getChildren().addAll(txtPenHome, vsPen, txtPenAway);
        penaltyContainer.getChildren().addAll(penaltyInfo, penaltyBox);

        // Add listener to show penalties if scores are equal and phase is not group stage
        cmbPendingMatches.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                txtGoalsHome.textProperty().addListener((o, ov, nv) -> {
                    checkPenaltiesVisible(txtGoalsHome, txtGoalsAway, newV, penaltyContainer);
                });
                txtGoalsAway.textProperty().addListener((o, ov, nv) -> {
                    checkPenaltiesVisible(txtGoalsHome, txtGoalsAway, newV, penaltyContainer);
                });
            } else {
                penaltyContainer.setVisible(false);
                penaltyContainer.setManaged(false);
            }
        });

        Button btnFinalize = new Button("Finalizar Jogo");
        btnFinalize.getStyleClass().add("btn-primary");
        Label lblFinalizeMsg = new Label();

        btnFinalize.setOnAction(e -> {
            Jogo selected = cmbPendingMatches.getValue();
            if (selected == null) {
                lblFinalizeMsg.setText("Selecione um jogo!");
                lblFinalizeMsg.setStyle("-fx-text-fill: #EF4444;");
                return;
            }
            try {
                int gh = Integer.parseInt(txtGoalsHome.getText().trim());
                int ga = Integer.parseInt(txtGoalsAway.getText().trim());
                if (gh < 0 || ga < 0) throw new NumberFormatException();

                int ph = -1;
                int pa = -1;
                if (penaltyContainer.isVisible()) {
                    ph = Integer.parseInt(txtPenHome.getText().trim());
                    pa = Integer.parseInt(txtPenAway.getText().trim());
                    if (ph < 0 || pa < 0 || ph == pa) {
                        lblFinalizeMsg.setText("Golos de penaltis devem ser maiores ou iguais a 0 e não podem ser empatados!");
                        lblFinalizeMsg.setStyle("-fx-text-fill: #EF4444;");
                        return;
                    }
                }

                // Default statistics for match
                EstatisticaJogo stats = new EstatisticaJogo(50, 50, 8, 8, 4, 4);
                if (gh == 0 && ga == 0) {
                    campManager.finalizarJogoECorrerBracket(selected.getId(), null, gh, ga, ph, pa, stats);
                    lblFinalizeMsg.setText("Jogo finalizado com sucesso!");
                    lblFinalizeMsg.setStyle("-fx-text-fill: #00D26A;");
                    txtGoalsHome.clear();
                    txtGoalsAway.clear();
                    txtPenHome.clear();
                    txtPenAway.clear();
                    cmbPendingMatches.setValue(null);
                    reloadPendingMatches.run();
                } else {
                    showRegisterScorersAndAssistantsDialog(selected, gh, ga, ph, pa, stats, () -> {
                        lblFinalizeMsg.setText("Jogo finalizado com sucesso!");
                        lblFinalizeMsg.setStyle("-fx-text-fill: #00D26A;");
                        txtGoalsHome.clear();
                        txtGoalsAway.clear();
                        txtPenHome.clear();
                        txtPenAway.clear();
                        cmbPendingMatches.setValue(null);
                        reloadPendingMatches.run();
                    });
                }
            } catch (NumberFormatException ex) {
                lblFinalizeMsg.setText("Os golos introduzidos devem ser números inteiros maiores ou iguais a 0!");
                lblFinalizeMsg.setStyle("-fx-text-fill: #EF4444;");
            } catch (IllegalArgumentException ex) {
                lblFinalizeMsg.setText(ex.getMessage());
                lblFinalizeMsg.setStyle("-fx-text-fill: #EF4444;");
            }
        });

        formMatches.getChildren().addAll(
            matchTitle, txtMatchId, dpDate, txtMatchTime, cmbMatchStadium, cmbPhase, cmbMatchGroupFilter, cmbHomeTeam, cmbAwayTeam, btnSchedule, lblMatchMsg,
            sep2,
            finalizeTitle, cmbPendingMatches, scoreBox, penaltyContainer, btnFinalize, lblFinalizeMsg
        );
        tabMatches.setContent(formMatches);

        // Tab 4: Auditoria de Fraude & Segurança
        Tab tabSecurity = new Tab("Auditoria de Fraude & Segurança");
        tabSecurity.setClosable(false);
        VBox vboxSecurity = new VBox(15);
        vboxSecurity.setPadding(new Insets(20));
        vboxSecurity.getStyleClass().add("card");

        TableView<FraudLog> tblFraud = new TableView<>();
        
        TableColumn<FraudLog, String> colTime = new TableColumn<>("Timestamp");
        colTime.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTimestamp()));
        colTime.setPrefWidth(130);

        TableColumn<FraudLog, String> colType = new TableColumn<>("Tipo");
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
        colType.setPrefWidth(120);

        TableColumn<FraudLog, String> colDesc = new TableColumn<>("Descrição");
        colDesc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));
        colDesc.setPrefWidth(380);

        TableColumn<FraudLog, Void> colBlock = new TableColumn<>("Ação");
        colBlock.setPrefWidth(110);
        colBlock.setCellFactory(column -> new TableCell<FraudLog, Void>() {
            private final Button btn = new Button("Bloquear");
            {
                btn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 6px 12px; -fx-cursor: hand; -fx-font-size: 11px;");
                btn.setOnAction(evt -> {
                    FraudLog log = getTableView().getItems().get(getIndex());
                    fraudLogs.remove(log);
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "Ação tomada: Transação/IP bloqueado preventivamente.");
                    a.showAndWait();
                    showGestaoGeral(); // Refresh the general management view
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(btn);
            }
        });

        tblFraud.getColumns().addAll(colTime, colType, colDesc, colBlock);
        tblFraud.setItems(this.fraudLogs);
        tblFraud.setPrefHeight(300);

        vboxSecurity.getChildren().addAll(new Label("Controle de Logs de Segurança e Auditoria de Fraudes:"), tblFraud);
        tabSecurity.setContent(vboxSecurity);

        tabPane.getTabs().addAll(tabTeams, tabStadiums, tabMatches, tabSecurity);
        content.getChildren().addAll(title, tabPane);
        setContent(content);
    }

    private void checkPenaltiesVisible(TextField h, TextField a, Jogo match, VBox penBox) {
        if (!"Grupos".equalsIgnoreCase(match.getPhase())) {
            try {
                int gh = Integer.parseInt(h.getText().trim());
                int ga = Integer.parseInt(a.getText().trim());
                if (gh == ga) {
                    penBox.setVisible(true);
                    penBox.setManaged(true);
                } else {
                    penBox.setVisible(false);
                    penBox.setManaged(false);
                }
            } catch (Exception ex) {
                penBox.setVisible(false);
                penBox.setManaged(false);
            }
        } else {
            penBox.setVisible(false);
            penBox.setManaged(false);
        }
    }

    // ==========================================
    // VIEW 3: Equipas e Plantéis
    // ==========================================
    @SuppressWarnings("unchecked")
    private void showEquipas() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label title = new Label("Seleções e Comitivas");
        title.getStyleClass().add("label-title");

        HBox split = new HBox(20);
        VBox.setVgrow(split, Priority.ALWAYS);

        // Left side: Team selector
        VBox left = new VBox(10);
        left.setPrefWidth(260);
        left.getStyleClass().add("card");
        left.setStyle("-fx-padding: 20px;");
        Label lblSelect = new Label("Selecionar Equipa");
        lblSelect.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ListView<Equipa> lvTeams = new ListView<>();
        lvTeams.setStyle("-fx-background-color: transparent; -fx-control-inner-background: #FFFFFF; -fx-background-radius: 8px;");
        
        // Populate teams
        ObservableList<Equipa> teamsList = FXCollections.observableArrayList(campManager.getEquipas());
        
        ComboBox<String> cmbGroup = new ComboBox<>();
        ComboBox<String> cmbRegion = new ComboBox<>();

        if (utilizadorLogado.getCargo() == TipoUtilizador.GESTOR_EQUIPA) {
            String assoc = utilizadorLogado.getEquipaAssociada();
            List<Equipa> filtered = new ArrayList<>();
            for (Equipa eq : teamsList) {
                if (eq.getNome().equalsIgnoreCase(assoc)) {
                    filtered.add(eq);
                }
            }
            lvTeams.setItems(FXCollections.observableArrayList(filtered));
            left.getChildren().addAll(lblSelect, lvTeams);
        } else {
            lvTeams.setItems(teamsList);

            Label lblFilterGroup = new Label("Filtrar por Grupo:");
            lblFilterGroup.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280; -fx-font-weight: bold;");
            cmbGroup.getItems().addAll("Todos", "Grupo A", "Grupo B", "Grupo C", "Grupo D", "Grupo E", "Grupo F", "Grupo G", "Grupo H");
            cmbGroup.setValue("Todos");
            cmbGroup.setMaxWidth(Double.MAX_VALUE);

            Label lblFilterRegion = new Label("Filtrar por Região:");
            lblFilterRegion.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280; -fx-font-weight: bold;");
            cmbRegion.getItems().addAll("Todas", "Europa (UEFA)", "América do Sul (CONMEBOL)", "América do Norte/Central (CONCACAF)", "África (CAF)", "Ásia/Oceânia (AFC)");
            cmbRegion.setValue("Todas");
            cmbRegion.setMaxWidth(Double.MAX_VALUE);

            left.getChildren().addAll(lblSelect, lblFilterGroup, cmbGroup, lblFilterRegion, cmbRegion, lvTeams);
        }

        // Right side: Player details and stats
        VBox right = new VBox(15);
        HBox.setHgrow(right, Priority.ALWAYS);

        // Selection placeholder
        VBox placeholder = new VBox(10);
        placeholder.setAlignment(Pos.CENTER);
        placeholder.getStyleClass().add("card");
        placeholder.setPadding(new Insets(50));
        placeholder.getChildren().add(new Label("Selecione uma equipa no menu lateral para consultar e gerir o plantel."));
        right.getChildren().add(placeholder);

        split.getChildren().addAll(left, right);

        // Filter Logic Setup (only if not gestor_equipa)
        if (utilizadorLogado.getCargo() != TipoUtilizador.GESTOR_EQUIPA) {
            Runnable updateFilter = () -> {
                String selectedGroup = cmbGroup.getValue();
                String selectedRegion = cmbRegion.getValue();

                List<Equipa> result = new ArrayList<>();
                for (Equipa eq : teamsList) {
                    boolean matchesGroup = "Todos".equals(selectedGroup) || isTeamInGroup(eq.getNome(), selectedGroup);
                    boolean matchesRegion = "Todas".equals(selectedRegion) || isTeamInRegion(eq.getNome(), selectedRegion);
                    if (matchesGroup && matchesRegion) {
                        result.add(eq);
                    }
                }
                lvTeams.setItems(FXCollections.observableArrayList(result));
                
                Equipa selected = lvTeams.getSelectionModel().getSelectedItem();
                if (selected == null || !result.contains(selected)) {
                    right.getChildren().clear();
                    right.getChildren().add(placeholder);
                }
            };

            cmbGroup.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter.run());
            cmbRegion.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter.run());
        }

        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-tab-min-width: 120px; -fx-tab-max-width: 200px;");

        Tab tabPlantel = new Tab("Plantel e Seleções");
        tabPlantel.setClosable(false);
        tabPlantel.setContent(split);

        Tab tabAlojamento = new Tab("Alojamento");
        tabAlojamento.setClosable(false);
        setupTabAlojamento(tabAlojamento);

        tabPane.getTabs().addAll(tabPlantel, tabAlojamento);

        content.getChildren().addAll(title, tabPane);
        setContent(content);

        // Detail update listener (registered BEFORE auto-selecting so it fires correctly)
        lvTeams.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                right.getChildren().clear();

                // 1. Team Header
                HBox headerBox = new HBox(15);
                headerBox.setAlignment(Pos.CENTER_LEFT);
                Label teamTitle = new Label("Seleção: " + newVal.getNome());
                teamTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
                
                // Encontrar grupo dinamicamente para o header
                String groupNameStr = "Grupo ?";
                for (Map.Entry<String, List<String>> entry : campManager.getGrupos().entrySet()) {
                    if (entry.getValue().contains(newVal.getNome())) {
                        groupNameStr = entry.getKey();
                        break;
                    }
                }
                
                Label coachLabel = new Label("Selecionador: " + newVal.getTreinador() + " | " + groupNameStr);
                coachLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #6B7280;");
                VBox headerTexts = new VBox(2, teamTitle, coachLabel);
                headerBox.getChildren().add(headerTexts);
                right.getChildren().add(headerBox);

                // 2. Stats Grid (4 cards)
                HBox statsGrid = new HBox(15);
                statsGrid.setAlignment(Pos.CENTER);
                
                int totalMatches = 0;
                for (Jogo j : campManager.getJogos()) {
                    if ((j.getHomeTeam() != null && j.getHomeTeam().getNome().equalsIgnoreCase(newVal.getNome())) ||
                        (j.getAwayTeam() != null && j.getAwayTeam().getNome().equalsIgnoreCase(newVal.getNome()))) {
                        totalMatches++;
                    }
                }
                
                int aptosCount = 0;
                int teamGoals = 0;
                int teamYellows = 0;
                for (Jogador jog : newVal.getJogadores()) {
                    if (EstadoJogador.APTO.equals(jog.getEstado())) {
                        aptosCount++;
                    }
                    teamGoals += jog.getGoals();
                    teamYellows += jog.getYellowCards();
                }

                statsGrid.getChildren().add(createEquipaMiniStatCard("Total de Jogos", String.valueOf(totalMatches), "📅", "#EFF6FF", "#2563EB"));
                statsGrid.getChildren().add(createEquipaMiniStatCard("Jogadores Aptos", aptosCount + " / " + newVal.getJogadores().size(), "🏃", "#ECFDF5", "#059669"));
                statsGrid.getChildren().add(createEquipaMiniStatCard("Golos Marcados", String.valueOf(teamGoals), "⚽", "#FDF2F2", "#DC2626"));
                statsGrid.getChildren().add(createEquipaMiniStatCard("Total de Amarelos", String.valueOf(teamYellows), "🟨", "#FEF3C7", "#D97706"));
                right.getChildren().add(statsGrid);

                // 3. Hotel Banner (Dark Banner style)
                HBox hotelBanner = new HBox(15);
                hotelBanner.setAlignment(Pos.CENTER_LEFT);
                hotelBanner.setStyle("-fx-background-color: linear-gradient(to right, #111827, #1F2937); -fx-background-radius: 16px; -fx-padding: 15px 20px;");
                
                Hotel allocated = null;
                for (Hotel h : logManager.getHoteis()) {
                    if (h.getEquipaHospedada() != null && h.getEquipaHospedada().getNome().equalsIgnoreCase(newVal.getNome())) {
                        allocated = h;
                        break;
                    }
                }

                VBox hotelTexts = new VBox(2);
                HBox.setHgrow(hotelTexts, Priority.ALWAYS);
                if (allocated != null) {
                    Label lblBTitle = new Label("🏨 Alojamento & Base da Seleção");
                    lblBTitle.setStyle("-fx-text-fill: #A0AEC0; -fx-font-size: 11px; -fx-font-weight: bold;");
                    Label lblBName = new Label(allocated.getNome() + " - " + allocated.getLocalizacao());
                    lblBName.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
                    Label lblBDates = new Label("In: " + allocated.getCheckInDate() + " • Out: " + allocated.getCheckOutDate() + " | Check-in Confirmado");
                    lblBDates.setStyle("-fx-text-fill: #00D26A; -fx-font-size: 12px;");
                    hotelTexts.getChildren().addAll(lblBTitle, lblBName, lblBDates);
                    
                    Button btnCheckout = new Button("Realizar Check-out");
                    btnCheckout.getStyleClass().add("btn-secondary");
                    btnCheckout.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-padding: 6px 12px; -fx-font-size: 11px;");
                    Hotel finalAllocated = allocated;
                    btnCheckout.setOnAction(e -> {
                        ButtonType btnSim = new ButtonType("Sim", ButtonBar.ButtonData.YES);
                        ButtonType btnNao = new ButtonType("Não", ButtonBar.ButtonData.NO);
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Confirmar Check-out de " + finalAllocated.getEquipaHospedada().getNome() + "?", btnSim, btnNao);
                        alert.setTitle("Confirmação de Check-out");
                        alert.setHeaderText("Realizar Check-out");
                        alert.showAndWait().ifPresent(res -> {
                            if (res == btnSim) {
                                logManager.registarCheckout(finalAllocated);
                                lvTeams.getSelectionModel().clearSelection();
                                lvTeams.getSelectionModel().select(newVal); // Reload view
                            }
                        });
                    });
                    hotelBanner.getChildren().addAll(hotelTexts, btnCheckout);
                } else {
                    Label lblBTitle = new Label("🏨 Alojamento & Base da Seleção");
                    lblBTitle.setStyle("-fx-text-fill: #A0AEC0; -fx-font-size: 11px; -fx-font-weight: bold;");
                    Label lblBName = new Label("Sem Alojamento Atribuído");
                    lblBName.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
                    hotelTexts.getChildren().addAll(lblBTitle, lblBName);
                    hotelBanner.getChildren().addAll(hotelTexts);
                }
                right.getChildren().add(hotelBanner);

                // 4. Split Squad Panel (Table + Player Details Card)
                HBox squadSplit = new HBox(20);
                
                VBox tableBox = new VBox(10);
                HBox.setHgrow(tableBox, Priority.ALWAYS);
                tableBox.getStyleClass().add("card");
                tableBox.setStyle("-fx-padding: 15px;");

                HBox tableActions = new HBox(10);
                tableActions.setAlignment(Pos.CENTER_LEFT);
                Label lblSquadTitle = new Label("Plantel Oficial");
                lblSquadTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
                Region space = new Region();
                HBox.setHgrow(space, Priority.ALWAYS);
                
                Button btnConfirmSquad = new Button("Confirmar Convocatória");
                btnConfirmSquad.getStyleClass().add("btn-primary");
                btnConfirmSquad.setStyle("-fx-padding: 6px 12px; -fx-font-size: 11px;");
                btnConfirmSquad.setOnAction(e -> {
                    int starters = 0;
                    for (Jogador j : newVal.getJogadores()) {
                        if (j.isStarter()) starters++;
                    }
                    if (starters == 11) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Sucesso: O onze inicial tem exatamente 11 titulares (Regras FIFA)!", ButtonType.OK);
                        alert.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Erro: O onze inicial deve ter exatamente 11 titulares selecionados (Regras FIFA). Atualmente tem: " + starters, ButtonType.OK);
                        alert.showAndWait();
                    }
                });
                
                Button btnExportSquad = new Button("📄 Exportar Convocatória");
                btnExportSquad.getStyleClass().add("btn-secondary");
                btnExportSquad.setStyle("-fx-padding: 6px 12px; -fx-font-size: 11px;");
                btnExportSquad.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Relatório oficial gerado com " + newVal.getJogadores().size() + " jogadores para " + newVal.getNome() + "!", ButtonType.OK);
                    alert.showAndWait();
                });
                
                tableActions.getChildren().addAll(lblSquadTitle, space, btnConfirmSquad, btnExportSquad);

                TableView<Jogador> tblPlayers = new TableView<>();
                
                TableColumn<Jogador, Number> colNum = new TableColumn<>("#");
                colNum.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getNumeroCamisola()));
                colNum.setPrefWidth(40);

                TableColumn<Jogador, String> colNome = new TableColumn<>("Nome");
                colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
                colNome.setPrefWidth(140);

                TableColumn<Jogador, String> colPos = new TableColumn<>("Posição");
                colPos.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPosicao()));
                colPos.setPrefWidth(90);

                TableColumn<Jogador, String> colEst = new TableColumn<>("Estado");
                colEst.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().toString()));
                colEst.setPrefWidth(80);

                TableColumn<Jogador, String> colFuncao = new TableColumn<>("Função");
                colFuncao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isStarter() ? "Titular" : "Reserva"));
                colFuncao.setPrefWidth(80);

                TableColumn<Jogador, Void> colAct = new TableColumn<>("Ação");
                colAct.setPrefWidth(120);
                colAct.setCellFactory(col -> new TableCell<Jogador, Void>() {
                    private final Button toggleBtn = new Button();
                    {
                        toggleBtn.getStyleClass().add("btn-secondary");
                        toggleBtn.setStyle("-fx-padding: 4px 8px; -fx-font-size: 10px;");
                        toggleBtn.setOnAction(e -> {
                            Jogador jog = getTableView().getItems().get(getIndex());
                            jog.setStarter(!jog.isStarter());
                            campManager.registarEquipa(newVal); // Save changes
                            lvTeams.getSelectionModel().clearSelection();
                            lvTeams.getSelectionModel().select(newVal); // Reload view
                        });
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Jogador jog = getTableView().getItems().get(getIndex());
                            toggleBtn.setText(jog.isStarter() ? "Mudar Reserva" : "Mudar Titular");
                            setGraphic(toggleBtn);
                        }
                    }
                });

                tblPlayers.getColumns().addAll(colNum, colNome, colPos, colEst, colFuncao, colAct);
                tblPlayers.setItems(FXCollections.observableArrayList(newVal.getJogadores()));
                tblPlayers.setPrefHeight(250);
                tableBox.getChildren().addAll(tableActions, tblPlayers);

                // Right side details Card
                VBox detailsCard = new VBox(12);
                detailsCard.setPrefWidth(320);
                detailsCard.getStyleClass().add("card");
                detailsCard.setStyle("-fx-padding: 20px;");
                detailsCard.setAlignment(Pos.TOP_LEFT);

                Label detailsPlaceholder = new Label("Selecione um jogador para consultar a sua ficha técnica e médica.");
                detailsPlaceholder.setWrapText(true);
                detailsPlaceholder.setStyle("-fx-text-fill: #6B7280; -fx-font-style: italic;");
                detailsCard.getChildren().add(detailsPlaceholder);

                // Selection Listener for Player Details
                tblPlayers.getSelectionModel().selectedItemProperty().addListener((o, oldP, selectedP) -> {
                    if (selectedP != null) {
                        detailsCard.getChildren().clear();

                        // Header with profile details
                        HBox pHead = new HBox(12);
                        pHead.setAlignment(Pos.CENTER_LEFT);
                        Label pAvatar = new Label("👤");
                        pAvatar.setStyle("-fx-background-color: #F3F4F6; -fx-min-width: 48px; -fx-min-height: 48px; -fx-background-radius: 12px; -fx-alignment: center; -fx-font-size: 20px;");
                        
                        Label pName = new Label(selectedP.getNome());
                        pName.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
                        Label pNumber = new Label("#" + selectedP.getNumeroCamisola() + " • " + selectedP.getPosicao());
                        pNumber.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 11px;");
                        VBox pHeadTexts = new VBox(2, pName, pNumber);
                        pHead.getChildren().addAll(pAvatar, pHeadTexts);
                        detailsCard.getChildren().add(pHead);

                        // Stats Grid (Goals, Assists, Yellow, Red)
                        GridPane pStats = new GridPane();
                        pStats.setHgap(8);
                        pStats.setVgap(8);
                        
                        pStats.add(createPlayerStatBox("Golos", String.valueOf(selectedP.getGoals())), 0, 0);
                        pStats.add(createPlayerStatBox("Assists", String.valueOf(selectedP.getAssists())), 1, 0);
                        pStats.add(createPlayerStatBox("Amarelos", String.valueOf(selectedP.getYellowCards())), 0, 1);
                        pStats.add(createPlayerStatBox("Vermelhos", String.valueOf(selectedP.getRedCards())), 1, 1);
                        detailsCard.getChildren().add(pStats);

                        // Energy Health Slider & Value
                        VBox energyBox = new VBox(4);
                        HBox energyLabelBox = new HBox();
                        Label energyTitle = new Label("Condição Física (Energia)");
                        energyTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
                        Region eSpace = new Region();
                        HBox.setHgrow(eSpace, Priority.ALWAYS);
                        Label energyVal = new Label(selectedP.getEnergy() + "%");
                        energyVal.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + (selectedP.getEnergy() > 50 ? "#059669" : "#DC2626") + ";");
                        energyLabelBox.getChildren().addAll(energyTitle, eSpace, energyVal);
                        
                        Slider sldEnergy = new Slider(0, 100, selectedP.getEnergy());
                        sldEnergy.valueProperty().addListener((sliderObs, oldEnergy, newEnergy) -> {
                            int val = newEnergy.intValue();
                            energyVal.setText(val + "%");
                            energyVal.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + (val > 50 ? "#059669" : "#DC2626") + ";");
                        });
                        energyBox.getChildren().addAll(energyLabelBox, sldEnergy);
                        detailsCard.getChildren().add(energyBox);

                        // Physical State Choice Box
                        VBox stateBox = new VBox(4);
                        Label lblEstado = new Label("Estado Físico / Disciplinar");
                        lblEstado.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
                        ComboBox<EstadoJogador> cmbEstado = new ComboBox<>(FXCollections.observableArrayList(EstadoJogador.values()));
                        cmbEstado.setValue(selectedP.getEstado());
                        cmbEstado.setMaxWidth(Double.MAX_VALUE);
                        stateBox.getChildren().addAll(lblEstado, cmbEstado);
                        detailsCard.getChildren().add(stateBox);

                        // Team Role Selection
                        VBox roleBox = new VBox(4);
                        Label lblRole = new Label("Função na Equipa");
                        lblRole.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
                        ComboBox<String> cmbRole = new ComboBox<>(FXCollections.observableArrayList("Titular", "Reserva"));
                        cmbRole.setValue(selectedP.isStarter() ? "Titular" : "Reserva");
                        cmbRole.setMaxWidth(Double.MAX_VALUE);
                        roleBox.getChildren().addAll(lblRole, cmbRole);
                        detailsCard.getChildren().add(roleBox);

                        // Injury History list
                        VBox injuryBox = new VBox(6);
                        Label injuryTitle = new Label("🩺 Histórico de Lesões (" + selectedP.getInjuryHistory().size() + ")");
                        injuryTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
                        injuryBox.getChildren().add(injuryTitle);
                        
                        VBox listInjuries = new VBox(5);
                        Runnable populateInjuries = () -> {
                            listInjuries.getChildren().clear();
                            if (selectedP.getInjuryHistory().isEmpty()) {
                                Label emptyLbl = new Label("Sem registo de lesões anteriores.");
                                emptyLbl.setStyle("-fx-text-fill: #6B7280; -fx-font-style: italic; -fx-font-size: 11px;");
                                listInjuries.getChildren().add(emptyLbl);
                            } else {
                                for (String injury : selectedP.getInjuryHistory()) {
                                    Label injuryLbl = new Label(injury);
                                    injuryLbl.setWrapText(true);
                                    injuryLbl.setStyle("-fx-padding: 5px 8px; -fx-background-color: #FDF2F2; -fx-text-fill: #991B1B; -fx-background-radius: 6px; -fx-border-color: #FCA5A5; -fx-border-width: 1px; -fx-border-radius: 6px; -fx-font-size: 10px;");
                                    listInjuries.getChildren().add(injuryLbl);
                                }
                            }
                        };
                        populateInjuries.run();
                        
                        HBox addInjuryBox = new HBox(8);
                        TextField txtNewInjury = new TextField();
                        txtNewInjury.setPromptText("Nova lesão...");
                        HBox.setHgrow(txtNewInjury, Priority.ALWAYS);
                        Button btnAddInjury = new Button("+ Adicionar");
                        btnAddInjury.setStyle("-fx-font-size: 10px; -fx-padding: 4px 8px;");
                        btnAddInjury.setOnAction(evt -> {
                            String desc = txtNewInjury.getText().trim();
                            if (!desc.isEmpty()) {
                                selectedP.addInjury(desc);
                                populateInjuries.run();
                                txtNewInjury.clear();
                            }
                        });
                        addInjuryBox.getChildren().addAll(txtNewInjury, btnAddInjury);
                        injuryBox.getChildren().addAll(listInjuries, addInjuryBox);
                        detailsCard.getChildren().add(injuryBox);

                        // Save & Delete buttons
                        Button btnSave = new Button("Guardar Alterações");
                        btnSave.setMaxWidth(Double.MAX_VALUE);
                        btnSave.getStyleClass().add("btn-primary");
                        btnSave.setOnAction(evt -> {
                            selectedP.setEstado(cmbEstado.getValue());
                            selectedP.setStarter("Titular".equals(cmbRole.getValue()));
                            selectedP.setEnergy((int) sldEnergy.getValue());
                            campManager.registarEquipa(newVal); // Save changes
                            
                            Alert confirm = new Alert(Alert.AlertType.INFORMATION, "Ficha do jogador '" + selectedP.getNome() + "' atualizada com sucesso!");
                            confirm.showAndWait();
                            
                            lvTeams.getSelectionModel().clearSelection();
                            lvTeams.getSelectionModel().select(newVal); // Reload view
                        });
                        
                        Button btnDeleteP = new Button("🗑️ Remover do Plantel");
                        btnDeleteP.setMaxWidth(Double.MAX_VALUE);
                        btnDeleteP.getStyleClass().add("btn-secondary");
                        btnDeleteP.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-padding: 8px; -fx-font-size: 12px;");
                        btnDeleteP.setOnAction(e -> {
                            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Tem a certeza que deseja remover o jogador " + selectedP.getNome() + " do plantel?", ButtonType.YES, ButtonType.NO);
                            confirm.showAndWait();
                            if (confirm.getResult() == ButtonType.YES) {
                                newVal.removerJogador(selectedP.getId());
                                campManager.registarEquipa(newVal); // Save changes
                                lvTeams.getSelectionModel().clearSelection();
                                lvTeams.getSelectionModel().select(newVal); // Reload view
                            }
                        });
                        
                        detailsCard.getChildren().addAll(btnSave, btnDeleteP);
                    }
                });

                squadSplit.getChildren().addAll(tableBox, detailsCard);
                right.getChildren().add(squadSplit);

                // 5. Add Player Card Form
                VBox addPlayerCard = new VBox(12);
                addPlayerCard.getStyleClass().add("card");
                addPlayerCard.setStyle("-fx-padding: 20px;");
                Label addTitle = new Label("Adicionar Novo Jogador");
                addTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                
                HBox addInputs = new HBox(12);
                addInputs.setAlignment(Pos.CENTER_LEFT);
                
                TextField txtPNum = new TextField();
                txtPNum.setPromptText("Camisola (ex: 9)");
                txtPNum.setPrefWidth(120);
                
                TextField txtPName = new TextField();
                txtPName.setPromptText("Nome Completo");
                HBox.setHgrow(txtPName, Priority.ALWAYS);
                
                ComboBox<String> cmbPPos = new ComboBox<>(FXCollections.observableArrayList(
                    "Guarda-Redes", "Defesa", "Médio", "Avançado"
                ));
                cmbPPos.setPromptText("Posição");
                cmbPPos.setPrefWidth(140);
                
                Button btnSaveP = new Button("+ Guardar");
                btnSaveP.getStyleClass().add("btn-primary");
                btnSaveP.setStyle("-fx-padding: 8px 16px;");
                
                addInputs.getChildren().addAll(txtPNum, txtPName, cmbPPos, btnSaveP);
                addPlayerCard.getChildren().addAll(addTitle, addInputs);
                
                btnSaveP.setOnAction(e -> {
                    try {
                        int num = Integer.parseInt(txtPNum.getText().trim());
                        String name = txtPName.getText().trim();
                        String pos = cmbPPos.getValue();
                        
                        if (name.isEmpty() || pos == null) {
                            Alert alert = new Alert(Alert.AlertType.WARNING, "Erro: Preencha todos os campos do jogador!", ButtonType.OK);
                            alert.showAndWait();
                            return;
                        }
                        
                        Jogador newPlayer = new Jogador((int)System.currentTimeMillis(), num, name, pos, EstadoJogador.APTO);
                        boolean ok = newVal.adicionarJogador(newPlayer);
                        if (ok) {
                            campManager.registarEquipa(newVal); // Save
                            lvTeams.getSelectionModel().clearSelection();
                            lvTeams.getSelectionModel().select(newVal); // Reload view
                        } else {
                            Alert alert = new Alert(Alert.AlertType.WARNING, "Erro: Não foi possível adicionar o jogador. Lotação máxima de 26 jogadores atingida ou número de camisola já existente!", ButtonType.OK);
                            alert.showAndWait();
                        }
                    } catch (NumberFormatException ex) {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Erro: O número da camisola deve ser um valor numérico inteiro!", ButtonType.OK);
                        alert.showAndWait();
                    }
                });
                
                right.getChildren().add(addPlayerCard);
            } else {
                right.getChildren().clear();
                right.getChildren().add(placeholder);
            }
        });

        // Auto-select the associated team for GESTOR_EQUIPA - after listener registration
        if (utilizadorLogado.getCargo() == TipoUtilizador.GESTOR_EQUIPA && !lvTeams.getItems().isEmpty()) {
            lvTeams.getSelectionModel().selectFirst();
        }
    }

    private boolean isTeamInGroup(String teamName, String groupName) {
        Map<String, List<String>> grps = campManager.getGrupos();
        List<String> teams = grps.get(groupName);
        return teams != null && teams.contains(teamName);
    }

    private boolean isTeamInRegion(String teamName, String regionName) {
        if (teamName == null) return false;
        String name = teamName.trim();
        switch (regionName) {
            case "Europa (UEFA)":
                return java.util.Arrays.asList(
                    "Portugal", "França", "Franca", "Espanha", "Inglaterra", "Holanda", "Alemanha", 
                    "Itália", "Italia", "Suíça", "Suica", "Bélgica", "Belgica", "Croácia", "Croacia", 
                    "Dinamarca", "Sérvia", "Servia", "Polónia", "Polonia", "Áustria", "Austria", 
                    "Turquia", "Ucrânia", "Ucrania"
                ).stream().anyMatch(name::equalsIgnoreCase);
            case "América do Sul (CONMEBOL)":
                return java.util.Arrays.asList("Brasil", "Argentina", "Uruguai", "Colômbia", "Colombia", "Chile").stream().anyMatch(name::equalsIgnoreCase);
            case "América do Norte/Central (CONCACAF)":
                return java.util.Arrays.asList("Cuba", "EUA", "Canadá", "Canada").stream().anyMatch(name::equalsIgnoreCase);
            case "África (CAF)":
                return java.util.Arrays.asList("Senegal", "Marrocos", "Gana", "Nigéria", "Nigeria").stream().anyMatch(name::equalsIgnoreCase);
            case "Ásia/Oceânia (AFC)":
                return java.util.Arrays.asList("Japão", "Japao", "Coreia do Sul", "Austrália", "Australia", "Irão", "Irao").stream().anyMatch(name::equalsIgnoreCase);
            default:
                return true;
        }
    }

    private void setupTabAlojamento(Tab tabAlojamento) {
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        scroll.setContent(container);

        if (utilizadorLogado.getCargo() == TipoUtilizador.GESTOR_EQUIPA) {
            String equipaAssociada = utilizadorLogado.getEquipaAssociada();
            Hotel allocated = null;
            for (Hotel h : logManager.getHoteis()) {
                if (h.getEquipaHospedada() != null && h.getEquipaHospedada().getNome().equalsIgnoreCase(equipaAssociada)) {
                    allocated = h;
                    break;
                }
            }
            if (allocated != null) {
                VBox hotelCard = createAlojamentoCard(allocated, tabAlojamento);
                container.getChildren().add(hotelCard);
            } else {
                VBox emptyCard = createEmptyAlojamentoCard("Sem alojamento atribuído à equipa: " + (equipaAssociada != null ? equipaAssociada : "Nenhuma"));
                container.getChildren().add(emptyCard);
            }
        } else {
            // ADMIN (ou outros gestores)
            List<Hotel> occupiedHotels = new ArrayList<>();
            for (Hotel h : logManager.getHoteis()) {
                if (h.getEquipaHospedada() != null) {
                    occupiedHotels.add(h);
                }
            }

            if (occupiedHotels.isEmpty()) {
                VBox emptyCard = createEmptyAlojamentoCard("Sem alojamentos atribuídos a equipas de momento.");
                container.getChildren().add(emptyCard);
            } else {
                Label lblTitle = new Label("Lista de Alojamentos das Seleções (" + occupiedHotels.size() + ")");
                lblTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1A202C;");
                container.getChildren().add(lblTitle);

                for (Hotel h : occupiedHotels) {
                    VBox hotelCard = createAlojamentoCard(h, tabAlojamento);
                    container.getChildren().add(hotelCard);
                }
            }
        }
        tabAlojamento.setContent(scroll);
    }

    private VBox createAlojamentoCard(Hotel allocated, Tab tabAlojamento) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setStyle("-fx-background-color: linear-gradient(to right, #111827, #1F2937); -fx-background-radius: 16px; -fx-padding: 25px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 4);");

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("🏨");
        icon.setStyle("-fx-font-size: 24px; -fx-background-color: rgba(255, 255, 255, 0.1); -fx-min-width: 48px; -fx-min-height: 48px; -fx-background-radius: 12px; -fx-alignment: center;");

        VBox titleBox = new VBox(2);
        Label lblHotelName = new Label(allocated.getNome());
        lblHotelName.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label lblLocal = new Label("📍 " + allocated.getLocalizacao());
        lblLocal.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(lblHotelName, lblLocal);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String teamName = allocated.getEquipaHospedada() != null ? allocated.getEquipaHospedada().getNome() : "Sem Equipa";
        Label lblTeamBadge = new Label("⚡ Equipa: " + teamName);
        lblTeamBadge.setStyle("-fx-background-color: #00D26A; -fx-text-fill: white; -fx-padding: 6px 12px; -fx-background-radius: 20px; -fx-font-weight: bold; -fx-font-size: 11px;");

        header.getChildren().addAll(icon, titleBox, spacer, lblTeamBadge);

        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(20);
        detailsGrid.setVgap(12);
        detailsGrid.setStyle("-fx-padding: 10px 0 0 0;");

        VBox boxQuartos = new VBox(4);
        Label lblQuartosTitle = new Label("CAPACIDADE DO HOTEL");
        lblQuartosTitle.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 10px; -fx-font-weight: bold;");
        Label lblQuartos = new Label(allocated.getCapacidadeQuartos() + " Quartos Reservados");
        lblQuartos.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        boxQuartos.getChildren().addAll(lblQuartosTitle, lblQuartos);

        VBox boxCheckIn = new VBox(4);
        Label lblCheckInTitle = new Label("DATA DE CHECK-IN");
        lblCheckInTitle.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 10px; -fx-font-weight: bold;");
        Label lblCheckIn = new Label(allocated.getCheckInDate() != null ? allocated.getCheckInDate() : "N/D");
        lblCheckIn.setStyle("-fx-text-fill: #34D399; -fx-font-size: 14px; -fx-font-weight: bold;");
        boxCheckIn.getChildren().addAll(lblCheckInTitle, lblCheckIn);

        detailsGrid.add(boxQuartos, 0, 0);
        detailsGrid.add(boxCheckIn, 1, 0);

        VBox boxCheckOut = new VBox(4);
        Label lblCheckOutTitle = new Label("DATA DE CHECK-OUT");
        lblCheckOutTitle.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 10px; -fx-font-weight: bold;");
        Label lblCheckOut = new Label(allocated.getCheckOutDate() != null ? allocated.getCheckOutDate() : "N/D");
        lblCheckOut.setStyle("-fx-text-fill: #F87171; -fx-font-size: 14px; -fx-font-weight: bold;");
        boxCheckOut.getChildren().addAll(lblCheckOutTitle, lblCheckOut);

        detailsGrid.add(boxCheckOut, 0, 1);

        card.getChildren().addAll(header, detailsGrid);

        HBox actions = new HBox();
        actions.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnCheckout = new Button("Realizar Check-out");
        btnCheckout.getStyleClass().add("btn-secondary");
        btnCheckout.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-padding: 8px 16px; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 8px;");
        btnCheckout.setOnAction(e -> {
            ButtonType btnSim = new ButtonType("Sim", ButtonBar.ButtonData.YES);
            ButtonType btnNao = new ButtonType("Não", ButtonBar.ButtonData.NO);
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Tem a certeza que deseja realizar o check-out da equipa " + teamName + "?", btnSim, btnNao);
            confirm.setTitle("Confirmação de Check-out");
            confirm.setHeaderText("Realizar Check-out");
            confirm.showAndWait();
            if (confirm.getResult() == btnSim) {
                logManager.registarCheckout(allocated);
                setupTabAlojamento(tabAlojamento);
                showEquipas();
            }
        });
        actions.getChildren().add(btnCheckout);
        card.getChildren().add(actions);

        return card;
    }

    private VBox createEmptyAlojamentoCard(String msg) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("card");
        card.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E5E7EB; -fx-border-radius: 16px; -fx-background-radius: 16px; -fx-padding: 40px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 8, 0, 0, 4);");

        Label icon = new Label("🏨");
        icon.setStyle("-fx-font-size: 32px;");

        Label label = new Label(msg);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #4B5563; -fx-font-weight: bold; -fx-text-alignment: center;");
        label.setWrapText(true);

        card.getChildren().addAll(icon, label);
        return card;
    }

    private VBox createEquipaMiniStatCard(String label, String val, String icon, String bgCol, String textCol) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E5E7EB; -fx-border-radius: 16px; -fx-background-radius: 16px; -fx-padding: 15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 8, 0, 0, 4);");
        card.setPrefWidth(160);
        HBox.setHgrow(card, Priority.ALWAYS);
        
        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);
        
        Label lblIcon = new Label(icon);
        lblIcon.setStyle("-fx-background-color: " + bgCol + "; -fx-text-fill: " + textCol + "; -fx-min-width: 32px; -fx-min-height: 32px; -fx-background-radius: 8px; -fx-alignment: center; -fx-font-size: 14px;");
        
        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280; -fx-font-weight: bold;");
        
        topRow.getChildren().addAll(lblIcon, lblLabel);
        
        Label lblVal = new Label(val);
        lblVal.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1A202C; -fx-padding: 5px 0 0 0;");
        
        card.getChildren().addAll(topRow, lblVal);
        return card;
    }

    private VBox createPlayerStatBox(String label, String val) {
        VBox box = new VBox(4);
        box.setStyle("-fx-background-color: #F9FAFB; -fx-padding: 8px 12px; -fx-background-radius: 8px; -fx-border-color: #E5E7EB; -fx-border-width: 1px; -fx-border-radius: 8px;");
        box.setPrefWidth(125);
        HBox.setHgrow(box, Priority.ALWAYS);
        
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 10px; -fx-font-weight: bold;");
        Label value = new Label(val);
        value.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1A202C;");
        
        box.getChildren().addAll(lbl, value);
        return box;
    }

    private void checkRefNationalityConflict(Arbitro arb, Jogo j, List<String> alerts) {
        if (arb == null || j == null) return;
        String country = arb.getNacionalidade();
        if (country == null || country.isEmpty()) return;
        
        String homeName = (j.getHomeTeam() != null) ? j.getHomeTeam().getNome() : "";
        String awayName = (j.getAwayTeam() != null) ? j.getAwayTeam().getNome() : "";
        
        if (country.equalsIgnoreCase(homeName) || country.equalsIgnoreCase(awayName)) {
            alerts.add("⚠️ Conflito de Nacionalidade: Árbitro " + arb.getNome() + " (" + country + ") escalado para " + homeName + " vs " + awayName + ".");
        }
    }

    private void checkRefRestConflict(Arbitro arb, Jogo jogo, List<String> alerts) {
        if (arb == null || jogo == null) return;
        List<Jogo> todosJogos = campManager.getJogos();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        try {
            LocalDateTime novoJogoTime = LocalDateTime.parse(jogo.getData() + " " + jogo.getHora(), formatter);
            for (Jogo j : todosJogos) {
                if (j.getStatus() != StatusJogo.FINALIZADO && j.getId() != jogo.getId() && temArbitroEscalado(j, arb)) {
                    LocalDateTime outroJogoTime = LocalDateTime.parse(j.getData() + " " + j.getHora(), formatter);
                    long diffHours = ChronoUnit.HOURS.between(outroJogoTime, novoJogoTime);
                    if (Math.abs(diffHours) < 48) {
                        String alertMsg = "⛔ Violação da Regra das 48h: Árbitro " + arb.getNome() + 
                                          " escalado para Jogo " + jogo.getId() + " e Jogo " + j.getId() + 
                                          " com apenas " + Math.abs(diffHours) + "h de intervalo.";
                        String altAlertMsg = "⛔ Violação da Regra das 48h: Árbitro " + arb.getNome() + 
                                             " escalado para Jogo " + j.getId() + " e Jogo " + jogo.getId() + 
                                             " com apenas " + Math.abs(diffHours) + "h de intervalo.";
                        if (!alerts.contains(alertMsg) && !alerts.contains(altAlertMsg)) {
                            alerts.add(alertMsg);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // ignorar falhas de parsing
        }
    }

    private boolean temArbitroEscalado(Jogo j, Arbitro ref) {
        EscalaoArbitral escala = j.getEscalaArbitros();
        if (escala == null) return false;
        return ref.equals(escala.getPrincipal()) ||
               ref.equals(escala.getAssistente1()) ||
               ref.equals(escala.getAssistente2()) ||
               ref.equals(escala.getQuarto()) ||
               ref.equals(escala.getVar());
    }

    // ==========================================
    // VIEW 4: Arbitragem
    // ==========================================
    @SuppressWarnings("unchecked")
    private void showArbitragem() {
        if (utilizadorLogado.getCargo() == TipoUtilizador.PUBLICO) {
            showArbitrosPublico();
            return;
        }

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label title = new Label("Gestão de Arbitragem");
        title.getStyleClass().add("label-title");
        Label subtitle = new Label("Controlo de integridade e escalamento do Mundial 2026");
        subtitle.getStyleClass().add("label-subtitle");

        // 1. Stats Row
        HBox statsGrid = new HBox(15);
        statsGrid.setAlignment(Pos.CENTER);
        
        List<Arbitro> allRefs = arbManager.getArbitros();
        int totalRefs = allRefs.size();
        int activeRefs = 0;
        int restingRefs = 0;
        int inactiveRefs = 0;
        for (Arbitro r : allRefs) {
            if (EstadoArbitro.ATIVO.equals(r.getEstado())) activeRefs++;
            else if (EstadoArbitro.DESCANSO.equals(r.getEstado())) restingRefs++;
            else if (EstadoArbitro.INATIVO.equals(r.getEstado())) inactiveRefs++;
        }
        
        statsGrid.getChildren().addAll(
            createEquipaMiniStatCard("Total de Árbitros", String.valueOf(totalRefs), "⚖️", "#EFF6FF", "#2563EB"),
            createEquipaMiniStatCard("Ativos", String.valueOf(activeRefs), "✅", "#ECFDF5", "#059669"),
            createEquipaMiniStatCard("A Descansar", String.valueOf(restingRefs), "😴", "#FEF3C7", "#D97706"),
            createEquipaMiniStatCard("Inativos", String.valueOf(inactiveRefs), "🚫", "#FDF2F2", "#DC2626")
        );

        TabPane tabPane = new TabPane();

        // ------------------------------------------
        // Tab 1: Lista & Registo
        // ------------------------------------------
        Tab tabList = new Tab("Árbitros Credenciados");
        tabList.setClosable(false);
        HBox splitList = new HBox(20);
        splitList.setPadding(new Insets(20));
        
        // Table (Left)
        VBox tblBox = new VBox(10);
        HBox.setHgrow(tblBox, Priority.ALWAYS);
        tblBox.getStyleClass().add("card");
        tblBox.setStyle("-fx-padding: 15px;");
        
        TableView<Arbitro> tblRefs = new TableView<>();
        TableColumn<Arbitro, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        colId.setPrefWidth(50);
        TableColumn<Arbitro, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colNome.setPrefWidth(130);
        TableColumn<Arbitro, String> colNac = new TableColumn<>("Nacionalidade");
        colNac.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNacionalidade()));
        colNac.setPrefWidth(100);
        TableColumn<Arbitro, String> colTipo = new TableColumn<>("Função");
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().toString()));
        colTipo.setPrefWidth(90);
        TableColumn<Arbitro, Number> colScore = new TableColumn<>("FIFA Score");
        colScore.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getScoreFIFA()));
        colScore.setPrefWidth(80);
        TableColumn<Arbitro, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().toString()));
        colEstado.setPrefWidth(80);

        tblRefs.getColumns().addAll(colId, colNome, colNac, colTipo, colScore, colEstado);
        tblRefs.setItems(FXCollections.observableArrayList(allRefs));
        tblRefs.setPrefHeight(300);
        Button btnResetScores = new Button("🗑️ Limpar Todas as Pontuações");
        btnResetScores.setStyle("-fx-text-fill: #DC2626; -fx-border-color: #DC2626; -fx-background-color: transparent; -fx-border-radius: 6px;");
        btnResetScores.setMaxWidth(Double.MAX_VALUE);
        btnResetScores.setOnAction(ev -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Deseja mesmo limpar o score FIFA de todos os árbitros?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    for (Arbitro r : arbManager.getArbitros()) {
                        r.resetScore();
                        arbManager.registarArbitro(r);
                    }
                    showArbitragem();
                }
            });
        });
        tblBox.getChildren().addAll(new Label("Árbitros Registados no Sistema:"), tblRefs, btnResetScores);

        // Sidebar Alerts + Form (Right)
        VBox sideBox = new VBox(15);
        sideBox.setPrefWidth(350);
        
        // Critical alerts banner
        VBox alertsCard = new VBox(10);
        alertsCard.getStyleClass().add("card");
        alertsCard.setStyle("-fx-padding: 15px;");
        Label lblAlertsTitle = new Label("🚨 Alertas Críticos de Validação");
        lblAlertsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        alertsCard.getChildren().add(lblAlertsTitle);
        
        List<String> refAlerts = new ArrayList<>();
        // Calculate conflicts
        for (Jogo j : campManager.getJogos()) {
            if (j.getStatus() != StatusJogo.FINALIZADO && j.getEscalaArbitros() != null) {
                EscalaoArbitral esc = j.getEscalaArbitros();
                // Neutrality checks
                checkRefNationalityConflict(esc.getPrincipal(), j, refAlerts);
                checkRefNationalityConflict(esc.getAssistente1(), j, refAlerts);
                checkRefNationalityConflict(esc.getAssistente2(), j, refAlerts);
                checkRefNationalityConflict(esc.getQuarto(), j, refAlerts);
                checkRefNationalityConflict(esc.getVar(), j, refAlerts);

                // 48h Resting checks
                checkRefRestConflict(esc.getPrincipal(), j, refAlerts);
                checkRefRestConflict(esc.getAssistente1(), j, refAlerts);
                checkRefRestConflict(esc.getAssistente2(), j, refAlerts);
                checkRefRestConflict(esc.getQuarto(), j, refAlerts);
                checkRefRestConflict(esc.getVar(), j, refAlerts);
            }
        }
        
        if (refAlerts.isEmpty()) {
            Label noAlerts = new Label("✅ Todos os escalamentos cumprem as regras de integridade e repouso da FIFA.");
            noAlerts.setWrapText(true);
            noAlerts.setStyle("-fx-text-fill: #059669; -fx-font-size: 11px;");
            alertsCard.getChildren().add(noAlerts);
        } else {
            for (String alertMsg : refAlerts) {
                Label alertLabel = new Label(alertMsg);
                alertLabel.setWrapText(true);
                alertLabel.setStyle("-fx-padding: 6px 10px; -fx-background-color: #FFFBEB; -fx-text-fill: #92400E; -fx-background-radius: 6px; -fx-border-color: #FBBF24; -fx-border-width: 1px; -fx-border-radius: 6px; -fx-font-size: 10px;");
                alertsCard.getChildren().add(alertLabel);
            }
        }
        sideBox.getChildren().add(alertsCard);
        
        // Form Card
        VBox regCard = new VBox(10);
        regCard.getStyleClass().add("card");
        regCard.setStyle("-fx-padding: 15px;");
        Label regHeader = new Label("Credenciar Novo Árbitro");
        regHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        
        TextField txtId = new TextField(); txtId.setPromptText("ID do Árbitro");
        TextField txtMail = new TextField(); txtMail.setPromptText("Email");
        TextField txtName = new TextField(); txtName.setPromptText("Nome Completo");
        TextField txtNac = new TextField(); txtNac.setPromptText("Nacionalidade (ex: Portugal)");
        
        ComboBox<TipoArbitro> cmbTipo = new ComboBox<>(FXCollections.observableArrayList(TipoArbitro.values()));
        cmbTipo.setPromptText("Função");
        cmbTipo.setMaxWidth(Double.MAX_VALUE);
        
        Button btnSaveRef = new Button("Credenciar Árbitro");
        btnSaveRef.getStyleClass().add("btn-primary");
        btnSaveRef.setMaxWidth(Double.MAX_VALUE);
        
        regCard.getChildren().addAll(regHeader, txtId, txtMail, txtName, txtNac, cmbTipo, btnSaveRef);
        sideBox.getChildren().add(regCard);
        
        btnSaveRef.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                String email = txtMail.getText().trim();
                String name = txtName.getText().trim();
                String nac = txtNac.getText().trim();
                TipoArbitro tipo = cmbTipo.getValue();

                if (email.isEmpty() || name.isEmpty() || nac.isEmpty() || tipo == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Erro: Preencha todos os campos!", ButtonType.OK);
                    alert.showAndWait();
                    return;
                }
                if (arbManager.procurarArbitroPorId(id) != null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Erro: ID do árbitro já existe!", ButtonType.OK);
                    alert.showAndWait();
                    return;
                }

                Arbitro ref = new Arbitro(id, email, name, nac, tipo);
                arbManager.registarArbitro(ref);
                showArbitragem(); // Refresh entire view
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Erro: ID do árbitro deve ser um número inteiro!", ButtonType.OK);
                alert.showAndWait();
            }
        });

        // State Editor Card
        VBox stateCard = new VBox(10);
        stateCard.getStyleClass().add("card");
        stateCard.setStyle("-fx-padding: 15px;");
        Label stateHeader = new Label("Alterar Estado de Árbitro");
        stateHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        
        ComboBox<EstadoArbitro> cmbEstado = new ComboBox<>(FXCollections.observableArrayList(EstadoArbitro.values()));
        cmbEstado.setPromptText("Selecione o Estado");
        cmbEstado.setMaxWidth(Double.MAX_VALUE);
        
        Button btnUpdateEstado = new Button("Atualizar Estado");
        btnUpdateEstado.getStyleClass().add("btn-primary");
        btnUpdateEstado.setMaxWidth(Double.MAX_VALUE);
        btnUpdateEstado.setDisable(true);
        cmbEstado.setDisable(true);
        
        stateCard.getChildren().addAll(stateHeader, cmbEstado, btnUpdateEstado);
        sideBox.getChildren().add(stateCard);

        tblRefs.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                cmbEstado.setValue(newSel.getEstado());
                cmbEstado.setDisable(false);
                btnUpdateEstado.setDisable(false);
            } else {
                cmbEstado.setValue(null);
                cmbEstado.setDisable(true);
                btnUpdateEstado.setDisable(true);
            }
        });

        btnUpdateEstado.setOnAction(ev -> {
            Arbitro selected = tblRefs.getSelectionModel().getSelectedItem();
            EstadoArbitro newEst = cmbEstado.getValue();
            if (selected != null && newEst != null) {
                selected.setEstado(newEst);
                arbManager.registarArbitro(selected); // Save/update
                showArbitragem(); // Refresh
            }
        });
        
        splitList.getChildren().addAll(tblBox, sideBox);
        tabList.setContent(splitList);

        // ------------------------------------------
        // Tab 2: Escalar Árbitro
        // ------------------------------------------
        Tab tabScale = new Tab("Escalar Árbitro para Jogo");
        tabScale.setClosable(false);
        VBox vboxScale = new VBox(15);
        vboxScale.setPadding(new Insets(20));
        
        HBox scaleSplit = new HBox(20);
        
        // Match selection and card (Left)
        VBox matchSelectBox = new VBox(12);
        matchSelectBox.getStyleClass().add("card");
        matchSelectBox.setStyle("-fx-padding: 20px;");
        matchSelectBox.setPrefWidth(450);
        
        ComboBox<Jogo> cmbScaleMatch = new ComboBox<>();
        cmbScaleMatch.setPromptText("Selecione o Jogo para Escalar");
        cmbScaleMatch.setMaxWidth(Double.MAX_VALUE);
        
        List<Jogo> activeMatches = new ArrayList<>();
        for (Jogo j : campManager.getJogos()) {
            if (!StatusJogo.FINALIZADO.equals(j.getStatus())) {
                activeMatches.add(j);
            }
        }
        cmbScaleMatch.setItems(FXCollections.observableArrayList(activeMatches));
        
        VBox matchInfoCard = new VBox(10);
        matchInfoCard.setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #E5E7EB; -fx-padding: 15px; -fx-background-radius: 12px; -fx-border-radius: 12px;");
        Label lblMatchTitle = new Label("Nenhum Jogo Selecionado");
        lblMatchTitle.setStyle("-fx-font-weight: bold;");
        Label lblMatchSub = new Label("Selecione um jogo acima para ver o estádio e a data.");
        lblMatchSub.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");
        matchInfoCard.getChildren().addAll(lblMatchTitle, lblMatchSub);
        
        matchSelectBox.getChildren().addAll(new Label("1. Selecione a Partida:"), cmbScaleMatch, matchInfoCard);
        
        // Assigner inputs (Right)
        VBox assignCard = new VBox(12);
        assignCard.getStyleClass().add("card");
        assignCard.setStyle("-fx-padding: 20px;");
        HBox.setHgrow(assignCard, Priority.ALWAYS);
        assignCard.setDisable(true);
        
        ComboBox<Arbitro> cmbPrincipal = new ComboBox<>(); cmbPrincipal.setPromptText("Árbitro Principal"); cmbPrincipal.setMaxWidth(Double.MAX_VALUE);
        ComboBox<Arbitro> cmbA1 = new ComboBox<>(); cmbA1.setPromptText("Assistente 1"); cmbA1.setMaxWidth(Double.MAX_VALUE);
        ComboBox<Arbitro> cmbA2 = new ComboBox<>(); cmbA2.setPromptText("Assistente 2"); cmbA2.setMaxWidth(Double.MAX_VALUE);
        ComboBox<Arbitro> cmbQuarto = new ComboBox<>(); cmbQuarto.setPromptText("Quarto Árbitro"); cmbQuarto.setMaxWidth(Double.MAX_VALUE);
        ComboBox<Arbitro> cmbVar = new ComboBox<>(); cmbVar.setPromptText("Árbitro VAR"); cmbVar.setMaxWidth(Double.MAX_VALUE);
        
        // Filter referees by type in comboboxes
        List<Arbitro> principals = new ArrayList<>();
        List<Arbitro> assistants = new ArrayList<>();
        List<Arbitro> fourths = new ArrayList<>();
        List<Arbitro> vars = new ArrayList<>();
        for (Arbitro r : allRefs) {
            if (TipoArbitro.PRINCIPAL.equals(r.getTipo())) principals.add(r);
            else if (TipoArbitro.ASSISTENTE.equals(r.getTipo())) assistants.add(r);
            else if (TipoArbitro.QUARTO.equals(r.getTipo())) fourths.add(r);
            else if (TipoArbitro.VAR.equals(r.getTipo())) vars.add(r);
        }
        
        cmbPrincipal.setItems(FXCollections.observableArrayList(principals));
        cmbA1.setItems(FXCollections.observableArrayList(assistants));
        cmbA2.setItems(FXCollections.observableArrayList(assistants));
        cmbQuarto.setItems(FXCollections.observableArrayList(fourths));
        cmbVar.setItems(FXCollections.observableArrayList(vars));
        
        Button btnConfirmScale = new Button("Confirmar Escala Oficial");
        btnConfirmScale.getStyleClass().add("btn-primary");
        btnConfirmScale.setMaxWidth(Double.MAX_VALUE);
        
        assignCard.getChildren().addAll(
            new Label("2. Escolha a Equipa de Arbitragem:"),
            cmbPrincipal, cmbA1, cmbA2, cmbQuarto, cmbVar, btnConfirmScale
        );
        
        cmbScaleMatch.valueProperty().addListener((o, ov, newVal) -> {
            if (newVal != null) {
                assignCard.setDisable(false);
                lblMatchTitle.setText((newVal.getHomeTeam() != null ? newVal.getHomeTeam().getNome() : "A definir") + " vs " + (newVal.getAwayTeam() != null ? newVal.getAwayTeam().getNome() : "A definir"));
                lblMatchSub.setText(newVal.getEstadio().getNome() + " • " + newVal.getData() + " às " + newVal.getHora());
                
                // Populate current scaled if already assigned
                EscalaoArbitral currentEsc = newVal.getEscalaArbitros();
                if (currentEsc != null) {
                    cmbPrincipal.setValue(currentEsc.getPrincipal());
                    cmbA1.setValue(currentEsc.getAssistente1());
                    cmbA2.setValue(currentEsc.getAssistente2());
                    cmbQuarto.setValue(currentEsc.getQuarto());
                    cmbVar.setValue(currentEsc.getVar());
                } else {
                    cmbPrincipal.setValue(null);
                    cmbA1.setValue(null);
                    cmbA2.setValue(null);
                    cmbQuarto.setValue(null);
                    cmbVar.setValue(null);
                }
            } else {
                assignCard.setDisable(true);
                lblMatchTitle.setText("Nenhum Jogo Selecionado");
                lblMatchSub.setText("Selecione um jogo acima para ver o estádio e a data.");
            }
        });
        
        btnConfirmScale.setOnAction(e -> {
            Jogo j = cmbScaleMatch.getValue();
            Arbitro p = cmbPrincipal.getValue();
            Arbitro a1 = cmbA1.getValue();
            Arbitro a2 = cmbA2.getValue();
            Arbitro q = cmbQuarto.getValue();
            Arbitro v = cmbVar.getValue();
            
            if (j == null || p == null || a1 == null || a2 == null || q == null || v == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Erro: Selecione os árbitros para todas as 5 funções!", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            if (a1.equals(a2)) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Erro: Os assistentes 1 e 2 devem ser árbitros diferentes!", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            
            try {
                // Perform escalation for each role
                boolean okP = arbManager.escalarArbitro(j, p, TipoArbitro.PRINCIPAL);
                boolean okA1 = arbManager.escalarArbitro(j, a1, TipoArbitro.ASSISTENTE);
                boolean okA2 = arbManager.escalarArbitro(j, a2, TipoArbitro.ASSISTENTE);
                boolean okQ = arbManager.escalarArbitro(j, q, TipoArbitro.QUARTO);
                boolean okV = arbManager.escalarArbitro(j, v, TipoArbitro.VAR);
                
                if (okP && okA1 && okA2 && okQ && okV) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Sucesso: Equipa de Arbitragem escalada com sucesso!", ButtonType.OK);
                    alert.showAndWait();
                    showArbitragem(); // Refresh entire view to recalculate validation rules
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Erro: Falha na escalação (um ou mais árbitros falharam as regras de elegibilidade da FIFA)!", ButtonType.OK);
                    alert.showAndWait();
                }
            } catch (IllegalStateException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Erro de Conflito: " + ex.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        });

        scaleSplit.getChildren().addAll(matchSelectBox, assignCard);
        vboxScale.getChildren().add(scaleSplit);
        tabScale.setContent(vboxScale);

        // ------------------------------------------
        // Tab 3: Avaliar Árbitro
        // ------------------------------------------
        Tab tabEval = new Tab("Avaliar Árbitros");
        tabEval.setClosable(false);
        VBox vboxEval = new VBox(15);
        vboxEval.setPadding(new Insets(20));
        
        HBox evalSplit = new HBox(20);
        
        VBox evalMatchBox = new VBox(12);
        evalMatchBox.getStyleClass().add("card");
        evalMatchBox.setStyle("-fx-padding: 20px;");
        evalMatchBox.setPrefWidth(450);
        
        ComboBox<Jogo> cmbEvalMatch = new ComboBox<>();
        cmbEvalMatch.setPromptText("Selecione o Jogo Finalizado");
        cmbEvalMatch.setMaxWidth(Double.MAX_VALUE);
        
        List<Jogo> finalizedMatches = new ArrayList<>();
        for (Jogo j : campManager.getJogos()) {
            if (StatusJogo.FINALIZADO.equals(j.getStatus())) {
                finalizedMatches.add(j);
            }
        }
        cmbEvalMatch.setItems(FXCollections.observableArrayList(finalizedMatches));
        
        VBox evalMatchInfo = new VBox(10);
        evalMatchInfo.setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #E5E7EB; -fx-padding: 15px; -fx-background-radius: 12px; -fx-border-radius: 12px;");
        Label lblEvalTitle = new Label("Nenhum Jogo Selecionado");
        lblEvalTitle.setStyle("-fx-font-weight: bold;");
        Label lblEvalSub = new Label("Selecione um jogo para iniciar a avaliação.");
        lblEvalSub.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");
        evalMatchInfo.getChildren().addAll(lblEvalTitle, lblEvalSub);
        
        evalMatchBox.getChildren().addAll(new Label("1. Escolha o Jogo Finalizado:"), cmbEvalMatch, evalMatchInfo);
        
        VBox evalCard = new VBox(12);
        evalCard.getStyleClass().add("card");
        evalCard.setStyle("-fx-padding: 20px;");
        HBox.setHgrow(evalCard, Priority.ALWAYS);
        evalCard.setDisable(true);
        
        ComboBox<Integer> cmbRatePrincipal = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5)); cmbRatePrincipal.setPromptText("Nota Principal (1-5)"); cmbRatePrincipal.setMaxWidth(Double.MAX_VALUE);
        ComboBox<Integer> cmbRateA1 = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5)); cmbRateA1.setPromptText("Nota Assistente 1 (1-5)"); cmbRateA1.setMaxWidth(Double.MAX_VALUE);
        ComboBox<Integer> cmbRateA2 = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5)); cmbRateA2.setPromptText("Nota Assistente 2 (1-5)"); cmbRateA2.setMaxWidth(Double.MAX_VALUE);
        ComboBox<Integer> cmbRateQuarto = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5)); cmbRateQuarto.setPromptText("Nota Quarto Árbitro (1-5)"); cmbRateQuarto.setMaxWidth(Double.MAX_VALUE);
        ComboBox<Integer> cmbRateVar = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5)); cmbRateVar.setPromptText("Nota VAR (1-5)"); cmbRateVar.setMaxWidth(Double.MAX_VALUE);
        
        Button btnSaveEval = new Button("Submeter Notas Oficiais");
        btnSaveEval.getStyleClass().add("btn-primary");
        btnSaveEval.setMaxWidth(Double.MAX_VALUE);
        
        evalCard.getChildren().addAll(
            new Label("2. Atribua as Notas da FIFA:"),
            cmbRatePrincipal, cmbRateA1, cmbRateA2, cmbRateQuarto, cmbRateVar, btnSaveEval
        );
        
        cmbEvalMatch.valueProperty().addListener((o, ov, newVal) -> {
            if (newVal != null) {
                evalCard.setDisable(false);
                lblEvalTitle.setText((newVal.getHomeTeam() != null ? newVal.getHomeTeam().getNome() : "A definir") + " vs " + (newVal.getAwayTeam() != null ? newVal.getAwayTeam().getNome() : "A definir"));
                lblEvalSub.setText("Resultado: " + newVal.getGoalsHome() + " - " + newVal.getGoalsAway() + "\nEstádio: " + newVal.getEstadio().getNome());
                
                EscalaoArbitral esc = newVal.getEscalaArbitros();
                if (esc == null) {
                    evalCard.setDisable(true);
                    lblEvalSub.setText(lblEvalSub.getText() + "\n\u26A0\uFE0F Este jogo não possui escala de árbitros para avaliar.");
                    cmbRatePrincipal.setPromptText("Nota Principal (1-5)");
                    cmbRateA1.setPromptText("Nota Assistente 1 (1-5)");
                    cmbRateA2.setPromptText("Nota Assistente 2 (1-5)");
                    cmbRateQuarto.setPromptText("Nota Quarto Árbitro (1-5)");
                    cmbRateVar.setPromptText("Nota VAR (1-5)");
                } else {
                    evalCard.setDisable(false);
                    cmbRatePrincipal.setDisable(esc.getPrincipal() == null);
                    cmbRateA1.setDisable(esc.getAssistente1() == null);
                    cmbRateA2.setDisable(esc.getAssistente2() == null);
                    cmbRateQuarto.setDisable(esc.getQuarto() == null);
                    cmbRateVar.setDisable(esc.getVar() == null);

                    cmbRatePrincipal.setPromptText(esc.getPrincipal() != null ? esc.getPrincipal().getNome() + " (" + esc.getPrincipal().getNacionalidade() + ")" : "Sem árbitro");
                    cmbRateA1.setPromptText(esc.getAssistente1() != null ? esc.getAssistente1().getNome() + " (" + esc.getAssistente1().getNacionalidade() + ")" : "Sem árbitro");
                    cmbRateA2.setPromptText(esc.getAssistente2() != null ? esc.getAssistente2().getNome() + " (" + esc.getAssistente2().getNacionalidade() + ")" : "Sem árbitro");
                    cmbRateQuarto.setPromptText(esc.getQuarto() != null ? esc.getQuarto().getNome() + " (" + esc.getQuarto().getNacionalidade() + ")" : "Sem árbitro");
                    cmbRateVar.setPromptText(esc.getVar() != null ? esc.getVar().getNome() + " (" + esc.getVar().getNacionalidade() + ")" : "Sem árbitro");
                }
            } else {
                evalCard.setDisable(true);
                lblEvalTitle.setText("Nenhum Jogo Selecionado");
                lblEvalSub.setText("Selecione um jogo para iniciar a avaliação.");
            }
        });
        
        btnSaveEval.setOnAction(e -> {
            Jogo j = cmbEvalMatch.getValue();
            if (j == null) return;
            
            int pVal = cmbRatePrincipal.isDisable() ? -1 : (cmbRatePrincipal.getValue() != null ? cmbRatePrincipal.getValue() : 5);
            int a1Val = cmbRateA1.isDisable() ? -1 : (cmbRateA1.getValue() != null ? cmbRateA1.getValue() : 5);
            int a2Val = cmbRateA2.isDisable() ? -1 : (cmbRateA2.getValue() != null ? cmbRateA2.getValue() : 5);
            int qVal = cmbRateQuarto.isDisable() ? -1 : (cmbRateQuarto.getValue() != null ? cmbRateQuarto.getValue() : 5);
            int vVal = cmbRateVar.isDisable() ? -1 : (cmbRateVar.getValue() != null ? cmbRateVar.getValue() : 5);
            
            try {
                arbManager.avaliarDesempenho(j, pVal, a1Val, a2Val, qVal, vVal);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Sucesso: Notas submetidas. FIFA Scores recalculados!", ButtonType.OK);
                alert.showAndWait();
                showArbitragem(); // Reload view
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Erro: " + ex.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        });
        
        evalSplit.getChildren().addAll(evalMatchBox, evalCard);
        vboxEval.getChildren().add(evalSplit);
        tabEval.setContent(vboxEval);

        tabPane.getTabs().addAll(tabList, tabScale, tabEval);
        content.getChildren().addAll(title, subtitle, statsGrid, tabPane);
        setContent(content);
    }

    private void showArbitrosPublico() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label title = new Label("Lista de Árbitros");
        title.getStyleClass().add("label-title");
        Label subtitle = new Label("Consulta oficial de árbitros, origem e classificações (Score FIFA).");
        subtitle.getStyleClass().add("label-subtitle");

        VBox tblBox = new VBox(15);
        tblBox.getStyleClass().add("card");
        tblBox.setStyle("-fx-padding: 20px;");

        TableView<Arbitro> tblRefs = new TableView<>();
        
        TableColumn<Arbitro, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()));
        colId.setPrefWidth(80);
        
        TableColumn<Arbitro, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colNome.setPrefWidth(280);
        
        TableColumn<Arbitro, String> colNac = new TableColumn<>("Origem");
        colNac.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNacionalidade()));
        colNac.setPrefWidth(220);
        
        TableColumn<Arbitro, Number> colScore = new TableColumn<>("Pontuação");
        colScore.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getScoreFIFA()));
        colScore.setPrefWidth(180);

        tblRefs.getColumns().addAll(colId, colNome, colNac, colScore);
        tblRefs.setItems(FXCollections.observableArrayList(arbManager.getArbitros()));
        tblRefs.setPrefHeight(450);

        tblBox.getChildren().addAll(new Label("Árbitros do Campeonato Mundial:"), tblRefs);
        content.getChildren().addAll(title, subtitle, tblBox);
        setContent(content);
    }

    // VIEW 5: Logística
    // ==========================================
    private void showLogistica() {
        showLogistica(0);
    }

    private void showLogistica(int activeTab) {
        VBox content = new VBox(25);
        content.setPadding(new Insets(30));

        Label title = new Label("Gestão de Logística");
        title.getStyleClass().add("label-title");
        Label subtitle = new Label("Controlo de alojamentos, frotas de transporte e inventários da FIFA");
        subtitle.getStyleClass().add("label-subtitle");

        VBox titleBox = new VBox(5);
        titleBox.getChildren().addAll(title, subtitle);

        // Stats Row
        HBox statsGrid = new HBox(15);
        statsGrid.setAlignment(Pos.CENTER);
        
        List<Hotel> hotelsList = logManager.getHoteis();
        int totalHotels = hotelsList.size();
        int occupiedHotels = 0;
        for (Hotel h : hotelsList) {
            if (h.getEquipaHospedada() != null) {
                occupiedHotels++;
            }
        }
        int availableHotels = totalHotels - occupiedHotels;

        VBox cardTotal = createEquipaMiniStatCard("Total de Hotéis", String.valueOf(totalHotels), "🏨", "#EFF6FF", "#2563EB");
        VBox cardOccupied = createEquipaMiniStatCard("Ocupados", String.valueOf(occupiedHotels), "🧡", "#FFFBEB", "#F59E0B");
        VBox cardAvailable = createEquipaMiniStatCard("Disponíveis", String.valueOf(availableHotels), "💚", "#ECFDF5", "#059669");

        Label lblTotalVal = (Label) cardTotal.getChildren().get(1);
        Label lblOccupiedVal = (Label) cardOccupied.getChildren().get(1);
        Label lblAvailableVal = (Label) cardAvailable.getChildren().get(1);

        statsGrid.getChildren().addAll(cardTotal, cardOccupied, cardAvailable);

        TabPane tabPane = new TabPane();

        // ------------------------------------------
        // Tab 1: Alojamento
        // ------------------------------------------
        Tab tabHotels = new Tab("Alojamento");
        tabHotels.setClosable(false);
        VBox vboxHotels = new VBox(15);
        vboxHotels.setPadding(new Insets(20));
        
        HBox hotelsHeader = new HBox(10);
        hotelsHeader.setAlignment(Pos.CENTER_LEFT);
        Label lblSummary = new Label(occupiedHotels + " de " + totalHotels + " hotéis ocupados");
        lblSummary.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1F2937;");
        Region sp1 = new Region();
        HBox.setHgrow(sp1, Priority.ALWAYS);
        Button btnAddHotel = new Button("+ Adicionar Hotel");
        btnAddHotel.getStyleClass().add("btn-primary");
        btnAddHotel.setOnAction(e -> showAddHotelDialog());
        hotelsHeader.getChildren().addAll(lblSummary, sp1, btnAddHotel);

        // Filters for Alojamento
        HBox filterHotelsBox = new HBox(15);
        filterHotelsBox.setAlignment(Pos.CENTER_LEFT);
        filterHotelsBox.setStyle("-fx-padding: 12px; -fx-background-color: #F9FAFB; -fx-background-radius: 12px; -fx-border-color: #E5E7EB; -fx-border-width: 1px;");
        
        TextField txtSearchHotel = new TextField();
        txtSearchHotel.setPromptText(" 🔍 Procurar hotel ou localização...");
        txtSearchHotel.setPrefWidth(250);
        txtSearchHotel.setStyle("-fx-font-size: 12px; -fx-background-radius: 8px; -fx-padding: 8px;");
        
        ComboBox<String> cmbStatusHotel = new ComboBox<>();
        cmbStatusHotel.getItems().addAll("Todos os Estados", "Disponível", "Ocupado");
        cmbStatusHotel.setValue("Todos os Estados");
        cmbStatusHotel.setStyle("-fx-font-size: 12px; -fx-background-radius: 8px;");
        cmbStatusHotel.setPrefWidth(150);
        
        ComboBox<String> cmbCapHotel = new ComboBox<>();
        cmbCapHotel.getItems().addAll("Qualquer capacidade", ">= 10 pessoas", ">= 50 pessoas", ">= 100 pessoas");
        cmbCapHotel.setValue("Qualquer capacidade");
        cmbCapHotel.setStyle("-fx-font-size: 12px; -fx-background-radius: 8px;");
        cmbCapHotel.setPrefWidth(180);
        
        filterHotelsBox.getChildren().addAll(new Label("Filtros:"), txtSearchHotel, cmbStatusHotel, cmbCapHotel);

        FlowPane hotelGrid = new FlowPane(20, 20);
        hotelGrid.setAlignment(Pos.TOP_LEFT);
        hotelGrid.setPrefWrapLength(950);
        
        // Populate hotel grid dynamically
        Runnable refreshHotels = () -> {
            hotelGrid.getChildren().clear();
            List<Hotel> currentHotels = logManager.getHoteis();
            
            String search = txtSearchHotel.getText().trim().toLowerCase();
            String status = cmbStatusHotel.getValue();
            String capSel = cmbCapHotel.getValue();
            int minCap = 0;
            if (">= 10 pessoas".equals(capSel)) minCap = 10;
            else if (">= 50 pessoas".equals(capSel)) minCap = 50;
            else if (">= 100 pessoas".equals(capSel)) minCap = 100;
            
            List<Hotel> filteredHotels = new ArrayList<>();
            for (Hotel h : currentHotels) {
                boolean matchesSearch = search.isEmpty() ||
                                        (h.getNome() != null && h.getNome().toLowerCase().contains(search)) ||
                                        (h.getLocalizacao() != null && h.getLocalizacao().toLowerCase().contains(search));
                
                boolean isOccupied = h.getEquipaHospedada() != null;
                boolean matchesStatus = "Todos os Estados".equals(status) ||
                                        ("Ocupado".equals(status) && isOccupied) ||
                                        ("Disponível".equals(status) && !isOccupied);
                                        
                boolean matchesCap = h.getCapacidadeQuartos() >= minCap;
                
                if (matchesSearch && matchesStatus && matchesCap) {
                    filteredHotels.add(h);
                }
            }
            
            int occ = 0;
            for (Hotel h : currentHotels) {
                if (h.getEquipaHospedada() != null) occ++;
            }
            lblTotalVal.setText(String.valueOf(currentHotels.size()));
            lblOccupiedVal.setText(String.valueOf(occ));
            lblAvailableVal.setText(String.valueOf(currentHotels.size() - occ));
            lblSummary.setText(occ + " de " + currentHotels.size() + " hotéis ocupados (filtrados: " + filteredHotels.size() + ")");
            
            for (Hotel h : filteredHotels) {
                VBox card = new VBox(12);
                boolean isOccupied = h.getEquipaHospedada() != null;
                
                card.setStyle("-fx-background-color: #FFFFFF; " +
                              "-fx-border-color: " + (isOccupied ? "#F59E0B" : "#00D26A") + " #E5E7EB #E5E7EB #E5E7EB; " +
                              "-fx-border-width: 6px 1px 1px 1px; " +
                              "-fx-border-radius: 20px; " +
                              "-fx-background-radius: 20px; " +
                              "-fx-padding: 20px; " +
                              "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 10, 0, 0, 4); " +
                              "-fx-min-width: 280px; -fx-max-width: 280px; -fx-pref-width: 280px;");
                
                HBox r1 = new HBox(10);
                r1.setAlignment(Pos.TOP_LEFT);
                
                VBox titleInfo = new VBox(3);
                Label lblName = new Label(h.getNome());
                lblName.setWrapText(true);
                lblName.setMaxWidth(160);
                lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1A202C;");
                Label lblLoc = new Label("📍 " + h.getLocalizacao());
                lblLoc.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 11px;");
                
                int stars = (h.getId() % 2 == 0) ? 5 : 4;
                Label lblStars = new Label("★".repeat(stars) + "☆".repeat(5-stars));
                lblStars.setStyle("-fx-text-fill: #FBBF24; -fx-font-size: 12px;");
                titleInfo.getChildren().addAll(lblName, lblLoc, lblStars);
                
                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);
                
                Label badge = new Label(isOccupied ? "OCUPADO" : "DISPONÍVEL");
                badge.setStyle("-fx-background-color: " + (isOccupied ? "#FEF3C7" : "#DCFCE7") + "; " +
                               "-fx-text-fill: " + (isOccupied ? "#92400E" : "#166534") + "; " +
                               "-fx-background-radius: 50px; " +
                               "-fx-padding: 3px 8px; " +
                               "-fx-font-size: 9px; " +
                               "-fx-font-weight: bold;");
                
                Button btnDelete = new Button("🗑");
                btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-padding: 2px 6px; -fx-cursor: hand; -fx-font-size: 13px; -fx-border-color: #FCA5A5; -fx-border-radius: 6px;");
                btnDelete.setOnAction(evt -> {
                    if (isOccupied) {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Este hotel está atualmente ocupado pela comitiva de " + h.getEquipaHospedada().getNome() + ". Por favor, realize o Check-out antes de o eliminar.");
                        alert.setTitle("Impossível Eliminar");
                        alert.setHeaderText("Hotel com Ocupação Ativa");
                        alert.showAndWait();
                    } else {
                        ButtonType btnSim = new ButtonType("Sim", ButtonBar.ButtonData.YES);
                        ButtonType btnNao = new ButtonType("Não", ButtonBar.ButtonData.NO);
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Tem a certeza que deseja eliminar permanentemente o hotel " + h.getNome() + "?", btnSim, btnNao);
                        alert.setTitle("Eliminar Hotel");
                        alert.setHeaderText("Confirmar Eliminação");
                        alert.showAndWait().ifPresent(res -> {
                            if (res == btnSim) {
                                logManager.removerHotel(h.getId());
                                showLogistica(0); // Refresh screen
                            }
                        });
                    }
                });

                HBox topActions = new HBox(5);
                topActions.setAlignment(Pos.CENTER_RIGHT);
                topActions.getChildren().addAll(badge, btnDelete);
                
                r1.getChildren().addAll(titleInfo, sp, topActions);
                
                Label lblCap = new Label("👤 Capacidade: " + h.getCapacidadeQuartos() + " pessoas");
                lblCap.setStyle("-fx-background-color: #F9FAFB; " +
                               "-fx-padding: 6px 10px; " +
                               "-fx-background-radius: 8px; " +
                               "-fx-border-color: #E5E7EB; " +
                               "-fx-border-width: 1px; " +
                               "-fx-text-fill: #4B5563; " +
                               "-fx-font-size: 11px;");
                lblCap.setMaxWidth(Double.MAX_VALUE);
                
                card.getChildren().addAll(r1, lblCap);
                
                if (isOccupied) {
                    VBox teamBox = new VBox(4);
                    teamBox.setStyle("-fx-background-color: #EFF6FF; " +
                                     "-fx-border-color: #DBEAFE; " +
                                     "-fx-border-width: 1px; " +
                                     "-fx-border-radius: 12px; " +
                                     "-fx-background-radius: 12px; " +
                                     "-fx-padding: 8px;");
                    Label lblTeam = new Label("🚩 " + h.getEquipaHospedada().getNome());
                    lblTeam.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E40AF; -fx-font-size: 12px;");
                    Label lblDates = new Label("In: " + h.getCheckInDate() + "\nOut: " + h.getCheckOutDate());
                    lblDates.setStyle("-fx-text-fill: #3B82F6; -fx-font-size: 10px;");
                    teamBox.getChildren().addAll(lblTeam, lblDates);
                    
                    Button btnOut = new Button("Realizar Check-out");
                    btnOut.setStyle("-fx-background-color: transparent; -fx-border-color: #FECACA; -fx-border-radius: 8px; -fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-padding: 6px; -fx-cursor: hand; -fx-font-size: 11px;");
                    btnOut.setMaxWidth(Double.MAX_VALUE);
                    btnOut.setOnAction(evt -> {
                        ButtonType btnSim = new ButtonType("Sim", ButtonBar.ButtonData.YES);
                        ButtonType btnNao = new ButtonType("Não", ButtonBar.ButtonData.NO);
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Confirmar Check-out de " + h.getEquipaHospedada().getNome() + "?", btnSim, btnNao);
                        alert.setTitle("Confirmação de Check-out");
                        alert.setHeaderText("Realizar Check-out");
                        alert.showAndWait().ifPresent(res -> {
                            if (res == btnSim) {
                                logManager.registarCheckout(h);
                                showLogistica(0); // Refresh entire screen
                            }
                        });
                    });
                    
                    card.getChildren().addAll(teamBox, btnOut);
                } else {
                    Region spacer = new Region();
                    VBox.setVgrow(spacer, Priority.ALWAYS);
                    spacer.setPrefHeight(45);
                    
                    Button btnIn = new Button("Atribuir Equipa");
                    btnIn.getStyleClass().add("btn-primary");
                    btnIn.setStyle("-fx-padding: 8px; -fx-font-size: 11px;");
                    btnIn.setMaxWidth(Double.MAX_VALUE);
                    btnIn.setOnAction(evt -> showAssignTeamDialog(h));
                    
                    card.getChildren().addAll(spacer, btnIn);
                }
                
                hotelGrid.getChildren().add(card);
            }
        };
        refreshHotels.run();
        
        txtSearchHotel.textProperty().addListener((obs, oldVal, newVal) -> refreshHotels.run());
        cmbStatusHotel.valueProperty().addListener((obs, oldVal, newVal) -> refreshHotels.run());
        cmbCapHotel.valueProperty().addListener((obs, oldVal, newVal) -> refreshHotels.run());
        
        ScrollPane scrollHotels = new ScrollPane(hotelGrid);
        scrollHotels.setFitToWidth(true);
        scrollHotels.setStyle("-fx-background-color: transparent; -fx-padding: 5px;");
        scrollHotels.setPrefHeight(400);

        vboxHotels.getChildren().addAll(hotelsHeader, filterHotelsBox, scrollHotels);
        tabHotels.setContent(vboxHotels);

        // ------------------------------------------
        // Tab 2: Transportes & Viagens
        // ------------------------------------------
        Tab tabTransports = new Tab("Transportes & Viagens");
        tabTransports.setClosable(false);
        VBox vboxTransports = new VBox(15);
        vboxTransports.setPadding(new Insets(20));

        HBox transHeader = new HBox(10);
        transHeader.setAlignment(Pos.CENTER_LEFT);
        Label lblTransTitle = new Label("Frota de Transportes e Planeamento de Viagens");
        lblTransTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1F2937;");
        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);
        Button btnPlanTravel = new Button("+ Nova Viagem");
        btnPlanTravel.getStyleClass().add("btn-primary");
        btnPlanTravel.setOnAction(e -> showPlanTravelDialog());
        transHeader.getChildren().addAll(lblTransTitle, sp2, btnPlanTravel);

        // Filters for Transportes & Viagens
        HBox filterTransportsBox = new HBox(15);
        filterTransportsBox.setAlignment(Pos.CENTER_LEFT);
        filterTransportsBox.setStyle("-fx-padding: 12px; -fx-background-color: #F9FAFB; -fx-background-radius: 12px; -fx-border-color: #E5E7EB; -fx-border-width: 1px;");
        
        TextField txtSearchTransport = new TextField();
        txtSearchTransport.setPromptText("🔍 Procurar origem, destino, equipa, motorista...");
        txtSearchTransport.setPrefWidth(320);
        txtSearchTransport.setStyle("-fx-font-size: 12px; -fx-background-radius: 8px; -fx-padding: 8px;");
        
        ComboBox<String> cmbTypeTransport = new ComboBox<>();
        cmbTypeTransport.getItems().addAll("Todos os Transportes", "Autocarro", "Avião");
        cmbTypeTransport.setValue("Todos os Transportes");
        cmbTypeTransport.setStyle("-fx-font-size: 12px; -fx-background-radius: 8px;");
        cmbTypeTransport.setPrefWidth(180);
        
        filterTransportsBox.getChildren().addAll(new Label("Filtros:"), txtSearchTransport, cmbTypeTransport);

        HBox transStats = new HBox(15);
        transStats.getChildren().addAll(
            createEquipaMiniStatCard("Total Frota", "32", "🚌", "#EFF6FF", "#2563EB"),
            createEquipaMiniStatCard("Rotas Ativas", "12", "🛣️", "#ECFDF5", "#059669"),
            createEquipaMiniStatCard("Atrasos Detetados", "0", "⚠️", "#FFF1F2", "#DC2626")
        );

        HBox transBody = new HBox(20);
        transBody.setAlignment(Pos.TOP_LEFT);

        // Column 1: Frota Atribuida (HBox list inside VBox card)
        VBox cardFleet = new VBox(12);
        cardFleet.getStyleClass().add("card");
        cardFleet.setStyle("-fx-padding: 20px;");
        HBox.setHgrow(cardFleet, Priority.ALWAYS);
        Label lblFleetTitle = new Label("Frota Atribuída (Simulação)");
        lblFleetTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1A202C;");
        
        VBox fleetList = new VBox(8);

        // Column 2: Viagens Planeadas (dynamic list from logManager.getViagens())
        VBox cardTravels = new VBox(12);
        cardTravels.getStyleClass().add("card");
        cardTravels.setStyle("-fx-padding: 20px;");
        HBox.setHgrow(cardTravels, Priority.ALWAYS);
        Label lblTravelsTitle = new Label("Viagens de Jogo Planeadas (Real)");
        lblTravelsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1A202C;");

        VBox travelList = new VBox(8);

        Runnable refreshTransports = () -> {
            fleetList.getChildren().clear();
            travelList.getChildren().clear();
            
            String search = txtSearchTransport.getText().trim().toLowerCase();
            String typeFilter = cmbTypeTransport.getValue();
            
            // 1. Populate fleetData (Simulação)
            String[][] fleetData = {
                {"BUS-001", "Portugal", "Carlos Silva", "Lisboa", "Em Rota", "Autocarro"},
                {"BUS-002", "Brasil", "Ricardo Gomes", "Porto", "Estacionado", "Autocarro"},
                {"BUS-003", "Espanha", "Mario Diaz", "Faro", "Em Rota", "Autocarro"},
                {"BUS-004", "Alemanha", "Hans Müller", "Porto", "Estacionado", "Autocarro"}
            };
            
            for (String[] bus : fleetData) {
                boolean matchesSearch = search.isEmpty() ||
                                        bus[0].toLowerCase().contains(search) ||
                                        bus[1].toLowerCase().contains(search) ||
                                        bus[2].toLowerCase().contains(search) ||
                                        bus[3].toLowerCase().contains(search);
                                        
                boolean matchesType = "Todos os Transportes".equals(typeFilter) ||
                                      ("Autocarro".equals(typeFilter) && "Autocarro".equalsIgnoreCase(bus[5])) ||
                                      ("Avião".equals(typeFilter) && "Avião".equalsIgnoreCase(bus[5]));
                                      
                if (matchesSearch && matchesType) {
                    HBox busRow = new HBox(10);
                    busRow.setStyle("-fx-padding: 10px; -fx-border-color: #E5E7EB; -fx-border-width: 0 0 1px 0;");
                    busRow.setAlignment(Pos.CENTER_LEFT);
                    
                    VBox busInfo = new VBox(2);
                    Label lblBusName = new Label(bus[0] + " • " + bus[1]);
                    lblBusName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
                    Label lblBusSub = new Label("Motorista: " + bus[2] + " • " + bus[3]);
                    lblBusSub.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 10px;");
                    busInfo.getChildren().addAll(lblBusName, lblBusSub);
                    
                    Region rSp = new Region();
                    HBox.setHgrow(rSp, Priority.ALWAYS);
                    
                    Label busBadge = new Label(bus[4]);
                    boolean inRoute = "Em Rota".equals(bus[4]);
                    busBadge.setStyle("-fx-background-color: " + (inRoute ? "#DBEAFE" : "#F3F4F6") + "; " +
                                     "-fx-text-fill: " + (inRoute ? "#1E40AF" : "#6B7280") + "; " +
                                     "-fx-background-radius: 50px; -fx-padding: 3px 8px; -fx-font-size: 9px; -fx-font-weight: bold;");
                    
                    busRow.getChildren().addAll(busInfo, rSp, busBadge);
                    fleetList.getChildren().add(busRow);
                }
            }
            if (fleetList.getChildren().isEmpty()) {
                Label lblEmpty = new Label("Nenhum transporte corresponde aos filtros.");
                lblEmpty.setStyle("-fx-text-fill: #6B7280; -fx-font-style: italic; -fx-font-size: 11px; -fx-padding: 10px;");
                fleetList.getChildren().add(lblEmpty);
            }
            
            // 2. Populate travels (Real)
            List<Viagem> viags = logManager.getViagens();
            List<Viagem> filteredViags = new ArrayList<>();
            for (Viagem v : viags) {
                boolean matchesSearch = search.isEmpty() ||
                                        (v.getOrigem() != null && v.getOrigem().toLowerCase().contains(search)) ||
                                        (v.getDestino() != null && v.getDestino().toLowerCase().contains(search));
                
                boolean matchesType = "Todos os Transportes".equals(typeFilter) ||
                                      ("Autocarro".equals(typeFilter) && "Autocarro".equalsIgnoreCase(v.getMeioTransporte())) ||
                                      ("Avião".equals(typeFilter) && "Avião".equalsIgnoreCase(v.getMeioTransporte()));
                                      
                if (matchesSearch && matchesType) {
                    filteredViags.add(v);
                }
            }
            
            if (filteredViags.isEmpty()) {
                Label lblEmpty = new Label("Nenhuma viagem registada com estes filtros.");
                lblEmpty.setStyle("-fx-text-fill: #6B7280; -fx-font-style: italic; -fx-font-size: 11px;");
                travelList.getChildren().add(lblEmpty);
            } else {
                for (Viagem v : filteredViags) {
                    HBox vRow = new HBox(10);
                    vRow.setStyle("-fx-padding: 10px; -fx-background-color: #F9FAFB; -fx-background-radius: 12px; -fx-border-color: #E5E7EB; -fx-border-width: 1px; -fx-border-radius: 12px;");
                    vRow.setAlignment(Pos.CENTER_LEFT);
                    
                    VBox vInfo = new VBox(2);
                    Label lblRoute = new Label(v.getOrigem() + " ➔ " + v.getDestino());
                    lblRoute.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #1F2937;");
                    Label lblDetails = new Label("Partida: " + v.getDataPartida() + " • Chegada: " + v.getDataChegada());
                    lblDetails.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 10px;");
                    vInfo.getChildren().addAll(lblRoute, lblDetails);
                    
                    Region rSp = new Region();
                    HBox.setHgrow(rSp, Priority.ALWAYS);
                    
                    Label vBadge = new Label(v.getMeioTransporte());
                    vBadge.setStyle("-fx-background-color: #E0F2FE; -fx-text-fill: #0369A1; -fx-background-radius: 50px; -fx-padding: 3px 8px; -fx-font-size: 9px; -fx-font-weight: bold;");
                    
                    vRow.getChildren().addAll(vInfo, rSp, vBadge);
                    travelList.getChildren().add(vRow);
                }
            }
        };
        
        refreshTransports.run();
        txtSearchTransport.textProperty().addListener((obs, oldVal, newVal) -> refreshTransports.run());
        cmbTypeTransport.valueProperty().addListener((obs, oldVal, newVal) -> refreshTransports.run());

        cardFleet.getChildren().addAll(lblFleetTitle, fleetList);
        cardTravels.getChildren().addAll(lblTravelsTitle, travelList);

        transBody.getChildren().addAll(cardFleet, cardTravels);
        vboxTransports.getChildren().addAll(transHeader, filterTransportsBox, transStats, transBody);
        tabTransports.setContent(vboxTransports);

        // ------------------------------------------
        // Tab 3: Inventario
        // ------------------------------------------
        Tab tabInventory = new Tab("Inventário");
        tabInventory.setClosable(false);
        VBox vboxInventory = new VBox(15);
        vboxInventory.setPadding(new Insets(20));

        Label lblInvTitle = new Label("Controlo de Equipamento e Stock FIFA");
        lblInvTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1F2937;");

        if (this.inventoryItems == null) {
            this.inventoryItems = FXCollections.observableArrayList(
                new InventoryItem("Bolas Oficiais (Adidas)", "Equipamento", 245, "OK"),
                new InventoryItem("Coletes de Treino (Verde)", "Têxtil", 120, "OK"),
                new InventoryItem("Kits de Primeiros Socorros", "Médico", 8, "Crítico"),
                new InventoryItem("Água Mineral (6 Pack)", "Consumíveis", 1500, "OK"),
                new InventoryItem("Tablets VAR (Suporte)", "Tecnologia", 42, "Baixo")
            );
        }

        // Filters for Inventário
        HBox filterInvBox = new HBox(15);
        filterInvBox.setAlignment(Pos.CENTER_LEFT);
        filterInvBox.setStyle("-fx-padding: 12px; -fx-background-color: #F9FAFB; -fx-background-radius: 12px; -fx-border-color: #E5E7EB; -fx-border-width: 1px;");
        
        TextField txtSearchInv = new TextField();
        txtSearchInv.setPromptText("🔍 Procurar item ou categoria...");
        txtSearchInv.setPrefWidth(220);
        txtSearchInv.setStyle("-fx-font-size: 12px; -fx-background-radius: 8px; -fx-padding: 8px;");
        
        ComboBox<String> cmbStatusInv = new ComboBox<>();
        cmbStatusInv.getItems().addAll("Todos os Estados", "OK", "Baixo", "Crítico");
        cmbStatusInv.setValue("Todos os Estados");
        cmbStatusInv.setStyle("-fx-font-size: 12px; -fx-background-radius: 8px;");
        cmbStatusInv.setPrefWidth(150);
        
        ToggleButton btnHighlightRep = new ToggleButton("⚠️ Reposição Necessária");
        btnHighlightRep.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 8px;");
        
        filterInvBox.getChildren().addAll(new Label("Filtros:"), txtSearchInv, cmbStatusInv, btnHighlightRep);

        javafx.collections.transformation.FilteredList<InventoryItem> filteredInv = new javafx.collections.transformation.FilteredList<>(this.inventoryItems, p -> true);
        
        Runnable updateInvFilter = () -> {
            String text = txtSearchInv.getText().trim().toLowerCase();
            String status = cmbStatusInv.getValue();
            boolean showReplenish = btnHighlightRep.isSelected();
            
            filteredInv.setPredicate(item -> {
                boolean matchesText = text.isEmpty() ||
                                      (item.getName() != null && item.getName().toLowerCase().contains(text)) ||
                                      (item.getCategory() != null && item.getCategory().toLowerCase().contains(text));
                                      
                boolean matchesStatus = "Todos os Estados".equals(status) ||
                                        status.equals(item.getStatus());
                                        
                boolean matchesReplenish = !showReplenish ||
                                           "Baixo".equals(item.getStatus()) ||
                                           "Crítico".equals(item.getStatus());
                                           
                return matchesText && matchesStatus && matchesReplenish;
            });
        };
        
        txtSearchInv.textProperty().addListener((obs, oldVal, newVal) -> updateInvFilter.run());
        cmbStatusInv.valueProperty().addListener((obs, oldVal, newVal) -> updateInvFilter.run());
        btnHighlightRep.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                btnHighlightRep.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; -fx-border-color: #F87171; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-weight: bold; -fx-font-size: 12px;");
            } else {
                btnHighlightRep.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 8px;");
            }
            updateInvFilter.run();
        });

        TableView<InventoryItem> tblInv = new TableView<>();
        
        TableColumn<InventoryItem, String> colItem = new TableColumn<>("Item");
        colItem.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colItem.setPrefWidth(250);

        TableColumn<InventoryItem, String> colCat = new TableColumn<>("Categoria");
        colCat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory()));
        colCat.setPrefWidth(150);

        TableColumn<InventoryItem, Number> colStock = new TableColumn<>("Stock Atual");
        colStock.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getStock()));
        colStock.setPrefWidth(120);

        TableColumn<InventoryItem, String> colStatus = new TableColumn<>("Estado");
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
        colStatus.setPrefWidth(120);
        colStatus.setCellFactory(column -> new TableCell<InventoryItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label(item);
                    if ("Crítico".equals(item)) {
                        badge.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; -fx-background-radius: 50px; -fx-padding: 4px 10px; -fx-font-size: 11px; -fx-font-weight: bold;");
                    } else if ("Baixo".equals(item)) {
                        badge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #92400E; -fx-background-radius: 50px; -fx-padding: 4px 10px; -fx-font-size: 11px; -fx-font-weight: bold;");
                    } else {
                        badge.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #166534; -fx-background-radius: 50px; -fx-padding: 4px 10px; -fx-font-size: 11px; -fx-font-weight: bold;");
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        TableColumn<InventoryItem, Void> colAction = new TableColumn<>("Ações");
        colAction.setPrefWidth(100);
        colAction.setCellFactory(column -> new TableCell<InventoryItem, Void>() {
            private final Button btn = new Button("Gerir");
            {
                btn.setStyle("-fx-background-color: transparent; -fx-border-color: #D1D5DB; -fx-border-radius: 6px; -fx-text-fill: #374151; -fx-font-size: 11px; -fx-cursor: hand;");
                btn.setOnAction(event -> {
                    InventoryItem item = getTableView().getItems().get(getIndex());
                    TextInputDialog tid = new TextInputDialog(String.valueOf(item.getStock()));
                    tid.setTitle("Ajustar Stock - " + item.getName());
                    tid.setHeaderText("Ajustar stock atual de " + item.getName());
                    tid.setContentText("Novo Stock:");
                    Button cancelBtn = (Button) tid.getDialogPane().lookupButton(ButtonType.CANCEL);
                    if (cancelBtn != null) cancelBtn.setText("Cancelar");
                    Button okBtn = (Button) tid.getDialogPane().lookupButton(ButtonType.OK);
                    if (okBtn != null) okBtn.setText("Confirmar");
                    tid.showAndWait().ifPresent(val -> {
                        try {
                            int newStock = Integer.parseInt(val.trim());
                            item.setStock(newStock);
                            getTableView().refresh();
                            updateInvFilter.run();
                        } catch (NumberFormatException ex) {
                            Alert err = new Alert(Alert.AlertType.ERROR, "Valor inválido!");
                            err.showAndWait();
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        tblInv.getColumns().addAll(colItem, colCat, colStock, colStatus, colAction);
        tblInv.setItems(filteredInv);
        tblInv.setPrefHeight(350);

        VBox cardInv = new VBox(10);
        cardInv.getStyleClass().add("card");
        cardInv.setStyle("-fx-padding: 15px;");
        cardInv.getChildren().addAll(tblInv);

        vboxInventory.getChildren().addAll(lblInvTitle, filterInvBox, cardInv);
        tabInventory.setContent(vboxInventory);

        tabPane.getTabs().addAll(tabHotels, tabTransports, tabInventory);
        tabPane.getSelectionModel().select(activeTab);
        content.getChildren().addAll(titleBox, statsGrid, tabPane);
        setContent(content);
    }

    // ==========================================
    // VIEW 6: Bilheteira
    // ==========================================
    private void showBilheteira() {
        VBox content = new VBox(25);
        content.setPadding(new Insets(30));

        if (utilizadorLogado.getCargo() == TipoUtilizador.PUBLICO) {
            // ========================================================
            // ADEPTO / PUBLICO VIEW: TICKET PURCHASE
            // ========================================================
            Label title = new Label("Bilheteira Oficial WC 2026");
            title.getStyleClass().add("label-title");
            Label subtitle = new Label("Selecione um jogo para consultar preços e comprar bilhetes oficiais");
            subtitle.getStyleClass().add("label-subtitle");

            VBox titleBox = new VBox(5);
            titleBox.getChildren().addAll(title, subtitle);

            VBox form = new VBox(15);
            form.getStyleClass().add("card");
            form.setPadding(new Insets(25));

            ComboBox<Jogo> cmbMatches = new ComboBox<>();
            cmbMatches.setPromptText("Selecione o Jogo");
            cmbMatches.setMinWidth(350);

            // Populate matches not finalized
            List<Jogo> active = new ArrayList<>();
            for (Jogo j : campManager.getJogos()) {
                if (!StatusJogo.FINALIZADO.equals(j.getStatus())) {
                    active.add(j);
                }
            }
            cmbMatches.setItems(FXCollections.observableArrayList(active));

            VBox sectorGrid = new VBox(10);
            sectorGrid.setStyle("-fx-padding: 10px 0;");
            Label sectorTitle = new Label("Setores Disponíveis no Estádio:");
            sectorTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #1A202C;");
            sectorTitle.setVisible(false);
            sectorTitle.setManaged(false);

            VBox sectorsBox = new VBox(8);
            sectorGrid.getChildren().addAll(sectorTitle, sectorsBox);

            HBox buyBox = new HBox(15);
            buyBox.setAlignment(Pos.CENTER_LEFT);
            buyBox.setVisible(false);
            buyBox.setManaged(false);

            ComboBox<String> cmbSectorSelect = new ComboBox<>();
            cmbSectorSelect.setPromptText("Escolha o Setor");

            ComboBox<Integer> cmbQty = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4));
            cmbQty.setPromptText("Quantidade");
            cmbQty.setValue(1);

            Button btnBuy = new Button("Comprar Bilhete(s)");
            btnBuy.getStyleClass().add("btn-primary");
            Label lblMsg = new Label();
            buyBox.getChildren().addAll(cmbSectorSelect, cmbQty, btnBuy, lblMsg);

            cmbMatches.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && newVal.getEstadio() != null) {
                    sectorTitle.setVisible(true);
                    sectorTitle.setManaged(true);
                    buyBox.setVisible(true);
                    buyBox.setManaged(true);

                    sectorsBox.getChildren().clear();
                    List<String> sectorNames = new ArrayList<>();
                    for (SetorEstadio s : newVal.getEstadio().getSetores()) {
                        int available = s.getCapacidadeTotal() - s.getBilhetesVendidos();
                        HBox row = new HBox(15);
                        row.setStyle("-fx-padding: 12px; -fx-background-color: #F9FAFB; -fx-background-radius: 12px; -fx-border-color: #E5E7EB; -fx-border-width: 1px; -fx-border-radius: 12px;");
                        row.setAlignment(Pos.CENTER_LEFT);
                        
                        Label nameLbl = new Label(s.getNome());
                        nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #1A202C; -fx-font-size: 14px;");
                        
                        Region sReg = new Region();
                        HBox.setHgrow(sReg, Priority.ALWAYS);

                        Label priceLbl = new Label(s.getPrecoBase() + " EUR");
                        priceLbl.setStyle("-fx-text-fill: #00D26A; -fx-font-weight: bold; -fx-font-size: 14px;");

                        double occPerc = s.getCapacidadeTotal() > 0 ? ((double) s.getBilhetesVendidos() / s.getCapacidadeTotal()) * 100 : 0;
                        Label capLbl = new Label("Disponível: " + available + "/" + s.getCapacidadeTotal() + " (" + Math.round(100 - occPerc) + "% livre)");
                        capLbl.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");

                        row.getChildren().addAll(nameLbl, sReg, priceLbl, capLbl);
                        sectorsBox.getChildren().add(row);
                        
                        if (available > 0) {
                            sectorNames.add(s.getNome());
                        }
                    }
                    cmbSectorSelect.setItems(FXCollections.observableArrayList(sectorNames));
                } else {
                    sectorTitle.setVisible(false);
                    sectorTitle.setManaged(false);
                    buyBox.setVisible(false);
                    buyBox.setManaged(false);
                    sectorsBox.getChildren().clear();
                }
            });

            btnBuy.setOnAction(e -> {
                Jogo j = cmbMatches.getValue();
                String sec = cmbSectorSelect.getValue();
                Integer qty = cmbQty.getValue();

                if (j == null || sec == null || qty == null) {
                    lblMsg.setText("Selecione o setor e a quantidade!");
                    lblMsg.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
                    return;
                }

                boolean ok = bilManager.venderBilhete(j, sec, qty);
                if (ok) {
                    double total = qty * j.getEstadio().getSetorPorNome(sec).getPrecoBase();
                    lblMsg.setText("Compra efetuada! Total: " + total + " EUR");
                    lblMsg.setStyle("-fx-text-fill: #00D26A; -fx-font-weight: bold;");
                    
                    // Refresh fields
                    cmbMatches.setValue(null);
                    cmbMatches.setValue(j);
                } else {
                    lblMsg.setText("Falha na compra. Limite excedido ou lotação cheia!");
                    lblMsg.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
                }
            });

            form.getChildren().addAll(new Label("Comprar Bilhetes Online:"), cmbMatches, sectorGrid, buyBox);
            content.getChildren().addAll(titleBox, form);
            setContent(content);

        } else {
            // ========================================================
            // MANAGER / ADMIN VIEW: SALES PERFORMANCE DASHBOARD
            // ========================================================
            Label title = new Label("Performance de Vendas");
            title.getStyleClass().add("label-title");
            Label subtitle = new Label("Monitorização de receitas, ocupações de estádios e integridade de bilhetes");
            subtitle.getStyleClass().add("label-subtitle");

            VBox titleBox = new VBox(5);
            titleBox.getChildren().addAll(title, subtitle);

            // fraudLogs already initialized in constructor

            // Stats Row Calculations
            double totalRev = 0;
            for (Bilhete b : bilManager.getBilhetes()) {
                totalRev += b.getPreco();
            }

            int totalSold = 0;
            int totalCap = 0;
            for (Jogo j : campManager.getJogos()) {
                if (j.getEstadio() != null) {
                    for (SetorEstadio s : j.getEstadio().getSetores()) {
                        totalSold += s.getBilhetesVendidos();
                        totalCap += s.getCapacidadeTotal();
                    }
                }
            }
            double occPercent = totalCap > 0 ? ((double) totalSold / totalCap) * 100 : 0;
            int fraudCount = fraudLogs.size();

            // Stats grid layout
            HBox statsGrid = new HBox(15);
            statsGrid.setAlignment(Pos.CENTER);

            VBox cardRevenue = createEquipaMiniStatCard("Faturação Total", String.format("%,.2f €", totalRev), "🎟️", "#ECFDF5", "#00D26A");
            VBox cardOccupancy = createEquipaMiniStatCard("Ocupação Global", String.format("%.1f %%", occPercent), "📊", "#EFF6FF", "#2563EB");
            VBox cardFraud = createEquipaMiniStatCard("Suspeitas Fraude", String.valueOf(fraudCount), "🛡️", "#FDF2F2", "#DC2626");

            statsGrid.getChildren().addAll(cardRevenue, cardOccupancy, cardFraud);

            TabPane tabPane = new TabPane();

            // Tab 1: Controlo de Jogos
            Tab tabMatches = new Tab("Controlo de Jogos");
            tabMatches.setClosable(false);
            VBox vboxMatches = new VBox(15);
            vboxMatches.setPadding(new Insets(20));

            TableView<Jogo> tblMatches = new TableView<>();
            
            TableColumn<Jogo, String> colMatch = new TableColumn<>("Jogo");
            colMatch.setCellValueFactory(c -> {
                Jogo j = c.getValue();
                String h = j.getHomeTeam() != null ? j.getHomeTeam().getNome() : "[A definir]";
                String a = j.getAwayTeam() != null ? j.getAwayTeam().getNome() : "[A definir]";
                return new SimpleStringProperty(h + " vs " + a);
            });
            colMatch.setPrefWidth(220);

            TableColumn<Jogo, String> colStadium = new TableColumn<>("Estádio");
            colStadium.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstadio() != null ? c.getValue().getEstadio().getNome() : "N/A"));
            colStadium.setPrefWidth(160);

            TableColumn<Jogo, String> colOcc = new TableColumn<>("Ocupação");
            colOcc.setPrefWidth(180);
            colOcc.setCellValueFactory(c -> {
                Jogo j = c.getValue();
                int sold = 0;
                int cap = 0;
                if (j.getEstadio() != null) {
                    for (SetorEstadio s : j.getEstadio().getSetores()) {
                        sold += s.getBilhetesVendidos();
                        cap += s.getCapacidadeTotal();
                    }
                }
                double pct = cap > 0 ? ((double) sold / cap) * 100 : 0;
                return new SimpleStringProperty(String.format("%.1f %% (%d/%d)", pct, sold, cap));
            });

            TableColumn<Jogo, String> colPrice = new TableColumn<>("Preços");
            colPrice.setPrefWidth(140);
            colPrice.setCellValueFactory(c -> {
                Jogo j = c.getValue();
                if (j.getEstadio() == null || j.getEstadio().getSetores().isEmpty()) return new SimpleStringProperty("N/A");
                double min = Double.MAX_VALUE;
                double max = 0;
                for (SetorEstadio s : j.getEstadio().getSetores()) {
                    if (s.getPrecoBase() < min) min = s.getPrecoBase();
                    if (s.getPrecoBase() > max) max = s.getPrecoBase();
                }
                return new SimpleStringProperty(String.format("%.1f€ - %.1f€", min, max));
            });

            TableColumn<Jogo, Void> colActions = new TableColumn<>("Ações");
            colActions.setPrefWidth(100);
            colActions.setCellFactory(column -> new TableCell<Jogo, Void>() {
                private final Button btn = new Button("Gerir");
                {
                    btn.setStyle("-fx-background-color: #111827; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 6px 12px; -fx-cursor: hand; -fx-font-size: 11px;");
                    btn.setOnAction(evt -> {
                        Jogo j = getTableView().getItems().get(getIndex());
                        showManagePricesDialog(j);
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableView().getItems().get(getIndex()).getEstadio() == null) {
                        setGraphic(null);
                    } else {
                        setGraphic(btn);
                    }
                }
            });

            tblMatches.getColumns().addAll(colMatch, colStadium, colOcc, colPrice, colActions);
            tblMatches.setItems(FXCollections.observableArrayList(campManager.getJogos()));
            tblMatches.setPrefHeight(300);

            VBox cardMatches = new VBox(10);
            cardMatches.getStyleClass().add("card");
            cardMatches.setStyle("-fx-padding: 15px;");
            cardMatches.getChildren().add(tblMatches);
            vboxMatches.getChildren().addAll(new Label("Controlo de Lotações por Jogo:"), cardMatches);
            tabMatches.setContent(vboxMatches);

            // Tab 2: Segurança
            Tab tabSecurity = new Tab("Segurança");
            tabSecurity.setClosable(false);
            VBox vboxSecurity = new VBox(15);
            vboxSecurity.setPadding(new Insets(20));

            TableView<FraudLog> tblFraud = new TableView<>();
            TableColumn<FraudLog, String> colTime = new TableColumn<>("Timestamp");
            colTime.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTimestamp()));
            colTime.setPrefWidth(130);

            TableColumn<FraudLog, String> colType = new TableColumn<>("Tipo");
            colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
            colType.setPrefWidth(120);

            TableColumn<FraudLog, String> colDesc = new TableColumn<>("Descrição");
            colDesc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));
            colDesc.setPrefWidth(380);

            TableColumn<FraudLog, Void> colBlock = new TableColumn<>("Ação");
            colBlock.setPrefWidth(110);
            colBlock.setCellFactory(column -> new TableCell<FraudLog, Void>() {
                private final Button btn = new Button("Bloquear");
                {
                    btn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 6px 12px; -fx-cursor: hand; -fx-font-size: 11px;");
                    btn.setOnAction(evt -> {
                        FraudLog log = getTableView().getItems().get(getIndex());
                        fraudLogs.remove(log);
                        Alert a = new Alert(Alert.AlertType.INFORMATION, "Ação tomada: Transação/IP bloqueado preventivamente.");
                        a.showAndWait();
                        showBilheteira(); // Refresh
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) setGraphic(null);
                    else setGraphic(btn);
                }
            });

            tblFraud.getColumns().addAll(colTime, colType, colDesc, colBlock);
            tblFraud.setItems(this.fraudLogs);
            tblFraud.setPrefHeight(300);

            VBox cardFraudTable = new VBox(10);
            cardFraudTable.getStyleClass().add("card");
            cardFraudTable.setStyle("-fx-padding: 15px;");
            cardFraudTable.getChildren().add(tblFraud);
            vboxSecurity.getChildren().addAll(new Label("Logs de Segurança Ativos:"), cardFraudTable);
            tabSecurity.setContent(vboxSecurity);

            // Tab 3: Inventário Global & Gráficos
            Tab tabInv = new Tab("Inventário Global");
            tabInv.setClosable(false);
            VBox vboxInv = new VBox(15);
            vboxInv.setPadding(new Insets(20));

            HBox chartBox = new HBox(20);
            chartBox.setAlignment(Pos.CENTER);

            // Pie Chart for global sold vs available
            PieChart pieChart = new PieChart();
            pieChart.setTitle("Resumo de Lotação");
            int totalTicketsSold = bilManager.getBilhetes().size();
            int totalTicketsCap = totalCap;
            int availableTickets = totalTicketsCap - totalTicketsSold;
            if (availableTickets < 0) availableTickets = 0;

            PieChart.Data sliceSold = new PieChart.Data("Vendidos (" + totalTicketsSold + ")", totalTicketsSold);
            PieChart.Data sliceAvail = new PieChart.Data("Disponíveis (" + availableTickets + ")", availableTickets);
            pieChart.getData().addAll(sliceSold, sliceAvail);
            pieChart.setPrefSize(350, 250);

            // Bar Chart for weekday sales
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Dia da Semana");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Bilhetes Vendidos");
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Vendas por Dia da Semana");

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            int[] salesByDay = new int[8]; // 1=Mon...7=Sun
            for (Bilhete b : bilManager.getBilhetes()) {
                Jogo match = null;
                for (Jogo j : campManager.getJogos()) {
                    if (j.getId() == b.getJogoId()) {
                        match = j;
                        break;
                    }
                }
                if (match != null && match.getData() != null) {
                    try {
                        java.time.LocalDate ld = java.time.LocalDate.parse(match.getData());
                        int dayOfWeek = ld.getDayOfWeek().getValue();
                        salesByDay[dayOfWeek]++;
                    } catch (Exception ex) {
                        int idx = (match.getId() % 7) + 1;
                        salesByDay[idx]++;
                    }
                } else {
                    salesByDay[3]++;
                }
            }
            series.getData().add(new XYChart.Data<>("Seg", salesByDay[1]));
            series.getData().add(new XYChart.Data<>("Ter", salesByDay[2]));
            series.getData().add(new XYChart.Data<>("Qua", salesByDay[3]));
            series.getData().add(new XYChart.Data<>("Qui", salesByDay[4]));
            series.getData().add(new XYChart.Data<>("Sex", salesByDay[5]));
            series.getData().add(new XYChart.Data<>("Sáb", salesByDay[6]));
            series.getData().add(new XYChart.Data<>("Dom", salesByDay[7]));
            
            barChart.getData().add(series);
            barChart.setPrefSize(450, 250);
            barChart.setLegendVisible(false);

            chartBox.getChildren().addAll(pieChart, barChart);
            vboxInv.getChildren().addAll(new Label("Análise de Lotação Global:"), chartBox);
            tabInv.setContent(vboxInv);

            // Tab 4: Relatórios
            Tab tabRep = new Tab("Relatórios");
            tabRep.setClosable(false);
            VBox vboxRep = new VBox(15);
            vboxRep.setPadding(new Insets(20));

            // Relatório por categoria
            TableView<String[]> tblRep = new TableView<>();
            TableColumn<String[], String> colCat = new TableColumn<>("Categoria");
            colCat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[0]));
            colCat.setPrefWidth(200);

            TableColumn<String[], String> colSoldNum = new TableColumn<>("Bilhetes Vendidos");
            colSoldNum.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[1]));
            colSoldNum.setPrefWidth(200);

            TableColumn<String[], String> colGross = new TableColumn<>("Receita Bruta");
            colGross.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[2]));
            colGross.setPrefWidth(200);

            tblRep.getColumns().addAll(colCat, colSoldNum, colGross);
            
            int premS = 0, intS = 0, ecoS = 0, locS = 0;
            double premR = 0, intR = 0, ecoR = 0, locR = 0;
            for (Bilhete b : bilManager.getBilhetes()) {
                String secName = b.getSetor();
                if ("Premium".equalsIgnoreCase(secName)) { premS++; premR += b.getPreco(); }
                else if ("Intermédia".equalsIgnoreCase(secName) || "Intermediária".equalsIgnoreCase(secName)) { intS++; intR += b.getPreco(); }
                else if ("Económica".equalsIgnoreCase(secName)) { ecoS++; ecoR += b.getPreco(); }
                else if ("Local".equalsIgnoreCase(secName)) { locS++; locR += b.getPreco(); }
            }

            tblRep.getItems().addAll(
                new String[]{"Premium", String.valueOf(premS), String.format("%,.2f €", premR)},
                new String[]{"Intermédia", String.valueOf(intS), String.format("%,.2f €", intR)},
                new String[]{"Económica", String.valueOf(ecoS), String.format("%,.2f €", ecoR)},
                new String[]{"Local", String.valueOf(locS), String.format("%,.2f €", locR)}
            );
            tblRep.setPrefHeight(250);

            vboxRep.getChildren().addAll(new Label("Receita por Categoria de Setor:"), tblRep);
            tabRep.setContent(vboxRep);

            tabPane.getTabs().addAll(tabMatches, tabSecurity, tabInv, tabRep);
            content.getChildren().addAll(titleBox, statsGrid, tabPane);
            setContent(content);
        }
    }

    // ==========================================
    // VIEW 7: Classificações e Bracket
    // ==========================================
    @SuppressWarnings("unchecked")
    private void showStandingsAndBracket() {
        VBox content = new VBox(25);
        content.setPadding(new Insets(30));

        Label title = new Label("Portal de Competição (Calendário, Bracket e Grupos)");
        title.getStyleClass().add("label-title");

        TabPane tabPane = new TabPane();

        // ------------------------------------------
        // Tab 1: Calendário de Jogos (Filtros + Jogos)
        // ------------------------------------------
        Tab tabCalendar = new Tab("Calendário de Jogos");
        tabCalendar.setClosable(false);
        
        VBox vboxCalendar = new VBox(20);
        vboxCalendar.setPadding(new Insets(20));
        vboxCalendar.getStyleClass().add("card");
        
        // Phase filters ( pills )
        HBox phaseFilterBox = new HBox(8);
        phaseFilterBox.setAlignment(Pos.CENTER_LEFT);
        
        // Combobox filters
        HBox dropDowns = new HBox(15);
        dropDowns.setAlignment(Pos.CENTER_LEFT);
        
        ComboBox<String> cmbCountryFilter = new ComboBox<>();
        cmbCountryFilter.setPromptText("Todos os países");
        cmbCountryFilter.setPrefWidth(180);
        
        ComboBox<String> cmbDateFilter = new ComboBox<>();
        cmbDateFilter.setPromptText("Todas as datas");
        cmbDateFilter.setPrefWidth(180);
        
        ComboBox<String> cmbTimeFilter = new ComboBox<>();
        cmbTimeFilter.setPromptText("Todos os horários");
        cmbTimeFilter.setPrefWidth(180);
        
        dropDowns.getChildren().addAll(
            new Label("Filtros:"), cmbCountryFilter, cmbDateFilter, cmbTimeFilter
        );
        
        TableView<Jogo> tblMatches = new TableView<>();
        
        TableColumn<Jogo, String> colDate = new TableColumn<>("Data");
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getData()));
        colDate.setPrefWidth(100);

        TableColumn<Jogo, String> colTime = new TableColumn<>("Hora");
        colTime.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHora()));
        colTime.setPrefWidth(80);

        TableColumn<Jogo, String> colMatch = new TableColumn<>("Jogo");
        colMatch.setCellValueFactory(c -> {
            Jogo j = c.getValue();
            String home = j.getHomeTeam() != null ? j.getHomeTeam().getNome() : "[A definir]";
            String away = j.getAwayTeam() != null ? j.getAwayTeam().getNome() : "[A definir]";
            return new SimpleStringProperty(home + " vs " + away);
        });
        colMatch.setPrefWidth(220);

        TableColumn<Jogo, String> colStadium = new TableColumn<>("Estádio");
        colStadium.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstadio() != null ? c.getValue().getEstadio().getNome() : ""));
        colStadium.setPrefWidth(180);

        TableColumn<Jogo, String> colPhase = new TableColumn<>("Fase");
        colPhase.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhase()));
        colPhase.setPrefWidth(120);

        TableColumn<Jogo, Void> colAction = new TableColumn<>("Plantéis");
        colAction.setPrefWidth(130);
        colAction.setCellFactory(col -> new TableCell<Jogo, Void>() {
            private final Button btn = new Button("🔍 Ver Plantéis");
            {
                btn.getStyleClass().add("btn-secondary");
                btn.setStyle("-fx-padding: 4px 10px; -fx-font-size: 11px;");
                btn.setOnAction(e -> {
                    Jogo j = getTableView().getItems().get(getIndex());
                    showRosterDialog(j);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        tblMatches.getColumns().addAll(colDate, colTime, colMatch, colStadium, colPhase, colAction);
        tblMatches.setPrefHeight(350);

        // Mutable state container for active phase
        final String[] selectedPhase = {"Todos"};
        
        // Filter actions
        Runnable runFilter = () -> {
            List<Jogo> allMatches = campManager.getJogos();
            List<Jogo> filtered = new ArrayList<>();
            String selCountry = cmbCountryFilter.getValue();
            String selDate = cmbDateFilter.getValue();
            String selTime = cmbTimeFilter.getValue();
            
            for (Jogo j : allMatches) {
                boolean phaseOk = "Todos".equals(selectedPhase[0]) || j.getPhase().equalsIgnoreCase(selectedPhase[0]);
                
                boolean countryOk = true;
                if (selCountry != null && !selCountry.equals("Todos os países")) {
                    String home = j.getHomeTeam() != null ? j.getHomeTeam().getNome() : "";
                    String away = j.getAwayTeam() != null ? j.getAwayTeam().getNome() : "";
                    countryOk = home.equalsIgnoreCase(selCountry) || away.equalsIgnoreCase(selCountry);
                }
                
                boolean dateOk = true;
                if (selDate != null && !selDate.equals("Todas as datas")) {
                    dateOk = j.getData().equals(selDate);
                }
                
                boolean timeOk = true;
                if (selTime != null && !selTime.equals("Todos os horários")) {
                    timeOk = j.getHora().equals(selTime);
                }
                
                if (phaseOk && countryOk && dateOk && timeOk) {
                    filtered.add(j);
                }
            }
            tblMatches.setItems(FXCollections.observableArrayList(filtered));
        };
        
        // Phase filter buttons
        String[] phaseOptions = {"Todos", "Grupos", "Oitavos", "Quartos", "Meias-Finais", "Final"};
        List<Button> phaseButtons = new ArrayList<>();
        for (String pOpt : phaseOptions) {
            Button btn = new Button(pOpt);
            btn.getStyleClass().add("btn-secondary");
            btn.setStyle("-fx-background-radius: 50px; -fx-padding: 6px 14px; -fx-font-size: 12px;");
            if (pOpt.equals("Todos")) {
                btn.getStyleClass().remove("btn-secondary");
                btn.getStyleClass().add("btn-primary");
            }
            btn.setOnAction(e -> {
                selectedPhase[0] = pOpt;
                for (Button b : phaseButtons) {
                    b.getStyleClass().remove("btn-primary");
                    b.getStyleClass().add("btn-secondary");
                }
                btn.getStyleClass().remove("btn-secondary");
                btn.getStyleClass().add("btn-primary");
                runFilter.run();
            });
            phaseButtons.add(btn);
            phaseFilterBox.getChildren().add(btn);
        }

        // Populate comboboxes
        List<Jogo> allMatchesList = campManager.getJogos();
        
        List<String> countriesList = new ArrayList<>();
        countriesList.add("Todos os países");
        for (Equipa e : campManager.getEquipas()) {
            countriesList.add(e.getNome());
        }
        cmbCountryFilter.setItems(FXCollections.observableArrayList(countriesList));
        cmbCountryFilter.setValue("Todos os países");
        
        List<String> datesList = new ArrayList<>();
        datesList.add("Todas as datas");
        for (Jogo j : allMatchesList) {
            if (!datesList.contains(j.getData())) {
                datesList.add(j.getData());
            }
        }
        cmbDateFilter.setItems(FXCollections.observableArrayList(datesList));
        cmbDateFilter.setValue("Todas as datas");
        
        List<String> timesList = new ArrayList<>();
        timesList.add("Todos os horários");
        for (Jogo j : allMatchesList) {
            if (!timesList.contains(j.getHora())) {
                timesList.add(j.getHora());
            }
        }
        cmbTimeFilter.setItems(FXCollections.observableArrayList(timesList));
        cmbTimeFilter.setValue("Todos os horários");

        cmbCountryFilter.valueProperty().addListener((o, ov, nv) -> runFilter.run());
        cmbDateFilter.valueProperty().addListener((o, ov, nv) -> runFilter.run());
        cmbTimeFilter.valueProperty().addListener((o, ov, nv) -> runFilter.run());

        // Initial Load
        runFilter.run();

        vboxCalendar.getChildren().addAll(new Label("Fase da Competição:"), phaseFilterBox, dropDowns, tblMatches);
        tabCalendar.setContent(vboxCalendar);
        tabCalendar.setOnSelectionChanged(event -> {
            if (tabCalendar.isSelected()) {
                runFilter.run();
            }
        });

        // ------------------------------------------
        // Tab 2: Bracket do Torneio (Visual Tree)
        // ------------------------------------------
        Tab tabBracket = new Tab("Bracket do Torneio");
        tabBracket.setClosable(false);
        
        ScrollPane scrollBracket = new ScrollPane();
        scrollBracket.setFitToHeight(true);
        scrollBracket.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        VBox vboxBracketWrap = new VBox(15);
        vboxBracketWrap.setPadding(new Insets(20));
        vboxBracketWrap.getStyleClass().add("card");
        
        HBox visualBracketTree = buildVisualBracket();
        vboxBracketWrap.getChildren().addAll(new Label("Sinalética e Progresso das Eliminatórias (Symmetrical Tree):"), visualBracketTree);
        scrollBracket.setContent(vboxBracketWrap);
        tabBracket.setContent(scrollBracket);
        tabBracket.setOnSelectionChanged(event -> {
            if (tabBracket.isSelected()) {
                HBox updatedBracket = buildVisualBracket();
                vboxBracketWrap.getChildren().clear();
                vboxBracketWrap.getChildren().addAll(new Label("Sinalética e Progresso das Eliminatórias (Symmetrical Tree):"), updatedBracket);
            }
        });

        // ------------------------------------------
        // Tab 3: Classificação dos Grupos
        // ------------------------------------------
        Tab tabStandings = new Tab("Classificação dos Grupos");
        tabStandings.setClosable(false);
        VBox vboxStandings = new VBox(15);
        vboxStandings.setPadding(new Insets(20));
        vboxStandings.getStyleClass().add("card");

        ComboBox<String> cmbGroups = new ComboBox<>(FXCollections.observableArrayList(
            "Grupo A", "Grupo B", "Grupo C", "Grupo D", "Grupo E", "Grupo F", "Grupo G", "Grupo H"
        ));
        cmbGroups.setPromptText("Selecione o Grupo");
        cmbGroups.setValue("Grupo A");

        TableView<ClassificacaoLinha> tblStandings = new TableView<>();
        
        TableColumn<ClassificacaoLinha, String> colTeam = new TableColumn<>("Equipa");
        colTeam.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEquipa().getNome()));
        colTeam.setPrefWidth(180);

        TableColumn<ClassificacaoLinha, Number> colPts = new TableColumn<>("PTS");
        colPts.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getPontos()));
        colPts.setPrefWidth(60);

        TableColumn<ClassificacaoLinha, Number> colJog = new TableColumn<>("J");
        colJog.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getJogados()));
        colJog.setPrefWidth(60);

        TableColumn<ClassificacaoLinha, Number> colVit = new TableColumn<>("V");
        colVit.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getVitorias()));
        colVit.setPrefWidth(60);

        TableColumn<ClassificacaoLinha, Number> colEmp = new TableColumn<>("E");
        colEmp.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getEmpates()));
        colEmp.setPrefWidth(60);

        TableColumn<ClassificacaoLinha, Number> colDer = new TableColumn<>("D");
        colDer.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getDerrotas()));
        colDer.setPrefWidth(60);

        TableColumn<ClassificacaoLinha, Number> colGm = new TableColumn<>("GM");
        colGm.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getGolosMarcados()));
        colGm.setPrefWidth(60);

        TableColumn<ClassificacaoLinha, Number> colGs = new TableColumn<>("GS");
        colGs.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getGolosSofridos()));
        colGs.setPrefWidth(60);

        TableColumn<ClassificacaoLinha, Number> colDg = new TableColumn<>("DG");
        colDg.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getSaldoGolos()));
        colDg.setPrefWidth(60);

        tblStandings.getColumns().addAll(colTeam, colPts, colJog, colVit, colEmp, colDer, colGm, colGs, colDg);
        tblStandings.setPrefHeight(300);

        cmbGroups.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                List<ClassificacaoLinha> lines = campManager.calcularClassificacaoGrupo(newV);
                tblStandings.setItems(FXCollections.observableArrayList(lines));
            }
        });
        tblStandings.setItems(FXCollections.observableArrayList(campManager.calcularClassificacaoGrupo("Grupo A")));

        vboxStandings.getChildren().addAll(cmbGroups, tblStandings);
        tabStandings.setContent(vboxStandings);
        tabStandings.setOnSelectionChanged(event -> {
            if (tabStandings.isSelected() && cmbGroups.getValue() != null) {
                List<ClassificacaoLinha> lines = campManager.calcularClassificacaoGrupo(cmbGroups.getValue());
                tblStandings.setItems(FXCollections.observableArrayList(lines));
            }
        });

        tabPane.getTabs().addAll(tabCalendar, tabBracket, tabStandings);
        content.getChildren().addAll(title, tabPane);
        setContent(content);
    }

    private HBox buildVisualBracket() {
        HBox bracketBox = new HBox(12);
        bracketBox.setAlignment(Pos.CENTER);
        bracketBox.setPadding(new Insets(20, 0, 20, 0));
        
        List<Jogo> oitavos = new ArrayList<>();
        List<Jogo> quartos = new ArrayList<>();
        List<Jogo> meias = new ArrayList<>();
        Jogo finalMatch = null;
        
        for (Jogo j : campManager.getJogos()) {
            if ("Oitavos".equalsIgnoreCase(j.getPhase())) oitavos.add(j);
            else if ("Quartos".equalsIgnoreCase(j.getPhase())) quartos.add(j);
            else if ("Meias-Finais".equalsIgnoreCase(j.getPhase())) meias.add(j);
            else if ("Final".equalsIgnoreCase(j.getPhase())) finalMatch = j;
        }
        
        oitavos.sort((a,b) -> Integer.compare(a.getId(), b.getId()));
        quartos.sort((a,b) -> Integer.compare(a.getId(), b.getId()));
        meias.sort((a,b) -> Integer.compare(a.getId(), b.getId()));
        
        // Left Column (Oitavos 1-4)
        VBox leftOitavos = new VBox(25);
        leftOitavos.setAlignment(Pos.CENTER);
        for (int i = 0; i < Math.min(4, oitavos.size()); i++) {
            leftOitavos.getChildren().add(createBracketMatchBox(oitavos.get(i)));
        }
        while (leftOitavos.getChildren().size() < 4) {
            leftOitavos.getChildren().add(createBracketMatchBox(null));
        }
        
        // Left Quartos (Quartos 1-2)
        VBox leftQuartos = new VBox(95);
        leftQuartos.setAlignment(Pos.CENTER);
        for (int i = 0; i < Math.min(2, quartos.size()); i++) {
            leftQuartos.getChildren().add(createBracketMatchBox(quartos.get(i)));
        }
        while (leftQuartos.getChildren().size() < 2) {
            leftQuartos.getChildren().add(createBracketMatchBox(null));
        }
        
        // Left Meias (Meias 1)
        VBox leftMeias = new VBox();
        leftMeias.setAlignment(Pos.CENTER);
        if (meias.size() > 0) {
            leftMeias.getChildren().add(createBracketMatchBox(meias.get(0)));
        } else {
            leftMeias.getChildren().add(createBracketMatchBox(null));
        }
        
        // Center (Grand Final & Trophy)
        VBox centerCol = new VBox(20);
        centerCol.setAlignment(Pos.CENTER);
        Label lblTrophy = new Label("🏆");
        lblTrophy.setStyle("-fx-font-size: 40px;");
        Label lblGrandFinal = new Label("GRANDE FINAL");
        lblGrandFinal.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1A202C;");
        VBox finalBox = createBracketMatchBox(finalMatch);
        finalBox.setStyle(finalBox.getStyle() + "-fx-border-color: #00D26A; -fx-border-width: 2px;");
        centerCol.getChildren().addAll(lblTrophy, lblGrandFinal, finalBox);
        
        // Right Meias (Meias 2)
        VBox rightMeias = new VBox();
        rightMeias.setAlignment(Pos.CENTER);
        if (meias.size() > 1) {
            rightMeias.getChildren().add(createBracketMatchBox(meias.get(1)));
        } else {
            rightMeias.getChildren().add(createBracketMatchBox(null));
        }
        
        // Right Quartos (Quartos 3-4)
        VBox rightQuartos = new VBox(95);
        rightQuartos.setAlignment(Pos.CENTER);
        for (int i = 2; i < Math.min(4, quartos.size()); i++) {
            rightQuartos.getChildren().add(createBracketMatchBox(quartos.get(i)));
        }
        while (rightQuartos.getChildren().size() < 2) {
            rightQuartos.getChildren().add(createBracketMatchBox(null));
        }
        
        // Right Oitavos (Oitavos 5-8)
        VBox rightOitavos = new VBox(25);
        rightOitavos.setAlignment(Pos.CENTER);
        for (int i = 4; i < Math.min(8, oitavos.size()); i++) {
            rightOitavos.getChildren().add(createBracketMatchBox(oitavos.get(i)));
        }
        while (rightOitavos.getChildren().size() < 4) {
            rightOitavos.getChildren().add(createBracketMatchBox(null));
        }
        
        bracketBox.getChildren().addAll(leftOitavos, leftQuartos, leftMeias, centerCol, rightMeias, rightQuartos, rightOitavos);
        return bracketBox;
    }

    private VBox createBracketMatchBox(Jogo j) {
        VBox box = new VBox(3);
        box.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 8px; -fx-min-width: 140px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");
        
        String homeName = "[A definir]";
        String awayName = "[A definir]";
        String score = "-";
        
        if (j != null) {
            if (j.getHomeTeam() != null) homeName = j.getHomeTeam().getNome();
            if (j.getAwayTeam() != null) awayName = j.getAwayTeam().getNome();
            if (StatusJogo.FINALIZADO.equals(j.getStatus())) {
                score = j.getGoalsHome() + " - " + j.getGoalsAway();
                if (j.getPenaltiesHome() >= 0) {
                    score += " (" + j.getPenaltiesHome() + "-" + j.getPenaltiesAway() + ")";
                }
            } else {
                score = j.getData() + " " + j.getHora();
            }
        }
        
        Label lblHome = new Label("⚽ " + homeName);
        lblHome.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #1A202C;");
        
        Label lblAway = new Label("⚽ " + awayName);
        lblAway.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #1A202C;");
        
        Label lblScore = new Label(score);
        lblScore.setStyle("-fx-font-size: 10px; -fx-text-fill: #00D26A; -fx-font-weight: bold;");
        
        box.getChildren().addAll(lblHome, lblAway, lblScore);
        return box;
    }

    private void showRosterDialog(Jogo j) {
        if (j == null) return;
        Stage dialog = new Stage();
        dialog.initOwner(this.scene.getWindow());
        
        String hName = j.getHomeTeam() != null ? j.getHomeTeam().getNome() : "A definir";
        String aName = j.getAwayTeam() != null ? j.getAwayTeam().getNome() : "A definir";
        dialog.setTitle("Plantéis: " + hName + " vs " + aName);
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F8F9FA;");
        
        Label title = new Label("Convocatórias Oficiais");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1A202C;");
        
        HBox rostersBox = new HBox(20);
        rostersBox.setAlignment(Pos.CENTER);
        
        VBox homeCol = createRosterColumn(j.getHomeTeam());
        VBox awayCol = createRosterColumn(j.getAwayTeam());
        HBox.setHgrow(homeCol, Priority.ALWAYS);
        HBox.setHgrow(awayCol, Priority.ALWAYS);
        
        rostersBox.getChildren().addAll(homeCol, awayCol);
        
        Button btnClose = new Button("Fechar");
        btnClose.getStyleClass().add("btn-secondary");
        btnClose.setOnAction(e -> dialog.close());
        
        root.getChildren().addAll(title, rostersBox, btnClose);
        Scene scene = new Scene(root, 700, 500);
        
        try {
            URL cssUrl = getClass().getResource("/ui/styles.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
        } catch(Exception e) {}
        
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private VBox createRosterColumn(Equipa eq) {
        VBox col = new VBox(10);
        col.getStyleClass().add("card");
        col.setStyle("-fx-padding: 15px; -fx-min-width: 300px;");
        
        String teamName = eq != null ? eq.getNome() : "A definir";
        Label lblTeam = new Label("⚽ " + teamName);
        lblTeam.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1A202C;");
        col.getChildren().add(lblTeam);
        
        VBox list = new VBox(5);
        if (eq == null || eq.getJogadores().isEmpty()) {
            Label empty = new Label("Convocatória ainda não fechada.");
            empty.setStyle("-fx-text-fill: #6B7280; -fx-font-style: italic;");
            list.getChildren().add(empty);
        } else {
            for (Jogador p : eq.getJogadores()) {
                HBox pRow = new HBox(10);
                pRow.setStyle("-fx-padding: 6px; -fx-background-color: #F9FAFB; -fx-border-color: #E5E7EB; -fx-border-radius: 6px; -fx-background-radius: 6px;");
                pRow.setAlignment(Pos.CENTER_LEFT);
                
                Label lblNum = new Label("#" + p.getNumeroCamisola());
                lblNum.setStyle("-fx-font-weight: bold; -fx-text-fill: #00D26A; -fx-min-width: 25px;");
                
                Label lblName = new Label(p.getNome());
                lblName.setStyle("-fx-font-weight: bold; -fx-text-fill: #1A202C;");
                
                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);
                
                Label lblPos = new Label(p.getPosicao());
                lblPos.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 11px;");
                
                pRow.getChildren().addAll(lblNum, lblName, sp, lblPos);
                list.getChildren().add(pRow);
            }
        }
        
        col.getChildren().add(list);
        return col;
    }

    private void showAssignTeamDialog(Hotel h) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Atribuir Hotel - " + h.getNome());
        dialog.setHeaderText("Hospedar comitiva em " + h.getNome() + "\nLocalização: " + h.getLocalizacao());
        
        ButtonType btnConfirm = new ButtonType("Confirmar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnConfirm, ButtonType.CANCEL);
        
        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (cancelBtn != null) {
            cancelBtn.setText("Cancelar");
        }
        
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(15));
        vbox.setPrefWidth(350);
        
        Label lblTeam = new Label("Seleção:");
        lblTeam.setStyle("-fx-font-weight: bold;");
        ComboBox<Equipa> cmbTeam = new ComboBox<>();
        cmbTeam.setItems(FXCollections.observableArrayList(campManager.getEquipas()));
        cmbTeam.setMaxWidth(Double.MAX_VALUE);
        
        Label lblIn = new Label("Data de Check-in:");
        lblIn.setStyle("-fx-font-weight: bold;");
        DatePicker dpIn = new DatePicker();
        dpIn.setMaxWidth(Double.MAX_VALUE);
        
        Label lblOut = new Label("Data de Check-out:");
        lblOut.setStyle("-fx-font-weight: bold;");
        DatePicker dpOut = new DatePicker();
        dpOut.setMaxWidth(Double.MAX_VALUE);
        
        vbox.getChildren().addAll(lblTeam, cmbTeam, lblIn, dpIn, lblOut, dpOut);
        dialog.getDialogPane().setContent(vbox);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == btnConfirm) {
                Equipa eq = cmbTeam.getValue();
                if (eq == null || dpIn.getValue() == null || dpOut.getValue() == null) {
                    Alert err = new Alert(Alert.AlertType.ERROR, "Todos os campos devem ser preenchidos!");
                    err.showAndWait();
                    return;
                }
                
                if (dpOut.getValue().isBefore(dpIn.getValue())) {
                    Alert err = new Alert(Alert.AlertType.ERROR, "A data de check-out não pode ser anterior à data de check-in!");
                    err.showAndWait();
                    return;
                }
                
                String inDate = dpIn.getValue().toString();
                String outDate = dpOut.getValue().toString();
                
                boolean ok = logManager.alocarHotel(eq, h, inDate, outDate);
                if (ok) {
                    Alert okAlert = new Alert(Alert.AlertType.INFORMATION, "Comitiva hospedada com sucesso!");
                    okAlert.showAndWait();
                    showLogistica(); // Refresh screen
                } else {
                    Alert err = new Alert(Alert.AlertType.ERROR, "Capacidade insuficiente ou hotel já ocupado!");
                    err.showAndWait();
                }
            }
        });
    }

    private void showAddHotelDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Registar Novo Hotel");
        dialog.setHeaderText("Registar nova infraestrutura de alojamento para o Mundial 2026");
        
        ButtonType btnConfirm = new ButtonType("Registar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnConfirm, ButtonType.CANCEL);
        
        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (cancelBtn != null) {
            cancelBtn.setText("Cancelar");
        }
        
        VBox vbox = new VBox(12);
        vbox.setPadding(new Insets(15));
        vbox.setPrefWidth(350);
        
        Label lblName = new Label("Nome do Hotel:");
        lblName.setStyle("-fx-font-weight: bold;");
        TextField txtName = new TextField();
        
        Label lblLoc = new Label("Localização:");
        lblLoc.setStyle("-fx-font-weight: bold;");
        TextField txtLoc = new TextField();
        
        Label lblCap = new Label("Capacidade (quartos/pessoas):");
        lblCap.setStyle("-fx-font-weight: bold;");
        TextField txtCap = new TextField();
        
        vbox.getChildren().addAll(lblName, txtName, lblLoc, txtLoc, lblCap, txtCap);
        dialog.getDialogPane().setContent(vbox);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == btnConfirm) {
                String name = txtName.getText().trim();
                String loc = txtLoc.getText().trim();
                String capStr = txtCap.getText().trim();
                
                if (name.isEmpty() || loc.isEmpty() || capStr.isEmpty()) {
                    Alert err = new Alert(Alert.AlertType.ERROR, "Todos os campos são obrigatórios!");
                    err.showAndWait();
                    return;
                }
                
                try {
                    int cap = Integer.parseInt(capStr);
                    if (cap <= 0) {
                        Alert err = new Alert(Alert.AlertType.ERROR, "Capacidade deve ser um número inteiro positivo");
                        err.showAndWait();
                        return;
                    }
                    
                    boolean exists = false;
                    for (Hotel existingH : logManager.getHoteis()) {
                        if (existingH.getNome().equalsIgnoreCase(name)) {
                            exists = true;
                            break;
                        }
                    }
                    if (exists) {
                        Alert err = new Alert(Alert.AlertType.ERROR, "Já existe um hotel com esse nome!");
                        err.showAndWait();
                        return;
                    }
                    
                    int id = logManager.getHoteis().size() + 1;
                    while (logManager.procurarHotelPorId(id) != null) {
                        id++;
                    }
                    
                    Hotel newH = new Hotel(id, name, loc, cap);
                    logManager.registarHotel(newH);
                    
                    Alert okAlert = new Alert(Alert.AlertType.INFORMATION, "Hotel registado com sucesso!");
                    okAlert.showAndWait();
                    showLogistica();
                } catch (NumberFormatException e) {
                    Alert err = new Alert(Alert.AlertType.ERROR, "Capacidade deve ser um número inteiro positivo");
                    err.showAndWait();
                }
            }
        });
    }

    private void showPlanTravelDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Planear Viagem de Jogo");
        dialog.setHeaderText("Agendar transporte de comitiva oficial para jogo");
        
        ButtonType btnConfirm = new ButtonType("Agendar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnConfirm, ButtonType.CANCEL);
        
        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (cancelBtn != null) {
            cancelBtn.setText("Cancelar");
        }
        
        VBox vbox = new VBox(12);
        vbox.setPadding(new Insets(15));
        vbox.setPrefWidth(400);
        
        Label lblMatch = new Label("Jogo Associado:");
        lblMatch.setStyle("-fx-font-weight: bold;");
        ComboBox<Jogo> cmbMatch = new ComboBox<>();
        cmbMatch.setItems(FXCollections.observableArrayList(campManager.getJogos()));
        cmbMatch.setMaxWidth(Double.MAX_VALUE);
        
        Label lblOrig = new Label("Origem (ex: Hotel Pestana Palace, Lisboa):");
        lblOrig.setStyle("-fx-font-weight: bold;");
        TextField txtOrig = new TextField();
        
        Label lblDest = new Label("Destino (ex: Estádio da Luz):");
        lblDest.setStyle("-fx-font-weight: bold;");
        TextField txtDest = new TextField();
        
        Label lblIn = new Label("Hora de Partida:");
        lblIn.setStyle("-fx-font-weight: bold;");
        TextField txtIn = new TextField();
        txtIn.setPromptText("ex: 14:30");
        
        Label lblOut = new Label("Hora de Chegada Prevista:");
        lblOut.setStyle("-fx-font-weight: bold;");
        TextField txtOut = new TextField();
        txtOut.setPromptText("ex: 15:15");
        
        Label lblTeam = new Label("Equipa que viaja:");
        lblTeam.setStyle("-fx-font-weight: bold;");
        ComboBox<Equipa> cmbTeam = new ComboBox<>();
        cmbTeam.setMaxWidth(Double.MAX_VALUE);
        
        cmbTeam.setItems(FXCollections.observableArrayList(campManager.getEquipas()));

        cmbMatch.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                List<Equipa> teams = new ArrayList<>();
                if (newVal.getHomeTeam() != null) teams.add(newVal.getHomeTeam());
                if (newVal.getAwayTeam() != null) teams.add(newVal.getAwayTeam());
                cmbTeam.setItems(FXCollections.observableArrayList(teams));
            } else {
                cmbTeam.setItems(FXCollections.observableArrayList(campManager.getEquipas()));
            }
        });

        Label lblMeio = new Label("Meio de Transporte:");
        lblMeio.setStyle("-fx-font-weight: bold;");
        ComboBox<String> cmbMeio = new ComboBox<>(FXCollections.observableArrayList("Autocarro", "Avião"));
        cmbMeio.setMaxWidth(Double.MAX_VALUE);
        
        vbox.getChildren().addAll(lblMatch, cmbMatch, lblTeam, cmbTeam, lblOrig, txtOrig, lblDest, txtDest, lblIn, txtIn, lblOut, txtOut, lblMeio, cmbMeio);
        dialog.getDialogPane().setContent(vbox);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == btnConfirm) {
                Jogo jogo = cmbMatch.getValue();
                Equipa equipa = cmbTeam.getValue();
                String orig = txtOrig.getText().trim();
                String dest = txtDest.getText().trim();
                String inT = txtIn.getText().trim();
                String outT = txtOut.getText().trim();
                String meio = cmbMeio.getValue();
                
                if (equipa == null || orig.isEmpty() || dest.isEmpty() || inT.isEmpty() || outT.isEmpty() || meio == null) {
                    Alert err = new Alert(Alert.AlertType.ERROR, "Todos os campos (exceto Jogo) são obrigatórios!");
                    err.showAndWait();
                    return;
                }
                
                java.time.LocalTime depTime = null;
                java.time.LocalTime arrTime = null;
                try {
                    depTime = java.time.LocalTime.parse(inT, java.time.format.DateTimeFormatter.ofPattern("H:m"));
                    arrTime = java.time.LocalTime.parse(outT, java.time.format.DateTimeFormatter.ofPattern("H:m"));
                } catch (java.time.format.DateTimeParseException e) {
                    Alert err = new Alert(Alert.AlertType.ERROR, "As horas devem estar no formato HH:mm (ex: 14:30)!");
                    err.showAndWait();
                    return;
                }
                
                if (!depTime.isBefore(arrTime)) {
                    Alert err = new Alert(Alert.AlertType.ERROR, "A hora de partida deve ser mais cedo que a de chegada!");
                    err.showAndWait();
                    return;
                }
                
                String formattedDep = depTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                String formattedArr = arrTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                
                logManager.planearViagem(jogo, equipa, orig, dest, formattedDep, formattedArr, meio);
                
                Alert okAlert = new Alert(Alert.AlertType.INFORMATION, "Viagem agendada com sucesso!");
                okAlert.showAndWait();
                showLogistica(1);
            }
        });
    }

    public static class InventoryItem {
        private final String name;
        private final String category;
        private int stock;
        private String status;

        public InventoryItem(String name, String category, int stock, String status) {
            this.name = name;
            this.category = category;
            this.stock = stock;
            this.status = status;
        }

        public String getName() { return name; }
        public String getCategory() { return category; }
        public int getStock() { return stock; }
        public String getStatus() { return status; }
        public void setStock(int stock) { 
            this.stock = stock; 
            if (stock <= 10) this.status = "Crítico";
            else if (stock <= 50) this.status = "Baixo";
            else this.status = "OK";
        }
    }

    public static class FraudLog {
        private final int id;
        private final String timestamp;
        private final String type;
        private final String description;

        public FraudLog(int id, String timestamp, String type, String description) {
            this.id = id;
            this.timestamp = timestamp;
            this.type = type;
            this.description = description;
        }

        public int getId() { return id; }
        public String getTimestamp() { return timestamp; }
        public String getType() { return type; }
        public String getDescription() { return description; }
    }

    private void showManagePricesDialog(Jogo j) {
        if (j == null || j.getEstadio() == null) return;
        
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Gestão de Lotação e Preços");
        dialog.setHeaderText("Definições para " + j.getHomeTeam().getNome() + " vs " + j.getAwayTeam().getNome() + "\nEstádio: " + j.getEstadio().getNome());
        
        ButtonType btnSave = new ButtonType("Guardar Alterações", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSave, ButtonType.CANCEL);
        
        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (cancelBtn != null) {
            cancelBtn.setText("Cancelar");
        }
        
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(15));
        vbox.setPrefWidth(500);
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        
        grid.add(new Label("Categoria"), 0, 0);
        grid.add(new Label("Preço Base (EUR)"), 1, 0);
        grid.add(new Label("Lotação Atribuída"), 2, 0);
        
        List<SetorEstadio> sectors = j.getEstadio().getSetores();
        List<TextField> priceFields = new ArrayList<>();
        List<TextField> capFields = new ArrayList<>();
        
        int idx = 1;
        for (SetorEstadio s : sectors) {
            grid.add(new Label(s.getNome()), 0, idx);
            
            TextField txtPrice = new TextField(String.valueOf(s.getPrecoBase()));
            txtPrice.setPrefWidth(120);
            grid.add(txtPrice, 1, idx);
            priceFields.add(txtPrice);
            
            TextField txtCap = new TextField(String.valueOf(s.getCapacidadeTotal()));
            txtCap.setPrefWidth(120);
            grid.add(txtCap, 2, idx);
            capFields.add(txtCap);
            
            idx++;
        }
        
        vbox.getChildren().add(grid);
        dialog.getDialogPane().setContent(vbox);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == btnSave) {
                boolean success = true;
                for (int i = 0; i < sectors.size(); i++) {
                    SetorEstadio s = sectors.get(i);
                    try {
                        double pr = Double.parseDouble(priceFields.get(i).getText().trim());
                        int cap = Integer.parseInt(capFields.get(i).getText().trim());
                        
                        if (cap < s.getBilhetesVendidos()) {
                            Alert a = new Alert(Alert.AlertType.ERROR, "Erro no setor " + s.getNome() + ": não pode reduzir a capacidade para menos dos bilhetes já vendidos (" + s.getBilhetesVendidos() + ")!");
                            a.showAndWait();
                            success = false;
                            break;
                        }
                        
                        s.setPrecoBase(pr);
                        s.setCapacidadeTotal(cap);
                    } catch (NumberFormatException ex) {
                        Alert a = new Alert(Alert.AlertType.ERROR, "Formatos inválidos de preço ou capacidade!");
                        a.showAndWait();
                        success = false;
                        break;
                    }
                }
                
                if (success) {
                    campManager.registarJogo(j);
                    Alert okAlert = new Alert(Alert.AlertType.INFORMATION, "Alterações gravadas com sucesso!");
                    okAlert.showAndWait();
                    showBilheteira(); // Refresh entire panel
                }
            }
        });
    }

    private void showRegisterScorersAndAssistantsDialog(Jogo jogo, int goalsHome, int goalsAway, int penaltiesHome, int penaltiesAway, EstatisticaJogo stats, Runnable postFinalizeAction) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Registrar Marcadores e Assistentes");
        dialog.setHeaderText("Introduza quem marcou os golos e fez as assistências para este jogo.");

        ButtonType btnConfirmar = new ButtonType("Confirmar e Finalizar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnConfirmar, btnCancelar);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setMinWidth(450);

        List<Jogador> homePlayers = jogo.getHomeTeam().getJogadores();
        List<Jogador> awayPlayers = jogo.getAwayTeam().getJogadores();

        List<ComboBox<Jogador>> homeScorersCombos = new ArrayList<>();
        List<ComboBox<Jogador>> homeAssistantsCombos = new ArrayList<>();
        List<ComboBox<Jogador>> awayScorersCombos = new ArrayList<>();
        List<ComboBox<Jogador>> awayAssistantsCombos = new ArrayList<>();

        if (goalsHome > 0) {
            Label lblHome = new Label("Golos de " + jogo.getHomeTeam().getNome() + ":");
            lblHome.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E40AF; -fx-font-size: 13px;");
            content.getChildren().add(lblHome);

            for (int i = 1; i <= goalsHome; i++) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);

                Label lblGolo = new Label("Golo " + i + ":");
                lblGolo.setMinWidth(50);

                ComboBox<Jogador> cmbScorer = new ComboBox<>();
                cmbScorer.getItems().addAll(homePlayers);
                cmbScorer.setPromptText("Selecione o Marcador");
                cmbScorer.setPrefWidth(180);
                cmbScorer.setCellFactory(lv -> new ListCell<Jogador>() {
                    @Override
                    protected void updateItem(Jogador item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? "" : item.getNumeroCamisola() + " - " + item.getNome() + " (" + item.getPosicao() + ")");
                    }
                });
                cmbScorer.setButtonCell(new ListCell<Jogador>() {
                    @Override
                    protected void updateItem(Jogador item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? "" : item.getNumeroCamisola() + " - " + item.getNome());
                    }
                });

                ComboBox<Jogador> cmbAssistant = new ComboBox<>();
                cmbAssistant.getItems().add(null);
                cmbAssistant.getItems().addAll(homePlayers);
                cmbAssistant.setPromptText("Selecione o Assistente (Opcional)");
                cmbAssistant.setPrefWidth(180);
                cmbAssistant.setCellFactory(lv -> new ListCell<Jogador>() {
                    @Override
                    protected void updateItem(Jogador item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("Sem Assistência");
                        } else {
                            setText(item.getNumeroCamisola() + " - " + item.getNome());
                        }
                    }
                });
                cmbAssistant.setButtonCell(new ListCell<Jogador>() {
                    @Override
                    protected void updateItem(Jogador item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("Sem Assistência");
                        } else {
                            setText(item.getNumeroCamisola() + " - " + item.getNome());
                        }
                    }
                });

                row.getChildren().addAll(lblGolo, cmbScorer, new Label("Assis:"), cmbAssistant);
                content.getChildren().add(row);
                homeScorersCombos.add(cmbScorer);
                homeAssistantsCombos.add(cmbAssistant);
            }
        }

        if (goalsAway > 0) {
            Label lblAway = new Label("Golos de " + jogo.getAwayTeam().getNome() + ":");
            lblAway.setStyle("-fx-font-weight: bold; -fx-text-fill: #B91C1C; -fx-font-size: 13px;");
            content.getChildren().add(lblAway);

            for (int i = 1; i <= goalsAway; i++) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);

                Label lblGolo = new Label("Golo " + i + ":");
                lblGolo.setMinWidth(50);

                ComboBox<Jogador> cmbScorer = new ComboBox<>();
                cmbScorer.getItems().addAll(awayPlayers);
                cmbScorer.setPromptText("Selecione o Marcador");
                cmbScorer.setPrefWidth(180);
                cmbScorer.setCellFactory(lv -> new ListCell<Jogador>() {
                    @Override
                    protected void updateItem(Jogador item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? "" : item.getNumeroCamisola() + " - " + item.getNome() + " (" + item.getPosicao() + ")");
                    }
                });
                cmbScorer.setButtonCell(new ListCell<Jogador>() {
                    @Override
                    protected void updateItem(Jogador item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? "" : item.getNumeroCamisola() + " - " + item.getNome());
                    }
                });

                ComboBox<Jogador> cmbAssistant = new ComboBox<>();
                cmbAssistant.getItems().add(null);
                cmbAssistant.getItems().addAll(awayPlayers);
                cmbAssistant.setPromptText("Selecione o Assistente (Opcional)");
                cmbAssistant.setPrefWidth(180);
                cmbAssistant.setCellFactory(lv -> new ListCell<Jogador>() {
                    @Override
                    protected void updateItem(Jogador item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("Sem Assistência");
                        } else {
                            setText(item.getNumeroCamisola() + " - " + item.getNome());
                        }
                    }
                });
                cmbAssistant.setButtonCell(new ListCell<Jogador>() {
                    @Override
                    protected void updateItem(Jogador item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("Sem Assistência");
                        } else {
                            setText(item.getNumeroCamisola() + " - " + item.getNome());
                        }
                    }
                });

                row.getChildren().addAll(lblGolo, cmbScorer, new Label("Assis:"), cmbAssistant);
                content.getChildren().add(row);
                awayScorersCombos.add(cmbScorer);
                awayAssistantsCombos.add(cmbAssistant);
            }
        }

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(350);
        dialog.getDialogPane().setContent(scroll);

        final Button confirmBtn = (Button) dialog.getDialogPane().lookupButton(btnConfirmar);
        confirmBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            boolean valid = true;
            for (ComboBox<Jogador> cb : homeScorersCombos) {
                if (cb.getValue() == null) valid = false;
            }
            for (ComboBox<Jogador> cb : awayScorersCombos) {
                if (cb.getValue() == null) valid = false;
            }
            if (!valid) {
                Alert error = new Alert(Alert.AlertType.ERROR, "Por favor, selecione o marcador de cada golo.");
                error.showAndWait();
                event.consume();
            }
        });

        dialog.showAndWait().ifPresent(res -> {
            if (res == btnConfirmar) {
                for (ComboBox<Jogador> cb : homeScorersCombos) {
                    Jogador j = cb.getValue();
                    if (j != null) {
                        j.incrementGoals();
                    }
                }
                for (ComboBox<Jogador> cb : homeAssistantsCombos) {
                    Jogador j = cb.getValue();
                    if (j != null) {
                        j.incrementAssists();
                    }
                }
                for (ComboBox<Jogador> cb : awayScorersCombos) {
                    Jogador j = cb.getValue();
                    if (j != null) {
                        j.incrementGoals();
                    }
                }
                for (ComboBox<Jogador> cb : awayAssistantsCombos) {
                    Jogador j = cb.getValue();
                    if (j != null) {
                        j.incrementAssists();
                    }
                }

                campManager.finalizarJogoECorrerBracket(jogo.getId(), null, goalsHome, goalsAway, penaltiesHome, penaltiesAway, stats);
                
                if (postFinalizeAction != null) {
                    postFinalizeAction.run();
                }
            }
        });
    }
}
