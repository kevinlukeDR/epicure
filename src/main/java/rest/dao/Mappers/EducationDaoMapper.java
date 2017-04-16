package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Education;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/1/13.
 */
public class EducationDaoMapper implements ResultSetMapper<Education> {
    public Education map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new Education(rs.getLong("id"), rs.getString("degree"), rs.getString("field"), rs.getTimestamp("fromDate"),
                rs.getTimestamp("toDate"),rs.getString("school"),rs.getString("country"),
                rs.getString("city"),rs.getLong("profile_id"));
    }
}
