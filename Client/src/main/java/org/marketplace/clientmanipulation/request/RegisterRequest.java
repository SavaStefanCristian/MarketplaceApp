package org.marketplace.clientmanipulation.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest extends Request {
    String username;
    String password;
    String phoneNumber;

    public RegisterRequest(String username, String password, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
}
