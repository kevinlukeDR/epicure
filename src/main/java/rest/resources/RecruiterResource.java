package rest.resources;

import com.teachoversea.rest.S3Client;
import com.teachoversea.rest.auth.AccountPrinciple;
import com.teachoversea.rest.business.representation.POJO.*;
import com.teachoversea.rest.dao.Mappers.*;
import com.teachoversea.rest.dao.Objects.Job.Form;
import com.teachoversea.rest.dao.Objects.Job.Recruiter;
import com.teachoversea.rest.util.MailUtil;
import io.dropwizard.auth.Auth;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.sqlobject.Transaction;

import javax.mail.MessagingException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.*;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by lu on 2017/1/24.
 */
@Path("/recruiter")
public class RecruiterResource {
    private final RecruiterDao recruiterDao;
    private final SessionDao sessionDao;
    private final ActivationTempDao activationTempDao;
    private S3Client s3Client;
    private PictureDao pictureDao;
    private final FormDao formDao;
    private static Logger logger = Logger.getLogger(RecruiterResource.class);
    public RecruiterResource(DBI dbi, S3Client s3Client) {
        this.recruiterDao = dbi.onDemand(RecruiterDao.class);
        this.sessionDao = dbi.onDemand(SessionDao.class);
        this.pictureDao = dbi.onDemand(PictureDao.class);
        this.activationTempDao = dbi.onDemand(ActivationTempDao.class);
        this.s3Client = s3Client;
        this.formDao = dbi.onDemand(FormDao.class);
    }

//    @POST
//    @Path("/insert")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.TEXT_PLAIN)
//    public long insertRecruiter(Recruiter recruiter){
//        String sha256hex = DigestUtils.sha256Hex(recruiter.getPassword());
//        long recruiterId = recruiterDao.addRecruiter(recruiter.getEmail(),sha256hex,recruiter.getFname(),recruiter.getLname(),
//                recruiter.getPhone(),recruiter.getContactEmail(),recruiter.getTitle(),recruiter.getCompanyName(),
//                recruiter.getCompanyAddress(),recruiter.getCompanySize(),recruiter.getFoundTime(),recruiter.getDescription(),
//                recruiter.getCompanyWebsite(),recruiter.getCompanyLogo(),recruiter.getCompanyPic());
//        return  recruiterId;
//    }

