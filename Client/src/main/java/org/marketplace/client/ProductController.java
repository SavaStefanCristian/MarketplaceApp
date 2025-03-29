package org.marketplace.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.marketplace.clientmanipulation.objects.ProductView;
import org.marketplace.clientmanipulation.request.ProductRequest;
import org.marketplace.clientmanipulation.request.RemoveProductRequest;
import org.marketplace.clientmanipulation.response.ProductResponse;
import org.marketplace.clientmanipulation.response.Response;
import org.marketplace.utils.ImageUtils;

import java.io.IOException;

public class ProductController extends ControllerAbstractAnchor{

    @FXML
    private Label messageLabel;
    @FXML
    private Pagination imagePagination;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label sellerLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Button removeButton;

    private ProductView productView;

    public void setProduct(Long id) {
        ProductRequest request = new ProductRequest();
        request.setId(id);
        try {
            this.out.writeObject(request);
            ProductResponse response = (ProductResponse) this.in.readObject();

            if(response.getProductView()== null) {
                messageLabel.setText("No product found");
            }
            else {
                this.productView = response.getProductView();
            }

            if(response.getStatus().equals(1)) {
                removeButton.setDisable(false);
                removeButton.setVisible(true);
            }
            else {
                removeButton.setDisable(true);
                removeButton.setVisible(false);
            }

            if(this.productView == null) {
                messageLabel.setText(response.getMessage());
                return;
            }

        } catch (IOException e) {
            messageLabel.setText("Error requesting product from server");
        } catch (ClassNotFoundException e) {
            messageLabel.setText("Error receiving product from server");
        }

        this.sellerLabel.setText("Seller: " + productView.getSeller());
        this.titleLabel.setText(productView.getTitle());
        this.descriptionLabel.setText(productView.getDescription());
        this.priceLabel.setText(String.valueOf(productView.getPrice())+ " Euro");
        this.phoneLabel.setText("(Phone) " + productView.getPhone());
        this.categoryLabel.setText("Category: " + productView.getCategory());

        if (productView.getImageBytes() != null && !productView.getImageBytes().isEmpty()) {
            imagePagination.setPageCount(productView.getImageBytes().size());
            imagePagination.setPageFactory(this::createImagePage);
        } else {
            imagePagination.setPageCount(1);
            imagePagination.setPageFactory(pageIndex -> new Label("No Images Available"));
        }

    }

    private ImageView createImagePage(int index) {
        if (productView== null || productView.getImageBytes() == null || productView.getImageBytes().isEmpty() || index >= productView.getImageBytes().size()) {
            return new ImageView();  // Empty image view
        }
        Image image = ImageUtils.byteArrayToFXImage(this.productView.getImageBytes().get(index));
        ImageView imageView = new ImageView(image);

        double targetWidth = 400;
        double targetHeight = 300;

        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();

        double scaleFactor = Math.min(targetWidth / imageWidth, targetHeight / imageHeight);

        double scaledWidth = imageWidth * scaleFactor;
        double scaledHeight = imageHeight * scaleFactor;

        imageView.setFitWidth(scaledWidth);
        imageView.setFitHeight(scaledHeight);
        imageView.setPreserveRatio(true);

        if (scaledWidth > targetWidth || scaledHeight > targetHeight) {
            double cropX = (scaledWidth - targetWidth) / 2;
            double cropY = (scaledHeight - targetHeight) / 2;
            imageView.setViewport(new Rectangle2D(cropX, cropY, targetWidth, targetHeight));
        }
        return imageView;
    }

    public void removeProduct(ActionEvent event) {
        if(productView == null) {
            messageLabel.setText("No product to remove");
            return;
        }
        try {
            RemoveProductRequest request = new RemoveProductRequest();
            request.setId(productView.getId());

            this.out.writeObject(request);
            Response response = (Response) this.in.readObject();

            if(response.getStatus().equals(1)) {
                removeButton.setDisable(true);
                removeButton.setVisible(false);
            }

            messageLabel.setText(response.getMessage());

        } catch (ClassNotFoundException e) {
            messageLabel.setText("Error sending the removal request to server");
        } catch (IOException e) {
            messageLabel.setText("Error receiving removal from server");
        }
    }

    public void back(ActionEvent event) {
        parentAnchorPane.getChildren().clear();
        parentAnchorPane.getChildren().addAll(parentRoot);
    }

}
