package rest;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.flyway.FlywayFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.stardog.starwizard.filters.LbHttpsRedirectFilter;
import org.eclipse.jetty.servlet.FilterHolder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.skife.jdbi.v2.DBI;
import rest.auth.AccountPrinciple;
import rest.auth.oauth2.OAuth2Authenticator;
import rest.auth.oauth2.OAuth2Authorizor;
import rest.health.TemplateHealthCheck;
import rest.resources.AccountResource;
import rest.resources.LinkbacResource;
import rest.resources.ResumeResource;

import javax.servlet.DispatcherType;
import java.util.EnumSet;


/**
 * Created by Yiyu Jia on 11/3/15.
 */

public class EpicureApplication extends Application<EpicureConfiguration> {

//    private GuiceBundle<EpicureConfiguration> guiceBundle;

    public static void main(String[] args) throws Exception {
        new EpicureApplication().run(args);
    }

    @Override
    public String getName() {
        return "teachOversea-Service";
    }

    @Override
    public void initialize(Bootstrap<EpicureConfiguration> bootstrap) {

        initFlaywayDB(bootstrap);

        bootstrap.addBundle(new AssetsBundle("/assets/test_html", "/", "index.html"));

//        bootstrap.addBundle(new RedirectBundle(
//                new HttpsRedirect()
//        ));
//        guiceBundle = GuiceBundle.<EpicureConfiguration>newBuilder()
//
//                .addModule(new ServerModule())
//
//                .setConfigClass(EpicureConfiguration.class)
//
//                .enableAutoConfig(getClass().getPackage().getName())
//
//                .build();
//
//        bootstrap.addBundle(guiceBundle);
    }

    private void initFlaywayDB(Bootstrap<EpicureConfiguration> bootstrap) {

        bootstrap.addBundle(new FlywayBundle<EpicureConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(EpicureConfiguration configuration) {
                return configuration.getDatabase();
            }

            @Override
            public FlywayFactory getFlywayFactory(EpicureConfiguration configuration) {
                return configuration.getFlywayFactory();
            }
        });
    }



    @Override
    public void run(EpicureConfiguration conf,
                    Environment env) {
        env.getApplicationContext().addFilter(new FilterHolder(new LbHttpsRedirectFilter()), "/*",
                EnumSet.of(DispatcherType.REQUEST));
//        final FilterRegistration.Dynamic cors = env.servlets().addFilter("crossOriginRequests", CrossOriginFilter.class);
//        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        // Create DAOs
        //AccessTokenDAO accessTokenDAO = new AccessTokenDAO();
        //UserDao userDAO = new UserDao();

//        ((DefaultServerFactory)conf.getServerFactory()).setRegisterDefaultExceptionMappers(false);
//        env.jersey().register(new MyConstraintViolationExceptionMapper());
//        // Restore Dropwizard's exception mappers
//        env.jersey().register(new LoggingExceptionMapper<Throwable>() {});
//        env.jersey().register(new JsonProcessingExceptionMapper(true));
//        env.jersey().register(new EarlyEofExceptionMapper());
        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(conf.getTemplate());
        env.healthChecks().register("template", healthCheck);


        // Create a DBI factory and build a JDBI instance
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory
                .build(env, conf.getDatabase(), "mysql");

        //DBInitializer initializer = new DBInitializer(jdbi);
        //initializer.folding(jdbi);
        //initializer.createTables();

        final LinkbacResource linkbacResource = new LinkbacResource(
                conf.getTemplate(),
                conf.getDefaultName(),
                jdbi
        );


        final S3Client s3Client = new S3Client(conf.getAccessKey(), conf.getSecretKey(), conf.getPrivatebucketName(),
                conf.getPublicbucketName(),conf.getPrivatevideobucketName());

        final AccountResource accountResource = new AccountResource(jdbi);
        final ResumeResource resumeResource = new ResumeResource(jdbi,s3Client);

        // Add the resource to the environment
        env.jersey().register(linkbacResource);
        env.jersey().register(accountResource);

        env.jersey().register(MultiPartFeature.class);
        //Register Candidate Resource
        env.jersey().register(resumeResource);

        //Request Filter



        // Register security component
        env.jersey().register(new AuthDynamicFeature(
                new OAuthCredentialAuthFilter.Builder<AccountPrinciple>()
                        .setAuthenticator(new OAuth2Authenticator(jdbi, Integer.parseInt(conf.getSessionExpireTime())))
                        .setAuthorizer(new OAuth2Authorizor())
                        .setRealm("realm")
                        .setPrefix("Bearer")
                        .buildAuthFilter()));
        env.jersey().register(RolesAllowedDynamicFeature.class);
        //If you want to use @Auth to inject a custom Principal type into your resource
        env.jersey().register(new AuthValueFactoryProvider.Binder<>(AccountPrinciple.class));


        /*
        //basic authentication
        env.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<AccountPrinciple>()
                .setAuthenticator(new MySQLAuthenticator(jdbi))
                .setAuthorizer(new MySQLAuthorizor())
                .setRealm("SUPER SECRET STUFF")
                .buildAuthFilter()));
        env.jersey().register(new AuthValueFactoryProvider.Binder<>(AccountPrinciple.class));
        */

        env.jersey().setUrlPattern("/api/*");


    }

//    @Provider
//    public class MyConstraintViolationExceptionMapper
//            implements ExceptionMapper<ConstraintViolationException> {
//
//        @Override
//        public Response toResponse(ConstraintViolationException exception) {
//            StringBuilder sb = new StringBuilder();
//            for (ConstraintViolation<?> violation : exception.getConstraintViolations())
//            {
//                String path = violation.getPropertyPath().toString();
//                path = path.substring(path.lastIndexOf(".")+1);
//                sb.append("Error: " + path+ " : " + violation.getMessage() + "\n");
//            }
//            return Response.status(200)
//                    .type(MediaType.APPLICATION_JSON)
//                    .entity(sb.toString())
//                    .build();
//        }
//    }
}