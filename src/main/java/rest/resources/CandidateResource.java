package rest.resources;


import com.teachoversea.rest.TeachOverseaConfiguration;
import com.teachoversea.rest.auth.AccountPrinciple;
import com.teachoversea.rest.business.representation.POJO.ErrorPOJO;
import com.teachoversea.rest.business.representation.POJO.ResultPOJO;
import com.teachoversea.rest.business.representation.POJO.SignUpPOJO;
import com.teachoversea.rest.dao.Mappers.*;
import com.teachoversea.rest.dao.Objects.Job.Account;
import com.teachoversea.rest.dao.Objects.Job.Profile;
import com.teachoversea.rest.util.MailUtil;
import io.dropwizard.auth.Auth;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.sqlobject.Transaction;

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lu on 2017/1/9.
 */

@Path("/candidate")

public class CandidateResource {
    private static Logger logger = Logger.getLogger(CandidateResource.class);
    private final ProfileDao profileDao;
    private final AccountDao accountDao;
    private final ActivationTempDao activationTempDao;
    private final SessionDao sessionDao;
    private final ProfileStrengthDao profileStrengthDao;
    private PictureDao pictureDao;
    private TeachOverseaConfiguration configuration;

    // Use for email valdiation
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public CandidateResource(DBI jdbi, TeachOverseaConfiguration configuration) {
        this.accountDao = jdbi.onDemand(AccountDao.class);
        this.profileDao = jdbi.onDemand(ProfileDao.class);
        this.activationTempDao = jdbi.onDemand(ActivationTempDao.class);
        this.pictureDao = jdbi.onDemand(PictureDao.class);
        this.sessionDao = jdbi.onDemand(SessionDao.class);
        this.profileStrengthDao = jdbi.onDemand(ProfileStrengthDao.class);
        this.configuration = configuration;
    }

    @GET
    @Path("/email")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmail(@Auth AccountPrinciple principle){
        long id = principle.getId();
        Account account = accountDao.getAccountByProfileId(id);
        ResultPOJO resultPOJO = new ResultPOJO("ok");
        resultPOJO.getData().put("email", account.getEmail());
        return Response.ok(resultPOJO).build();
    }



    @GET
    @Path("/totalaccountsnumber")
    public Response getTotalNumber(){
        return Response.ok(accountDao.getTotalNumber()).build();
    }