    @POST
    @Path("/logo")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeLogo(
            @Context UriInfo info,
            @Auth AccountPrinciple principle,
            @FormDataParam("logo") InputStream uploadedInputStream,
            @FormDataParam("logo") FormDataContentDisposition fileDetail){
        // save it
        try {
            String filename = new String(fileDetail.getFileName().getBytes("iso-8859-1"), "UTF-8");
            if (!fileDetail.getFileName().equals("")) {
                long id = principle.getId();
                UUID uuid = UUID.randomUUID();
                pictureDao.updateRecruiterLogo(20, id);
                String path = writeToFile(uploadedInputStream, uuid + filename,
                        id, 10, "logo");
                ResultPOJO resultPOJO = new ResultPOJO("ok");
                resultPOJO.getData().put("path", path);
                return Response.ok(resultPOJO).build();
            } else {
                ResultPOJO resultPOJO = new ResultPOJO("error");
                resultPOJO.getMetadata().put("error", new ErrorPOJO("NOFI-LESE-LECT", "Please choose a file first!"));
                return Response.ok(resultPOJO).build();
            }
        }catch (Exception e){
            logger.error("Error's here "+ info.getPath()+"; Message: "+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX", "Something error, please check logs"));
            return Response.ok(resultPOJO).build();
        }
    }


    @Transaction
    @POST
    @Path("/register")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insert(
            @Context UriInfo uriInfo,
            FormDataMultiPart multiPart) throws IOException, MessagingException {
        try {
            String email = multiPart.getField("email").getValue();
            String sha256hex = DigestUtils.sha256Hex(multiPart.getField("password").getValue());
            String fname = multiPart.getField("fname").getValue();
            String lname = multiPart.getField("lname").getValue();
            String phone = multiPart.getField("phone").getValue();
            String contactEmail = multiPart.getField("contactEmail").getValue();
            String title = multiPart.getField("title").getValue();
            String companyName = multiPart.getField("companyName").getValue();
            String companyAddress = multiPart.getField("companyAddress").getValue();
            int companySize = multiPart.getField("companySize").getValueAs(Integer.class);
            int foundTime = multiPart.getField("foundTime").getValueAs(Integer.class);
            String description = multiPart.getField("description").getValue();
            String companyWebsite = multiPart.getField("companyWebsite").getValue();

            Recruiter recruiter = recruiterDao.getRecruiterByEmail(email);

            //Check whether Email has been registered
            if (recruiter != null)
                return Response.ok("error").build();
            if(!EmailValidator.getInstance().isValid(email)){
                ResultPOJO resultPOJO = new ResultPOJO("error");
                resultPOJO.getMetadata().put("error", new ErrorPOJO("WRON-GEMA-ILRE", "" +
                        "Wrong email format"));
                return Response.ok(resultPOJO).build();
            }
            long recruiterId = recruiterDao.addRecruiter(20, email, sha256hex, fname, lname, phone, contactEmail, title,
                    companyName, companyAddress, companySize, foundTime, description, companyWebsite);

            List<FormDataBodyPart> file = multiPart.getFields("companyLogo");
            if (!file.get(0).getFormDataContentDisposition().getFileName().equals("")) {
                for (FormDataBodyPart f : file) {
                    InputStream uploadedInputStream = f.getValueAs(InputStream.class);
                    UUID uuid = UUID.randomUUID();
                    writeToFile(uploadedInputStream, uuid + new String (f.getFormDataContentDisposition().getFileName().getBytes ("iso-8859-1"), "UTF-8"),
                            recruiterId, 10, "logo");
                }
            }
            else {
                pictureDao.insertRecruiterLogo("RecruiterPicture/nologo.jpg",10, recruiterId);
            }
            List<FormDataBodyPart> files = multiPart.getFields("companyPic");

            if (!files.get(0).getFormDataContentDisposition().getFileName().equals("")) {
                String filename = "";
                for (FormDataBodyPart f : files) {
                    InputStream uploadedInputStream = f.getValueAs(InputStream.class);
                    UUID uuid = UUID.randomUUID();
                    filename = new String(f.getFormDataContentDisposition().getFileName().getBytes("iso-8859-1"), "UTF-8");
                    writeToFile(uploadedInputStream, uuid + filename,
                            recruiterId, 10, "pic");
                }
            }
            String head = uriInfo.getBaseUriBuilder().toTemplate();
            UUID uuid = UUID.randomUUID();
            activationTempDao.insert(recruiterId, uuid.toString());
            MailUtil.sendEmailRegistrationLink(contactEmail, uuid.toString(), fname, "recruiter", head, "regular");
            // save it
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("recruiter", recruiterId);
            resultPOJO.getData().put("email", email);
            return Response.ok(resultPOJO).build();
        }catch (Exception e){
            logger.error(uriInfo.getPath()+" error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("SOME-THIN-GERR", "" +
                    "This source is not yours"));
            return Response.ok(resultPOJO).build();
        }

    }
    // save uploaded file to new location
    private String writeToFile(InputStream uploadedInputStream, String filename, long recruiter_id, int status,
                             String des) throws IOException {
        int read;
        final int BUFFER_LENGTH = 1024;
        final byte[] buffer = new byte[BUFFER_LENGTH];
        UUID uuid = UUID.randomUUID();
        File file = new File("./src/main/resources/doc/"+uuid.toString());
        OutputStream out = new FileOutputStream(file);
        while ((read = uploadedInputStream.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        out.flush();
        out.close();
        //TODO need to be got from config.yml
        String folder = "RecruiterPicture";
        s3Client.uploadFileToPublic(folder, filename, file);
        if(des.equals("logo"))
            pictureDao.insertRecruiterLogo(folder+"/"+filename,status, recruiter_id);
        else
            pictureDao.insertRecruiterPic(folder+"/"+filename,status, recruiter_id);
        file.delete();
        return folder+"/"+filename;
    }

    @GET
    @Path("/test/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecruiterByEmail(@PathParam("email") String email){
        Recruiter recruiter = recruiterDao.getRecruiterByEmail(email);
        if(recruiter == null){
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("message", "ok");
            return Response.ok(resultPOJO).build();
        }
        else if(recruiter.getStatus() == 10){
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("REGI-STER-STAT","You have already registered, please log in"));
            return Response.ok(resultPOJO).build();
        }
        else if(recruiter.getStatus() == 20){
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("REGI-STER-STAT","You have already registered, please activate first"));
            return Response.ok(resultPOJO).build();
        }
        else if(recruiter.getStatus() == 30){
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("REGI-STER-STAT","You have already inactived, do you want to reactivate right now?"));
            return Response.ok(resultPOJO).build();
        }
        else {
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("REGI-STER-STAT","Something wrong here"));
            return Response.ok(email).build();
        }
    }

    //TODO need modify later
    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecruiterById(@Auth AccountPrinciple principle){
        long id = principle.getId();
        Recruiter recruiter = recruiterDao.findById(id);
        RecruiterPOJO recruiterPOJO = new RecruiterPOJO(recruiter.getId(),recruiter.getStatus(),recruiter.getEmail(),recruiter.getPassword(),
                recruiter.getFname(),recruiter.getLname(),recruiter.getPhone(),recruiter.getContactEmail(),recruiter.getTitle(),
                recruiter.getCompanyName(),recruiter.getCompanyAddress(),recruiter.getCompanySize(),recruiter.getDescription(),
                recruiter.getCompanyWebsite(),recruiter.getFoundTime(),pictureDao.findRecruiterLogoById(id,10),
                pictureDao.findRecruiterPicById(id,10),recruiter.getCreateDate(),recruiter.getActiveDate(),recruiter.getDisableDate());
        return Response.ok(recruiterPOJO).build();
    }

