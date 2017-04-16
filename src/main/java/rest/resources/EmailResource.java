package rest.resources;

import com.teachoversea.rest.TeachOverseaConfiguration;
import com.teachoversea.rest.business.representation.POJO.ResultPOJO;
import com.teachoversea.rest.dao.Mappers.*;
import com.teachoversea.rest.dao.Objects.Job.EmailCampaign;
import com.teachoversea.rest.dao.Objects.Job.Recruiter;
import com.teachoversea.rest.util.MailUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.skife.jdbi.v2.DBI;

import javax.mail.MessagingException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by lu on 2017/2/8.
 */
@Path("email")
public class EmailResource {
    private final static Logger logger = Logger.getLogger(EmailResource.class);
    private final RecruiterDao recruiterDao;
    private final ProfileDao profileDao;
    private final AccountDao accountDao;
    private final ActivationTempDao activationTempDao;
    private final SystemEmailDao systemEmailDao;
    private final FeedbackDao feedbackDao;
    private final EmailCampaignDao emailCampaignDao;
    private final EmailCampaignDetailsDao emailCampaignDetailsDao;
    public EmailResource(DBI dbi, TeachOverseaConfiguration configuration) {
        this.feedbackDao = dbi.onDemand(FeedbackDao.class);
        this.systemEmailDao = dbi.onDemand(SystemEmailDao.class);
        this.recruiterDao = dbi.onDemand(RecruiterDao.class);
        this.profileDao = dbi.onDemand(ProfileDao.class);
        this.accountDao = dbi.onDemand(AccountDao.class);
        this.activationTempDao = dbi.onDemand(ActivationTempDao.class);
        this.emailCampaignDao = dbi.onDemand(EmailCampaignDao.class);
        this.emailCampaignDetailsDao = dbi.onDemand(EmailCampaignDetailsDao.class);
    }

    @POST
    @Path("/password")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response forgetPassword(@Context UriInfo uriInfo,@FormDataParam("email") String email, @FormDataParam("type") String type) throws MessagingException, UnsupportedEncodingException {
        String head = uriInfo.getBaseUriBuilder().toTemplate().replaceAll("/api","");
        UUID uuid = UUID.randomUUID();
        String fname;
        if(type.equals("Recruiter")){
            Recruiter recruiter = recruiterDao.getRecruiterByEmail(email);
            if(recruiter==null){
                return Response.ok("no such user").build();
            }
            fname = recruiterDao.getRecruiterByEmail(email).getFname();
            activationTempDao.insert(recruiter.getId(),uuid.toString());
        }
        else if(type.equals("Candidate")){
            long account_id = accountDao.getAccountByEmail(email).getId();
            fname = profileDao.findById(accountDao.getAccountByEmail(email).getProfile_id()).getFname();
            activationTempDao.insert(account_id,uuid.toString());
        }
        //TODO Need add more role if necessary
        else
            fname = "";
        MailUtil.sendEmailForgetPassword(email,uuid.toString(),fname, type, head);
        return Response.ok("Successful").build();
    }

    // Password Redirect
    @POST
    @Path("/passwordRe")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verification(@Context UriInfo info, @QueryParam("uuid") String uuid, @QueryParam("type") String type){
        long id = activationTempDao.findByToken(uuid);
        if(id!=0){
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("uuid", uuid);
            return Response.ok(resultPOJO).build();
        }
        else {
            logger.error("Forget Password no such user "+info.getPath());
            ResultPOJO resultPOJO = new ResultPOJO("error");
            return Response.ok(resultPOJO).build();
        }
    }


