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
    private String defaultName = "Stranger";


    @Valid
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();


    @NotNull
    @JsonProperty
    private FlywayFactory flywayFactory;


}
