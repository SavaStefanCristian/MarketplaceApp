package org.marketplace.clientmanipulation.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddProductRequest extends Request {
    String title;
    String price;
    String description;
    String category;
    List<byte[]> images;
}
