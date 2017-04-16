package rest.resources;

import com.teachoversea.rest.auth.AccountPrinciple;
import com.teachoversea.rest.business.representation.POJO.*;
import com.teachoversea.rest.dao.Mappers.*;
import com.teachoversea.rest.dao.Objects.Job.Account;
import com.teachoversea.rest.dao.Objects.Job.Job;
import com.teachoversea.rest.dao.Objects.Job.JobHasAccount;
import com.teachoversea.rest.dao.Objects.Job.Profile;
import com.teachoversea.rest.util.MailUtil;
import io.dropwizard.auth.Auth;
import org.apache.log4j.Logger;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.sqlobject.Transaction;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lu on 2017/1/13.
 */
@Path("/applystatus")
public class JobHasAccountResource {
    private static Logger logger = Logger.getLogger(JobHasAccountResource.class);
    private final JobHasAccountDao jobHasAccountDao;
    private final AccountDao accountDao;
    private final ProfileDao profileDao;
    private final PictureDao pictureDao;
    private final EducationDao educationDao;
    private final CertificationDao certificationDao;
    private final RelavantExDao relavantExDao;
    private final JobDao jobDao;
    private final RecruiterDao recruiterDao;

    public JobHasAccountResource(DBI jdbi){
        this.jobHasAccountDao = jdbi.onDemand(JobHasAccountDao.class);
        this.recruiterDao = jdbi.onDemand(RecruiterDao.class);
        this.jobDao = jdbi.onDemand(JobDao.class);
        this.accountDao = jdbi.onDemand(AccountDao.class);
        this.profileDao = jdbi.onDemand(ProfileDao.class);
        this.pictureDao = jdbi.onDemand(PictureDao.class);
        this.educationDao = jdbi.onDemand(EducationDao.class);
        this.certificationDao = jdbi.onDemand(CertificationDao.class);
        this.relavantExDao = jdbi.onDemand(RelavantExDao.class);
    }

    @GET
    @Path("/getaccounts/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@Auth AccountPrinciple principle,@PathParam("id") long id) {
        List<String> candidates = jobHasAccountDao.getAccounts(id);
        List<ProfileShortPOJO> res = new ArrayList<>();
        for(String account_id :candidates){
            long profileId = accountDao.findProfileIdById(Long.parseLong(account_id));
            Profile profile = profileDao.findById(profileId);
            ProfileShortPOJO profileShortPOJO = new ProfileShortPOJO(profile.getId(),pictureDao.findProfilePicById(profileId,10),
                    String.valueOf(profile.getFname().toUpperCase().charAt(0)),String.valueOf(profile.getLname().toUpperCase().charAt(0)),profile.getAvailableDate(),profile.getBirth(),
                    profile.getNationality(),profile.getGender(),profile.getLastUpdate(),relavantExDao.findById(profileId),
                    certificationDao.findById(profileId),educationDao.findById(profileId));
            res.add(profileShortPOJO);
        }
        return Response.ok(res).build();
    }



    @GET
    @Path("/getjobs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByEmail(@Auth AccountPrinciple principle) {
        long account_id = accountDao.getAccountByProfileId(principle.getId()).getId();
        List<ApplyJobPOJO> applyJobPOJOS = new ArrayList<>();
        List<JobHasAccount> jobHasAccounts = jobHasAccountDao.getJobsObject(account_id);
        for(JobHasAccount jobHasAccount:jobHasAccounts){
            ApplyJobPOJO applyJobPOJO = new ApplyJobPOJO(jobHasAccount, jobDao.findById(jobHasAccount.getJob_id()));
            applyJobPOJOS.add(applyJobPOJO);
        }
        ResultPOJO resultPOJO = new ResultPOJO("ok");
        resultPOJO.getData().put("jobs", applyJobPOJOS);
        return Response.ok(resultPOJO).build();
    }

//    @GET
//    @Path("/test")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response test() {
//        JobHasAccount jobHasAccount = jobHasAccountDao.test();
//        return Response.ok(jobHasAccount).build();
//    }

    @Transaction
    @POST
    @Path("/apply")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response apply(@Context UriInfo uriInfo, @Auth AccountPrinciple principle, JobHasAccount jobHasAccount) {
        try{
            long profile_id = principle.getId();
            Account account = accountDao.getAccountByProfileId(profile_id);
            Profile profile = profileDao.findById(profile_id);
            long recruiter_id = jobDao.findById(jobHasAccount.getJob_id()).getRecruiter_id();
            jobHasAccountDao.applyJob(jobHasAccount.getJob_id(),accountDao.getAccountByProfileId(principle.getId())
            .getId(), jobHasAccount.getContactEmail(), jobHasAccount.getResumePath(), jobHasAccount.getAdditionalMessage());
            String header = uriInfo.getBaseUriBuilder().toTemplate().replaceAll("api/","");
            String joblink = header+"index.html#jobs/"+jobHasAccount.getJob_id();
            String postion = jobDao.findById(jobHasAccount.getJob_id()).getPosition();
            List<Job> jobs = jobDao.getRandomJobs(3);
            MailUtil.sendApplyConfirm(jobHasAccount.getContactEmail(),profile.getFname(),recruiterDao.findById(recruiter_id).getCompanyName(),joblink, postion, jobs);
            ResultPOJO resultPOJO = new ResultPOJO("ok");
            JobHasAccount res = jobHasAccountDao.getApplyDetail(jobHasAccount.getJob_id(), accountDao.getAccountByProfileId(principle.getId())
                    .getId());
            resultPOJO.getData().put("details", res);
            return Response.ok(resultPOJO).build();
        }catch (Exception e){
            logger.error("JobHasAccountResource error; Message:"+e.getMessage(), e);
            ResultPOJO resultPOJO = new ResultPOJO("error");
            resultPOJO.getMetadata().put("error", new ErrorPOJO("SOME-THIN-GERR", "" +
                    "You have already applied!"));
            return Response.ok(resultPOJO).build();
        }

    }

    @PUT
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public int delete(JobHasAccount jobHasAccount) {
        try{
            jobHasAccountDao.updateStatus("Closed",jobHasAccount.getJob_id(),jobHasAccount.getAccount_id());
            return 1;
        }catch (Exception e){
            return 0;
        }
    }


}
