package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.FeaturedJob;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/2/13.
 */
public class FeaturedJobDaoMapper implements ResultSetMapper<FeaturedJob> {
    public FeaturedJob map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new FeaturedJob(rs.getInt("id"),rs.getLong("job_id"));
    }
}
