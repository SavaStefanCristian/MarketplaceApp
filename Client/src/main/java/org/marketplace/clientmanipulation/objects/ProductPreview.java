package org.marketplace.clientmanipulation.objects;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ProductPreview implements Serializable {
    Long id;
    String title;
    double price;
    byte[] imageBytes;
}
