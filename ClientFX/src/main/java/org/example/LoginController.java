package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private Button loginButton;
    @FXML
    private TextField usernameTextField;

    private ServiceInterface serviceInterface;
    private Parent mainRoot;
    private MainController mainController;
    public void setService(ServiceInterface serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    public void setParent(Parent p) {
        this.mainRoot = p;
    }
    public void onLogInButtonClick(ActionEvent actionEvent) throws Exception {
        String username = usernameTextField.getText();
        if(username.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Username cannot be empty!");
            alert.showAndWait();
        }
        else {
            try {
                Jucator jucator = new Jucator(username);
                Jucator jucator1 = serviceInterface.login(jucator, mainController);
                Stage s = (Stage) loginButton.getScene().getWindow();
                s.close();
                Stage stage = new Stage();
                Scene scene = new Scene(mainRoot, 600,400);
                stage.setTitle(jucator1.getUsername());
                stage.setScene(scene);
                mainController.setJucator(jucator1);
                mainController.setStartGame();
                mainController.generateConfiguration();
                mainController.setJucator(jucator1);
                mainController.initializeClasament();
                stage.show();

                this.usernameTextField.clear();
            } catch(Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(e.getMessage());
                alert.setContentText("Username does not exist!");
                alert.showAndWait();
            }
        }
    }

}
