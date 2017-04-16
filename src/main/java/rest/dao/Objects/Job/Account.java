package rest.dao.Objects.Job;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Created by Yiyu Jia on 1/6/16.
 */
public class Account {

    @NotNull
    private long id;

    @NotNull
    private String name;

    @NotNull
    //@Length(max = 18)
    private String password;

    @NotNull
    private boolean active;

    @NotNull
    private String email;


    public Account() {
        // Jackson deserialization
    }


    public Account(int id, String name, String password, String email, boolean active) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.active = active;

    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public boolean getActived() {
        return active;
    }

    @JsonProperty
    public String getEmail() {
        return email;
    }
}
