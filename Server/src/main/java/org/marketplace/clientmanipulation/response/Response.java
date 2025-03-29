package org.marketplace.clientmanipulation.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class Response implements Serializable {
    private Integer status;
    private String message;

    public Response(Integer status, String message) {
        this.status = status;
        this.message = message;
    }
}
