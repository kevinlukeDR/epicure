package rest;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

/**
 * Created by lu on 2017/3/21.
 */
public class ServerModule implements Module {

    private DBI jdbi;


    @Override

    public void configure(Binder binder) {

//        binder.bind(OAuth2Authenticator.class);

    }



    @Provides

    @Named("DBI")

    public DBI prepareJdbi(Environment environment,

                           EpicureConfiguration epicureConfiguration) throws ClassNotFoundException {

        if (jdbi == null) {

            final DBIFactory factory = new DBIFactory();

            jdbi = factory.build(environment, epicureConfiguration.getDatabase(), "mysql");

        }

        return jdbi;

    }



    @Provides

    @Named("expireDate")

    public int expireDays(EpicureConfiguration epicureConfiguration) throws ClassNotFoundException {

        return Integer.parseInt(epicureConfiguration.getSessionExpireTime());

    }



    @Provides

    @Named("S3Client")

    public S3Client injectS3Client(EpicureConfiguration epicureConfiguration) throws ClassNotFoundException {

        return new S3Client(epicureConfiguration.getAccessKey(), epicureConfiguration.getSecretKey(), epicureConfiguration.getPrivatebucketName(),
                epicureConfiguration.getPublicbucketName(), epicureConfiguration.getPrivatevideobucketName());

    }







}