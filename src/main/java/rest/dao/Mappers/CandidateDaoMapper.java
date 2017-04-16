package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Candidate.Candidate;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/1/9.
 */
public class CandidateDaoMapper implements ResultSetMapper<Candidate> {
    public Candidate map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new Candidate(rs.getInt("id"), rs.getString("email"),rs.getString("fname"),rs.getString("lname"),
                rs.getInt("status"),rs.getString("phone"),rs.getString("skype"),rs.getString("age"),rs.getString("nationalty"),
                rs.getString("location"),rs.getString("allocated_district"),rs.getString("allocated_city"),rs.getString("allocated_school"),
                rs.getString("gender"),rs.getString("education"),rs.getString("years"), rs.getString("employment"), rs.getString("email_status")
                , rs.getTimestamp("setup_time"), rs.getTimestamp("sentemail_time"), rs.getTimestamp("responded_time"), rs.getString("uuid"), rs.getString("source"));
    }
}
