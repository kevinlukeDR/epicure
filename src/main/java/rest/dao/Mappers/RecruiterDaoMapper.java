package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Recruiter;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/1/24.
 */
public class RecruiterDaoMapper implements ResultSetMapper<Recruiter> {
    public Recruiter map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new Recruiter(rs.getLong("id"),rs.getInt("status"), rs.getString("email"), rs.getString("password"), rs.getString("fname"),
                rs.getString("lname"),rs.getString("phone"),rs.getString("contactEmail"),rs.getString("title"),
                rs.getString("companyName"), rs.getString("companyAddress"),rs.getInt("companySize"),
                rs.getString("description"),rs.getString("companyWebsite"),rs.getInt("foundTime"), rs.getTimestamp("createDate"),
                rs.getTimestamp("activeDate"),rs.getTimestamp("disableDate"));

    }
}
