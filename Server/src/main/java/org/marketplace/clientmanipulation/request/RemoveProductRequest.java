package org.marketplace.clientmanipulation.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveProductRequest extends Request {
    private Long id;
}
