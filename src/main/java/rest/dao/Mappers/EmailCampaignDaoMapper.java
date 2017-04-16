package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.EmailCampaign;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/3/13.
 */
public class EmailCampaignDaoMapper implements ResultSetMapper<EmailCampaign> {
    public EmailCampaign map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new EmailCampaign(rs.getLong("id"), rs.getString("email"), rs.getString("uuid"), rs.getString("fname"), rs.getString("lname"));
    }
}
