package rest.resources;


import com.teachoversea.rest.S3Client;
import com.teachoversea.rest.auth.AccountPrinciple;
import com.teachoversea.rest.business.representation.POJO.*;
import com.teachoversea.rest.dao.Mappers.*;
import com.teachoversea.rest.dao.Objects.Job.*;
import io.dropwizard.auth.Auth;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Yiyu Jia on 3/20/16.
 */
@Path("/profile")
@Produces(MediaType.APPLICATION_JSON)
public class ProfileResource {
    private static Logger logger = Logger.getLogger(ProfileResource.class);
    private final ProfileDao profileDao;
    private final EducationDao educationDao;
    private final CertificationDao certificationDao;
    private final RelavantExDao relavantExDao;
    private final ResumeDao resumeDao;
    private final PictureDao pictureDao;
    private final VideoDao videoDao;
    private final TagDao tagDao;
    private final ProfileHasTagDao profileHasTagDao;
    private S3Client s3Client;
    private final ProfileStrengthDao profileStrengthDao;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public ProfileResource(DBI jdbi, S3Client s3Client) {
        this.profileDao = jdbi.onDemand(ProfileDao.class);
        this.educationDao = jdbi.onDemand(EducationDao.class);
        this.videoDao = jdbi.onDemand(VideoDao.class);
        this.certificationDao = jdbi.onDemand(CertificationDao.class);
        this.relavantExDao = jdbi.onDemand(RelavantExDao.class);
        this.resumeDao = jdbi.onDemand(ResumeDao.class);
        this.pictureDao = jdbi.onDemand(PictureDao.class);
        this.tagDao = jdbi.onDemand(TagDao.class);
        this.profileHasTagDao = jdbi.onDemand(ProfileHasTagDao.class);
        this.profileStrengthDao = jdbi.onDemand(ProfileStrengthDao.class);
        this.s3Client = s3Client;
    }

    //@RolesAllowed("ADMIN")
    //TODO Integration
    @GET
    @Path("/")
    public Response getProfile(@Auth AccountPrinciple principle) {
        // retrieve information about the device status with the provided id
        long id = principle.getId();
        logger.info(this.getClass().getName());
        Profile profiles = profileDao.findById(id);
        List<RelavantEx> relavantExs = relavantExDao.findById(id);
        List<Certification> certifications = certificationDao.findById(id);
        List<Education> educationList = educationDao.findById(id);

        List<RelavantExPOJO> relavantExPOJOs = new ArrayList<>();
        List<CertificationPOJO> certificationPOJOs = new ArrayList<>();
        List<EducationPOJO> educationPOJOs = new ArrayList<>();

        for(RelavantEx relavantEx : relavantExs){
            relavantExPOJOs.add(new RelavantExPOJO(String.valueOf(relavantEx.getId()),relavantEx.getRole(),relavantEx.getCompany(),
                    sdf.format(relavantEx.getFromDate().getTime()),
                    sdf.format(relavantEx.getToDate().getTime()),relavantEx.getResponsibility()));
        }
        for(Certification certification : certifications){
            certificationPOJOs.add(new CertificationPOJO(String.valueOf(certification.getId()),certification.getType(),
                    sdf.format(certification.getIssueDate().getTime()),
                    certification.getIssuingBody()));
        }
        for(Education education : educationList){
            educationPOJOs.add(new EducationPOJO(String.valueOf(education.getId()),education.getDegree(),education.getField(),
                    sdf.format(education.getFromDate().getTime()),
                    sdf.format(education.getToDate().getTime()),education.getSchool(),education.getCountry(),education.getCity()));
        }
        Timestamp s = profiles.getAvailableDate();
        ProfilePOJO profilePOJO = new ProfilePOJO(profiles.getId(), pictureDao.findProfilePicById(id,10),resumeDao.findLatestById(id),profiles.getFname(),
                profiles.getLname(),sdf.format(profiles.getAvailableDate().getTime()),
                profiles.getBirth(),profiles.getNationality(), profiles.getGender(),
                sdf.format(profiles.getLastUpdate().getTime()),profiles.getPersonalWebsite(),profiles.getLinkedin(),profiles.getFacebook(),
                profiles.getInstagram(),profiles.getTwitter(),profiles.getWechat(),relavantExPOJOs,certificationPOJOs,educationPOJOs);
        return Response
                .ok(profilePOJO)
                .build();
    }

