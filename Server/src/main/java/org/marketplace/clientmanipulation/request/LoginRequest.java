package org.marketplace.clientmanipulation.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest extends Request {
    String username;
    String password;
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