//TODO NEW get API:
//    @GET
//    @Path("/")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getRecruiterById(@Auth AccountPrinciple principle){
//        long id = principle.getId();
//        Recruiter recruiter = recruiterDao.findById(id);
//        RecruiterPOJO recruiterPOJO = new RecruiterPOJO(recruiter.getId(),recruiter.getStatus(),recruiter.getEmail(),"",
//                recruiter.getFname(),recruiter.getLname(),recruiter.getPhone(),recruiter.getContactEmail(),recruiter.getTitle(),
//                recruiter.getCompanyName(),recruiter.getCompanyAddress(),recruiter.getCompanySize(),recruiter.getDescription(),
//                recruiter.getCompanyWebsite(),recruiter.getFoundTime(),pictureDao.findRecruiterLogoById(id,10),
//                pictureDao.findRecruiterPicById(id,10),recruiter.getCreateDate(),recruiter.getActiveDate(),recruiter.getDisableDate());
//        ResultPOJO resultPOJO = new ResultPOJO("ok");
//        resultPOJO.getData().put("recruiter", recruiterPOJO);
//        return Response.ok(resultPOJO).build();
//    }


    @GET
    @Path("/verification")
    public String receiveEmail(@Context UriInfo info,@QueryParam("uuid") String uuid){
        long accountId = activationTempDao.findByToken(uuid);
        int count = recruiterDao.updateStatus(10,new Timestamp((new Date()).getTime()),accountId);
        if(count == 0){
            try {
                java.net.URI location = new java.net.URI("../index.html#errorPage");
                throw new WebApplicationException(Response.temporaryRedirect(location).build());
            }catch (URISyntaxException e) {
                logger.error(info.getPath()+" error; Message:"+e.getMessage(), e);
            }
        }
        try {
            java.net.URI location = new java.net.URI("../index.html#recruiterLogin");
            throw new WebApplicationException(Response.temporaryRedirect(location).build());
        }catch (URISyntaxException e) {
            logger.error(info.getPath()+" error; Message:"+e.getMessage(), e);
        }
        return "Successful";
    }




    @GET
    @Path("/resend")
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendAgain(@Context UriInfo uriInfo,@QueryParam("email") String email) throws MessagingException, UnsupportedEncodingException {
        Recruiter recruiter = recruiterDao.getRecruiterByEmail(email);
        UUID uuid = UUID.randomUUID();
        activationTempDao.insert(recruiter.getId(),uuid.toString());
        MailUtil.sendEmailRegistrationLink(recruiter.getEmail(),uuid.toString(), recruiter.getFname(),"recruiter", uriInfo.getBaseUriBuilder().toTemplate(), "regular");
        return Response.ok("Successful").build();
    }

    @PUT
    @Path("/changeemail")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response changeEmail(@Context UriInfo uriInfo,@FormDataParam("email") String email, @FormDataParam("id") long id) throws MessagingException, UnsupportedEncodingException {
        Recruiter recruiter = recruiterDao.getRecruiterByEmail(email);
        recruiterDao.updateEmail(email,recruiter.getEmail());
        UUID uuid = UUID.randomUUID();
        activationTempDao.insert(recruiter.getId(),uuid.toString());
        MailUtil.sendEmailRegistrationLink(email,uuid.toString(), recruiter.getFname(),"recruiter",
                uriInfo.getBaseUriBuilder().toTemplate(), "regular");
        return Response.ok("Successful").build();
    }

