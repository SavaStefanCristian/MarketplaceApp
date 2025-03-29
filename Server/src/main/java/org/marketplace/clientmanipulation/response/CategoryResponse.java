package org.marketplace.clientmanipulation.response;

import lombok.Getter;

import java.util.List;

public class CategoryResponse extends Response {

    @Getter
    List<String> categories;
    public CategoryResponse(Integer status, String message, List<String> categories) {
        super(status, message);
        this.categories = categories;
    }

}
