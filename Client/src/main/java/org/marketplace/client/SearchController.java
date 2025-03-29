package org.marketplace.client;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.marketplace.clientmanipulation.objects.ProductPreview;
import org.marketplace.clientmanipulation.request.CategoryRequest;
import org.marketplace.clientmanipulation.response.CategoryResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SearchController extends ControllerAbstractAnchor {

    @FXML
    private Label messageLabel;
    @FXML
    private ListView<String> categoryListView;



    @Override
    public void setConnection(ObjectInputStream in, ObjectOutputStream out) {
        this.in = in;
        this.out = out;
        setCategoryList();
    }

    public void setCategoryList() {
        try {
            out.writeObject(new CategoryRequest());
            CategoryResponse response = (CategoryResponse) in.readObject();

            categoryListView.setItems(FXCollections.observableArrayList(response.getCategories()));

        } catch (IOException e) {
            messageLabel.setText("Could not connect to server... Please try again later.");
        } catch (ClassNotFoundException e) {
            messageLabel.setText("An error occurred while receiving from server... Please try again later.");
        }


        categoryListView.setOnMouseClicked(event -> {
            String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
            if (selectedCategory != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("SearchProductByCategoryScene.fxml"));

                    Parent productRoot = loader.load();

                    this.getParentAnchorPane().getChildren().clear();
                    this.getParentAnchorPane().getChildren().add(productRoot);


                    SearchProductByCategoryController controller = loader.getController();
                    controller.setCategory(selectedCategory);
                    controller.setConnection(in,out);
                    controller.setParentAnchorPane(this.parentAnchorPane);
                    controller.setParentRoot(this.parentRoot);

                } catch (IOException e) {
                    this.messageLabel.setText("Something went wrong while displaying a product");
                }
            }
        });
    }



}
