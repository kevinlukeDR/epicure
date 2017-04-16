package rest.resources;

import com.teachoversea.rest.TeachOverseaConfiguration;
import com.teachoversea.rest.business.representation.POJO.ErrorPOJO;
import com.teachoversea.rest.business.representation.POJO.FormPage.*;
import com.teachoversea.rest.business.representation.POJO.ResultPOJO;
import com.teachoversea.rest.dao.Mappers.*;
import com.teachoversea.rest.dao.Objects.Job.Account;
import com.teachoversea.rest.util.MailUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lu on 2017/3/31.
 */
@Path("/form")
@Produces(MediaType.APPLICATION_JSON)
public class FormResource {
    private static Logger logger = Logger.getLogger(FormResource.class);
    private final CertificationDao certificationDao;
    private final ProfileDao profileDao;
    private final AccountDao accountDao;
    private final PictureDao pictureDao;
    private final TagDao tagDao;
    private final ProfileHasTagDao profileHasTagDao;
    private final ProfileStrengthDao profileStrengthDao;
    private final ActivationTempDao activationTempDao;
    private final FormDao formDao;
    private TeachOverseaConfiguration configuration;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public FormResource(DBI jdbi, TeachOverseaConfiguration configuration) {
        this.profileDao = jdbi.onDemand(ProfileDao.class);
        this.pictureDao = jdbi.onDemand(PictureDao.class);
        this.tagDao = jdbi.onDemand(TagDao.class);
        this.profileHasTagDao = jdbi.onDemand(ProfileHasTagDao.class);
        this.profileStrengthDao = jdbi.onDemand(ProfileStrengthDao.class);
        this.formDao = jdbi.onDemand(FormDao.class);
        this.accountDao = jdbi.onDemand(AccountDao.class);
        this.configuration = configuration;
        this.certificationDao = jdbi.onDemand(CertificationDao.class);
        this.activationTempDao = jdbi.onDemand(ActivationTempDao.class);
    }

    @Path("/test")
    @GET
    public Response test(){
        return Response.ok(formDao.findById(1)).build();
    }


