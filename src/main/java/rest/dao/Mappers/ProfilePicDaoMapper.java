package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.ProfilePic;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/2/6.
 */
public class ProfilePicDaoMapper implements ResultSetMapper<ProfilePic> {
    public ProfilePic map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new ProfilePic(rs.getLong("id"), rs.getLong("profile_id"), rs.getString("path"),
                rs.getInt("status"));
    }
}
