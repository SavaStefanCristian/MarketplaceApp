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
import lombok.Setter;
import org.marketplace.clientmanipulation.request.LoginRequest;
import org.marketplace.clientmanipulation.request.RegisterRequest;
import org.marketplace.clientmanipulation.response.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RegisterController extends ControllerAbstract {

    @Setter
    private Stage stage;
    private Scene scene;
    private Parent root;


    @FXML
    private Label messageLabel;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField phoneField;

    private String messageToLogin = "";


    public void register(ActionEvent event) {

        if(usernameField.getText().isEmpty() || passwordField.getText().isEmpty() || phoneField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
            messageLabel.setText("Please complete all the fields...");
            return;
        }
        if(!passwordField.getText().equals(confirmPasswordField.getText())) {
            messageLabel.setText("Password does not match the confirm password...");
            return;
        }

        RegisterRequest registerRequest = new RegisterRequest(usernameField.getText(), passwordField.getText(), phoneField.getText());
        try {
            if(this.out==null || this.in==null) {
                messageLabel.setText("Server is not connected...");
                return;
            }

            out.writeObject(registerRequest);
            Response response = (Response) in.readObject();

            if(response.getStatus() == 1) {
                messageToLogin = response.getMessage();

                goToLogin(new ActionEvent());
                return;
            }

            messageLabel.setText(response.getMessage());

        } catch (IOException e) {
            messageLabel.setText("An error occurred while creating your account. Please try again later...");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void goToLogin(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));

            root = fxmlLoader.load();
            //if(stage==null) stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);

            LoginController loginController = fxmlLoader.getController();
            loginController.setConnection(in,out);
            loginController.setStage(stage);

            loginController.getMessageLabel().setText(messageToLogin);

            scene.addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
                if (ev.getCode() == KeyCode.ENTER) {
                    loginController.login(new ActionEvent());
                    ev.consume();
                }
            });

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            messageLabel.setText("An error occurred while going to the login menu. Please try again later...");
        }
    }

}
