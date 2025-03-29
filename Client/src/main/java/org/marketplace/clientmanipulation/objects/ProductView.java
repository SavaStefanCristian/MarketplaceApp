package org.marketplace.clientmanipulation.objects;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ProductView implements Serializable {
    Long id;
    String title;
    String description;
    String category;
    String seller;
    String phone;
    double price;
    List<byte[]> imageBytes;
}
