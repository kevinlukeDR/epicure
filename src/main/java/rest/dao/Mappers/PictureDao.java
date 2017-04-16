package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Picture;
import com.teachoversea.rest.dao.Objects.Job.ProfilePic;
import com.teachoversea.rest.dao.Objects.Job.SchoolPic;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.List;

/**
 * Created by lu on 2017/2/3.
 */
public interface PictureDao {
    /**
     * @@apiNote  Recruiter Pictures
     */

    @Mapper(PictureDaoMapper.class)
    @SqlQuery("SELECT * FROM picture WHERE recruiter_id = :id and status = :status")
    List<Picture> findRecruiterPicById(@Bind("id") long id, @Bind("status") int status);

    @SqlUpdate("INSERT INTO picture (path, status, recruiter_id) VALUES (:path, :status, :recruiter_id)")
    int insertRecruiterPic(@Bind("path") String path, @Bind("status") int status, @Bind("recruiter_id") long recruiter_id);

    @SqlUpdate("UPDATE logo SET (status=:status) WHERE recruiter_id = :recruiter_id")
    int updateRecruiterPicStatus(@Bind("status") int status, @Bind("recruiter_id") long recruiter_id);

    /**
     * @@apiNote  Recruiter Logo
     */

    @Mapper(PictureDaoMapper.class)
    @SqlQuery("SELECT * FROM logo WHERE recruiter_id = :id and status = :status")
    Picture findRecruiterLogoById(@Bind("id") long id, @Bind("status") int status);

    @SqlUpdate("INSERT INTO logo (path, status, recruiter_id) VALUES (:path, :status, :recruiter_id)")
    int insertRecruiterLogo(@Bind("path") String path, @Bind("status") int status, @Bind("recruiter_id") long recruiter_id);

    @SqlUpdate("UPDATE logo SET (status=:status) WHERE recruiter_id = :recruiter_id")
    int updateRecruiterLogo(@Bind("status") int status, @Bind("recruiter_id") long recruiter_id);

    /**
     * @@apiNote  School Pictures
     */

    @Mapper(SchoolPicDaoMapper.class)
    @SqlQuery("SELECT * FROM school_picture WHERE job_id = :id and status = :status")
    List<SchoolPic> findSchoolPicById(@Bind("id") long id, @Bind("status") int status);

    @SqlUpdate("INSERT INTO school_picture (path, status, job_id) VALUES (:path, :status, :job_id)")
    int insertSchoolPic(@Bind("path") String path, @Bind("status") int status, @Bind("job_id") long job_id);


    @SqlUpdate("UPDATE school_picture SET status =:status where job_id = :job_id")
    int updateSchoolPicStatus(@Bind("status") int status, @Bind("job_id") long job_id);

    /**
     * @@apiNote  Profile Picture
     */
    @Mapper(ProfilePicDaoMapper.class)
    @SqlQuery("SELECT * FROM profile_picture WHERE profile_id = :profile_id and status = :status")
    ProfilePic findProfilePicById(@Bind("profile_id") long profile_id, @Bind("status") int status);

    @SqlUpdate("INSERT INTO profile_picture (path, status, profile_id) VALUES (:path, :status, :profile_id)")
    int insertProfilePic(@Bind("path") String path, @Bind("status") int status, @Bind("profile_id") long profile_id);


    @SqlUpdate("UPDATE profile_picture SET status =:status where profile_id = :profile_id")
    int updateProfilePicStatus(@Bind("status") int status, @Bind("profile_id") long profile_id);
}