    @POST
    @Path("/profilepic")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadPic(
            @Context UriInfo info,
            @Auth AccountPrinciple principle,
            @FormDataParam("profilepic") String uploadedInputStream
            ) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(uploadedInputStream);
            JSONObject array = (JSONObject) json.get("output");
            String name = (String) array.get("name");
            String pic = (String) array.get("image");
            long profileId = principle.getId();
            UUID uuid = UUID.randomUUID();
//            String filename = new String (name.getBytes ("iso-8859-1"), "UTF-8");
            String ext = name.substring(name.lastIndexOf(".") + 1, name.length());
            String filename = uuid.toString()+"."+ext;
            String base64Image = pic.split(",")[1];
            byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
            String path = writeToFile(new ByteArrayInputStream(imageBytes), uuid + filename,
                    profileId, 10, "logo");
            profileStrengthDao.avatar(1, profileId);
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("path", path);
            return Response.ok(resultPOJO).build();
        }catch (Exception e){
            logger.error(info.getPath()+" error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX", "Something wrong here"));
            return Response.ok(resultPOJO).build();
        }
    }

    @POST
    @Path("/photo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadPhoto(
            @Context UriInfo info,
            @Auth AccountPrinciple principle,
            String photo) {
        try {
            long profileId = principle.getId();
            UUID uuid = UUID.randomUUID();
            String name = uuid.toString()+".png";
            String filename = name;
            String base64Image = photo.split(",")[1];
            byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
            String path = writeToFile(new ByteArrayInputStream(imageBytes), uuid + filename,
                    profileId, 10, "logo");
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("path", path);
            profileStrengthDao.avatar(1, profileId);
            return Response.ok(resultPOJO).build();
        }catch (Exception e){
            logger.error(info.getPath()+" error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX", "Something wrong here"));
            return Response.ok(resultPOJO).build();
        }
    }



    @PUT
    @Path("/profilepic")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePic(
            @Auth AccountPrinciple principle,
            @FormDataParam("profilepic") InputStream uploadedInputStream,
            @FormDataParam("profilepic") FormDataContentDisposition fileDetail) throws IOException {
        String filename = new String (fileDetail.getFileName().getBytes ("iso-8859-1"), "UTF-8");
        long profileId = principle.getId();
        if(!fileDetail.getFileName().equals("")) {
            UUID uuid = UUID.randomUUID();
            pictureDao.updateProfilePicStatus(20, profileId);
            String path = writeToFile(uploadedInputStream, uuid + filename,
                    profileId, 10, "logo");
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("path", path);
            return Response.ok(resultPOJO).build();
        }
        else {
            ErrorPOJO errorPOJO = new ErrorPOJO("NOFI-LEUP-LOAD",
                    "You need upload a file first");
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", errorPOJO);
            return Response.ok(resultPOJO).build();
        }
    }

    private String writeToFile(InputStream uploadedInputStream, String filename, long profile_id, int status,
                             String des){
        try {
            int read;
            final int BUFFER_LENGTH = 1024;
            final byte[] buffer = new byte[BUFFER_LENGTH];
            UUID uuid = UUID.randomUUID();
            File file = new File("./src/main/resources/doc/" + uuid.toString());
            OutputStream out = new FileOutputStream(file);
            while ((read = uploadedInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
            out.close();
            //TODO need to be got from config.yml
            String folder = "ProfilePicture";
            s3Client.uploadFileToPublic(folder, filename, file);
            pictureDao.updateProfilePicStatus(20, profile_id);
            pictureDao.insertProfilePic(folder + "/" + filename, status, profile_id);
            file.delete();
            return folder + "/" + filename;
        }catch (IOException e){
            logger.error(this.getClass().getName()+" error; Message:"+e.getMessage(), e);
            return null;
        }
    }



    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateProfile(@Context UriInfo info,ProfilePOJO profilePOJO) {
        try {
            if (!profilePOJO.getGender().equals(""))
                profileStrengthDao.gender(1, profilePOJO.getId());
            if (!profilePOJO.getBirth().equals(""))
                profileStrengthDao.birth(1, profilePOJO.getId());
            if (!profilePOJO.getAvailableDate().equals(""))
                profileStrengthDao.availability(1, profilePOJO.getId());
            if (!profilePOJO.getNationality().equals(""))
                profileStrengthDao.nationality(1, profilePOJO.getId());
            if (!profilePOJO.getFacebook().equals("") || !profilePOJO.getPersonalWebsite().equals("") || !profilePOJO.getLinkedin().equals("")
                    || !profilePOJO.getTwitter().equals("") || !profilePOJO.getWechat().equals("") || !profilePOJO.getInstagram().equals(""))
                profileStrengthDao.social(1, profilePOJO.getId());
            profileDao.updateProfile(profilePOJO.getFname(), profilePOJO.getLname(), new Timestamp(sdf.parse(profilePOJO.getAvailableDate()).getTime()),
                    profilePOJO.getBirth(), profilePOJO.getNationality(), profilePOJO.getGender(), profilePOJO.getPersonalWebsite(),
                    profilePOJO.getLinkedin(), profilePOJO.getFacebook(), profilePOJO.getInstagram(), profilePOJO.getTwitter(),
                    profilePOJO.getFacebook(), profilePOJO.getId());
            for (CertificationPOJO certification : profilePOJO.getCertification()) {
                if (certification.getId().equals("")) {
                    certificationDao.addCertification(certification.getType(), new Timestamp(sdf.parse(certification.getIssueDate()).getTime()),
                            certification.getIssuingBody(),
                            profilePOJO.getId());
                    profileStrengthDao.certification(1, profilePOJO.getId());
                } else
                    certificationDao.updateCertification(certification.getType(), new Timestamp(sdf.parse(certification.getIssueDate()).getTime()),
                            certification.getIssuingBody(),
                            Long.parseLong(certification.getId()));
            }
            for (EducationPOJO education : profilePOJO.getEducation()) {
                if (education.getId().equals("")) {
                    educationDao.addEducation(education.getDegree(), education.getField(), new Timestamp(sdf.parse(education.getFromDate()).getTime()),
                            new Timestamp(sdf.parse(education.getToDate()).getTime()),
                            education.getSchool(), education.getCountry(), education.getCity(), profilePOJO.getId());
                    profileStrengthDao.education(1, profilePOJO.getId());
                } else {
                    educationDao.updateEducation(education.getDegree(), education.getField(), new Timestamp(sdf.parse(education.getFromDate()).getTime()),
                            new Timestamp(sdf.parse(education.getToDate()).getTime()),
                            education.getSchool(), education.getCountry(), education.getCity(), Long.parseLong(education.getId()));
                }
            }
            for (RelavantExPOJO relavantEx : profilePOJO.getRelavantEx()) {
                if (relavantEx.getId().equals("")) {
                    relavantExDao.addRelevant(relavantEx.getRole(), relavantEx.getCompany(), new Timestamp(sdf.parse(relavantEx.getFromDate()).getTime()),
                            new Timestamp(sdf.parse(relavantEx.getToDate()).getTime()),
                            relavantEx.getResponsibility(), profilePOJO.getId());
                    profileStrengthDao.experience(1, profilePOJO.getId());
                } else {
                    relavantExDao.addRelevant(relavantEx.getRole(), relavantEx.getCompany(), new Timestamp(sdf.parse(relavantEx.getFromDate()).getTime()),
                            new Timestamp(sdf.parse(relavantEx.getToDate()).getTime()),
                            relavantEx.getResponsibility(), Long.parseLong(relavantEx.getId()));
                }
            }


            //TODO Resume and Profile Picture need to be added

            //TODO need to be more specific
            return Response.ok("Successful").build();
        }catch (ParseException e){
            logger.error(info.getPath()+" error; Message:"+e.getMessage(), e);
            return null;
        }
    }

//    @POST
//    @Path("/addcertification")
//    public long addCertification(Certification certification){
//        long id = certificationDao.addCertification(certification.getType(),certification.getIssueDate(),
//                certification.getIssuingBody(),certification.getProfileId());
//        return id;
//    }
//
//    @POST
//    @Path("/addeducation")
//    public long addEducation(Education education){
//        long id = educationDao.addEducation(education.getDegree(),education.getField(),education.getFromDate(),
//                education.getToDate(),education.getSchool(),education.getCountry(),education.getProfileId());
//        return id;
//    }
//
//    @POST
//    @Path("/addrelevant")
//    public long addRelevant(RelavantEx relavantEx){
//        long id = relavantExDao.addRelevant(relavantEx.getRole(),relavantEx.getCompany(),relavantEx.getFromDate(),
//                relavantEx.getToDate(),relavantEx.getResponsibility(),relavantEx.getProfileId());
//        return id;
//    }

    @PUT
    @Path("/basic")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBasicInfo(@Auth AccountPrinciple principle, @Valid Profile profile){
        long id = principle.getId();
        int res = profileDao.updateBasic(profile.getFname(),profile.getLname(), profile.getAvailableDate(),profile.getBirth(),
                profile.getNationality(),profile.getGender(), id);
        if(res == 0){
            ErrorPOJO errorPOJO = new ErrorPOJO("DATA-BASE-ERRO",
                    "Database error");
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", errorPOJO);
            return Response.ok(resultPOJO).build();
        }
        else{
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("personalInfo", profile);
            profileStrengthDao.gender(1,id);
            profileStrengthDao.nationality(1,id);
            if(!profile.getBirth().equals(""))
                profileStrengthDao.birth(1, id);
            else {
                profileStrengthDao.birth(0, id);
            }
            profileStrengthDao.availability(1,id);
            return Response.ok(resultPOJO).build();
        }
    }

    @PUT
    @Path("/contact")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateContactInfo(@Auth AccountPrinciple principle,  Profile profile){
        long id = principle.getId();
        int res = profileDao.updateBasicContact(profile.getPersonalWebsite(),profile.getLinkedin(),profile.getFacebook(),profile.getInstagram(),
                profile.getTwitter(),profile.getWechat(),id);
        if(res == 0){
            ErrorPOJO errorPOJO = new ErrorPOJO("DATA-BASE-ERRO",
                    "Database error");
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", errorPOJO);
            return Response.ok(resultPOJO).build();
        }
        else{
            if(!profile.getFacebook().equals("") || !profile.getPersonalWebsite().equals("") || !profile.getLinkedin().equals("")
                    || !profile.getTwitter().equals("") || !profile.getWechat().equals("") || !profile.getInstagram().equals(""))
                profileStrengthDao.social(1,id);
            else {
                profileStrengthDao.social(0,id);
            }
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("contactInfo", profile);
            return Response.ok(resultPOJO).build();
        }
    }

    //TODO Integration
    @GET
    @Path("/certification")
    public Response getCertification(@Auth AccountPrinciple principle){
        long id = principle.getId();
        List<Certification> certificationList = certificationDao.findById(id);
        ResultPOJO resultPOJO = new ResultPOJO();
        resultPOJO.getMetadata().put("status","ok");
        //Total Number
        resultPOJO.getMetadata().put("total",certificationList.size());
        resultPOJO.getData().put("certifications",certificationList);
        return Response
                .ok(resultPOJO)
                .build();
    }

    @PUT
    @Path("/certification")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putCertification(@Context UriInfo info,@Auth AccountPrinciple principle, Certification certification){
        try {
            long id = principle.getId();
            List<Long> ids = certificationDao.getAllIdbyProfileId(id);
            ResultPOJO resultPOJO = new ResultPOJO();
            if (!ids.contains(certification.getId())) {
                //TODO error_code need to be specified
                ErrorPOJO errorPOJO = new ErrorPOJO("XXXX-XXXX-XXXX",
                        "This resource is not yours");
                resultPOJO.getMetadata().put("status", "error");
                resultPOJO.getMetadata().put("error", errorPOJO);
                return Response.ok(resultPOJO).build();
            }
            int res = certificationDao.updateCertification(certification.getType(),
                    certification.getIssueDate(), certification.getIssuingBody(), certification.getId());
            if (res == 0) {
                resultPOJO.getMetadata().put("status", "error");
                resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX",
                        "There is no such resource"));
            }

            resultPOJO.getMetadata().put("status", "ok");
            resultPOJO.getData().put("certification", certification);
            return Response
                    .ok(resultPOJO)
                    .build();
        }catch (Exception e){
            logger.error(info.getPath()+" error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO();
            resultPOJO.getMetadata().put("status", "error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX",
                    "Something error in database, maybe no certification id"));
            return Response
                    .ok(resultPOJO)
                    .build();
        }
    }

    @POST
    @Path("/certification")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postCertification(@Context UriInfo info,@Auth AccountPrinciple principle,@Valid Certification certification){
        try {
            long id = principle.getId();
            ResultPOJO resultPOJO = new ResultPOJO();
            long res = certificationDao.addCertification(certification.getType(),
                    certification.getIssueDate(), certification.getIssuingBody(), id);
            resultPOJO.getMetadata().put("status", "ok");
            certification.setId(res);
            profileStrengthDao.certification(1, id);
            resultPOJO.getData().put("certification",certification);
            return Response
                    .ok(resultPOJO)
                    .build();
        }catch (Exception e){
            logger.error(info.getPath()+" error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO();
            resultPOJO.getMetadata().put("status", "error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX",
                    "Something error in database, maybe no profile id"));
            return Response
                    .ok(resultPOJO)
                    .build();
        }
    }

    @DELETE
    @Path("/certification/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCertification(@Context UriInfo info,@Auth AccountPrinciple principle, @PathParam("id") long certification_id){
        try {
            long id = principle.getId();
            List<Long> ids = certificationDao.getAllIdbyProfileId(id);
            ResultPOJO resultPOJO = new ResultPOJO();
            if(!ids.contains(certification_id)){
                //TODO error_code need to be specified
                ErrorPOJO errorPOJO = new ErrorPOJO("XXXX-XXXX-XXXX",
                        "This resource is not yours");
                resultPOJO.getMetadata().put("status","error");
                resultPOJO.getMetadata().put("error", errorPOJO);
                return Response.ok(resultPOJO).build();
            }
            int res = certificationDao.deleteCertification(certification_id);
            resultPOJO.getMetadata().put("status", "ok");
            if(certificationDao.findById(id).size()==0)
                profileStrengthDao.certification(0, id);
            return Response
                    .ok(resultPOJO)
                    .build();
        }catch (Exception e){
            logger.error(info.getPath()+" error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO();
            resultPOJO.getMetadata().put("status", "error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX",
                    "Something error in database, maybe no id"));
            return Response
                    .ok(resultPOJO)
                    .build();
        }
    }



    @GET
    @Path("/education")
    public Response getEducation(@Auth AccountPrinciple principle){
        long id = principle.getId();
        List<Education> educationList = educationDao.findById(id);
        ResultPOJO resultPOJO = new ResultPOJO();
        resultPOJO.getMetadata().put("status","ok");
        //Total Number
        resultPOJO.getMetadata().put("total",educationList.size());
        resultPOJO.getData().put("educations",educationList);
        return Response
                .ok(resultPOJO)
                .build();
    }

    @DELETE
    @Path("/education/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEducation(@Context UriInfo info,@Auth AccountPrinciple principle, @PathParam("id") long education_id){
        try {
            long id = principle.getId();
            List<Long> ids = educationDao.getAllIdbyProfileId(id);
            ResultPOJO resultPOJO = new ResultPOJO();
            if(!ids.contains(education_id)){
                //TODO error_code need to be specified
                ErrorPOJO errorPOJO = new ErrorPOJO("XXXX-XXXX-XXXX",
                        "This resource is not yours or no such job");
                resultPOJO.getMetadata().put("status","error");
                resultPOJO.getMetadata().put("error", errorPOJO);
                return Response.ok(resultPOJO).build();
            }
            int res = educationDao.deleteEducation(education_id);
            resultPOJO.getMetadata().put("status", "ok");
            if(educationDao.findById(id).size()==0)
                profileStrengthDao.education(0, id);
            return Response
                    .ok(resultPOJO)
                    .build();
        }catch (Exception e){
            logger.error(info.getPath()+" error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO();
            resultPOJO.getMetadata().put("status", "error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX",
                    "Something error in database, maybe no id"));
            return Response
                    .ok(resultPOJO)
                    .build();
        }
    }

    @POST
    @Path("/education")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postEducation(@Context UriInfo info,@Auth AccountPrinciple principle,@Valid Education education){
        try {
            long id = principle.getId();
            ResultPOJO resultPOJO = new ResultPOJO();
            long res = educationDao.addEducation(education.getDegree(),education.getField(),education.getFromDate(),
                    education.getToDate(),education.getSchool(),education.getCountry(),education.getCity(),id);
            resultPOJO.getMetadata().put("status", "ok");
            education.setId(res);
            resultPOJO.getData().put("education",education);
            profileStrengthDao.education(1, id);
            return Response
                    .ok(resultPOJO)
                    .build();
        }catch (Exception e){
            logger.error(info.getPath()+" error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO();
            resultPOJO.getMetadata().put("status", "error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX",
                    "Something error in database, maybe no profile id"));
            return Response
                    .ok(resultPOJO)
                    .build();
        }
    }

    @PUT
    @Path("/education")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putCertification(@Context UriInfo info,@Auth AccountPrinciple principle, @Valid Education education){
        try {
            long id = principle.getId();
            List<Long> ids = educationDao.getAllIdbyProfileId(id);
            ResultPOJO resultPOJO = new ResultPOJO();
            if (!ids.contains(education.getId())) {
                //TODO error_code need to be specified
                ErrorPOJO errorPOJO = new ErrorPOJO("XXXX-XXXX-XXXX",
                        "This resource is not yours");
                resultPOJO.getMetadata().put("status", "error");
                resultPOJO.getMetadata().put("error", errorPOJO);
                return Response.ok(resultPOJO).build();
            }
            int res = educationDao.updateEducation(education.getDegree(),education.getField(),education.getFromDate(),
                    education.getToDate(),education.getSchool(),education.getCountry(),education.getCity(),education.getId());
            if (res == 0) {
                resultPOJO.getMetadata().put("status", "error");
                resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX",
                        "There is no such resource"));
            }
            resultPOJO.getMetadata().put("status", "ok");
            resultPOJO.getData().put("education", education);
            return Response
                    .ok(resultPOJO)
                    .build();
        }catch (Exception e){
            logger.error(info.getPath()+" error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO();
            resultPOJO.getMetadata().put("status", "error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX",
                    "Something error in database, maybe no certification id"));
            return Response
                    .ok(resultPOJO)
                    .build();
        }
    }

    @GET
    @Path("/relevant")
    public Response getRelevant(@Auth AccountPrinciple principle){
        long id = principle.getId();
        List<RelavantEx> relavantExList = relavantExDao.findById(id);
        ResultPOJO resultPOJO = new ResultPOJO();
        resultPOJO.getMetadata().put("status","ok");
        //Total Number
        resultPOJO.getMetadata().put("total",relavantExList.size());
        resultPOJO.getData().put("relavantExes",relavantExList);
        return Response
                .ok(resultPOJO)
                .build();
    }

    @PUT
    @Path("/relevant")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putRelevant(@Context UriInfo info,@Auth AccountPrinciple principle, @Valid RelavantEx relavantEx){
        try {
            long id = principle.getId();
            List<Long> ids = relavantExDao.getAllIdbyProfileId(id);
            ResultPOJO resultPOJO = new ResultPOJO();
            if (!ids.contains(relavantEx.getId())) {
                //TODO error_code need to be specified
                ErrorPOJO errorPOJO = new ErrorPOJO("XXXX-XXXX-XXXX",
                        "This resource is not yours");
                resultPOJO.getMetadata().put("status", "error");
                resultPOJO.getMetadata().put("error", errorPOJO);
                return Response.ok(resultPOJO).build();
            }
            int res = relavantExDao.updateRelevant(relavantEx.getRole(),relavantEx.getCompany(),relavantEx.getFromDate(),
                    relavantEx.getToDate(),relavantEx.getResponsibility(),relavantEx.getId());
            if (res == 0) {
                resultPOJO.getMetadata().put("status", "error");
                resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX",
                        "There is no such resource"));
            }
            resultPOJO.getMetadata().put("status", "ok");
            resultPOJO.getData().put("relavantEx", relavantEx);

            return Response
                    .ok(resultPOJO)
                    .build();
        }catch (Exception e){
            logger.error(info.getPath()+" error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO();
            resultPOJO.getMetadata().put("status", "error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX",
                    "Something error in database, maybe no relevantEx id"));
            return Response
                    .ok(resultPOJO)
                    .build();
        }
    }

    @POST
    @Path("/relevant")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postRelevant(@Context UriInfo info,@Auth AccountPrinciple principle,@Valid RelavantEx relavantEx){
        try {
            long id = principle.getId();
            ResultPOJO resultPOJO = new ResultPOJO();
            long res = relavantExDao.addRelevant(relavantEx.getRole(),relavantEx.getCompany(),relavantEx.getFromDate(),
                    relavantEx.getToDate(),relavantEx.getResponsibility(),id);
            resultPOJO.getMetadata().put("status", "ok");
            relavantEx.setId(res);
            profileStrengthDao.experience(1, id);
            resultPOJO.getData().put("relavantEx",relavantEx);
            return Response
                    .ok(resultPOJO)
                    .build();
        }catch (Exception e){
            logger.error(info.getPath()+" error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO();
            resultPOJO.getMetadata().put("status", "error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX",
                    "Something error in database, maybe no profile id"));
            return Response
                    .ok(resultPOJO)
                    .build();
        }
    }

    @DELETE
    @Path("/relevant/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRelevant(@Context UriInfo info,@Auth AccountPrinciple principle, @PathParam("id") long relevant_id){
        try {
            long id = principle.getId();
            List<Long> ids = relavantExDao.getAllIdbyProfileId(id);
            ResultPOJO resultPOJO = new ResultPOJO();
            if(!ids.contains(relevant_id)){
                //TODO error_code need to be specified
                ErrorPOJO errorPOJO = new ErrorPOJO("XXXX-XXXX-XXXX",
                        "This resource is not yours");
                resultPOJO.getMetadata().put("status","error");
                resultPOJO.getMetadata().put("error", errorPOJO);
                return Response.ok(resultPOJO).build();
            }
            int res = relavantExDao.deleteExperience(relevant_id);
            resultPOJO.getMetadata().put("status", "ok");
            if(relavantExDao.findById(id).size()==0)
                profileStrengthDao.experience(0, id);
            return Response
                    .ok(resultPOJO)
                    .build();
        }catch (Exception e){
            logger.error(info.getPath()+" error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO();
            resultPOJO.getMetadata().put("status", "error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX",
                    "Something error in database, maybe no id"));
            return Response
                    .ok(resultPOJO)
                    .build();
        }
    }


    @POST
    @Path("/tag")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertTag(@Context UriInfo info,
            @Auth AccountPrinciple principle,
            CandidateTagPOJO candidateTagPOJO){
        try {
            long profile_id = principle.getId();
            profileHasTagDao.deleteTag(profile_id);
            for (String name : candidateTagPOJO.getAge_group()) {
                try {
                    tagDao.insertTag("age_group:" + name);
                }catch (UnableToExecuteStatementException e){
                    continue;
                }
                finally {
                    profileHasTagDao.insertProfileTag(profile_id, tagDao.getIdByTagName("age_group:" + name));
                }
            }
            try {
                tagDao.insertTag("contract_length:" + candidateTagPOJO.getContract_length());
            }catch (Exception e){
            }finally {
                profileHasTagDao.insertProfileTag(profile_id, tagDao.getIdByTagName("contract_length:" + candidateTagPOJO.getContract_length()));

            }
            for (String name : candidateTagPOJO.getJob_type()) {
                try {
                    tagDao.insertTag("job_type:" + name);
                }catch (UnableToExecuteStatementException e){
                    continue;
                }finally {
                    profileHasTagDao.insertProfileTag(profile_id, tagDao.getIdByTagName("job_type:" + name));
                }
            }
            for (String name : candidateTagPOJO.getLocation()) {
                try {
                    tagDao.insertTag("location:" + name);
                }catch (UnableToExecuteStatementException e){
                    continue;
                }finally {
                    profileHasTagDao.insertProfileTag(profile_id, tagDao.getIdByTagName("location:" + name));
                }
            }
            for (String name : candidateTagPOJO.getSchool_type()) {
                try {
                    tagDao.insertTag("school_type:" + name);
                }catch (UnableToExecuteStatementException e){
                    continue;
                }finally {
                    profileHasTagDao.insertProfileTag(profile_id, tagDao.getIdByTagName("school_type:" + name));
                }
            }
            for (String name : candidateTagPOJO.getSubject()) {
                try {
                    tagDao.insertTag("subject:" + name);
                }catch (UnableToExecuteStatementException e){
                    continue;
                }finally {
                    profileHasTagDao.insertProfileTag(profile_id, tagDao.getIdByTagName("subject:" + name));
                }
            }
            profileStrengthDao.preference(1, profile_id);
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("tags", candidateTagPOJO);
            return Response.ok(resultPOJO).build();
        }catch(Exception e){
            logger.error(info.getPath()+" error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("DATA-BASE-ERRO", "Database error here!"));
            return Response.ok(resultPOJO).build();
        }
    }

    @GET
    @Path("/tag")
    public Response getTag(@Context UriInfo info,@Auth AccountPrinciple principle){
        try {
            long profile_id = principle.getId();
            List<Integer> tagids = profileHasTagDao.getTagId(profile_id);
            String[] names;
            List<String> locations = new ArrayList<>();
            List<String> jobs = new ArrayList<>();
            List<String> subjects = new ArrayList<>();
            List<String> ages = new ArrayList<>();
            List<String> schools = new ArrayList<>();
            String contract="";

            for (int tagid : tagids) {
                names = tagDao.getNameById(tagid).split(":");
                if(names[0].equals("location"))
                    locations.add(names[1]);
                else if(names[0].equals("job_type"))
                    jobs.add(names[1]);
                else if(names[0].equals("subject"))
                    subjects.add(names[1]);
                else if(names[0].equals("age_group"))
                    ages.add(names[1]);
                else if(names[0].equals("school_type"))
                    schools.add(names[1]);
                else
                    contract = names[1];
            }
            CandidateTagPOJO candidateTagPOJO = new CandidateTagPOJO(locations,jobs,subjects,ages,schools, contract);
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getMetadata().put("profile_id", profile_id);
            resultPOJO.getData().put("tags", candidateTagPOJO);
            return Response.ok(resultPOJO).build();
        }catch(Exception e){
            logger.error(info.getPath()+" error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("SOME-THIN-GERR", "Something error here!"));
            return Response.ok(resultPOJO).build();
        }
    }

//    @GET
//    @Path("/strength")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getStrength(@Auth AccountPrinciple principle){
//        long profile_id = principle.getId();
//        int count = 100;
//        List<String> res = new ArrayList<>();
//        Video video = videoDao.getVideoByProfileID(profile_id);
//        if(video==null) {
//            count -= 15;
//            res.add("video");
//        }
//        Resume resume = resumeDao.findLatestById(profile_id);
//        if(resume==null) {
//            count -= 15;
//            res.add("resume");
//        }
//        ProfilePic picture = pictureDao.findProfilePicById(profile_id,10);
//        if(picture==null){
//            count -= 10;
//            res.add("picture");
//        }
//        Profile profile = profileDao.findById(profile_id);
//        if(profile.getGender().equals("") || profile.getBirth().equals("") || profile.getAvailableDate()==null ||
//                profile.getNationality().equals("")){
//            count -= 10;
//            res.add("basic");
//        }
//        if(profile.getFacebook().equals("") || profile.getFacebook().equals("") || profile.getLinkedin().equals("") ||
//                profile.getFacebook().equals("") || profile.getTwitter().equals("") || profile.getWechat().equals("") ||
//                profile.getInstagram().equals("")){
//
//        }
//        List<Education> educations = educationDao.findById(profile_id);
//        if(educations.size()==0){
//            count -= 10;
//            res.add("education");
//        }
//        List<Certification> certifications = certificationDao.findById(profile_id);
//        if(certifications.size()==0){
//            count -= 10;
//            res.add("certification");
//        }
//        List<RelavantEx> relavantExes = relavantExDao.findById(profile_id);
//        if(relavantExes.size()==0){
//            count -= 10;
//            res.add("experience");
//        }
//
//    }

    @GET
    @Path("/strength")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStrength(@Auth AccountPrinciple principle) {
        long profile_id = principle.getId();
        int score=99;
        List<String> res = new ArrayList<>();
        ProfileStrength strength = profileStrengthDao.getStrengthById(profile_id);
        if(strength==null){
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("score", 0);
            resultPOJO.getData().put("details", res);
            return Response.ok(res).build();
        }
        if(strength.getAvatar()!=1) {
            score -= 10;
            res.add("avatar");
        }
        if(strength.getResume()!=1) {
            score -= 15;
            res.add("resume");
        }
        if(strength.getVideo()!=1) {
            score -= 15;
            res.add("video");
        }
        if(strength.getEducation()!=1) {
            score -= 5;
            res.add("education");
        }
        if(strength.getCertification()!=1) {
            score -= 8;
            res.add("certification");
        }
        if(strength.getExperience()!=1) {
            score -= 5;
            res.add("experience");
        }
        if(strength.getSocial()!=1) {
            score -= 5;
            res.add("social");
        }
        if(strength.getPreference()!=1) {
            score -= 15;
            res.add("preference");
        }
        if(strength.getBirth()!=1){
            score -= 5;
            res.add("birth");
        }
        if(strength.getAvailableDate()!=1){
            score -= 8;
            res.add("availableDate");
        }
        if(strength.getNationality()!=1){
            score -= 8;
            res.add("nationality");
        }
        ResultPOJO resultPOJO = new ResultPOJO("ok");
        resultPOJO.getData().put("score", score);
        resultPOJO.getData().put("details", res);
        return Response.ok(resultPOJO).build();
    }

//    @POST
//    public Response createProfile(Profile profile) throws
//            URISyntaxException {
//// store the new contact
//        int newProfileId = profileDao.createProfile(profile.getAccount_id(), profile.getFirst_name(), profile.getLast_name(), profile.getSex(),
//                profile.getPhone_one(), profile.getPhone_two(), profile.getAddress(), profile.getBusiness_type(), profile.getActive());
//        return Response.created(new URI(String.valueOf(newProfileId))).build();
//    }
//

}