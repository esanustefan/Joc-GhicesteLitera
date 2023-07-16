package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.rpcprotocol.ServiceRpcProxy;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class HelloApplication extends Application {

    private static int defaultPort = 55555;
    private static String defaultServer = "localhost";
    private static Stage stg;

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        Properties props = new Properties();


        try {
            props.load(HelloApplication.class.getResourceAsStream("/client.properties"));
            System.out.println("Client properties set. ");
            props.list(System.out);
        } catch (IOException e) {
            System.out.println("Cannot find client.properties "+e);
            return;
        }
        String serverIP = props.getProperty("host", defaultServer);
        int serverPort = defaultPort;

        try {
            serverPort = Integer.parseInt(props.getProperty("port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong port number "+ex.getMessage());
            System.out.println("Using default port: "+defaultPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        ServiceInterface service = new ServiceRpcProxy(serverIP, serverPort);

        stg = stage;
        stage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/login.fxml"));
        Parent loginRoot = fxmlLoader.load();
        LoginController loginController = fxmlLoader.getController();
        loginController.setService(service);

        FXMLLoader mainLoader = new FXMLLoader(HelloApplication.class.getResource("/main.fxml"));
        Parent mainRoot = mainLoader.load();
        MainController mainController = mainLoader.getController();
        mainController.setService(service);

        loginController.setMainController(mainController);
        loginController.setParent(mainRoot);

        stage.setTitle("Login");
        stage.setScene(new Scene(loginRoot, 600, 400));
        stage.show();

    }


    public static void main(String[] args) {
        Application.launch();
    }
}