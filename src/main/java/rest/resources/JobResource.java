package rest.resources;

import com.teachoversea.rest.S3Client;
import com.teachoversea.rest.TeachOverseaConfiguration;
import com.teachoversea.rest.auth.AccountPrinciple;
import com.teachoversea.rest.business.representation.POJO.*;
import com.teachoversea.rest.dao.Mappers.*;
import com.teachoversea.rest.dao.Objects.Job.Favorite;
import com.teachoversea.rest.dao.Objects.Job.FeaturedJob;
import com.teachoversea.rest.dao.Objects.Job.Job;
import com.teachoversea.rest.dao.Objects.Job.Recruiter;
import io.dropwizard.auth.Auth;
import org.apache.log4j.Logger;
import org.skife.jdbi.v2.DBI;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by lu on 2017/1/10.
 */
@Path("/job")
//@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
public class JobResource {
    private final JobDao jobDao;
    private final RecruiterDao recruiterDao;
    private final PictureDao pictureDao;
    private final AccountDao accountDao;
    private S3Client s3Client;
    private final FeaturedJobDao featuredJobDao;
    private final JobHasAccountDao jobHasAccountDao;
    private final TagDao tagDao;
    private final JobHasTagDao jobHasTagDao;
    private static Logger logger = Logger.getLogger(JobResource.class);
    private final FavoriteDao favoriteDao;
    private TeachOverseaConfiguration configuration;

    public JobResource(DBI jdbi, S3Client s3Client, TeachOverseaConfiguration configuration) {
        this.jobHasAccountDao = jdbi.onDemand(JobHasAccountDao.class);
        this.accountDao = jdbi.onDemand(AccountDao.class);
        this.jobDao = jdbi.onDemand(JobDao.class);
        this.recruiterDao = jdbi.onDemand(RecruiterDao.class);
        this.pictureDao = jdbi.onDemand(PictureDao.class);
        this.featuredJobDao = jdbi.onDemand(FeaturedJobDao.class);
        this.tagDao = jdbi.onDemand(TagDao.class);
        this.jobHasTagDao = jdbi.onDemand(JobHasTagDao.class);
        this.s3Client = s3Client;
        this.favoriteDao = jdbi.onDemand(FavoriteDao.class);
        this.configuration =configuration;
    }

//  Test Get method
    @GET
    @Path("/totaljobs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTotalJobs(){
        return Response.ok(jobDao.getTotalJobs()).build();
    }

    @GET
    @Path("/totaljobsnumber")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTotalJobsNumber(){
        return Response.ok(jobDao.getTotalNumber()).build();
    }