    @Path("/one")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertOne(@Valid PageOnePOJO pojo){
        try {
            // The reason why we use randomUUID is we allow candidates free typing email address
            UUID uuid = UUID.randomUUID();
            Date date = new Date();
            if(!EmailValidator.getInstance().isValid(pojo.getEmail())) {
                logger.error("invalid email address");
                ResultPOJO resultPOJO = new ResultPOJO("error");
                resultPOJO.getMetadata().put("error", new ErrorPOJO("WRON-GEMA-ILRE", "" +
                        "Wrong email format"));
                return Response.ok(resultPOJO).build();
            }

            Account account = accountDao.getAccountByEmail(pojo.getEmail());

            if(account!=null ){
//                if(account.getStatus() == 40) {
                long profile_id = account.getProfile_id();
                long id = formDao.findByEmail(pojo.getEmail()).getId();
                logger.info("Account is" + account.getEmail() + "FormDao insert page one: uuid:" + uuid.toString());
                formDao.updatePageOne(pojo.getEmail(), pojo.getFname(), pojo.getLname(), pojo.getMname(), pojo.getSkype(),
                        pojo.getPhone(), pojo.getGender(), pojo.getNationality(), pojo.getBirth(), pojo.getAvailableDate(),
                        pojo.getMajor(), pojo.getGraduateDate(), pojo.getLanguage(), pojo.getHighestEdu(),
                        uuid.toString(), new Timestamp(date.getTime()), id);
                logger.info("Successfully");
                accountDao.updateEmailById(pojo.getEmail(), account.getId());
                accountDao.updateStatus(40, new Timestamp((new Date()).getTime()), account.getId());
                profileDao.updateBasic(pojo.getFname(), pojo.getMname().equals("") || pojo.getMname() == null ? pojo.getLname() : pojo.getMname() + " " + pojo.getLname(),
                        pojo.getAvailableDate(), pojo.getBirth(), pojo.getNationality(), pojo.getGender(), profile_id);
                ResultPOJO resultPOJO = new ResultPOJO("ok");
                resultPOJO.getData().put("uuid", uuid.toString());
                resultPOJO.getData().put("pageOne", pojo);
                return Response.ok(resultPOJO).build();
//            }
//                else if(account.getStatus() == 10){
//                    ResultPOJO resultPOJO = new ResultPOJO("error");
//                    resultPOJO.getMetadata().put("error", new ErrorPOJO("SOME-THIN-GERR", "" +
//                            "You have registered, please log in"));
//                    return Response.ok(resultPOJO).build();
//                }
//                else if(account.getStatus() == 20){
//                    ResultPOJO resultPOJO = new ResultPOJO("error");
//                    resultPOJO.getMetadata().put("error", new ErrorPOJO("SOME-THIN-GERR", "" +
//                            "You have registered, please activate your account"));
//                    return Response.ok(resultPOJO).build();
//                }
//                else{
//                    ResultPOJO resultPOJO = new ResultPOJO("error");
//                    resultPOJO.getMetadata().put("error", new ErrorPOJO("SOME-THIN-GERR", "" +
//                            "You have inactive your account, please activate your account"));
//                    return Response.ok(resultPOJO).build();
//                }
            }
            else {
                logger.info("Account is null"+"FormDao insert page one: uuid:"+ uuid.toString());
                long id = formDao.insertPageOne(pojo.getEmail(), pojo.getFname(), pojo.getLname(), pojo.getMname(), pojo.getSkype(),
                        pojo.getPhone(), pojo.getGender(),
                        pojo.getNationality(), pojo.getBirth(), pojo.getAvailableDate(), pojo.getMajor(), pojo.getGraduateDate(),
                        pojo.getLanguage(), pojo.getHighestEdu(), uuid.toString(), new Timestamp(date.getTime()));
                logger.info("Successfully");
                ResultPOJO resultPOJO = new ResultPOJO("ok");
                long profile_id = profileDao.addProfile(pojo.getFname(), (pojo.getMname().equals("") || pojo.getMname() == null) ? pojo.getLname() : pojo.getMname() + " " + pojo.getLname());
                accountDao.addAccount(pojo.getEmail(), "", profile_id, 40);
                profileDao.updateBasic(pojo.getFname(), pojo.getMname().equals("") || pojo.getMname() == null ? pojo.getLname() : pojo.getMname() + " " + pojo.getLname(),
                        pojo.getAvailableDate(), pojo.getBirth(), pojo.getNationality(), pojo.getGender(), profile_id);
                pictureDao.insertProfilePic(configuration.getAvatarDefaultPic(),
                        10, profile_id);
                profileStrengthDao.insert(profile_id);
                profileStrengthDao.birth(1, profile_id);
                profileStrengthDao.gender(1, profile_id);
                profileStrengthDao.nationality(1, profile_id);
                profileStrengthDao.availability(1, profile_id);
                resultPOJO.getData().put("uuid", uuid.toString());
                resultPOJO.getData().put("pageOne", pojo);
                return Response.ok(resultPOJO).build();
            }
        }catch (Exception e){
            logger.error("FromResource error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("SOME-THIN-GERR", "" +
                    "Something wrong in datebase"));
            return Response.ok(resultPOJO).build();
        }

    }

    @Path("/two")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertTwo(@Valid PageTwoPOJO pojo){
        try {
            Date date = new Date();
            long id = formDao.insertPageTwo(pojo.getCertification(),pojo.getHours(),pojo.isOnline(),pojo.isPracticum(),
                    pojo.isLicense(),pojo.getExperience(),new Timestamp(date.getTime()), pojo.getUuid());
            long profile_id = accountDao.getAccountByEmail(formDao.findByUUId(pojo.getUuid()).getEmail()).getProfile_id();
            List<Long> ids = certificationDao.getAllIdbyProfileId(profile_id);
            for(long cid : ids){
                certificationDao.deleteCertification(cid);
            }
            if(!pojo.getCertification().equals("")){
                certificationDao.addCertification(pojo.getCertification(), new Timestamp((new Date()).getTime()),"Other", profile_id);
                profileStrengthDao.certification(1,profile_id);
            }
            else{
                profileStrengthDao.certification(0,profile_id);
            }
            if(tagDao.getIdByTagName("job_type:fulltime")!=0){
                try {
                    profileHasTagDao.insertProfileTag(profile_id, tagDao.getIdByTagName("job_type:fulltime"));
                }catch (UnableToExecuteStatementException e){
                    System.out.println("Same Tag: job_type:fulltime");
                }
            }
            else {
                int tag_id = tagDao.insertTag("job_type:fulltime");
                profileHasTagDao.insertProfileTag(profile_id, tag_id);
            }
            if(pojo.isOnline()==true) {
                if (tagDao.getIdByTagName("job_type:online") != 0) {
                    try {
                        profileHasTagDao.insertProfileTag(profile_id, tagDao.getIdByTagName("job_type:online"));
                    }catch (UnableToExecuteStatementException e){
                        System.out.println("Same Tag: "+"job_type:online");
                    }
                } else {
                    int tag_id = tagDao.insertTag("job_type:online");
                    profileHasTagDao.insertProfileTag(profile_id, tag_id);
                }
            }
            profileStrengthDao.preference(1,profile_id);

            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("uuid", pojo.getUuid());
            resultPOJO.getData().put("pageOne", pojo);
            return Response.ok(resultPOJO).build();
        }catch (Exception e){
            logger.error("error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("SOME-THIN-GERR", "" +
                    "Something wrong in datebase"));
            return Response.ok(resultPOJO).build();
        }

    }

    @Path("/three")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertThree(@Valid PageThreePOJO pojo){
        try {
            long profile_id = accountDao.getAccountByEmail(formDao.findByUUId(pojo.getUuid()).getEmail()).getProfile_id();
            StringBuilder sb = new StringBuilder();
            for(String temp: pojo.getAgeGroup()){
                sb.append(temp);
                sb.append(";");
                if (tagDao.getIdByTagName("age_group:"+temp) != 0) {
                    try {
                        profileHasTagDao.insertProfileTag(profile_id, tagDao.getIdByTagName("age_group:" + temp));
                    }catch (UnableToExecuteStatementException e){
                        logger.info("Same Tag: "+"age_group:"+temp);
                    }
                } else {
                    int tag_id = tagDao.insertTag("age_group:"+temp);
                    profileHasTagDao.insertProfileTag(profile_id, tag_id);
                }
            }
            StringBuilder sb2 = new StringBuilder();
            for(String temp: pojo.getSubject()){
                sb2.append(temp);
                sb2.append(";");
                if (tagDao.getIdByTagName("subject:"+temp) != 0) {
                    try{
                    profileHasTagDao.insertProfileTag(profile_id, tagDao.getIdByTagName("subject:"+temp));
                    }catch (UnableToExecuteStatementException e){
                        logger.info("Same Tag: "+"subject:"+temp);
                    }
                } else {
                    int tag_id = tagDao.insertTag("subject:"+temp);
                    profileHasTagDao.insertProfileTag(profile_id, tag_id);
                }
            }
            Date date = new Date();
            long id = formDao.insertPageThree(sb.substring(0,sb.length()),sb2.substring(0, sb2.length()),
                    new Timestamp(date.getTime()),pojo.getUuid());
            profileStrengthDao.preference(1,profile_id);
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("uuid", pojo.getUuid());
            resultPOJO.getData().put("pageOne", pojo);
            return Response.ok(resultPOJO).build();
        }catch (Exception e){
            logger.error("error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("SOME-THIN-GERR", "" +
                    "Something wrong in datebase"));
            return Response.ok(resultPOJO).build();
        }

    }


    @Path("/four")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertFour(@Valid PageFourPOJO pojo){
        try {
            long profile_id = accountDao.getAccountByEmail(formDao.findByUUId(pojo.getUuid()).getEmail()).getProfile_id();
            Date date = new Date();
            StringBuilder sb = new StringBuilder();
            for(String temp: pojo.getLocation()){
                sb.append(temp);
                sb.append(";");
                if (tagDao.getIdByTagName("location:"+temp) != 0) {
                    try {
                        profileHasTagDao.insertProfileTag(profile_id, tagDao.getIdByTagName("location:" + temp));
                    }catch (UnableToExecuteStatementException e){
                        logger.info("Same Tag: "+"location:"+temp);
                    }
                } else {
                    int tag_id = tagDao.insertTag("location:"+temp);
                    profileHasTagDao.insertProfileTag(profile_id, tag_id);
                }
            }
            long id = formDao.insertPageFour(pojo.isVisa(),pojo.getSalaryMin(),pojo.getSalaryMax(),sb.substring(0,sb.length()),
                    pojo.isFamily(), pojo.getSpouse(),new Timestamp(date.getTime()),pojo.getUuid());


            if(pojo.isVisa()==true) {
                if (tagDao.getIdByTagName("visa:Z-Visa") != 0) {
                    try{
                    profileHasTagDao.insertProfileTag(profile_id, tagDao.getIdByTagName("visa:Z-Visa"));
                    }catch (UnableToExecuteStatementException e){
                        logger.info("Same Tag: visa:Z-Visa");
                    }
                } else {
                    int tag_id = tagDao.insertTag("visa:Z-Visa");
                    profileHasTagDao.insertProfileTag(profile_id, tag_id);
                }
            }
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("uuid", pojo.getUuid());
            resultPOJO.getData().put("pageOne", pojo);
            MailUtil.nextStep(formDao.findByUUId(pojo.getUuid()).getEmail(), profileDao.findById(profile_id).getFname());
            return Response.ok(resultPOJO).build();
        }catch (Exception e){
            logger.error("error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("SOME-THIN-GERR", "" +
                    "Something wrong in datebase"));
            return Response.ok(resultPOJO).build();
        }

    }


    @Path("/five")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response pageFive(@Context UriInfo info, @Valid PageFivePOJO pojo){
        try {
            Account account = accountDao.getAccountByEmail(formDao.findByUUId(pojo.getUuid()).getEmail());
            if (pojo.getPassword().length() < 6) {
                ResultPOJO resultPOJO = new ResultPOJO("error");
                resultPOJO.getMetadata().put("error", new ErrorPOJO("SOME-THIN-GERR", "" +
                        "Password has to be not less than 6 digits"));
                return Response.ok(resultPOJO).build();
            }
            String sha256hex = DigestUtils.sha256Hex(pojo.getPassword());
            accountDao.forgetPassword(sha256hex, account.getId());
            accountDao.updateStatus(20, new Timestamp((new Date().getTime())), account.getId());
            formDao.updateRegister(new Timestamp(new Date().getTime()), pojo.getUuid());
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            Map<String, Object> map = new HashMap<>();
            map.put("pojo",pojo);
            map.put("email", formDao.findByUUId(pojo.getUuid()).getEmail());
            resultPOJO.getData().put("pageFive", map);
            UUID uuid = UUID.randomUUID();
            activationTempDao.insert(account.getId(), uuid.toString());
            MailUtil.sendEmailRegistrationLink(account.getEmail(), uuid.toString(),
                    profileDao.findById(accountDao.findProfileIdById(account.getId())).getFname(),
                    "candidate", info.getBaseUriBuilder().toTemplate(), "no");
            return Response.ok(resultPOJO).build();
        }catch (Exception e){
            logger.error("error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("SOME-THIN-GERR", "" +
                    "Something wrong in datebase"));
            return Response.ok(resultPOJO).build();
        }
    }





//    @Path("/register")
//    @POST
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    public Response register(@FormDataParam("uuid") String uuid,@FormDataParam("password") String password){
//        String sha256hex = DigestUtils.sha256Hex(password);
//        Form form = formDao.findByUUId(uuid);
//        accountDao.addAccount(form.getEmail(),sha256hex);
//    }
}
