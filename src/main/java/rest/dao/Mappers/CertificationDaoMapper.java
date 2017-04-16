package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Certification;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/1/13.
 */
public class CertificationDaoMapper implements ResultSetMapper<Certification> {
    public Certification map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
            return new Certification(rs.getLong("id"), rs.getString("type"), rs.getTimestamp("issueDate"),
                    rs.getString("issuingBody"), rs.getLong("profile_id"));
            }
}
