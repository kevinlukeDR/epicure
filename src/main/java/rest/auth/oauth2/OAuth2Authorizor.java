package rest.auth.oauth2;

import com.teachoversea.rest.auth.AccountPrinciple;
import io.dropwizard.auth.Authorizer;

/**
 * Created by Yiyu Jia on 3/21/16.
 */

public class OAuth2Authorizor implements Authorizer<AccountPrinciple> {

    @Override
    public boolean authorize(AccountPrinciple accountPrinciple, String role) {
//        return !(role.equals("CANDIDATE") && !accountPrinciple.getName().startsWith("chief"));
        return role.equals("CANDIDATE");
    }

}


