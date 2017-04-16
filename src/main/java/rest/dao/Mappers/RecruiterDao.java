package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Recruiter;
import com.teachoversea.rest.util.LogSqlFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.sql.Timestamp;

/**
 * Created by lu on 2017/1/24.
 */
@LogSqlFactory
public interface RecruiterDao {

    @SqlQuery("SELECT id FROM recruiter WHERE email = :email and password = :password")
    int findRecruiterId(@Bind("email") String email, @Bind("password") String password);

    @Mapper(RecruiterDaoMapper.class)
    @SqlQuery("SELECT * FROM recruiter WHERE email = :email")
    Recruiter getRecruiterByEmail(@Bind("email") String email);

    @SqlUpdate("UPDATE recruiter SET email = :newemail WHERE email = :oldemail")
    int updateEmail(@Bind("newemail") String newemail, @Bind("oldemail") String oldemail);

    @SqlUpdate("UPDATE recruiter SET password=:newpassword WHERE password=:oldpassword")
    int updatePassword(@Bind("newpassword") String newpassword, @Bind("oldpassword") String oldpassword);

    @SqlUpdate("UPDATE recruiter SET password=:newpassword WHERE id = :id")
    int forgetPassword(@Bind("newpassword") String newpassword, @Bind("id") long id);

    @SqlUpdate("UPDATE recruiter SET status = :status, activeDate=:activeDate WHERE id = :id")
    int updateStatus(@Bind("status") int status, @Bind("activeDate") Timestamp activeDate, @Bind("id") long id);

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO recruiter (status, email, password, fname, lname, phone, contactEmail, title, " +
            "companyName, companyAddress, companySize, foundTime, description, companyWebsite) " +
            "VALUES (:status, :email, :password, :fname, :lname, :phone, :contactEmail, :title, :companyName, " +
            ":companyAddress, :companySize, :foundTime, :description, :companyWebsite)")
    long addRecruiter(@Bind("status") int status, @Bind("email") String email, @Bind("password") String password, @Bind("fname") String fname,
                      @Bind("lname") String lname, @Bind("phone") String phone, @Bind("contactEmail") String contactEmail,
                      @Bind("title") String title, @Bind("companyName") String companyName, @Bind("companyAddress") String companyAddress,
                      @Bind("companySize") int companySize, @Bind("foundTime") int foundTime,
                      @Bind("description") String description, @Bind("companyWebsite") String companyWebsite);


    @Mapper(RecruiterDaoMapper.class)
    @SqlQuery("SELECT * FROM recruiter WHERE id = :id")
    Recruiter findById(@Bind("id") long id);


}
