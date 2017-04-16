package rest.auth;

import com.teachoversea.rest.dao.AccountDao;
import com.teachoversea.rest.dao.Objects.Account;
import com.teachoversea.rest.util.SecurityUtil;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import org.skife.jdbi.v2.DBI;

import java.util.Optional;

/**
 * Created by Yiyu Jia on 1/2/16.
 */


public class MySQLAuthenticator implements Authenticator<BasicCredentials, AccountPrinciple> {

    private final AccountDao accountDao;


    public MySQLAuthenticator(DBI jdbi) {

        this.accountDao = jdbi.onDemand(AccountDao.class);
    }

    @Override
    public Optional<AccountPrinciple> authenticate(BasicCredentials credentials) throws AuthenticationException {

        // Note: this is horrible authentication. Normally we'd use some
        // service to identify the password from the user name.
        Account account = accountDao.findByNamePassword(credentials.getUsername(), SecurityUtil.md5(credentials.getPassword()));//credentials.getPassword());//

        if (account == null) {
            return Optional.empty();
        }

        // from some user service get the roles for this user
        // I am explicitly setting it just for simplicity
        AccountPrinciple prince = new AccountPrinciple(credentials.getUsername());
        prince.getRoles().add(AccountRoles.ADMIN);
        //return Optional.fromNullable(prince);
        return Optional.of(prince);

    }
}
