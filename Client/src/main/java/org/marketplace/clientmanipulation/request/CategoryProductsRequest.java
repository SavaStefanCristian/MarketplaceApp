package org.marketplace.clientmanipulation.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryProductsRequest extends Request {
    String category;
    private Integer index;
}