    @GET
    @Path("/getbyid/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") int id){
        Map<String, String> string = new HashMap<>();
        List<JobPOJO> jobs = new ArrayList<>();
        Job job = jobDao.findById(id);
        if(job == null) {
            return Response.ok("Null").build();
        }
        Recruiter recruiter = recruiterDao.findById(job.getRecruiter_id());
        RecruiterLimitPOJO recruiterLimitPOJO = new RecruiterLimitPOJO(recruiter.getId(),recruiter.getStatus(),
                recruiter.getFname(),recruiter.getLname(),recruiter.getTitle(),
                recruiter.getCompanyName(),recruiter.getCompanyAddress(),recruiter.getCompanySize(),recruiter.getDescription(),
                recruiter.getCompanyWebsite(),recruiter.getFoundTime(),pictureDao.findRecruiterLogoById(recruiter.getId(),10),
                pictureDao.findRecruiterPicById(recruiter.getId(),10),recruiter.getCreateDate(),recruiter.getActiveDate(),
                recruiter.getDisableDate());
        jobs.add(new JobPOJO(job.getId(),job.getPosition(),job.getCity(),job.getSubject(),job.getGrade(),
                job.getJobType(),job.getClassSize(),job.getContractLen(),job.getSalaryMin(),job.getSalaryMax(),job.getExperience(),
                job.getPostDate(), job.getStartDate(),job.getRecruiter_id(),job.getSchoolName(),
                job.isEligibleCandidates(),job.isNativeSpeaker(),job.getCertificates(),job.getEducation(),
                job.getAdditional(),job.getAccommodation(),job.getAirfare(),job.getWeeklyRestDay(),job.getVacation(),
                job.getAnnualLeave(),job.getBonus(),job.isLunch(),job.isChinese(),job.getAboutSchool(),job.getStatus(),
                "",null,
                pictureDao.findSchoolPicById(job.getId(),10),job.getSchoolType(),favoriteDao.numberOfFavorite(job.getId()), recruiterLimitPOJO));
        JobJSONPOJO jobJSON = new JobJSONPOJO(string, jobs);
        return Response.ok(jobJSON).build();
    }


    @GET
    @Path("/getallbyid")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllById(@Auth AccountPrinciple principle){
        long id = principle.getId();
        Map<String, String> string = new HashMap<>();
        //TODO getTotal() needs to be specified
        string.put("total", String.valueOf(jobDao.getTotalByRecruiter(id)));
        //TODO number of pages needs to be specified
        string.put("currentPage", "1");
        List<JobPOJO> jobs = new ArrayList<>();
        List<Job> job = jobDao.getTotalById(id);
        if(job.size() == 0)
            return Response.ok("Null").build();
        for(Job j : job){
            jobs.add(new JobPOJO(j.getId(),j.getPosition(),j.getCity(),j.getSubject(),j.getGrade(),
                    j.getJobType(),j.getClassSize(),j.getContractLen(),j.getSalaryMin(),j.getSalaryMax(),j.getExperience(),
                    j.getPostDate(), j.getStartDate(),j.getRecruiter_id(),j.getSchoolName(), j.isEligibleCandidates(),
                    j.isNativeSpeaker(),j.getCertificates(),j.getEducation(), j.getAdditional(),j.getAccommodation(),
                    j.getAirfare(),j.getWeeklyRestDay(),j.getVacation(), j.getAnnualLeave(),
                    j.getBonus(),j.isLunch(),j.isChinese(),j.getAboutSchool(),j.getStatus(), "",null,
                    pictureDao.findSchoolPicById(j.getId(),10),
                    j.getSchoolType(),favoriteDao.numberOfFavorite(j.getId()),
                    null));
        }
        JobJSONPOJO jobJSON = new JobJSONPOJO(string, jobs);
        return Response.ok(jobJSON).build();
    }

    @POST
    @Path("/tag")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveTags(@Auth AccountPrinciple accountPrinciple, JobTagPOJO jobTagPOJO){
        try {
            long job_id = jobTagPOJO.getJob_id();
            jobHasTagDao.deleteJobTag(job_id);
            for (String name : jobTagPOJO.getNationalities()) {
                try {
                    tagDao.insertTag("nationality:" + name);
                }catch (Exception e){
                    continue;
                }finally {
                    jobHasTagDao.insertJobTag(job_id, tagDao.getIdByTagName("nationality:" + name));
                }
            }
            for (String name : jobTagPOJO.getAge()) {
                try {
                    tagDao.insertTag("age:" + name);
                }catch (Exception e){
                    continue;
                }finally {
                    jobHasTagDao.insertJobTag(job_id, tagDao.getIdByTagName("age:" + name));
                }
            }
            for (String name : jobTagPOJO.getVisa()) {
                try {
                    tagDao.insertTag("visa:" + name);
                }catch (Exception e){
                    continue;
                }finally {
                    jobHasTagDao.insertJobTag(job_id, tagDao.getIdByTagName("visa:" + name));
                }
            }
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("tags", jobTagPOJO);
            return Response.ok(resultPOJO).build();
        }catch(Exception e){
            logger.error("JobResource error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("DATA-BASE-ERRO", "Database error here!"));
            return Response.ok(resultPOJO).build();
        }

    }

    @GET
    @Path("/tag/{job_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTag(@Auth AccountPrinciple principle, @PathParam("job_id") long job_id){
        long recruiter_id = principle.getId();
        if(!jobDao.getJobIdByRecrutierId(recruiter_id).contains(job_id)){
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("UNAU-THOR-IZED", "Sources are not yours!"));
            return Response.ok(resultPOJO).build();
        }
        else{
            List<Integer> tagids = jobHasTagDao.getTag(job_id);
            String[] names;
            List<String> nations = new ArrayList<>();
            List<String> visas = new ArrayList<>();
            List<String> ages = new ArrayList<>();

            for (int tagid : tagids) {
                names = tagDao.getNameById(tagid).split(":");
                if(names[0].equals("age"))
                    ages.add(names[1]);
                else if(names[0].equals("nationality"))
                    nations.add(names[1]);
                else
                    visas.add(names[1]);
            }
            JobTagPOJO jobTagPOJO = new JobTagPOJO(job_id,nations,visas,ages);
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("tags", jobTagPOJO);
            return Response.ok(resultPOJO).build();
        }
    }


    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJob(@Auth AccountPrinciple principle, @QueryParam("position") String position,
                           @QueryParam("subject") String subject, @QueryParam("city") String city,
                           @QueryParam("grade") String grade){
        long account_id =  accountDao.getAccountByProfileId(principle.getId()).getId();
        List<Long> jobIds = jobHasAccountDao.getJobs(account_id);
        Map<String, String> string = new HashMap<>();
        string.put("total", String.valueOf(jobDao.getTotalNumber()));
        string.put("currentPage", "1");

        List<JobPOJO> jobPOJOS = new ArrayList<>();
        List<Job> jobs = jobDao.getAllJob("%"+((position.equals(""))? "%":position)+"%",(city.equals(""))? "%": city,
                (subject.equals(""))? "%": subject, (grade.equals(""))? "%": grade);
        for(Job job : jobs){
            Recruiter recruiter = recruiterDao.findById(job.getRecruiter_id());
            RecruiterLimitPOJO recruiterLimitPOJO = new RecruiterLimitPOJO(recruiter.getId(),recruiter.getStatus(),
                    recruiter.getFname(),recruiter.getLname(),recruiter.getTitle(),
                    recruiter.getCompanyName(),recruiter.getCompanyAddress(),recruiter.getCompanySize(),recruiter.getDescription(),
                    recruiter.getCompanyWebsite(),recruiter.getFoundTime(),pictureDao.findRecruiterLogoById(recruiter.getId(),10),
                    pictureDao.findRecruiterPicById(recruiter.getId(),10),recruiter.getCreateDate(),recruiter.getActiveDate(),
                    recruiter.getDisableDate());
            JobPOJO jobPOJO;
            if(jobIds.contains(job.getId())) {
                jobPOJO = new JobPOJO(job.getId(), job.getPosition(), job.getCity(), job.getSubject(), job.getGrade(),
                        job.getJobType(), job.getClassSize(), job.getContractLen(), job.getSalaryMin(),job.getSalaryMax(), job.getExperience(),
                        job.getPostDate(), job.getStartDate(), job.getRecruiter_id(), job.getSchoolName(),
                        job.isEligibleCandidates(), job.isNativeSpeaker(), job.getCertificates(), job.getEducation(),
                        job.getAdditional(), job.getAccommodation(), job.getAirfare(), job.getWeeklyRestDay(), job.getVacation(),
                        job.getAnnualLeave(), job.getBonus(), job.isLunch(), job.isChinese(), job.getAboutSchool(), job.getStatus(),
                        jobHasAccountDao.getJHAbyJob_id(job.getId(), account_id).getStatus(),jobHasAccountDao.getJHAbyJob_id(job.getId(),account_id).getApplyDate(),
                        pictureDao.findSchoolPicById(job.getId(), 10), job.getSchoolType(),favoriteDao.numberOfFavorite(job.getId()),
                        recruiterLimitPOJO);
            }
            else {
                jobPOJO = new JobPOJO(job.getId(), job.getPosition(), job.getCity(), job.getSubject(), job.getGrade(),
                        job.getJobType(), job.getClassSize(), job.getContractLen(),job.getSalaryMin(),job.getSalaryMax(),job.getExperience(),
                        job.getPostDate(), job.getStartDate(), job.getRecruiter_id(), job.getSchoolName(),
                        job.isEligibleCandidates(), job.isNativeSpeaker(), job.getCertificates(), job.getEducation(),
                        job.getAdditional(), job.getAccommodation(), job.getAirfare(), job.getWeeklyRestDay(), job.getVacation(),
                        job.getAnnualLeave(), job.getBonus(), job.isLunch(), job.isChinese(), job.getAboutSchool(), job.getStatus(),
                        "",null,
                        pictureDao.findSchoolPicById(job.getId(), 10), job.getSchoolType(),favoriteDao.numberOfFavorite(job.getId()),
                        recruiterLimitPOJO);
            }
            jobPOJOS.add(jobPOJO);
        }
        JobJSONPOJO jobJSON = new JobJSONPOJO(string, jobPOJOS);
//        ResultPOJO resultPOJO = new ResultPOJO("ok");
//        resultPOJO.getMetadata().put("total", String.valueOf(jobDao.getTotalNumber()));
//        resultPOJO.getMetadata().put("currentPage", "1");
//        resultPOJO.getData().put("jobs", jobPOJOS);

        return  Response.ok(jobJSON).build();
    }


    @GET
    @Path("/guest")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJobGuest(@QueryParam("position") String position,
                                @QueryParam("subject") String subject, @QueryParam("city") String city,
                                @QueryParam("grade") String grade){
        Map<String, String> string = new HashMap<>();
        string.put("total", String.valueOf(jobDao.getTotalNumber()));
        string.put("currentPage", "1");
        List<JobPOJO> jobPOJOS = new ArrayList<>();
        List<Job> jobs = jobDao.getAllJob("%"+((position.equals(""))? "%":position)+"%",(city.equals(""))? "%": city,
                (subject.equals(""))? "%": subject, (grade.equals(""))? "%": grade);
        for(Job job : jobs){
            Recruiter recruiter = recruiterDao.findById(job.getRecruiter_id());
            RecruiterLimitPOJO recruiterLimitPOJO = new RecruiterLimitPOJO(recruiter.getId(),recruiter.getStatus(),
                    recruiter.getFname(),recruiter.getLname(),recruiter.getTitle(),
                    recruiter.getCompanyName(),recruiter.getCompanyAddress(),recruiter.getCompanySize(),recruiter.getDescription(),
                    recruiter.getCompanyWebsite(),recruiter.getFoundTime(),pictureDao.findRecruiterLogoById(recruiter.getId(),10),
                    pictureDao.findRecruiterPicById(recruiter.getId(),10),recruiter.getCreateDate(),recruiter.getActiveDate(),
                    recruiter.getDisableDate());
            JobPOJO jobPOJO = new JobPOJO(job.getId(),job.getPosition(),job.getCity(),job.getSubject(),job.getGrade(),
                    job.getJobType(),job.getClassSize(),job.getContractLen(),job.getSalaryMin(),job.getSalaryMax(),job.getExperience(),
                    job.getPostDate(), job.getStartDate(),job.getRecruiter_id(),job.getSchoolName(),
                    job.isEligibleCandidates(),job.isNativeSpeaker(),job.getCertificates(),job.getEducation(),
                    job.getAdditional(),job.getAccommodation(),job.getAirfare(),job.getWeeklyRestDay(),job.getVacation(),
                    job.getAnnualLeave(),job.getBonus(),job.isLunch(),job.isChinese(),job.getAboutSchool(),job.getStatus(),
                    "",null,
                    pictureDao.findSchoolPicById(job.getId(),10), job.getSchoolType(),favoriteDao.numberOfFavorite(job.getId()),
                    recruiterLimitPOJO);
            jobPOJOS.add(jobPOJO);
        }
        JobJSONPOJO jobJSON = new JobJSONPOJO(string, jobPOJOS);
        return  Response.ok(jobJSON).build();
    }

    // Get JOB by ID with auth

    /**
     *
     * @param info
     * @param principle
     * @param id
     * @return
     */
    @GET
    @Path("/{job_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDraft(@Context UriInfo info,@Auth AccountPrinciple principle, @PathParam("job_id") long id ){
        try {
            long recruiterId = principle.getId();
            if(!jobDao.getJobIdByRecrutierId(recruiterId).contains(id)){
                ResultPOJO resultPOJO = new ResultPOJO("error");
                resultPOJO.getMetadata().put("error", new ErrorPOJO("UNAU-THOR-IZED", "This resource is not yours"));
                return Response.ok(resultPOJO).build();
            }
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("jobs", jobDao.findById(id));
            return Response.ok(resultPOJO).build();

        }catch (Exception e){
            logger.error(info.getPath()+", Message: "+ e.getMessage(),e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX", "Something error here."));
            return Response.ok(resultPOJO).build();
        }
    }


    // Draft Jobs

    /**
     * Insert draft job, can be modified later
     * @param info
     * @param principle
     * @param job position must be inputted
     * @return
     */
    @POST
    @Path("/draft")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response draftJob(@Context UriInfo info,@Auth AccountPrinciple principle, Job job){
        try {
            // Check position cannot be empty or null
            if(job.getPosition().equals("") || job.getPosition() == null){
                ResultPOJO resultPOJO = new ResultPOJO("error");
                resultPOJO.getMetadata().put("error", new ErrorPOJO("NOTN-ULLV-ALID", "Come on! You have to input position at least."));
                return Response.ok(resultPOJO).build();
            }
            long recruiterId = principle.getId();
            long jobId = jobDao.insertJob(job.getPosition(), job.getCity(), job.getSubject(), job.getGrade(), job.getJobType(), job.getClassSize(),
                    job.getContractLen(), job.getSalaryMin(), job.getSalaryMax(), job.getSalaryMessage(), job.getExperience(), new Timestamp((new Date()).getTime()),
                    job.getStartDate(), job.getSchoolName(), job.isEligibleCandidates(), job.isNativeSpeaker(), job.getCertificates(), job.getEducation(), job.getAdditional(),
                    job.getAccommodation(), job.getAirfare(), job.getWeeklyRestDay(), job.getVacation(), job.getAnnualLeave(),
                    job.getBonus(), job.isLunch(), job.isChinese(), job.getAboutSchool(), job.getSchoolType(), recruiterId);
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            job.setId(jobId);
            resultPOJO.getData().put("job", jobDao.findById(jobId));
            return Response.ok(resultPOJO).build();
        }catch (Exception e){
            logger.error(info.getPath()+", Message: "+ e.getMessage(),e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX", "Something error here."));
            return Response.ok(resultPOJO).build();
        }
    }

    /**
     * update draft, update job status to "Draft"
     * @param info
     * @param principle
     * @param job position must be inputted
     * @return
     */
    @PUT
    @Path("/draft/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDraft(@Context UriInfo info,@Auth AccountPrinciple principle,@PathParam("id") long id, Job job){
        try {
            // Check position cannot be empty or null
            if(job.getPosition().equals("") || job.getPosition() == null){
                ResultPOJO resultPOJO = new ResultPOJO("error");
                resultPOJO.getMetadata().put("error", new ErrorPOJO("NOTN-ULLV-ALID", "Come on! You have to input position at least."));
                return Response.ok(resultPOJO).build();
            }
            else {
                long recruiterId = principle.getId();
                if(!jobDao.getJobIdByRecrutierId(recruiterId).contains(id)){
                    ResultPOJO resultPOJO = new ResultPOJO("error");
                    resultPOJO.getMetadata().put("error", new ErrorPOJO("UNAU-THOR-IZED", "This resource is not yours"));
                    return Response.ok(resultPOJO).build();
                }
                else {
                    jobDao.updateJob(job.getPosition(), job.getCity(), job.getSubject(), job.getGrade(), job.getJobType(), job.getClassSize(),
                            job.getContractLen(), job.getSalaryMin(), job.getSalaryMax(), job.getSalaryMessage(), job.getExperience(), job.getStartDate(),
                            job.getSchoolName(), job.isEligibleCandidates(), job.isNativeSpeaker(), job.getCertificates(), job.getEducation(), job.getAdditional(),
                            job.getAccommodation(), job.getAirfare(), job.getWeeklyRestDay(), job.getVacation(), job.getAnnualLeave(),
                            job.getBonus(), job.isLunch(), job.isChinese(), job.getAboutSchool(), job.getSchoolType(), recruiterId);
                    jobDao.updateStatus("Draft", id);
                    ResultPOJO resultPOJO = new ResultPOJO("ok");
                    resultPOJO.getData().put("job", jobDao.findById(id));
                    return Response.ok(resultPOJO).build();
                }
            }
        }catch (Exception e){
            logger.error(info.getPath()+", Message: "+ e.getMessage(),e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX", "Something error here."));
            return Response.ok(resultPOJO).build();
        }
    }

    /**
     * Directly delete draft from database
     * @param info
     * @param principle
     * @param id
     * @return
     */
    @DELETE
    @Path("/draft/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDraft(@Context UriInfo info,@Auth AccountPrinciple principle, @PathParam("id") long id){
        try {
            // Check authorization
            long recruiterId = principle.getId();
            if(!jobDao.getJobIdByRecrutierId(recruiterId).contains(id)){
                ResultPOJO resultPOJO = new ResultPOJO("error");
                resultPOJO.getMetadata().put("error", new ErrorPOJO("UNAU-THOR-IZED", "This resource is not yours"));
                return Response.ok(resultPOJO).build();
            }
            else {
                jobDao.deleteDraft(id);
                ResultPOJO resultPOJO = new ResultPOJO("ok");
                return Response.ok(resultPOJO).build();
            }
        }catch (Exception e){
            logger.error(info.getPath()+", Message: "+ e.getMessage(),e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX", "Something error here."));
            return Response.ok(resultPOJO).build();
        }
    }

    /**
     * Get all draft jobs by recruiter id as list
     * @param info
     * @param principle
     * @return
     */
    @GET
    @Path("/draft")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDraft(@Context UriInfo info,@Auth AccountPrinciple principle){
        try {
            long recruiterId = principle.getId();
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("jobs", jobDao.getAllByIdAndStatus(recruiterId, "Draft"));
            return Response.ok(resultPOJO).build();

        }catch (Exception e){
            logger.error(info.getPath()+", Message: "+ e.getMessage(),e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX", "Something error here."));
            return Response.ok(resultPOJO).build();
        }
    }


    // Published Jobs

    /**
     * insert job as published one directly
     * @param info
     * @param principle
     * @param job do validation about job table
     * @return
     */
    @POST
    @Path("/publish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response publishJob(@Context UriInfo info,@Auth AccountPrinciple principle, @Valid Job job){
        try {
            // Check authorization
            if(job.getPosition().equals("") || job.getPosition() == null){
                ResultPOJO resultPOJO = new ResultPOJO("error");
                resultPOJO.getMetadata().put("error", new ErrorPOJO("NOTN-ULLV-ALID", "Come on! You have to input position at least."));
                return Response.ok(resultPOJO).build();
            }
            long recruiterId = principle.getId();
            long jobId = jobDao.insertJob(job.getPosition(), job.getCity(), job.getSubject(), job.getGrade(), job.getJobType(), job.getClassSize(),
                    job.getContractLen(), job.getSalaryMin(), job.getSalaryMax(), job.getSalaryMessage(), job.getExperience(),
                    new Timestamp((new Date()).getTime()),job.getStartDate(),
                    job.getSchoolName(), job.isEligibleCandidates(), job.isNativeSpeaker(), job.getCertificates(), job.getEducation(), job.getAdditional(),
                    job.getAccommodation(), job.getAirfare(), job.getWeeklyRestDay(), job.getVacation(), job.getAnnualLeave(),
                    job.getBonus(), job.isLunch(), job.isChinese(), job.getAboutSchool(), job.getSchoolType(), recruiterId);
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            job.setId(jobId);
            jobDao.updateStatus("Published", jobId);
            resultPOJO.getData().put("job", jobDao.findById(jobId));
            return Response.ok(resultPOJO).build();
        }catch (Exception e){
            logger.error(info.getPath()+", Message: "+ e.getMessage(),e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX", "Something error here."));
            return Response.ok(resultPOJO).build();
        }
    }

    /**
     * update published job, save as "Published"
     * @param info
     * @param principle
     * @param job
     * @return
     */
    @PUT
    @Path("/publish/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePublished(@Context UriInfo info,@Auth AccountPrinciple principle, @PathParam("id") long id, @Valid Job job){
        try {
            // Check authorization
            if(job.getPosition().equals("") || job.getPosition() == null){
                ResultPOJO resultPOJO = new ResultPOJO("error");
                resultPOJO.getMetadata().put("error", new ErrorPOJO("NOTN-ULLV-ALID", "Come on! You have to input position at least."));
                return Response.ok(resultPOJO).build();
            }
            else {
                long recruiterId = principle.getId();
                if(!jobDao.getJobIdByRecrutierId(recruiterId).contains(id)){
                    ResultPOJO resultPOJO = new ResultPOJO("error");
                    resultPOJO.getMetadata().put("error", new ErrorPOJO("UNAU-THOR-IZED", "This resource is not yours"));
                    return Response.ok(resultPOJO).build();
                }
                else {
                    jobDao.updateJob(job.getPosition(), job.getCity(), job.getSubject(), job.getGrade(), job.getJobType(), job.getClassSize(),
                            job.getContractLen(), job.getSalaryMin(), job.getSalaryMax(), job.getSalaryMessage(), job.getExperience(), job.getStartDate(),
                            job.getSchoolName(), job.isEligibleCandidates(), job.isNativeSpeaker(), job.getCertificates(), job.getEducation(), job.getAdditional(),
                            job.getAccommodation(), job.getAirfare(), job.getWeeklyRestDay(), job.getVacation(), job.getAnnualLeave(),
                            job.getBonus(), job.isLunch(), job.isChinese(), job.getAboutSchool(), job.getSchoolType(), recruiterId);
                    jobDao.updateStatus("Published", id);
                    ResultPOJO resultPOJO = new ResultPOJO("ok");
                    resultPOJO.getData().put("job", jobDao.findById(id));
                    return Response.ok(resultPOJO).build();
                }
            }
        }catch (Exception e){
            logger.error(info.getPath()+", Message: "+ e.getMessage(),e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX", "Something error here."));
            return Response.ok(resultPOJO).build();
        }
    }

    /**
     * Get all published jobs
     * @param info
     * @param principle
     * @return
     */
    @GET
    @Path("/publish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPublished(@Context UriInfo info,@Auth AccountPrinciple principle){
        try {
            long recruiterId = principle.getId();
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            List<Map<String, Object>> res = new ArrayList<>();
            for(Job job : jobDao.getAllByIdAndStatus(recruiterId,"Published")){
                Map<String ,Object> maps = new HashMap<>();
                maps.put("details", job);
                List<Integer> tagIds = jobHasTagDao.getTag(job.getId());
                List<String> tags = new ArrayList<>();
                for(int tagId: tagIds){
                    tags.add(tagDao.getNameById(tagId));
                }
                maps.put("tags", tags);
                maps.put("appliedNum", jobHasAccountDao.getAccountsObject(job.getId()).size());
                //TODO recommend algorithm
                maps.put("recommendNum", 10);
                //TODO purchased method
                maps.put("purchasedNum", 10);
                res.add(maps);
            }
            resultPOJO.getData().put("jobs", res);

            return Response.ok(resultPOJO).build();
        }catch (Exception e){
            logger.error(info.getPath()+", Message: "+ e.getMessage(),e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX", "Something error here."));
            return Response.ok(resultPOJO).build();
        }
    }

//    @GET
//    @Path("/publish/{job_id}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getPublished(@Context UriInfo info,@Auth AccountPrinciple principle, @PathParam("job_id") long job_id){
//        try {
//            long recruiterId = principle.getId();
//            Job job = jobDao.findById(job_id);
//            Map<String, Object> maps = new HashMap<>();
//            if(job.getStatus().equals("Published")) {
//                maps.put("details", job);
//                ResultPOJO resultPOJO = new ResultPOJO("ok");
//                List<Integer> tagIds = jobHasTagDao.getTag(job.getId());
//                List<String> tags = new ArrayList<>();
//                for (int tagId : tagIds) {
//                    tags.add(tagDao.getNameById(tagId));
//                }
//                maps.put("tags", tags);
//                maps.put("appliedNum", jobHasAccountDao.getAccountsObject(job.getId()).size());
//                //TODO recommend algorithm
//                maps.put("recommendNum", 10);
//                //TODO purchased method
//                maps.put("purchasedNum", 10);
//                resultPOJO.getData().put("jobs", maps);
//                return Response.ok(resultPOJO).build();
//            }
//            else {
//                ResultPOJO resultPOJO = new ResultPOJO("error");
//                resultPOJO.getMetadata().put("error", new ErrorPOJO("NOPU-BLIS-HEDJ", "This job is not published"));
//                return Response.ok(resultPOJO).build();
//            }
//
//        }catch (Exception e){
//            logger.error(info.getPath()+", Message: "+ e.getMessage(),e);
//            ResultPOJO resultPOJO = new ResultPOJO("error");
//            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX", "Something error here."));
//            return Response.ok(resultPOJO).build();
//        }
//    }

    //Closed Jobs

    /**
     * Get all closed jobs
     * @param info
     * @param principle
     * @return
     */
    @GET
    @Path("/close")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClosed(@Context UriInfo info,@Auth AccountPrinciple principle){
        try {
            long recruiterId = principle.getId();
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("jobs", jobDao.getAllByIdAndStatus(recruiterId,"Closed"));
            return Response.ok(resultPOJO).build();
        }catch (Exception e){
            logger.error(info.getPath()+", Message: "+ e.getMessage(),e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX", "Something error here."));
            return Response.ok(resultPOJO).build();
        }
    }

    /**
     * Inactive published job
     * @param info
     * @param principle
     * @param id
     * @return
     */
    @PUT
    @Path("/close/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response closePublish(@Context UriInfo info,@Auth AccountPrinciple principle, @PathParam("id") long id){
        try {
            // Check authorization
            long recruiterId = principle.getId();
            if(!jobDao.getJobIdByRecrutierId(recruiterId).contains(id)){
                ResultPOJO resultPOJO = new ResultPOJO("error");
                resultPOJO.getMetadata().put("error", new ErrorPOJO("UNAU-THOR-IZED", "This resource is not yours"));
                return Response.ok(resultPOJO).build();
            }
            else {
                jobDao.updateStatus("Closed", id);
                ResultPOJO resultPOJO = new ResultPOJO("ok");
                return Response.ok(resultPOJO).build();
            }
        }catch (Exception e){
            logger.error(info.getPath()+", Message: "+ e.getMessage(),e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("XXXX-XXXX-XXXX", "Something error here."));
            return Response.ok(resultPOJO).build();
        }
    }



//    @PUT
//    @Path("/update")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Produces(MediaType.TEXT_PLAIN)
//    public long updateJob(@Auth AccountPrinciple principle,FormDataMultiPart multiPart) throws IOException, ParseException {
//        String position = multiPart.getField("position").getValue();
//        String city = multiPart.getField("city").getValue();
//        String subject = multiPart.getField("subject").getValue();
//        String grade = multiPart.getField("grade").getValue();
//        String jobType = multiPart.getField("jobType").getValue();
//        int classSize = multiPart.getField("classSize").getValueAs(Integer.class);
//        int contractLen = multiPart.getField("contractLen").getValueAs(Integer.class);
//        int salaryMin = 0, salaryMax = 0;
//        try {
//             salaryMin = multiPart.getField("salaryMin").getValueAs(Integer.class);
//        }catch (Exception e){
//
//        }
//        try {
//            salaryMax = multiPart.getField("salaryMax").getValueAs(Integer.class);
//        }catch (Exception e){
//
//        }
//        int experience = multiPart.getField("experience").getValueAs(Integer.class);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = sdf.parse(multiPart.getField("startDate").getValue());
//        Timestamp startDate = new Timestamp(date.getTime());
//        String schoolName = multiPart.getField("schoolName").getValue();
//        boolean eligibleCandidates = multiPart.getField("eligibleCandidates").getValueAs(Boolean.class);
//        boolean nativeSpeaker = multiPart.getField("nativeSpeaker").getValueAs(Boolean.class);
//        String certificates = multiPart.getField("certificates").getValue();
//        String education = multiPart.getField("education").getValue();
//        String additional = multiPart.getField("additional").getValue();
//        String accommodation = multiPart.getField("accommodation").getValue();
//        String airfare = multiPart.getField("airfare").getValue();
//        String weeklyRestDay = multiPart.getField("weeklyRestDay").getValue();
//        String vacation = multiPart.getField("vacation").getValue();
//        String annualLeave = multiPart.getField("annualLeave").getValue();
//        String bonus = multiPart.getField("bonus").getValue();
//        String aboutSchool = multiPart.getField("aboutSchool").getValue();
//        boolean lunch = multiPart.getField("lunch").getValueAs(Boolean.class);
//        boolean chinese = multiPart.getField("chinese").getValueAs(Boolean.class);
//
//        //TODO Get Job ID by Hidden Type
//        long id = multiPart.getField("id").getValueAs(Long.class);
//        String schoolType = multiPart.getField("schoolType").getValue();
//
//
//        int update= jobDao.updateJob(position,city,subject,grade,jobType,classSize,contractLen,salaryMin,salaryMax,experience,
//                startDate,schoolName,eligibleCandidates,nativeSpeaker,certificates,education,
//                additional,accommodation,airfare,weeklyRestDay,vacation,annualLeave,bonus,lunch,chinese,aboutSchool,
//                schoolType,id);
//        if(update == 0)
//            return 0;
//
//        List<FormDataBodyPart> file = multiPart.getFields("schoolPic");
//        if(!file.get(0).getFormDataContentDisposition().getFileName().equals("")){
//            //Update school pictures' status
//            pictureDao.updateSchoolPicStatus(20, id);
//            for(FormDataBodyPart f: file){
//                InputStream uploadedInputStream = f.getValueAs(InputStream.class);
//                UUID uuid = UUID.randomUUID();
//                writeToFile(uploadedInputStream, uuid+new String(f.getFormDataContentDisposition().getFileName().getBytes("iso-8859-1"), "UTF-8"),
//                        id, 10);
//            }
//        }
//        jobDao.updateStatus("Draft", id);
//        return  1;
//    }
//
//    @POST
//    @Path("/insert")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Produces(MediaType.TEXT_PLAIN)
//    public long insertJob(@Auth AccountPrinciple principle,FormDataMultiPart multiPart) throws IOException, ParseException {
//        String position = multiPart.getField("position").getValue();
//        String city = multiPart.getField("city").getValue();
//        String subject = multiPart.getField("subject").getValue();
//        String grade = multiPart.getField("grade").getValue();
//        String jobType = multiPart.getField("jobType").getValue();
//        int classSize = multiPart.getField("classSize").getValueAs(Integer.class);
//        int contractLen = multiPart.getField("contractLen").getValueAs(Integer.class);
//        int salaryMin = multiPart.getField("salaryMin").getValueAs(Integer.class);
//        String test = multiPart.getField("salaryMax").getValue();
//        int salaryMax = multiPart.getField("salaryMax").getValue().equals("")? salaryMin :
//                multiPart.getField("salaryMax").getValueAs(Integer.class);
//        int experience = multiPart.getField("experience").getValueAs(Integer.class);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = sdf.parse(multiPart.getField("startDate").getValue());
//        Timestamp startDate = new Timestamp(date.getTime());
//        String schoolName = multiPart.getField("schoolName").getValue();
//        boolean eligibleCandidates = multiPart.getField("eligibleCandidates").getValueAs(Boolean.class);
//        boolean nativeSpeaker = multiPart.getField("nativeSpeaker").getValueAs(Boolean.class);
//        String certificates = multiPart.getField("certificates").getValue();
//        String education = multiPart.getField("education").getValue();
//        String additional = multiPart.getField("additional").getValue();
//        String accommodation = multiPart.getField("accommodation").getValue();
//        String airfare = multiPart.getField("airfare").getValue();
//        String weeklyRestDay = multiPart.getField("weeklyRestDay").getValue();
//        String vacation = multiPart.getField("vacation").getValue();
//        String annualLeave = multiPart.getField("annualLeave").getValue();
//        String bonus = multiPart.getField("bonus").getValue();
//        String aboutSchool = multiPart.getField("aboutSchool").getValue();
//        String salaryMessage = multiPart.getField("salaryMessage").getValue();
//        boolean lunch = multiPart.getField("lunch").getValueAs(Boolean.class);
//        boolean chinese = multiPart.getField("chinese").getValueAs(Boolean.class);
//        long recruiter_id = principle.getId();
//        String schoolType = multiPart.getField("schoolType").getValue();
//
//
//        long key = jobDao.insertJob(position,city,subject,grade,jobType,classSize,contractLen,salaryMin,salaryMax,experience,
//                startDate,schoolName,eligibleCandidates,nativeSpeaker,certificates,education,
//                additional,accommodation,airfare,weeklyRestDay,vacation,annualLeave,bonus,lunch,chinese,aboutSchool,
//                schoolType,recruiter_id);
//
//        List<FormDataBodyPart> file = multiPart.getFields("schoolPic");
//        if(!file.get(0).getFormDataContentDisposition().getFileName().equals("")){
//            for(FormDataBodyPart f: file){
//                String filename = new String(f.getFormDataContentDisposition().getFileName().getBytes("iso-8859-1"), "UTF-8");
//
//                InputStream uploadedInputStream = f.getValueAs(InputStream.class);
//                UUID uuid = UUID.randomUUID();
//                writeToFile(uploadedInputStream, uuid+filename,
//                        key, 10);
//            }
//        }
//        else{
//            pictureDao.insertSchoolPic(configuration.getJobDefaultPic(),10, key);
//        }
//        jobDao.updateStatus("Draft", key);
//        return  key;
//    }


    @PUT
    @Path("/publish/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public int publish(@PathParam("id") long id){
        int success = jobDao.updateStatus("Published", id);
        return success;
    }

    @PUT
    @Path("/close/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public int close(@PathParam("id") long id){
        int success = jobDao.updateStatus("Closed", id);
        return success;
    }

    private void writeToFile(InputStream uploadedInputStream, String filename, long job_id, int status) throws IOException {
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
        String folder = "SchoolPictures";
        s3Client.uploadFileToPublic(folder, filename, file);
        pictureDao.insertSchoolPic(folder+"/"+filename,status, job_id);
        file.delete();
    }

    @GET
    @Path("/featured")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFeatured(){
        if(featuredJobDao.find().size() == 0){
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error",new ErrorPOJO("NOSU-CHFE-ATUR","No featured job right now"));
            return Response.ok(resultPOJO).build();
        }
        else{
            List<FeaturedJob> featuredJobs = featuredJobDao.find();
            List<Job> jobs = new ArrayList<>();
            for(FeaturedJob job : featuredJobs){
                jobs.add(jobDao.findById(job.getJob_id()));
            }
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            resultPOJO.getData().put("featured", jobs);
            return Response.ok(resultPOJO).build();
        }
    }

    @GET
    @Path("/relevant/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRelevant(@PathParam("id") long id){
        ResultPOJO resultPOJO = new ResultPOJO("ok");
        resultPOJO.getData().put("jobs",jobDao.getRandomJobs(3));
        return Response.ok(resultPOJO).build();
    }

    @GET
    @Path("/favorite/total/{job_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFavorite(@PathParam("job_id") long job_id){
        ResultPOJO resultPOJO = new ResultPOJO("ok");
        int number = favoriteDao.numberOfFavorite(job_id);
        resultPOJO.getData().put("number", number);
        return  Response.ok(resultPOJO).build();
    }

    @GET
    @Path("/favorite/{job_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFavoriteJob(@Auth AccountPrinciple principle, @PathParam("job_id") long job_id){
        long profile_id = principle.getId();
        boolean res = jobHasAccountDao.getJobs(accountDao.getAccountByProfileId(profile_id).getId()).contains(jobDao.findById(job_id));
        ResultPOJO resultPOJO = new ResultPOJO("ok");
        resultPOJO.getData().put("saved", res);
        return  Response.ok(resultPOJO).build();
    }

    @DELETE
    @Path("/favorite/{job_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFavorite(@PathParam("job_id") long job_id, @Auth AccountPrinciple principle){
        long account_id = accountDao.getAccountByProfileId(principle.getId()).getId();
        favoriteDao.deleteFavorite(account_id, job_id);
        ResultPOJO resultPOJO = new ResultPOJO("ok");
        return  Response.ok(resultPOJO).build();
    }

    @GET
    @Path("/favorite")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllFavorite(@Auth AccountPrinciple principle){
        long account_id = accountDao.getAccountByProfileId(principle.getId()).getId();
        List<Favorite> favorites = favoriteDao.getAllJobs(account_id);
        Map<String, Object> map;
        List<Map<String, Object>> res = new ArrayList<>();
        for(Favorite favorite : favorites){
            map = new HashMap<>();
            map.put("jobName",jobDao.findById(favorite.getJob_id()).getPosition());
            map.put("jobId", favorite.getJob_id());
            map.put("date", favorite.getFavoredDate());
            res.add(map);
        }
        ResultPOJO resultPOJO = new ResultPOJO("ok");
        resultPOJO.getData().put("favored_jobs", res);
        return  Response.ok(resultPOJO).build();
    }

    @POST
    @Path("/favorite/{job_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response postFavorite(@Auth AccountPrinciple principle, @PathParam("job_id") long job_id){
        try {
            long account_id = accountDao.getAccountByProfileId(principle.getId()).getId();
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            favoriteDao.insertFavorite(account_id, job_id);
            return Response.ok(resultPOJO).build();
        }catch (Exception e){
            logger.error("JobResource error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("DATA-BASE-ERRO", "You have" +
                    " already liked this job"));
            return Response.ok(resultPOJO).build();
        }
    }
}
