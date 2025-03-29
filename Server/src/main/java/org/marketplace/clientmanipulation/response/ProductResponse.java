package org.marketplace.clientmanipulation.response;

import lombok.Getter;
import lombok.Setter;
import org.marketplace.clientmanipulation.objects.ProductView;

@Getter
@Setter
public class ProductResponse extends Response {
    ProductView productView;
    public ProductResponse(Integer status, String message, ProductView productView) {
        super(status, message);
        this.productView = productView;
    }
}
