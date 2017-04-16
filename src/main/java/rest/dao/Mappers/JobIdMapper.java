package rest.dao.Mappers;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/1/13.
 */
public class JobIdMapper implements ResultSetMapper<Long> {
    public Long map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new Long(rs.getLong("job_id"));
    }
}
