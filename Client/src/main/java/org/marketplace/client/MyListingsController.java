package org.marketplace.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.marketplace.clientmanipulation.objects.ProductPreview;
import org.marketplace.clientmanipulation.request.MyProductsRequest;
import org.marketplace.clientmanipulation.response.ProductPreviewsResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MyListingsController extends ControllerAbstractAnchor {

    @FXML
    private Label messageLabel;
    @FXML
    private ListView<ProductPreview> productListView;

    private Integer index = 0;
    private Boolean hasReachedEnd = false;
    private List<ProductPreview> displayedProducts = new ArrayList<>();

    @Override
    public void setConnection(ObjectInputStream in, ObjectOutputStream out) {
        this.in = in;
        this.out = out;
        loadProducts();
        setProductsList();
    }

    private void loadProducts() {
        if(hasReachedEnd) return;

        MyProductsRequest request = new MyProductsRequest();
        request.setIndex(this.index);

        try {
            this.out.writeObject(request);
            ProductPreviewsResponse response = (ProductPreviewsResponse) this.in.readObject();

            if(response.getStatus().equals(0)) {
                messageLabel.setText(response.getMessage());
                return;
            }

            if(response.getProductPreviews() == null || response.getProductPreviews().isEmpty()) {
                hasReachedEnd = true;
                return;
            }

            index = index + response.getProductPreviews().size();
            displayedProducts.addAll(response.getProductPreviews());

        } catch (IOException e) {
            this.messageLabel.setText("Something went wrong while requesting products from server");
        } catch (ClassNotFoundException e) {
            this.messageLabel.setText("Something went wrong while receiving products from server");
        }

    }

    private void setProductsList() {
        productListView.setItems(FXCollections.observableArrayList(displayedProducts));

        productListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ProductPreview> call(ListView<ProductPreview> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(ProductPreview product, boolean empty) {
                        super.updateItem(product, empty);
                        if (empty || product == null) {
                            setGraphic(null);
                        } else {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductPreview.fxml"));
                                AnchorPane pane = loader.load();
                                ProductPreviewController controller = loader.getController();
                                controller.setProduct(product);
                                setGraphic(pane);
                            } catch (IOException e) {
                                messageLabel.setText("Something went wrong while displaying a product");
                            }
                        }
                    }
                };
            }
        });

        productListView.setOnMouseClicked(event -> {
            ProductPreview selectedProduct = productListView.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("Product.fxml"));

                    Parent productRoot = loader.load();

                    this.getParentAnchorPane().getChildren().clear();
                    this.getParentAnchorPane().getChildren().add(productRoot);


                    ProductController controller = loader.getController();
                    controller.setConnection(in,out);
                    controller.setParentAnchorPane(this.parentAnchorPane);
                    controller.setParentRoot(this.parentRoot);
                    controller.setProduct(selectedProduct.getId());

                } catch (IOException e) {
                    this.messageLabel.setText("Something went wrong while displaying a product");
                }
            }
        });

        productListView.setOnScrollFinished(event -> {

            if(hasReachedEnd) return;

            loadProducts();
        });

    }
}
