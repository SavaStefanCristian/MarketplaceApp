package org.marketplace.client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.marketplace.clientmanipulation.request.CloseRequest;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class Client extends Application {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    @Override
    public void start(Stage stage) {
        initializeConnection();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            LoginController loginController = fxmlLoader.getController();
            loginController.setConnection(in,out);
            loginController.setStage(stage);

            if(this.in == null || this.out == null) {
                loginController.getMessageLabel().setText("Server is not connected");
            }

            stage.setOnCloseRequest(event -> {
                try {

                    out.writeObject(new CloseRequest());
                    socket.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                finally {

                    stage.close();
                    event.consume();
                }
            });

            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    loginController.login(new ActionEvent());
                    event.consume();
                }
            });

            stage.setTitle("Marketplace!");
            stage.setScene(scene);
            stage.show();


        } catch (IOException e) {
            System.err.println("Error loading UI: " + e.getMessage());
        }
    }

    private void initializeConnection() {
        try {
            this.socket = new Socket("localhost", 0000);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}