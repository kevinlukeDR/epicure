package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.JobHasAccount;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.List;

/**
 * Created by lu on 2017/1/13.
 */
public interface JobHasAccountDao {
    @Mapper(JobIdMapper.class)
    @SqlQuery("SELECT job_id FROM job_has_account WHERE account_id = :account_id")
    List<Long> getJobs(@Bind("account_id") long account_id);

    @Mapper(JobHasAccountDaoMapper.class)
    @SqlQuery("SELECT * FROM job_has_account WHERE job_id = :job_id AND account_id=:account_id")
    JobHasAccount getJHAbyJob_id(@Bind("job_id") long job_id, @Bind("account_id") long account_id);

    @Mapper(JobHasAccountDaoMapper.class)
    @SqlQuery("SELECT * FROM job_has_account WHERE job_id = :job_id AND account_id =:account_id")
    JobHasAccount getApplyDetail(@Bind("job_id") long job_id, @Bind("account_id") long account_id);

    @Mapper(JobHasAccountDaoMapper.class)
    @SqlQuery("SELECT * FROM job_has_account WHERE account_id =:account_id")
    List<JobHasAccount> getJobsObject(@Bind("account_id") long account_id);


    @Mapper(JobHasAccountDaoMapper.class)
    @SqlQuery("SELECT * FROM job_has_account WHERE job_id =:job_id")
    List<JobHasAccount> getAccountsObject(@Bind("job_id") long job_id);

    @SqlUpdate("UPDATE job_has_account SET status = :status WHERE job_id = :job_id AND account_id = :account_id")
    int updateStatus(@Bind("status") String status, @Bind("job_id") long job_id, @Bind("account_id") long account_id);

    @Mapper(AccountMapper.class)
    @SqlQuery("SELECT account_id FROM job_has_account WHERE job_id = :job_id")
    List<String> getAccounts(@Bind("job_id") long job_id);

    @SqlUpdate("INSERT INTO job_has_account (job_id, account_id, status, contactEmail,resumePath,additionalMessage) " +
            "VALUES (:job_id, :account_id, 'Reviewing', :contactEmail, :resumePath, :additionalMessage)")
    int applyJob(@Bind("job_id") long job_id, @Bind("account_id") long account_id, @Bind("contactEmail") String contactEmail,
                 @Bind("resumePath") String resumePath, @Bind("additionalMessage") String additionalMessage);


    /**
     * @@apiNote Never use DELETE easily
     */
//    @SqlUpdate("DELETE FROM job_has_account WHERE job_id = :job_id and account_id = :account_id")
//    int deleteApplication(@Bind("job_id") long job_id, @Bind("account_id") long account_id);
}
