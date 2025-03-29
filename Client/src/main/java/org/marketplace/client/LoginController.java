package org.marketplace.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.marketplace.clientmanipulation.request.LoginRequest;
import org.marketplace.clientmanipulation.response.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class LoginController extends ControllerAbstract{

    @Setter
    private Stage stage;
    private Scene scene;
    private Parent root;


    @Getter
    @FXML
    private Label messageLabel;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;



    public void login(ActionEvent event) {
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            messageLabel.setText("Please enter your username and password...");
            return;
        }
        LoginRequest request = new LoginRequest(usernameField.getText(), passwordField.getText());
        try {
            if (this.out == null || this.in == null) {
                messageLabel.setText("Server is not connected...");
                return;
            }

            out.writeObject(request);
            Response response = (Response) in.readObject();
            if (response.getStatus() == 1) {
                goToMainMenu();
                return;
            } else
                messageLabel.setText(response.getMessage());
        } catch (IOException e) {
            messageLabel.setText("An error occurred while logging in. Please try again later...");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public void goToRegister(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Register.fxml"));

            root = fxmlLoader.load();
            scene = new Scene(root);

            RegisterController registerController = fxmlLoader.getController();
            registerController.setConnection(in, out);
            registerController.setStage(stage);

            scene.addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
                if (ev.getCode() == KeyCode.ENTER) {
                    registerController.register(new ActionEvent());
                    ev.consume();
                }
            });

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            messageLabel.setText("An error occurred while going to the register menu. Please try again later...");
        }
    }

    private void goToMainMenu() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));

            root = fxmlLoader.load();
            scene = new Scene(root);

            MainController mainController = fxmlLoader.getController();
            mainController.setConnection(in, out);
            mainController.preloadScenes();
            mainController.setStage(stage);

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
            //messageLabel.setText("An error occurred while going to the main menu. Please try again later...");
        }


    }
}