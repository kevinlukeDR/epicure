package rest.auth;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yiyu Jia on 1/2/16.
 */

public class AccountPrinciple implements Principal {
    private final String name;
    private List<String> roles = new ArrayList<String>();
    private long id;

    public AccountPrinciple(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public List<String> getRoles() {
        return roles;
    }

    public boolean isUserInRole(String roleToCheck) {
        return roles.contains(roleToCheck);
    }

    public long getId() {
        return id;
    }

    public void setId(long id){
        this.id = id;
    }


}