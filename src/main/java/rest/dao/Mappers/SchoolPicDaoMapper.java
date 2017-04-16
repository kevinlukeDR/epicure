package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.SchoolPic;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/2/3.
 */
public class SchoolPicDaoMapper implements ResultSetMapper<SchoolPic> {
public SchoolPic map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new SchoolPic(rs.getLong("id"), rs.getLong("job_id"), rs.getString("path"),
        rs.getInt("status"));
        }
}
