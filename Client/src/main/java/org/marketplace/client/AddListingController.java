package org.marketplace.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Setter;
import org.marketplace.clientmanipulation.request.AddProductRequest;
import org.marketplace.clientmanipulation.request.CategoryRequest;
import org.marketplace.clientmanipulation.response.CategoryResponse;
import org.marketplace.clientmanipulation.response.Response;
import org.marketplace.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddListingController extends ControllerAbstractAnchor {


    @FXML
    private Label messageLabel;
    @FXML
    private TextField titleField;
    @FXML
    private TextField priceField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ListView<String> imageListView;
    @FXML
    private ChoiceBox<String> categoryBox;



    private final FileChooser fileChooser = new FileChooser();
    List<File> images = new ArrayList<>();

    @Override
    public void setConnection(ObjectInputStream in, ObjectOutputStream out) {
        this.in = in;
        this.out = out;
        setCategoryBox();
    }


    public void setCategoryBox() {
        try {
            out.writeObject(new CategoryRequest());
            CategoryResponse response = (CategoryResponse) in.readObject();

            categoryBox.setItems(FXCollections.observableArrayList(response.getCategories()));

        } catch (IOException e) {
            messageLabel.setText("Could not connect to server... Please try again later.");
        } catch (ClassNotFoundException e) {
            messageLabel.setText("An error occurred while receiving from server... Please try again later.");
        }

    }

    public void addImages(ActionEvent event) {
        fileChooser.setTitle("Add Images");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        images.addAll(fileChooser.showOpenMultipleDialog(((Node)event.getSource()).getScene().getWindow()));
        List<String> imageNames = new ArrayList<>();

        for (File file : images) {
            imageNames.add(file.getName());
        }
        imageListView.setItems(FXCollections.observableArrayList(imageNames));
    }

    public void clearImages(ActionEvent event) {
        images.clear();
        imageListView.setItems(FXCollections.observableArrayList());
    }

    public void addProduct(ActionEvent event) {
        AddProductRequest request = new AddProductRequest();
        request.setTitle(titleField.getText());
        request.setDescription(descriptionArea.getText());
        request.setCategory(categoryBox.getValue());
        request.setPrice(priceField.getText());
        request.setImages(new ArrayList<>());
        try {
            for (File file : images) {
                request.getImages().add(ImageUtils.imageFileToByteArray(file));
            }
        } catch (IOException e) {
            messageLabel.setText("An error occurred while reading from file" + e.getMessage());
        }


        try {
            out.writeObject(request);
            Response response = (Response) in.readObject();
            messageLabel.setText(response.getMessage());

        } catch (IOException e) {
            messageLabel.setText("An error occurred while sending to server... Please try again later.");
        } catch (ClassNotFoundException e) {
            messageLabel.setText("An error occurred while receiving from server... Please try again later.");
        }
    }



}
