package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.SystemEmail;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/2/21.
 */
public class SystemEmailDaoMapper implements ResultSetMapper<SystemEmail> {
    public SystemEmail map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new SystemEmail(rs.getLong("id"),rs.getInt("status"), rs.getInt("info"),rs.getTimestamp("inactiveDate"),
                rs.getLong("email_campaign_id"));
    }
}
