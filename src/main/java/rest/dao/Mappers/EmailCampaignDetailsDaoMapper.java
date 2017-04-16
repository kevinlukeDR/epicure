package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.EmailCampaignDetails;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/3/13.
 */
public class EmailCampaignDetailsDaoMapper implements ResultSetMapper<EmailCampaignDetails> {
    public EmailCampaignDetails map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new EmailCampaignDetails(rs.getLong("id"), rs.getTimestamp("sent_date"), rs.getTimestamp("respond_date"),
                rs.getString("template_version"), rs.getLong("email_campaign_id"));
    }
}
