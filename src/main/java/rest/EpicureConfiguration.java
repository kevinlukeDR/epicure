package rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayFactory;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by Yiyu Jia on 11/3/15.
 */
@Getter
@Setter
public class EpicureConfiguration extends Configuration {

    @Valid
    @JsonProperty
    private ImmutableList<String> allowedGrantTypes;

    @Valid
    @JsonProperty
    @NotEmpty
    private String bearerRealm;


    @NotEmpty
    @JsonProperty
    private String template;


    @NotEmpty
    @JsonProperty
    private String accessKey;

    @NotEmpty
    @JsonProperty
    private String secretKey;

    @NotEmpty
    @JsonProperty
    private String sessionExpireTime;


    @NotEmpty
    @JsonProperty
    private String publicbucketName;

    @NotEmpty
    @JsonProperty
    private String privatebucketName;

    @NotEmpty
    @JsonProperty
    private String defaultName = "Stranger";


    @Valid
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();


    @NotNull
    @JsonProperty
    private FlywayFactory flywayFactory;

    @NotEmpty
    @JsonProperty
    private String privatevideobucketName;


    @JsonProperty
    private EmailYAML contactemail;

    @JsonProperty
    private EmailYAML applyconfirmemail;

    @JsonProperty
    private EmailYAML registrationemail;

    @JsonProperty
    private EmailYAML forgetpasswordemail;

    @JsonProperty
    private String jobDefaultPic;

    @JsonProperty
    private EmailYAML contactUsEmail;

    @JsonProperty
    private EmailYAML applicationEmail;

    @JsonProperty
    private String authenticationCachePolicy;

    @JsonProperty
    private String avatarDefaultPic;

    @JsonProperty
    private EmailYAML qualifiedEmail;

    @JsonProperty
    private EmailYAML unqualifiedEmail;
}
