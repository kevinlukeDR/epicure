package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Session;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/2/10.
 */
public class SessionDaoMapper implements ResultSetMapper<Session> {
    public Session map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new Session(rs.getLong("id"), rs.getString("uuid"), rs.getLong("account_id"),
                rs.getInt("type"),rs.getTimestamp("refreshedDate"));
    }
}
