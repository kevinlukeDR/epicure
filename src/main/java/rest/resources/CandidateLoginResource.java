package rest.resources;

import com.teachoversea.rest.business.representation.POJO.*;
import com.teachoversea.rest.dao.Mappers.*;
import com.teachoversea.rest.dao.Objects.Job.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.skife.jdbi.v2.DBI;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lu on 2017/1/13.
 */

@Path("/candidateLogin")
public class CandidateLoginResource {
    private static Logger logger = Logger.getLogger(CandidateLoginResource.class);
    private final AccountDao accountDao;
    private final ProfileDao profileDao;
    private final SessionDao sessionDao;
    private final FormDao formDao;

    public CandidateLoginResource(DBI jdbi) {
        this.accountDao = jdbi.onDemand(AccountDao.class);
        this.profileDao = jdbi.onDemand(ProfileDao.class);
        this.sessionDao = jdbi.onDemand(SessionDao.class);
        this.formDao = jdbi.onDemand(FormDao.class);
    }

    @GET
    @Path("/test/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountById(@PathParam("email") String email){
        Account account = accountDao.getAccountByEmail(email);
        if(account == null){
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("message", "ok");
            return Response.ok(resultPOJO).build();
        }
        else if(account.getStatus() == 10){
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("REGI-STER-STAT","You have already registered, please log in"));
            return Response.ok(resultPOJO).build();
        }
        else if(account.getStatus() == 20){
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("REGI-STER-STAT","You have already registered, please activate first"));
            return Response.ok(resultPOJO).build();
        }
        else if(account.getStatus() == 30){
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("REGI-STER-STAT","You have already inactived, do you want to reactivate right now?"));
            return Response.ok(resultPOJO).build();
        }
        else if(account.getStatus() == 40){
            String uuid = formDao.findByEmail(email).getUuid();
            ResultPOJO resultPOJO = new ResultPOJO("import");
            resultPOJO.getData().put("uuid", uuid);
            return Response.ok(resultPOJO).build();
        }
        else {
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("REGI-STER-STAT","Something wrong here"));
            return Response.ok(email).build();
        }
    }

    @GET
    @Path("/import/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response importFun(@PathParam("email") String email){
        String uuid = formDao.findByEmail(email).getUuid();
        ResultPOJO resultPOJO = new ResultPOJO("import");
        resultPOJO.getData().put("uuid", uuid);
        return Response.ok(resultPOJO).build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginStatus(@Context UriInfo uriInfo, Account account){
        String test = uriInfo.getBaseUriBuilder().toTemplate();
        String sha256hex = DigestUtils.sha256Hex(account.getPassword());
        try{
            int profileId = accountDao.findProfileId(account.getEmail(), sha256hex);
            Account res = accountDao.getAccountByEmail(account.getEmail());
            AccountPOJO pojo = null;
            Map<String, String> map = new HashMap<>();
            if(res == null)
                map.put("error", "no such user");
            else if(profileId == 0) {
                map.put("error", "password not match");
            }
            else if(res.getStatus() == 40){
                map.put("error", "your information has been in our database, please sign up");
            }
            else if(res.getStatus() != 10) {
                map.put("error", "please active your account first");
            }
            else {
                pojo = new AccountPOJO(res.getId(),res.getEmail(),"",res.getProfile_id(),
                        profileDao.findById(res.getProfile_id()));
                map.put("status", "successful");
                accountDao.loginTime(new Timestamp((new Date()).getTime()), pojo.getId());
                String uuid = UUID.randomUUID().toString();
                map.put("session_token",uuid);
                sessionDao.insert(uuid, pojo.getId(), 1);
            }
            MetaDataPOJO metaDataPOJO = new MetaDataPOJO(map);
            if(pojo!=null)
                pojo.setPassword("");

            return Response.ok(new LoginPOJO(metaDataPOJO.getMetadata(), pojo)).build();
        }
        catch (Exception e){
            logger.error("CandidateLoginResource; Message:"+e.getMessage(), e);
            return null;
        }
    }


//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getProfile(Account account){
//        String sha256hex = DigestUtils.sha256Hex(account.getPassword());
//        int profileId = accountDao.findProfileId(account.getEmail(), sha256hex);
//        Profile profile = profileDao.findById(profileId);
//        List<RelavantExPOJO> relavantExPOJOList = new ArrayList<>();
//        List<CertificationPOJO> certificationPOJOList = new ArrayList<>();
//        List<EducationPOJO> educationPOJOList = new ArrayList<>();
//        for(RelavantEx ex : relavantExDao.findById(profileId)){
//            relavantExPOJOList.add(new RelavantExPOJO(ex.getId(),ex.getRole(),ex.getCompany(),ex.getFromDate(),
//                    ex.getToDate(),ex.getResponsibility()));
//        }
//        for(Certification certification : certificationDao.findById(profileId)){
//            certificationPOJOList.add(new CertificationPOJO(certification.getId(),certification.getType(),certification.getIssueDate(),
//                    certification.getIssuingBody()));
//        }
//        for(Education education :educationDao.findById(profileId)){
//            educationPOJOList.add(new EducationPOJO(education.getId(),education.getDegree(),education.getField(),
//                    education.getFromDate(),education.getToDate(),education.getSchool(),education.getCountry()));
//        }
//        ProfilePOJO profilePOJO = new ProfilePOJO(profile.getId(), profile.getResume(), profile.getFname(), profile.getLname(),
//                profile.getAvailableDate(), profile.getBirth(), profile.getNationality(), profile.getGender(), profile.getPersonalWebsite(),
//                profile.getLinkedin(),profile.getFacebook(),profile.getInstagram(),profile.getTwitter(), relavantExPOJOList,certificationPOJOList,
//                educationPOJOList);
//        return Response.ok(profilePOJO).build();
//    }

}
