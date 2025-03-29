package org.marketplace.clientmanipulation.response;

import lombok.Getter;
import lombok.Setter;
import org.marketplace.clientmanipulation.objects.ProductPreview;

import java.util.List;

@Getter
@Setter
public class ProductPreviewsResponse extends Response {

    private List<ProductPreview> productPreviews;

    public ProductPreviewsResponse(Integer status, String message, List<ProductPreview> productPreviews) {
        super(status, message);
        this.productPreviews = productPreviews;
    }

}
