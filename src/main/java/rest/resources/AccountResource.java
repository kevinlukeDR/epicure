package rest.resources;

import io.dropwizard.auth.Auth;
import org.apache.log4j.Logger;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.sqlobject.Transaction;
import rest.dao.Mappers.AccountDao;
import rest.dao.Objects.Job.Account;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Yiyu Jia on 1/6/16.
 */
@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    private static Logger logger = Logger.getLogger(AccountResource.class);
    private final AccountDao accountDao;

    public AccountResource(DBI jdbi) {

        this.accountDao = jdbi.onDemand(AccountDao.class);

    }

    @PermitAll
    //@RolesAllowed("ADMIN")
    @GET
    @Path("/{id}")
    public Response getAccount(@PathParam("id") int id) {
        // retrieve information about the device status with the provided id
        Account accounts = accountDao.findById(id);
        return Response
                .ok(accounts)
                .build();
    }





}