    @GET
    @Path("/emailcampaign")
    @Produces(MediaType.APPLICATION_JSON)
    public Response emailCampaign(@Context UriInfo info, @QueryParam("uuid") String uuid,@QueryParam("date") String date,
                                  @QueryParam("version") String version) throws ParseException, URISyntaxException {
        EmailCampaign emailCampaign = emailCampaignDao.getByUUID(uuid);
        //TODO delete them after release
        long email_id;
        if(!version.equals("")){
            try {
                email_id = emailCampaign.getId();
            }catch (NullPointerException e){
                logger.error("no such user in email campaign table; Message:"+e.getMessage(), e);
                java.net.URI location = new java.net.URI("../index.html");
                throw new WebApplicationException(Response.temporaryRedirect(location).build());

            }
            Date respond = new Date();

            int res = emailCampaignDetailsDao.updateRecord(new Timestamp(respond.getTime()),
                    email_id);

            if (res != 0) {
                try {
                    java.net.URI location = new java.net.URI("../index.html#applicationForm/" + uuid);
                    throw new WebApplicationException(Response.temporaryRedirect(location).build());
                } catch (URISyntaxException e) {
                    logger.error("URL cannot redirect; Message:"+e.getMessage(), e);
                }
            }
            else{
                logger.error("no such user in email campaign table");
                java.net.URI location = new java.net.URI("../index.html");
                throw new WebApplicationException(Response.temporaryRedirect(location).build());
            }
        }
        else {
            try {
                email_id = emailCampaign.getId();
            } catch (NullPointerException e) {
                logger.error(info.getAbsolutePath().getPath() + "no such user; Message:" + e.getMessage(), e);
                ResultPOJO resultPOJO = new ResultPOJO("error");
                return Response.ok(resultPOJO).build();
            }
            Date respond = new Date();

            int res = emailCampaignDetailsDao.updateRecord(new Timestamp(respond.getTime()), email_id);

            if (res != 0) {
                try {
                    String encode = URLEncoder.encode(emailCampaign.getEmail(), "UTF-8");
                    ResultPOJO resultPOJO = new ResultPOJO("ok");
                    resultPOJO.getData().put("email", encode);
                    return Response.ok(resultPOJO).build();
                } catch (UnsupportedEncodingException e) {
                    logger.error("URL cannot redirect; Message:" + e.getMessage(), e);
                }
            }
        }
            return null;
    }


    @PUT
    @Path("/password")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response changePassword(@FormDataParam("password") String password, @FormDataParam("uuid") String uuid,
                                   @FormDataParam("type") String type){
        String sha256hex = DigestUtils.sha256Hex(password);
        long id = activationTempDao.findByToken(uuid);
        int res = 0;
        if(type.equals("Candidate")) {
            accountDao.forgetPassword(sha256hex, id);
        }
        else if(type.equals("Recruiter"))
            recruiterDao.forgetPassword(sha256hex,id);
        else
            id = 0;
        return Response.ok("Successful").build();
    }

    @POST
    @Path("/unsubscribe")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response unsubscribe(@Context UriInfo info, @FormDataParam("email") String email){
        try {
            long emailId = emailCampaignDao.getByEmail(email).getId();
            int res = systemEmailDao.unsubscribe(20, new Timestamp((new Date()).getTime()), emailId);
            return res == 1 ? Response.ok("Successful").build() : Response.ok("error").build();
        }catch (NullPointerException e){
            logger.error(info.getPath()+"; Message:"+e.getMessage(), e);
            return Response.ok("error").build();
        }
    }

    @POST
    @Path("/foundjob/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response foundJob(@Context UriInfo info, @PathParam("uuid") String uuid) throws URISyntaxException {
        long emailid;
        EmailCampaign emailCampaign = emailCampaignDao.getByUUID(uuid);
        try {
             emailid = emailCampaign.getId();
        }catch (NullPointerException e){
            logger.error(info.getAbsolutePath().getPath()+"Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            return Response.ok(resultPOJO).build();
        }
        int res = systemEmailDao.unsubscribe(30, new Timestamp((new Date()).getTime()),emailid);
        if (res != 0) {
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("uuid", uuid);
            return Response.ok(resultPOJO).build();
        }
        return null;
    }


    @POST
    @Path("/feedback")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response feedback(@FormDataParam("reason") String reason){
        long res = feedbackDao.addReason(reason);
        return res!=0 ? Response.ok(new ResultPOJO("ok")).build() :
                Response.ok(new ResultPOJO("error")).build();
    }


    //TODO total three scenarios
//    @POST
//    @Path("/contact")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response contactCandidate(
//            @Context UriInfo uriInfo,
//            @Auth AccountPrinciple principle,
//            @FormDataParam("profile_id") long profile_id,
//            @FormDataParam("message") String message) throws MessagingException {
//        long recruiter_id = principle.getId();
//        String recruiter = recruiterDao.findById(recruiter_id).getCompanyName();
//        Account account = accountDao.getAccountByProfileId(profile_id);
//        MailUtil.sendContact(account.getEmail(), UUID.randomUUID().toString(), profileDao.findById(profile_id).getFname(),
//                "contact",uriInfo.getBaseUriBuilder().toTemplate(), recruiter);
//        return Response.ok().build();
//    }

    @POST
    @Path("/contactus")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response contactUs(@FormDataParam("name") String name, @FormDataParam("email") String email,
                              @FormDataParam("message") String message){
        MailUtil.sendContactUs(name, email, message);
        ResultPOJO resultPOJO = new ResultPOJO("ok");
        return Response.ok(resultPOJO).build();
    }

}
