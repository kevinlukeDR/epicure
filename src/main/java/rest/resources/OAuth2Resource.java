package rest.resources;


import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.teachoversea.rest.auth.oauth2.OAuthSettings;
import com.teachoversea.rest.dao.AccessTokenDAO;
import com.teachoversea.rest.dao.AccountDao;
import com.teachoversea.rest.dao.Objects.AccessToken;
import com.teachoversea.rest.dao.Objects.Account;
import com.teachoversea.rest.dao.Objects.TokenRequest;
import com.teachoversea.rest.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.DBI;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Base64;

/**
 * To get the initial access token and refresh token, what is required is:
 * <pre>
 * The user ID
 * The user password
 * The client ID
 *
 * To get a refreshed access token however the client uses the following information:
 *
 * The client ID
 * The client secret
 * The refresh token
 * </pre>
 * <p>
 * But, in linkedBAC client is device. owner is account. each accoutn may login from different devices.
 */


@Slf4j
@Path("/oauth2/token")
@Produces(MediaType.APPLICATION_JSON)
public class OAuth2Resource {

    private ImmutableList<String> allowedGrantTypes;
    private AccessTokenDAO accessTokenDAO;
    //private UserDAO userDAO;

    private final AccountDao accountDao;

    private final Base64.Decoder base64Decoder = Base64.getDecoder();

    //public OAuth2Resource(ImmutableList<String> allowedGrantTypes, AccessTokenDAO accessTokenDAO, UserDAO userDAO) {
    public OAuth2Resource(DBI jdbi) {
        //this.allowedGrantTypes = allowedGrantTypes;
        //this.accessTokenDAO = accessTokenDAO;
        //this.userDAO = userDAO;
        this.accountDao = jdbi.onDemand(AccountDao.class);
        //userDAO = new UserDAO();
        accessTokenDAO = new AccessTokenDAO();

        allowedGrantTypes = ImmutableList.of("password", "cert");

        log.info("Constructed OAuth2Resource with grant types {}", allowedGrantTypes);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/accessToken")
    public Response postForToken(TokenRequest tokenRequest) {

        return getAccessToken(tokenRequest);
    }


    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/accessToken")
    public Response postForToken(
            @FormParam("grant_type") String grantType,
            @FormParam("user_name") String username,
            @FormParam("password") String password,
            @FormParam("device_id") String deviceId
    ) {
        TokenRequest tokenRequest = new TokenRequest();

        tokenRequest.setGrant_type(grantType);
        tokenRequest.setUsername(username);
        tokenRequest.setPassword(password);
        tokenRequest.setDeviceId(deviceId); //does this have good performance?

        return getAccessToken(tokenRequest);

    }


    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/refreshToken")
    public Response refreshForToken(
            @FormParam("grant_type") String grantType,
            @FormParam("refresh_token") String refreshToken,
            @FormParam("device_id") String deviceId
    ) {
        if (grantType.equals(OAuthSettings.GrantTypes.REFRESH)) {
            TokenRequest tokenRequest = new TokenRequest();

            tokenRequest.setGrant_type(grantType);
            tokenRequest.setRefreshToken(refreshToken);
            tokenRequest.setDeviceId(deviceId); //does this have good performance?

            return refreshAccessToken(tokenRequest);
        } else {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

    }

    private Response refreshAccessToken(TokenRequest tokenRequest) {
        if (tokenRequest.getGrant_type().equals(OAuthSettings.GrantTypes.REFRESH)) {

            AccessToken accessToken;
            // Issue AccessToken for RefreshToken
            String refresh_token = tokenRequest.getRefreshToken();
            accessToken = accessTokenDAO.refreshToken(tokenRequest.getDeviceId(), refresh_token);
            return Response.ok(accessToken).build();

        } else {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }


    private Response getAccessToken(TokenRequest tokenRequest) {

        AccessToken accessToken;
/*
        if (tokenRequest.getGrant_type().equals(OAuthSettings.GrantTypes.REFRESH)) {
            // Issue AccessToken for RefreshToken

            String refresh_token = tokenRequest.getRefreshToken();
            accessToken = accessTokenDAO.refreshToken(tokenRequest.getDeviceId(), refresh_token);
        } else {
*/
            Optional<Account> user = Optional.of(
                    accountDao.findByNamePassword(String.valueOf(tokenRequest.getUsername()),
                            SecurityUtil.md5(tokenRequest.getPassword()))); //check if really need to base64 encoded before save to db.

            if (user == null || !user.isPresent()) {
                throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
            }

            // User was found, generate a token and return it.
        accessToken = accessTokenDAO.generateNewAccessToken(user.get(), tokenRequest.getDeviceId(), new DateTime());

/*        }  */


        // Try to find a user with the supplied credentials.

        //username and password should be base64 encoded.


        return Response.ok(accessToken).build();
    }


}
