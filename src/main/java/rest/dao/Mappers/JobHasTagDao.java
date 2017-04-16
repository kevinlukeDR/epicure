package rest.dao.Mappers;

import com.teachoversea.rest.util.LogSqlFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.util.IntegerMapper;

import java.util.List;

/**
 * Created by lu on 2017/3/10.
 */
@LogSqlFactory
public interface JobHasTagDao {

    @SqlUpdate("DELETE FROM job_has_tag WHERE job_id=:job_id")
    int deleteJobTag(@Bind("job_id") long job_id);

    @SqlUpdate("INSERT INTO job_has_tag SET job_id=:job_id, tag_id=:tag_id")
    int insertJobTag(@Bind("job_id") long job_id, @Bind("tag_id") int tag_id);

    @Mapper(IntegerMapper.class)
    @SqlQuery("SELECT tag_id FROM job_has_tag WHERE job_id=:job_id")
    List<Integer> getTag(@Bind("job_id") long job_id);
}
