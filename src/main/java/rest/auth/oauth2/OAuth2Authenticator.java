package rest.auth.oauth2;

import com.teachoversea.rest.auth.AccountPrinciple;
import com.teachoversea.rest.auth.AccountRoles;
import com.teachoversea.rest.dao.AccessTokenDAO;
import com.teachoversea.rest.dao.Mappers.AccountDao;
import com.teachoversea.rest.dao.Mappers.RecruiterDao;
import com.teachoversea.rest.dao.Mappers.SessionDao;
import com.teachoversea.rest.dao.Objects.Job.Recruiter;
import com.teachoversea.rest.dao.Objects.Job.Session;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import lombok.AllArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.skife.jdbi.v2.DBI;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;


/**
 * Created by Yiyu Jia on 3/21/16.
 */


@AllArgsConstructor
public class OAuth2Authenticator implements Authenticator<String, AccountPrinciple> {
    public static final int ACCESS_TOKEN_EXPIRE_TIME_MIN = 30; //put this limitation to be modified later.
    private AccessTokenDAO accessTokenDAO;
    private AccountDao accountDao;
    private RecruiterDao recruiterDao;
    private SessionDao sessionDao;
    private int expireDate;

    public OAuth2Authenticator(DBI jdbi, int expireDate) {
        this.expireDate = expireDate;
        this.sessionDao = jdbi.onDemand(SessionDao.class);
        this.accountDao = jdbi.onDemand(AccountDao.class);
        this.recruiterDao = jdbi.onDemand(RecruiterDao.class);
        accessTokenDAO = new AccessTokenDAO();
    }

    @Override
    public Optional<AccountPrinciple> authenticate(String accessTokenId) throws AuthenticationException {

        //System.out.println(accessTokenId);

        // Check input, must be a valid UUID
        UUID accessTokenUUID;
        try {
            accessTokenUUID = UUID.fromString(accessTokenId);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }

        // Get the access token from the database
        //currently, the "database" is a in memory hashmap. Modify it to use Mysql or something like redit later.
        Session sessionObj = sessionDao.getSession(accessTokenId);
        Optional<Session> session = Optional.ofNullable(sessionObj);
        if (session == null || !session.isPresent()) {
            //System.out.println(accessTokenUUID + " has no token");
            return Optional.empty();
        }

        // Check if the last access time is not too far in the past (the access token is expired)
        DateTime before = new DateTime(session.get().getRefreshedDate().getTime());
        DateTime now = new DateTime();
        int days = Days.daysBetween(before, now).getDays();
        if (days>= expireDate) {
            return Optional.empty();
        }

        // Update the access time for the token
        sessionDao.update(new Timestamp(new Date().getTime()),accessTokenId);


        // from some user service get the roles for this user
        // I am explicitly setting it just for simplicity
        AccountPrinciple prince;
        prince = new AccountPrinciple(session.get().getUuid());
//        prince.getRoles().add(AccountRoles.ADMIN); // get role by using Candidate account dao or recruiter account dao.

        //merge candidate and recruiter session table into one. add type.
        //if session type == 1/candiate use candidate account dao
        //if session type == 2/recruiter use recruiter account dao.
        if(session.get().getType() == 1) { //will define enum later.
            long profile_id = accountDao.findProfileIdById(session.get().getAccount_id()); // get account or profile info and set into principle if needed.
            prince.getRoles().add(AccountRoles.CANDIDATE); //so, account database table need a column for storing role. we need RBAC table design later.
            prince.setId(profile_id);

        }
        else if(session.get().getType() == 2){
            Recruiter recruiter = recruiterDao.findById(session.get().getAccount_id());
            prince.getRoles().add(AccountRoles.RECRUITER);
            prince.setId(recruiter.getId());
        }

        return Optional.of(prince);

    }
}
