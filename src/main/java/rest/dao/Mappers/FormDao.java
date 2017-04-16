package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Form;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by lu on 2017/3/31.
 */
//@LogSqlFactory
public interface FormDao {
    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO email_campaign_form SET email=:email, fname=:fname, lname=:lname, mname=:mname, skype=:skype, phone=:phone,gender=:gender," +
            "nationality=:nationality, birth=:birth, availableDate=:availableDate, major=:major, graduateDate=:graduateDate," +
            "language=:language, highestEdu=:highestEdu, uuid=:uuid, pageone=:pageone")
    long insertPageOne(@Bind("email") String email, @Bind("fname") String fname, @Bind("lname") String lname, @Bind("mname") String mname,
                       @Bind("skype") String skype, @Bind("phone") String phone,
                       @Bind("gender") String gender, @Bind("nationality") String nationality, @Bind("birth") String birth,
                       @Bind("availableDate") Timestamp fnaavailableDateme, @Bind("major") String major,
                       @Bind("graduateDate") Timestamp graduateDate, @Bind("language") String language,
                       @Bind("highestEdu") String highestEdu, @Bind("uuid") String uuid, @Bind("pageone") Timestamp pageOne);

    @SqlUpdate("UPDATE email_campaign_form SET email=:email, fname=:fname, lname=:lname, mname=:mname, skype=:skype, phone=:phone,gender=:gender," +
            "nationality=:nationality, birth=:birth, availableDate=:availableDate, major=:major, graduateDate=:graduateDate," +
            "language=:language, highestEdu=:highestEdu, uuid=:uuid, pageone=:pageone WHERE id=:id")
    long updatePageOne(@Bind("email") String email, @Bind("fname") String fname, @Bind("lname") String lname, @Bind("mname") String mname,
                       @Bind("skype") String skype, @Bind("phone") String phone,
                       @Bind("gender") String gender, @Bind("nationality") String nationality, @Bind("birth") String birth,
                       @Bind("availableDate") Timestamp fnaavailableDateme, @Bind("major") String major,
                       @Bind("graduateDate") Timestamp graduateDate, @Bind("language") String language,
                       @Bind("highestEdu") String highestEdu, @Bind("uuid") String uuid, @Bind("pageone") Timestamp pageOne, @Bind("id") long id);

    @SqlUpdate("UPDATE email_campaign_form SET certification=:certification, hours=:hours, online=:online, practicum=:practicum," +
            "license=:license, experience=:experience, pagetwo=:pagetwo WHERE uuid=:uuid")
    int insertPageTwo(@Bind("certification") String certification, @Bind("hours") int hours, @Bind("online") boolean online,
                      @Bind("practicum") boolean practicum, @Bind("license") boolean license, @Bind("experience") String experience,
                      @Bind("pagetwo") Timestamp pageTwo, @Bind("uuid") String uuid);

    @SqlUpdate("UPDATE email_campaign_form SET ageGroup=:ageGroup, subject=:subject,pagethree=:pagethree WHERE uuid=:uuid")
    int insertPageThree(@Bind("ageGroup") String ageGroup, @Bind("subject") String subject, @Bind("pagethree") Timestamp pageThree,
                        @Bind("uuid") String uuid);

    @SqlUpdate("UPDATE email_campaign_form SET visa=:visa, salaryMin=:salaryMin, salaryMax=:salaryMax, location=:location," +
            "family=:family, spouse=:spouse, pagefour=:pagefour WHERE uuid=:uuid")
    int insertPageFour(@Bind("visa") boolean visa, @Bind("salaryMin") int salaryMin, @Bind("salaryMax") int salaryMax,
                       @Bind("location") String location, @Bind("family") boolean family,
                       @Bind("spouse") String spouse, @Bind("pagefour") Timestamp pagefour, @Bind("uuid") String uuid);

    @Mapper(FormDaoMapper.class)
    @SqlQuery("SELECT * FROM email_campaign_form WHERE id=:id")
    Form findById(@Bind("id") long id);

    @Mapper(FormDaoMapper.class)
    @SqlQuery("SELECT * FROM email_campaign_form WHERE email=:email")
    Form findByEmail(@Bind("email") String email);

    @Mapper(FormDaoMapper.class)
    @SqlQuery("SELECT * FROM email_campaign_form WHERE uuid=:uuid")
    Form findByUUId(@Bind("uuid") String uuid);

    @SqlUpdate("UPDATE email_campaign_form SET registerDate=:registerDate WHERE uuid=:uuid")
    int updateRegister(@Bind("registerDate") Timestamp registerDate, @Bind("uuid") String uuid);

    @Mapper(FormDaoMapper.class)
    @SqlQuery("SELECT * FROM email_campaign_form")
    List<Form> findAll();

    @Mapper(FormDaoMapper.class)
    @SqlQuery("SELECT * FROM email_campaign_form WHERE (nationality LIKE '%United Kingdom%' OR nationality LIKE '%United States%'\n" +
            "OR nationality LIKE '%New Zealand%' OR nationality LIKE '%Australia%' OR nationality LIKE '%South Africa%' OR nationality LIKE '%Ireland%'\n" +
            "OR nationality LIKE '%Canada%') AND (highestEdu = 'Master' OR highestEdu = 'Bachelor' OR highestEdu = 'PhD') AND (year(current_date())-birth >= 18 AND year(current_date())-birth <= 55) " +
            "AND autoReplyDate IS NULL")
    List<Form> findAllQualifiedCandidates();

    @Mapper(FormDaoMapper.class)
    @SqlQuery("SELECT * FROM email_campaign_form WHERE (!((nationality LIKE '%United Kingdom%' OR nationality LIKE '%United States%'\n" +
            "OR nationality LIKE '%New Zealand%' OR nationality LIKE '%Australia%' OR nationality LIKE '%South Africa%' OR nationality LIKE '%Ireland%'\n" +
            "            OR nationality LIKE '%Canada%') AND (highestEdu = 'Master' OR highestEdu = 'Bachelor' OR highestEdu = 'PhD') AND (year(current_date())-birth >= 18 AND year(current_date())-birth <= 55)\n" +
            "            )) AND autoReplyDate IS NULL")
    List<Form> findAllUnqualifiedCandidates();

    @SqlUpdate("UPDATE email_campaign_form SET autoReplyDate=:autoReplyDate WHERE email=:email")
    int setAutoReplyDate(@Bind("autoReplyDate") Timestamp autoReplyDate, @Bind("email") String email);
}