//    @PUT
//    @Path("/password")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    public Response forgetPassword(@FormParam("email") String email, @FormParam("id") long id) throws MessagingException {
//        Recruiter recruiter = recruiterDao.getRecruiterByEmail(email);
//        recruiterDao.updateEmail(email,recruiter.getEmail());
//        UUID uuid = UUID.randomUUID();
//        activationTempDao.insert(recruiter.getId(),uuid.toString());
//        mailUtil.sendEmailRegistrationLink(email,uuid.toString(), recruiter.getFname());
//        return Response.ok("Successful").build();
//    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response recruiterLogin(@Context UriInfo info,
            Recruiter r) throws Exception {

        String sha256hex = DigestUtils.sha256Hex(r.getPassword());
        try{
            int recruiterId = recruiterDao.findRecruiterId(r.getEmail(),sha256hex);
            Recruiter recruiter = recruiterDao.getRecruiterByEmail(r.getEmail());

            Map<String, String> map = new HashMap<>();
            if(recruiter == null)
                map.put("error", "no such user");
            else if(recruiterId == 0) {
                map.put("error", "password not match");
            }
            else if(recruiter.getStatus()!=10){
                map.put("error","please active your account first");
            }
            else {
                map.put("status", "successful");
                String uuid = UUID.randomUUID().toString();
                map.put("session_token",uuid);
                sessionDao.insert(uuid, recruiter.getId(), 2);
            }
            MetaDataPOJO metaDataPOJO = new MetaDataPOJO(map);
            if(recruiter!=null)
                recruiter.setPassword("");
            return Response.ok(new RecruiterLoginPOJO(metaDataPOJO.getMetadata(), recruiter)).build();
        }
        catch (Exception e){
            logger.error(info.getPath()+" error; Message:"+e.getMessage(), e);
            return null;
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
        if(sha256hex.equals(recruiterDao.findById(id).getPassword()))
            return Response.ok("Same").build();
        recruiterDao.forgetPassword(sha256hex,id);
        return Response.ok("Successful").build();
    }


    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Auth AccountPrinciple principle){
        List<Form> forms = formDao.findAllQualifiedCandidates();
        ResultPOJO resultPOJO = new ResultPOJO("ok");
        resultPOJO.getData().put("candidates", forms);
        return  Response.ok(resultPOJO).build();
    }

    @POST
    @Path("/qualify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response qualify(@Auth AccountPrinciple principle){
        List<Form> forms = formDao.findAllQualifiedCandidates();
        for(Form form : forms){
            MailUtil.qualify(form.getEmail(), form.getFname());
            formDao.setAutoReplyDate(new Timestamp((new Date()).getTime()), form.getEmail());
        }
        ResultPOJO resultPOJO = new ResultPOJO("ok");
        return Response.ok(resultPOJO).build();
    }

    @POST
    @Path("/unqualify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unqualify(@Auth AccountPrinciple principle){
        List<Form> forms = formDao.findAllUnqualifiedCandidates();
        for(Form form : forms){
            MailUtil.unqualify(form.getEmail(), form.getFname());
            formDao.setAutoReplyDate(new Timestamp((new Date()).getTime()), form.getEmail());
        }
        ResultPOJO resultPOJO = new ResultPOJO("ok");
        return Response.ok(resultPOJO).build();
    }
}
