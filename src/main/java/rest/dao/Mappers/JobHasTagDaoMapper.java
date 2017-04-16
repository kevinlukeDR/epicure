package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.JobHasTag;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/3/10.
 */
public class JobHasTagDaoMapper implements ResultSetMapper<JobHasTag> {
    public JobHasTag map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new JobHasTag(rs.getLong("job_id"),rs.getInt("tag_id"));
    }
}
