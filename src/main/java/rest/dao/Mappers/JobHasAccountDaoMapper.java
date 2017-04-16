package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.JobHasAccount;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/1/13.
 */
public class JobHasAccountDaoMapper implements ResultSetMapper<JobHasAccount> {
    public JobHasAccount map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new JobHasAccount(rs.getLong("job_id"), rs.getLong("account_id"), rs.getString("status"),
                rs.getTimestamp("applyDate"), rs.getString("contactEmail"), rs.getString("resumePath"),
                rs.getString("additionalMessage"));
    }
}
