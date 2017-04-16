package rest.auth;

import io.dropwizard.auth.Authorizer;

/**
 * Created by Yiyu Jia on 1/2/16.
 */
public class MySQLAuthorizor implements Authorizer<AccountPrinciple> {

    @Override
    public boolean authorize(AccountPrinciple accountPrinciple, String role) {
        return !(role.equals("ADMIN") && !accountPrinciple.getName().startsWith("chief"));
    }

}


