package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Feedback;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/2/23.
 */
public class FeedbackDaoMapper implements ResultSetMapper<Feedback> {
    public Feedback map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new Feedback(rs.getLong("id"), rs.getString("reason"));
    }
}
