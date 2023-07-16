package org.example;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;

public class MainController implements ObserverInterface {

    @FXML
    private TableColumn<Clasament, Long> punctajColumn;
    @FXML
    private TextField introduceLiteraTextField;
    @FXML
    private Button submitButton;
    @FXML
    private Label litereLabel;
    @FXML
    private Button logoutButton;
    @FXML
    private TableColumn<Clasament, String> usernameColumn;
    @FXML
    private TableColumn<Clasament, String> incepereJocColumn;

    @FXML
    private TableView<Clasament> clasamentTableView;
    private ServiceInterface service;
    private Jucator jucator;
    private final ObservableList<Clasament> clasamentModel = FXCollections.observableArrayList();

    private Configuratie configuratie;

    private LocalDateTime incepereJoc;

    private Clasament clasament;

    public void setService(ServiceInterface service) {
        this.service = service;
    }

    private Long points = 0L;

    private Integer round = 0;
    @Override
    public void updateClasament() throws Exception {
        Platform.runLater(() -> {
            try {
                initializeClasament();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void setJucator(Jucator jucator1) {
        this.jucator = jucator1;
    }

    public void initializeClasament() {
        try {
            List<Clasament> clasamentList = service.getClasamente();
            System.out.println(clasamentList);

            clasamentModel.setAll(clasamentList);

            usernameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
            incepereJocColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStart()));
            punctajColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPunctaj()));
            clasamentTableView.setItems(clasamentModel);
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    public void generateConfiguration() {
        try {
            Configuratie configuratie = service.generateRandomConfiguration();
            this.configuratie = configuratie;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Configuratie generata");
            alert.setHeaderText(configuratie.toString());
            alert.showAndWait();
            this.litereLabel.setText(configuratie.getLitere());
            this.clasament = new Clasament(jucator.getUsername(), incepereJoc.toString(), 0L,configuratie.getId(), "");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void setStartGame() {
        this.incepereJoc = LocalDateTime.now();
    }

    public void onLogoutButtonClick(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            service.logout(jucator, this);
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSubmitButtonClick(ActionEvent actionEvent) throws Exception {
        String litera = introduceLiteraTextField.getText();
        if(litera.isEmpty() || !configuratie.getLitere().contains(litera)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Litera trebuie sa fie din cele primite!");
            alert.setContentText("Litera nu poate fi camp gol!");
            alert.showAndWait();
        }
        else {
            if(clasament.getLitere_alese().contains(litera)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Litera a fost deja aleasa!");
                alert.setContentText("Litera nu poate fi aleasa de 2 ori!");
                alert.showAndWait();
            }
            else {
                round++;
                String literaRandomDeLaServer = service.generateRandomLetter(configuratie, this);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Litera de la server");
                alert.setHeaderText("Litera de la server este: " + literaRandomDeLaServer);
                alert.showAndWait();
                Long punctaj = service.getPointsForChosenLetter(litera,configuratie, literaRandomDeLaServer, this);
                points += punctaj;
                clasament.setPunctaj(points);
                clasament.setLitere_alese(clasament.getLitere_alese() + litera + " ");
                if(punctaj < 0L) {
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                    alert1.setTitle("Server WON!");
                    alert1.setHeaderText("Serverul a castigat!");
                    alert1.showAndWait();
                }
                else {
                    if (punctaj == 0L) {
                        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                        alert1.setTitle("Egalitate!");
                        alert1.setHeaderText("Egalitate!");
                        alert1.showAndWait();
                    }
                    else {
                        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                        alert1.setTitle("Ai castigat runda!");
                        alert1.setHeaderText("Ai castigat runda!");
                        alert1.showAndWait();
                    }
                }
                if(round == 4) {
                    try {
                        service.addClasament(clasament, this);
                        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                        alert1.setTitle("S-a terminat jocul!");
                        alert1.setHeaderText("S-a terminat jocul!");
                        alert1.showAndWait();
                        submitButton.setDisable(true);
                        introduceLiteraTextField.setDisable(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
