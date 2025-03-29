package org.marketplace.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.marketplace.clientmanipulation.objects.ProductPreview;
import org.marketplace.utils.ImageUtils;

public class ProductPreviewController extends ControllerAbstract {
    @FXML
    private ImageView productImageView;
    @FXML
    private Label titleLabel;
    @FXML
    private Label priceLabel;

    public void setProduct(ProductPreview product) {
        if(product == null) {return;}

        if(product.getImageBytes()==null) {return;}
        productImageView.setImage(ImageUtils.byteArrayToFXImage(product.getImageBytes()));

        if(product.getTitle()!=null) {titleLabel.setText(product.getTitle());}

        priceLabel.setText(String.valueOf(product.getPrice()));

    }
}