    @GET
    @Path("/verification")
    @Produces(MediaType.APPLICATION_JSON)
    public Response receiveEmail(@QueryParam("uuid") String uuid, @QueryParam("where") String where){
        try {
            long accountId = activationTempDao.findByToken(uuid);
            int count = accountDao.updateStatus(10, new Timestamp((new Date()).getTime()), accountId);
            if (count == 0) {
                ResultPOJO resultPOJO = new ResultPOJO("error");
                return Response.ok(resultPOJO).build();
//            try {
//                java.net.URI location = new java.net.URI("../index.html#errorPage");
//                throw new WebApplicationException(Response.temporaryRedirect(location).build());
//            }catch (URISyntaxException e) {
//                logger.error("CandidateResource error; Message:"+e.getMessage(), e);
//            }
            } else {
                //Default Profile Picture
                pictureDao.insertProfilePic(configuration.getAvatarDefaultPic(),
                        10, accountDao.findProfileIdById(accountId));
                String accesstoken = UUID.randomUUID().toString();
                sessionDao.insert(accesstoken, accountId, 1);
                ResultPOJO resultPOJO = new ResultPOJO("ok");
                resultPOJO.getData().put("accesstoken", accesstoken);
                resultPOJO.getData().put("where", where);
                return Response.ok(resultPOJO).build();
            }
        } catch (Exception e) {
            logger.error("CandidateResource error; Message:" + e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            return Response.ok(resultPOJO).build();
        }
    }


    @GET
    @Path("/resend")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendAgain(@Context UriInfo uriInfo,@QueryParam("email") String email) throws MessagingException, UnsupportedEncodingException {
        Account account = accountDao.getAccountByEmail(email);
        Profile profile = profileDao.findById(account.getProfile_id());
        UUID uuid = UUID.randomUUID();
        activationTempDao.insert(account.getId(),uuid.toString());

        MailUtil.sendEmailRegistrationLink(email,uuid.toString(), profile.getFname(), "candidate",
                uriInfo.getBaseUriBuilder().toTemplate(), "redirect");
        return Response.ok("Successful").build();
    }

    @PUT
    @Path("/changeemail")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response changeEmail(@Context UriInfo uriInfo, @FormDataParam("email") String email, @Auth AccountPrinciple principle) throws MessagingException, UnsupportedEncodingException {
        long profile_id = principle.getId();
        long id = accountDao.getAccountByProfileId(profile_id).getId();
        Account account = accountDao.getAccountById(id);
        if(email.equals(account.getEmail()))
            return Response.ok("Same").build();
        if(!EmailValidator.getInstance().isValid(email))
            return Response.ok("Wrong Format").build();
        accountDao.updateEmail(email,account.getEmail());
        UUID uuid = UUID.randomUUID();
        activationTempDao.insert(account.getId(),uuid.toString());
        Profile profile = profileDao.findById(account.getProfile_id());
        MailUtil.sendEmailRegistrationLink(account.getEmail(),uuid.toString(), profile.getFname(), "candidate",
                uriInfo.getBaseUriBuilder().toTemplate(),"no");
        return Response.ok("Successful").build();
    }

    @Transaction
    @POST
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCandidates(@Context UriInfo uriInfo, @Valid SignUpPOJO signUpPOJO) throws MessagingException {
//        Account test = accountDao.getAccountByEmail(signUpPOJO.getEmail());
//        if(test !=null){
//            return Response.ok().build();
//        }
        long profileId = profileDao.addProfile(signUpPOJO.getFname(), signUpPOJO.getLname());
        profileStrengthDao.insert(profileId);

        // Email Validator
        if(!EmailValidator.getInstance().isValid(signUpPOJO.getEmail())){
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("WRON-GEMA-ILRE", "" +
                    "Wrong email format"));
            return Response.ok(resultPOJO).build();
        }
            String sha256hex = DigestUtils.sha256Hex(signUpPOJO.getPassword());
        try {
            long id = accountDao.addAccount(signUpPOJO.getEmail(), sha256hex, profileId, 20);//Need Verified

            long key = accountDao.findProfileId(signUpPOJO.getEmail(), sha256hex);
            UUID uuid = UUID.randomUUID();
            activationTempDao.insert(id, uuid.toString());
            MailUtil.sendEmailRegistrationLink(signUpPOJO.getEmail(), uuid.toString(), signUpPOJO.getFname(), "candidate",
                    uriInfo.getBaseUriBuilder().toTemplate(),"redirect");
            return  Response.ok(key).type(MediaType.TEXT_PLAIN_TYPE).build();
        }catch (Exception e){
            logger.error("CandidateResource error; Message:"+e.getMessage(), e);
            profileDao.deleteProfile(profileId);
            return Response.status(500).type(MediaType.TEXT_PLAIN_TYPE)
                    .entity("Something goes wrong...").build();
        }
    }

    @PUT
    @Path("/password")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response changePassword(
            @Auth AccountPrinciple principle,
            @FormDataParam("password") String password){
        String sha256hex = DigestUtils.sha256Hex(password);
        long id = principle.getId();
        long account_id= accountDao.getAccountByProfileId(id).getId();
        if(sha256hex.equals(accountDao.getAccountByProfileId(id).getPassword()))
            return Response.ok("Same").build();
        accountDao.forgetPassword(sha256hex,account_id);
        return Response.ok("Successful").build();
    }


    @PUT
    @Path("/email")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeEmail(
            @Context UriInfo uriInfo,
            @Auth AccountPrinciple principle) throws MessagingException {
        long id = principle.getId();
        String email = "kevinluke1993@hotmail.com";
        if(validate(email)){
            Account account= accountDao.getAccountByProfileId(id);
            accountDao.updateEmail(email, account.getEmail());
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("email", email);
            UUID uuid = UUID.randomUUID();
            activationTempDao.insert(account.getId(), uuid.toString());
            accountDao.updateStatus(20,null,account.getId());
            try {
                MailUtil.sendEmailRegistrationLink(email, uuid.toString(), profileDao.findById(id).getFname(), "candidate",
                        uriInfo.getBaseUriBuilder().toTemplate(),"no");
            }catch (Exception e){
                logger.error("CandidateResource error; Message:"+e.getMessage(), e);
                resultPOJO = new ResultPOJO("error");
                ErrorPOJO errorPOJO = new ErrorPOJO("NOTV-ALID-ADDR", "Cannot reach at this email address");
                resultPOJO.getMetadata().put("error", errorPOJO);
                return Response.ok(resultPOJO).build();
            }
            return Response.ok(resultPOJO).build();
        }
        else {
            ResultPOJO resultPOJO = new ResultPOJO("error");
            ErrorPOJO errorPOJO = new ErrorPOJO("NOTV-ALID-ADDR", "not a valid email address");
            resultPOJO.getMetadata().put("error", errorPOJO);
            return Response.ok(resultPOJO).build();
        }

    }


    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }


}
