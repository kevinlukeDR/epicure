package rest.resources;

import com.teachoversea.rest.auth.AccountPrinciple;
import com.teachoversea.rest.dao.AccountDao;
import com.teachoversea.rest.dao.Objects.Account;
import com.teachoversea.rest.dao.ProfileDao;
import com.teachoversea.rest.util.SecurityUtil;
import io.dropwizard.auth.Auth;
import org.apache.log4j.Logger;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.sqlobject.Transaction;

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
    private final ProfileDao profileDao;

    public AccountResource(DBI jdbi) {

        this.accountDao = jdbi.onDemand(AccountDao.class);
        this.profileDao = jdbi.onDemand(ProfileDao.class);
    }

    @PermitAll
    //@RolesAllowed("ADMIN")
    @GET
    @Path("/{id}")
    public Response getAccount(@Auth AccountPrinciple principle, @PathParam("id") int id) {
        // retrieve information about the device status with the provided id
        Account accounts = accountDao.findById(id);
        return Response
                .ok(accounts)
                .build();
    }


    @POST
    public Response createAccount(Account account) throws
            URISyntaxException {
// store the new contact
        int newAccountId = accountDao.createAccount(account.getName(), SecurityUtil.md5(account.getPassword()), account.getEmail());
        return Response.created(new URI(String.valueOf(newAccountId))).build();
    }

    @PermitAll
    @PUT
    @Path("/{id}")
    @Transaction
    public Response inactivAccountWithProfile(@Auth AccountPrinciple principle, @PathParam("id") int id) throws Exception {
        accountDao.inactiveAccount(id);
        profileDao.inactiveProfileByAccountId(id);
        return Response.ok().build();

    }



}
