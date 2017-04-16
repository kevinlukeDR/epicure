package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Video;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.sql.Timestamp;

/**
 * Created by lu on 2017/2/24.
 */
public interface VideoDao {
    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO profile_video SET path=:path, nudity=:nudity, uploadDate=:uploadDate, status=:status," +
            " answeredQuestion=:answeredQuestion, profile_id=:profile_id")
    long insertVideo(@Bind("path") String path, @Bind("nudity") boolean nudity, @Bind("uploadDate") Timestamp uploadDate,
                     @Bind("status") int status, @Bind("answeredQuestion") String answeredQuestion,
                     @Bind("profile_id") long profile_id);
    @SqlUpdate("UPDATE profile_video SET status = 20 WHERE profile_id=:profile_id")
    int inactiveByProfileId(@Bind("profile_id") long profile_id);

    @Mapper(VideoDaoMapper.class)
    @SqlQuery("SELECT * FROM profile_video WHERE profile_id =:profile_id AND status = 10")
    Video getVideoByProfileID(@Bind("profile_id") long profile_id);
}
