package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Resume;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/2/3.
 */
public class ResumeDaoMapper implements ResultSetMapper<Resume> {
    public Resume map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new Resume(rs.getLong("id"), rs.getLong("profile_id"), rs.getString("path"), rs.getInt("status"),
                rs.getInt("version"),rs.getTimestamp("setDate"));
    }
}
