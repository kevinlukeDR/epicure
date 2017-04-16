package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Profile;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.sql.Timestamp;

/**
 * Created by lu on 2017/1/13.
 */

public interface ProfileDao {

    @Mapper(ProfileDaoMapper.class)
    @SqlQuery("SELECT * FROM profile WHERE id = :id")
    Profile findById(@Bind("id") long id);


    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO profile (fname, lname) VALUES (:fname, :lname)")
    long addProfile(@Bind("fname") String fname, @Bind("lname") String lname);

    @SqlUpdate("DELETE FROM profile WHERE id=:id")
    int deleteProfile(@Bind("id") long id);

    //TODO Have problem
    @SqlUpdate("UPDATE profile SET fname = :fname, lname= :lname, availableDate= :availableDate, birth= :birth, " +
            "nationality= :nationality, gender= :gender, personalWebsite= :personalWebsite, linkedin= :linkedin, " +
            "facebook= :facebook,instagram= :instagram, twitter= :twitter, wechat= :wechat, lastUpdate2=lastUpdate," +
            "lastUpdate3 = lastUpdate2 WHERE " +
            "( id = :profile_id)")
    int updateProfile(@Bind("fname") String fname, @Bind("lname") String lname, @Bind("availableDate") Timestamp availableDate,
                      @Bind("birth") String birth, @Bind("nationality") String nationality, @Bind("gender") String gender,
                      @Bind("personalWebsite") String personalWebsite,
                      @Bind("linkedin") String linkedin, @Bind("facebook") String facebook, @Bind("instagram") String instagram,
                      @Bind("twitter") String twitter, @Bind("wechat") String wechat, @Bind("profile_id") long profile_id);

//
//    @SqlUpdate("UPDATE profile SET resume VALUES (:resume)")
//    void addResume(@Bind("resume") String resume);

    @SqlUpdate("UPDATE profile SET fname =:fname, lname =:lname, availableDate = :availableDate, birth=:birth, " +
            "nationality =:nationality, gender =:gender WHERE id =:id")
    int updateBasic(@Bind("fname") String fname, @Bind("lname") String lname, @Bind("availableDate") Timestamp availableDate,
                    @Bind("birth") String birth, @Bind("nationality") String nationality, @Bind("gender") String gender, @Bind("id") long id);

    @SqlUpdate("UPDATE profile SET personalWebsite= :personalWebsite, linkedin= :linkedin, " +
            "facebook= :facebook,instagram= :instagram, twitter= :twitter, wechat= :wechat WHERE id =:id")
    int updateBasicContact(@Bind("personalWebsite") String personalWebsite,
                           @Bind("linkedin") String linkedin, @Bind("facebook") String facebook, @Bind("instagram") String instagram,
                           @Bind("twitter") String twitter, @Bind("wechat") String wechat, @Bind("id") long id);

    @SqlUpdate("UPDATE profile SET lastUpdate2=lastUpdate," +
            "lastUpdate3 = lastUpdate2 WHERE id=:id")
    int updateTime(@Bind("id") long id);

    @SqlUpdate("UPDATE profile SET salaryMin=:salaryMin, salaryMax=:salaryMax WHERE id=:id")
    int updateSalary(@Bind("salaryMin") int salaryMin, @Bind("salaryMax") int salaryMax, @Bind("id") long id);
}
