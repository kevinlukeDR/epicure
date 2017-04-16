package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Job;
import com.teachoversea.rest.util.LogSqlFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.unstable.BindIn;
import org.skife.jdbi.v2.util.LongMapper;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by lu on 2017/1/10.
 */
@LogSqlFactory
public interface JobDao {

    @Mapper(JobDaoMapper.class)
    @SqlQuery("SELECT * FROM job WHERE id = :id")
    Job findById(@Bind("id") long id);

    @Mapper(JobDaoMapper.class)
    @SqlQuery("SELECT * FROM job WHERE position = :position")
    Job findByPosition(@Bind("position") String position);


    @Mapper(JobDaoMapper.class)
    @SqlQuery("SELECT * FROM job WHERE status='Published' and position like :position and city like :city and subject like :subject and grade like :grade")
    List<Job> getAllJob(@Bind("position") String position, @Bind("city") String city, @Bind("subject") String subject, @Bind("grade") String grade);

    @Mapper(JobDaoMapper.class)
    @SqlQuery("select * from job where position like :position and city in (<cityList>) and subject in (<subList>) and grade in (<gradeList>)")
    List<Job> getAllJobByMulti(@Bind("position") String position, @BindIn("cityList") List<String> cityList, @BindIn("subList") List<String> subList, @BindIn("gradeList") List<String> gradeList);

    @SqlQuery("SELECT count(*) FROM job WHERE status='Published'")
    int getTotalNumber();

    @SqlQuery("SELECT count(*) FROM job where recruiter_id = :recruiter_id")
    int getTotalByRecruiter(@Bind("recruiter_id") long recruiter_id);

    @Mapper(JobDaoMapper.class)
    @SqlQuery("SELECT * FROM job where recruiter_id = :recruiter_id")
    List<Job> getTotalById(@Bind("recruiter_id") long recruiter_id);
    @Mapper(JobDaoMapper.class)
    @SqlQuery("SELECT * FROM job where recruiter_id = :recruiter_id  AND status=:status")
    List<Job> getAllByIdAndStatus(@Bind("recruiter_id") long recruiter_id, @Bind("status") String status);

    @Mapper(JobDaoMapper.class)
    @SqlQuery("SELECT * FROM job")
    List<Job> getTotalJobs();

    @Mapper(LongMapper.class)
    @SqlQuery("SELECT id FROM job WHERE recruiter_id = :recruiter_id")
    List<Long> getJobIdByRecrutierId(@Bind("recruiter_id") long recruiter_id);

    @SqlUpdate("UPDATE job SET status = 1 where email = :email")
    int sendEmail(@Bind("email") String email);

    @Mapper(JobDaoMapper.class)
    @SqlQuery("SELECT * FROM job ORDER BY rand() LIMIT :limit")
    List<Job> getRandomJobs(@Bind("limit") int limit);

    @SqlUpdate("UPDATE job SET status = :status where id = :id")
    int updateStatus(@Bind("status") String status, @Bind("id") long id);

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO job (position, city, subject, grade, jobType, classSize, contractLen, salaryMin, " +
            "salaryMax, salaryMessage, experience, postDate,startDate, schoolName, eligibleCandidates, nativeSpeaker, " +
            "certificates, education, additional, accommodation, airfare, weeklyRestDay, vacation, annualLeave, " +
            "bonus, lunch, chinese, aboutSchool,status, schoolType, recruiter_id) VALUES (" +
            ":position, :city, :subject, :grade, :jobType, :classSize, :contractLen, :salaryMin, :salaryMax, :salaryMessage, :experience,:postDate," +
            " :startDate, :schoolName, :eligibleCandidates, :nativeSpeaker, :certificates, :education, " +
            ":additional, :accommodation, :airfare, :weeklyRestDay, :vacation, :annualLeave, :bonus, :lunch, :chinese, " +
            ":aboutSchool, 'Draft', :schoolType, :recruiter_id)")
    long insertJob(@Bind("position") String position, @Bind("city") String city, @Bind("subject") String subject, @Bind("grade") String grade,
                   @Bind("jobType") String jobType, @Bind("classSize") int classSize, @Bind("contractLen") int contractLen,
                   @Bind("salaryMin") int salaryMin, @Bind("salaryMax") int salaryMax, @Bind("salaryMessage") String salaryMessage, @Bind("experience") int experience,
                   @Bind("postDate") Timestamp postDate, @Bind("startDate") Timestamp startDate,
                   @Bind("schoolName") String schoolName, @Bind("eligibleCandidates") boolean eligibleCandidates,
                   @Bind("nativeSpeaker") boolean nativeSpeaker, @Bind("certificates") String certificates, @Bind("education") String education,
                   @Bind("additional") String additional, @Bind("accommodation") String accommodation, @Bind("airfare") String airfare,
                   @Bind("weeklyRestDay") String weeklyRestDay, @Bind("vacation") String vacation, @Bind("annualLeave") String annualLeave,
                   @Bind("bonus") String bonus, @Bind("lunch") boolean lunch, @Bind("chinese") boolean chinese, @Bind("aboutSchool") String aboutSchool,
                   @Bind("schoolType") String schoolType, @Bind("recruiter_id") long recruiter_id);

    @SqlUpdate("UPDATE job SET position = :position, city= :city, subject =:subject, grade=:grade, jobType=:jobType," +
            "classSize=:classSize, contractLen=:contractLen, salaryMin=:salaryMin, salaryMax=:salaryMax,salaryMessage=:salaryMessage,experience=:experience, " +
            "startDate=:startDate, schoolName=:schoolName, eligibleCandidates=:eligibleCandidates," +
            "nativeSpeaker=:nativeSpeaker,certificates=:certificates, education=:education, additional=:additional, " +
            "accommodation=:accommodation, airfare=:airfare, weeklyRestDay=:weeklyRestDay, vacation=:vacation, annualLeave=:annualLeave, " +
            "bonus=:bonus, lunch=:lunch, chinese=:chinese, aboutSchool=:aboutSchool, schoolType=:schoolType WHERE id = :id")
    int updateJob(@Bind("position") String position, @Bind("city") String city, @Bind("subject") String subject, @Bind("grade") String grade,
                  @Bind("jobType") String jobType, @Bind("classSize") int classSize, @Bind("contractLen") int contractLen,
                  @Bind("salaryMin") int salaryMin, @Bind("salaryMax") int salaryMax, @Bind("salaryMessage") String salaryMessage, @Bind("experience") int experience, @Bind("startDate") Timestamp startDate,
                  @Bind("schoolName") String schoolName, @Bind("eligibleCandidates") boolean eligibleCandidates,
                  @Bind("nativeSpeaker") boolean nativeSpeaker, @Bind("certificates") String certificates, @Bind("education") String education,
                  @Bind("additional") String additional, @Bind("accommodation") String accommodation, @Bind("airfare") String airfare,
                  @Bind("weeklyRestDay") String weeklyRestDay, @Bind("vacation") String vacation, @Bind("annualLeave") String annualLeave,
                  @Bind("bonus") String bonus, @Bind("lunch") boolean lunch, @Bind("chinese") boolean chinese, @Bind("aboutSchool") String aboutSchool,
                  @Bind("schoolType") String schoolType, @Bind("id") long id);

    @SqlUpdate("DELETE FROM job WHERE id = :id")
    int deleteDraft(@Bind("id") long id);

}
