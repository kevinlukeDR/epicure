package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.ProfileHasTag;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/3/10.
 */
public class ProfileHasTagDaoMapper implements ResultSetMapper<ProfileHasTag> {
    public ProfileHasTag map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new ProfileHasTag(rs.getLong("profile_id"),rs.getInt("tag_id"));
    }
}
