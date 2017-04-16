package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Form;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/3/31.
 */
public class FormDaoMapper implements ResultSetMapper<Form> {
    public Form map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new Form(rs.getLong("id"),rs.getString("email"), rs.getString("fname"),rs.getString("lname"),rs.getString("mname"),
                rs.getString("skype"), rs.getString("phone"),
                rs.getString("gender"),rs.getString("nationality"),rs.getString("birth"),rs.getTimestamp("availableDate"),
                rs.getString("major"),rs.getTimestamp("graduateDate"),rs.getString("language"),rs.getString("highestEdu"),
                rs.getString("certification"),rs.getInt("hours"),rs.getBoolean("online"),rs.getBoolean("practicum"),rs.getBoolean("license"),
                rs.getString("experience"),rs.getString("ageGroup"),rs.getString("subject"),rs.getBoolean("visa"),
                rs.getInt("salaryMin"),rs.getInt("salaryMax"),rs.getString("location"),rs.getBoolean("family"),
                rs.getString("spouse"),rs.getString("uuid"),rs.getTimestamp("pageone"),rs.getTimestamp("pagetwo"),
                rs.getTimestamp("pagethree"),rs.getTimestamp("pagefour"), rs.getTimestamp("registerDate"),
                rs.getTimestamp("autoReplyDate"));
    }
}