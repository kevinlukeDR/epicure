package rest.auth.oauth2;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Yiyu Jia on 4/1/16.
 */

public class AccessToken implements Serializable {

    private String token;
    private String client;
    private String owner;
    private String scopes;
    private String type;
    private String refreshToken;
    private int expiresIn;
    private Date expiryDate;
    private static final long serialVersionUID = 1L;


    public AccessToken(String token, String owner, String refreshToken, int expiresIn) {
        this.token = token;
        this.owner = owner;
        this.refreshToken = refreshToken;
        this.type = OAuthSettings.DEFAULT_TOKEN_TYPE;
        this.expiresIn = expiresIn;
        this.expiryDate = new Date(new Date().getTime() + this.expiresIn * 1000);
    }

    public AccessToken(String token, String client, String owner, String scopes, String refreshToken, int expiresIn) {
        this.token = token;
        this.client = client;
        this.owner = owner;
        this.scopes = scopes;
        this.refreshToken = refreshToken;
        this.type = OAuthSettings.DEFAULT_TOKEN_TYPE;
        this.expiresIn = expiresIn;
        this.expiryDate = new Date(new Date().getTime() + this.expiresIn * 1000);
    }


}