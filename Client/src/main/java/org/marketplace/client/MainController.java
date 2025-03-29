package org.marketplace.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController extends ControllerAbstract implements Initializable {

    @Setter
    private Stage stage;
    private Scene scene;
    private Parent root;


    @FXML
    private AnchorPane centerAnchorPane;

    private final HashMap<String, String> sceneFxml = new HashMap<>();
    private final HashMap<String, Parent> sceneMap = new HashMap<>();

    private String currentScene;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentScene = "";
    }

    public void preloadScenes() {
        sceneFxml.put("searchScene", "SearchScene.fxml");
        sceneFxml.put("myListingsScene", "MyListingsScene.fxml");
        sceneFxml.put("addListingScene", "AddListingScene.fxml");

        for( String sceneName : sceneFxml.keySet() ) {
            loadScene(sceneName, sceneFxml.get(sceneName));
        }

        switchScene("searchScene");
    }

    private void loadScene(String name, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));

            Parent sceneRoot = loader.load();

            ControllerAbstractAnchor controller = loader.getController();

            controller.setConnection(in,out);

            sceneMap.put(name, sceneRoot);

            controller.setParentAnchorPane(centerAnchorPane);
            controller.setParentRoot(sceneMap.get(name));

        } catch (IOException e) {
            throw new RuntimeException("Scene" + name + "could not be loaded");
        }
    }

    public void switchScene(String sceneName) {
        try {
            if(currentScene.equals(sceneName)) {
                loadScene(sceneName, sceneFxml.get(sceneName));
            }

            if (sceneMap.containsKey(sceneName)) {
                centerAnchorPane.getChildren().setAll(sceneMap.get(sceneName));
                currentScene = sceneName;
            } else {
                throw new RuntimeException("Scene " + sceneName + " not found");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void goToSearch(ActionEvent event) {
        switchScene("searchScene");
    }
    public void goToMyListings(ActionEvent event) {
        switchScene("myListingsScene");
    }
    public void goToAddListing(ActionEvent event) {
        switchScene("addListingScene");
    }
}
