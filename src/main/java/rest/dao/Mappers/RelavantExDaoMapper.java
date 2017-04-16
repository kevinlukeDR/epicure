package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.RelavantEx;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/1/13.
 */
public class RelavantExDaoMapper implements ResultSetMapper<RelavantEx> {
    public RelavantEx map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new RelavantEx(rs.getLong("id"), rs.getString("role"), rs.getString("company"), rs.getTimestamp("fromDate"),
                rs.getTimestamp("toDate"),rs.getString("responsibility"),rs.getLong("profile_id"));
    }
}
