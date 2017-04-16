package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Resume;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.util.LongMapper;

import java.util.List;

/**
 * Created by lu on 2017/2/3.
 */
public interface ResumeDao {
    @Mapper(ResumeDaoMapper.class)
    @SqlQuery("SELECT * FROM resume WHERE profile_id = :profile_id")
    List<Resume> findAllById(@Bind("profile_id") long profile_id);

    @Mapper(ResumeDaoMapper.class)
    @SqlQuery("SELECT * FROM resume WHERE profile_id= :profile_id AND status=30")
    Resume findLatestById(@Bind("profile_id") long profile_id);

    @Mapper(ResumeDaoMapper.class)
    @SqlQuery("SELECT * FROM resume where id=:id")
    Resume findById(@Bind("id") long id);

    @Mapper(LongMapper.class)
    @SqlQuery("SELECT id FROM resume where profile_id=:profile_id AND status != 20")
    List<Long> findAllIdById(@Bind("profile_id") long profile_id);

    @SqlQuery("SELECT version FROM resume WHERE profile_id = :profile_id ORDER BY version DESC LIMIT 1")
    int findMaxVersionById(@Bind("profile_id") long profile_id);

    @SqlUpdate("UPDATE resume SET status=:status WHERE id=:id")
    int setStatus(@Bind("status") int status, @Bind("id") long id);

//    @SqlQuery("SELECT path FROM resume WHERE profile_id = :profile_id AND status = 10")
//    String findActivePathById(@Bind("profile_id") long profile_id);

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO resume (path, status, version, profile_id) VALUES " +
            "(:path, :status, :version, :profile_id) ")
    int updateResume(@Bind("path") String path, @Bind("status") int active, @Bind("version") int version,
                     @Bind("profile_id") long profile_id);
}
